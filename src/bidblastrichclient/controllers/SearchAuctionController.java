package bidblastrichclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import lib.ImageToolkit;
import model.AuctionCategory;
import repositories.AuctionCategoriesRepository;
import repositories.AuctionsRepository;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;
import model.Auction;
import model.HypermediaFile;
import model.Offer;
import model.User;
import java.util.Date;
import lib.DateToolkit;

public class SearchAuctionController implements Initializable {

    @FXML
    private TextField tfSearchQuery;
    @FXML
    private ComboBox<AuctionCategory> cbCategories;
    @FXML
    private ComboBox<?> cbPriceRanges;
    @FXML
    private TextField tfLimit;
    @FXML
    private TextField tfOffset;
    @FXML
    private Pane pnAuctionsList;
    @FXML
    private TableColumn colAuctionId;
    @FXML
    private TableColumn colAuctionTitle;
    @FXML
    private TableColumn<Auction, String> colAuctionClosingDate;
    @FXML
    private TableColumn<Auction, Image> colAuctionImage;
    @FXML
    private TableColumn<Auction, String> colAuctioneerName;
    @FXML
    private TableColumn<Auction, String> colLastOfferAmount;
    @FXML
    private TableView<Auction> tvAuctions;
    
    private ObservableList<AuctionCategory> auctionCategories;
    
    private ObservableList<Auction> allAuctions;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureAuctionsTable();
        loadAllAuctionCategories();
        loadAuctions();
    }
    
    private void configureAuctionsTable() {
        colAuctionTitle.setCellValueFactory(new PropertyValueFactory("title"));
        colAuctionId.setCellValueFactory(new PropertyValueFactory("id"));
        colAuctionClosingDate.setCellValueFactory(cellData -> {
            Date closingDate = cellData.getValue().getClosesAt();
            
            return new SimpleStringProperty(
                DateToolkit.parseToFullDateWithHour(closingDate)
            );
        });
        colAuctioneerName.setCellValueFactory(cellData -> {
            User auctioneer = cellData.getValue().getAuctioneer();
            
            return new SimpleStringProperty(
                auctioneer != null ? auctioneer.getFullName() : "NA"
            );
        });
        colLastOfferAmount.setCellValueFactory(cellData -> {
            Offer lastOffer = cellData.getValue().getLastOffer();
            
            return new SimpleStringProperty(
                lastOffer != null ? ("$" + lastOffer.getAmount()) : "$0"
            );
        });
        configureAuctionImageColumn();
    }
    
    private void configureAuctionImageColumn() {
        colAuctionImage.setCellValueFactory(cellData -> {
            HypermediaFile defaultAuctionImage = 
                cellData.getValue().getMediaFiles().isEmpty() 
                    ? null 
                    : cellData.getValue().getMediaFiles().get(0);
            
            if (defaultAuctionImage != null) {
                Image jfxImage = 
                    ImageToolkit.decodeBase64ToImage(defaultAuctionImage.getContent());
                return new javafx.beans.property.SimpleObjectProperty<>(jfxImage);
            }
            return new javafx.beans.property.SimpleObjectProperty<>(null);
        });

        colAuctionImage.setCellFactory(new Callback<TableColumn<Auction, Image>, TableCell<Auction, Image>>() {
            @Override
            public TableCell<Auction, Image> call(TableColumn<Auction, Image> param) {
                return new TableCell<Auction, Image>() {
                    private final ImageView imageView = new ImageView();

                    @Override
                    protected void updateItem(Image item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            imageView.setImage(item);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50 * item.getHeight() / item.getWidth());
                            setGraphic(imageView);
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });
    }
    
    private void loadAllAuctionCategories() {
        new AuctionCategoriesRepository().getAuctionCategories(
            new IProcessStatusListener<List<AuctionCategory>>() {
                @Override
                public void onSuccess(List<AuctionCategory> categories) {
                    Platform.runLater(() -> {
                        auctionCategories = FXCollections.observableArrayList();
                        auctionCategories.addAll(categories);
                        cbCategories.setItems(auctionCategories);
                        
                        cbCategories.setDisable(false);
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de conexión");
                        alert.setHeaderText(null);
                        alert.setContentText("Ocurrió un error al cargar las "
                            + "categorías, por favor intente más tarde");
                        alert.showAndWait();
                        
                        cbCategories.setDisable(true);
                    });
                }
            }
        );
    }
    
    private void loadAuctions() {
        int minimumPrice = 0, 
            maximumPrice = Integer.MAX_VALUE,
            limit = 10,
            offset = 0;
        String searchQuery = "",
            categories = "";
        /*PriceRange priceFilter = priceFilterSelected.getValue();
        if(priceFilter != null) {
            if(priceFilter.getMinimumAmount() != Float.NEGATIVE_INFINITY) {
                minimumPrice = (int)priceFilter.getMinimumAmount();
            }

            if(priceFilter.getMaximumAmount() != Float.POSITIVE_INFINITY) {
                maximumPrice = (int)priceFilter.getMaximumAmount();
            }
        }*/

        new AuctionsRepository().getAuctionsList(
            searchQuery, limit, offset,
            //ApiFormatter.parseToPlainMultiValueParam(categoryFiltersSelected.getValue()),
            categories,
            minimumPrice, maximumPrice,
            new IProcessStatusListener<List<Auction>>() {
                @Override
                public void onSuccess(List<Auction> auctions) {
                    Platform.runLater(() -> {
                        allAuctions = FXCollections.observableArrayList();
                        allAuctions.addAll(auctions);
                        tvAuctions.setItems(allAuctions);
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de conexión");
                        alert.setHeaderText(null);
                        alert.setContentText("Ocurrió un error al cargar las "
                            + "subastas, por favor intente más tarde");
                        alert.showAndWait();
                    });
                }
            }
        );
    }

    @FXML
    private void btnSearchClick(ActionEvent event) {
    }
}
