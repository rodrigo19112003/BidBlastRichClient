package bidblastrichclient.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import model.PriceRange;
import java.util.Arrays;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lib.Navigation;
import lib.ValidationToolkit;

public class SearchAuctionController implements Initializable {

    @FXML
    private TextField tfSearchQuery;
    @FXML
    private ComboBox<AuctionCategory> cbCategories;
    @FXML
    private ComboBox<PriceRange> cbPriceRanges;
    @FXML
    private TextField tfLimit;
    @FXML
    private TextField tfOffset;
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
    @FXML
    private ImageView imgReturnToPreviousPage;
    
    private ObservableList<AuctionCategory> allAuctionCategories;
    
    private ObservableList<Auction> allAuctions;
    
    private ObservableList<PriceRange> allPriceRanges;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureAuctionsTable();
        loadPriceRanges();
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
                        allAuctionCategories = FXCollections.observableArrayList();
                        allAuctionCategories.addAll(categories);
                        cbCategories.setItems(allAuctionCategories);
                        
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
        int minimumPrice = getMinimumPriceFilterValue(), 
            maximumPrice = getMaximumPriceFilterValue(),
            limit = getLimitFilterValue(),
            offset = getOffsetFilterValue();
        String searchQuery = tfSearchQuery.getText().trim(),
            categories = getCategoryFilterValue();

        new AuctionsRepository().getAuctionsList(
            searchQuery, limit, offset, categories, minimumPrice, maximumPrice,
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
    
    private int getMinimumPriceFilterValue() {
        int minimumPrice = 0;
        
        PriceRange priceFilter = cbPriceRanges.getSelectionModel().getSelectedItem();
        if(priceFilter != null) {
            if(priceFilter.getMinimumAmount() != Float.NEGATIVE_INFINITY) {
                minimumPrice = (int)priceFilter.getMinimumAmount();
            }
        }
        
        return minimumPrice;
    }
    
    private int getMaximumPriceFilterValue() {
        int maximumPrice = Integer.MAX_VALUE;
        
        PriceRange priceFilter = cbPriceRanges.getSelectionModel().getSelectedItem();
        if(priceFilter != null) {
            if(priceFilter.getMaximumAmount() != Float.POSITIVE_INFINITY) {
                maximumPrice = (int)priceFilter.getMaximumAmount();
            }
        }
        
        return maximumPrice;
    }
    
    private int getLimitFilterValue() {
        int limit = 10;
        
        String limitValue = tfLimit.getText().trim();
        if(ValidationToolkit.isNumeric(limitValue)) {
            limit = Integer.parseInt(limitValue);
        }
        
        return limit;
    }
    
    private int getOffsetFilterValue() {
        int offset = 0;
        
        String offsetValue = tfOffset.getText().trim();
        if(ValidationToolkit.isNumeric(offsetValue)) {
            offset = Integer.parseInt(offsetValue);
        }
        
        return offset;
    }
    
    private String getCategoryFilterValue() {
        String categoryValue = "";
        
        AuctionCategory selectedCategory = cbCategories.getSelectionModel().getSelectedItem();
        if(selectedCategory != null) {
            categoryValue = String.valueOf(selectedCategory.getId());
        }
        
        return categoryValue;
    }
    
    private void loadPriceRanges() {
        allPriceRanges = FXCollections.observableArrayList();
        allPriceRanges.addAll(
            new ArrayList<>(Arrays.asList(
                new PriceRange("Menos de $100", Float.NEGATIVE_INFINITY, 100.0f),
                new PriceRange("$100 a menos de $200", 100.0f, 200.0f),
                new PriceRange("$200 a menos de $300", 200.0f, 300.0f),
                new PriceRange("$300 a menos de $500", 300.0f, 500.0f),
                new PriceRange("$500 a menos de $750", 500.0f, 750.0f),
                new PriceRange("$750 a menos de $1000", 750.0f, 1000.0f),
                new PriceRange("$1000 o más", 1000.0f, Float.POSITIVE_INFINITY)
            ))
        );
        cbPriceRanges.setItems(allPriceRanges);
    }

    @FXML
    private void btnSearchClick(ActionEvent event) {
        boolean validFilters = validateFiltersValues();
        
        if(!validFilters) {
            showInvalidFiltersValuesError();
        } else {
            loadAuctions();
        }
    }
    
    private boolean validateFiltersValues() {
        String limit = tfLimit.getText().trim();
        String offset = tfOffset.getText().trim();
        
        boolean isValidLimit = limit.isEmpty() 
            || (ValidationToolkit.isNumeric(limit) && Integer.parseInt(limit) > 0);
        boolean isValidOffset = offset.isEmpty() || ValidationToolkit.isNumeric(offset);
        
        return isValidLimit && isValidOffset;
    }
    
    private void showInvalidFiltersValuesError() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Filtros inválidos");
        alert.setHeaderText(null);
        alert.setContentText("Verifique que los valores ingresados en los campos "
            + "offset y limit sean números enteros no negativos. Tome en cuenta "
            + "que el valor mínimo aceptado de limit es 1");
        alert.showAndWait();
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/MainMenuView.fxml"));
        baseStage.setTitle("Menu principal");
        baseStage.show();
    }

    @FXML
    private void btnMakeOfferClick(ActionEvent event) {
        Auction selectedAcution = tvAuctions.getSelectionModel().getSelectedItem();
        
        if(selectedAcution == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Seleccione una subasta");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione la subasta sobre la cual le "
                + "gustaría realizar su oferta");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bidblastrichclient/views/MakeOfferView.fxml"));
                Parent root = loader.load();
                MakeOfferController controller = loader.getController();

                controller.setIdAuction(selectedAcution.getId());
                Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();
                baseStage.setScene(new Scene(root));
                baseStage.setTitle("Hacer oferta en subasta");
                baseStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
