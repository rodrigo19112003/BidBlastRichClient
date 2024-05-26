package bidblastrichclient.controllers;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import lib.CurrencyToolkit;
import lib.DateToolkit;
import lib.ImageToolkit;
import lib.Navigation;
import lib.ValidationToolkit;
import model.Auction;
import model.HypermediaFile;
import model.User;
import repositories.AuctionsRepository;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;

public class CompletedAuctionsListController implements Initializable {

    @FXML
    private TextField tfAuctionToSearch;
    @FXML
    private TableView<Auction> tvCompletedAuctions;
    @FXML
    private TableColumn<?, ?> colAuctionTitle;
    @FXML
    private TableColumn<Auction, Image> colAuctioneerAvatar;
    @FXML
    private TableColumn<Auction, String> colAuctioneerFullName;
    @FXML
    private TableColumn<Auction, Image> colAuctionImage;
    @FXML
    private TableColumn<Auction, String> colPurchaseDate;
    @FXML
    private TableColumn<Auction, String> colPrice;
    @FXML
    private TextField tfOffset;
    @FXML
    private TextField tfLimit;
    private ObservableList<Auction> completedAuctions;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureCompletedAuctionsTable();
        loadCompletedAuctions();
    }    
    
    private void configureCompletedAuctionsTable() {
        colAuctionTitle.setCellValueFactory(new PropertyValueFactory("title"));
        colPurchaseDate.setCellValueFactory(cellData -> {
            Date purchaseDate = cellData.getValue().getUpdatedDate();
            
            return new SimpleStringProperty(
                DateToolkit.parseToFullDateWithHour(purchaseDate)
            );
        });
        colAuctioneerFullName.setCellValueFactory(cellData -> {
            User auctioneer = cellData.getValue().getAuctioneer();
            
            return new SimpleStringProperty(
                auctioneer != null ? auctioneer.getFullName() : "NA"
            );
        });
        colPrice.setCellValueFactory(cellData -> {
            float price = cellData.getValue().getLastOffer().getAmount();
            
            return new SimpleStringProperty(
                    CurrencyToolkit.parseToMXN(price)
            );
        });
        configureAuctionesAvatarColumn();
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
    
    private void configureAuctionesAvatarColumn() {
        colAuctioneerAvatar.setCellValueFactory(cellData -> {
            String auctioneerAvatar = 
                cellData.getValue().getAuctioneer().getAvatar().isEmpty() 
                    ? null 
                    : cellData.getValue().getAuctioneer().getAvatar();
            
            if (auctioneerAvatar != null) {
                Image jfxImage = 
                    ImageToolkit.decodeBase64ToImage(auctioneerAvatar);
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
                            imageView.setFitWidth(30);
                            imageView.setFitHeight(30 * item.getHeight() / item.getWidth());
                            setGraphic(imageView);
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });
    }
    
    private void loadCompletedAuctions() {
        int limit = getLimitFilterValue(),
            offset = getOffsetFilterValue();
        String searchQuery = tfAuctionToSearch.getText().trim();

        new AuctionsRepository().getCompletedAuctionsList(
            searchQuery, limit, offset,
            new IProcessStatusListener<List<Auction>>() {
                @Override
                public void onSuccess(List<Auction> auctions) {
                    Platform.runLater(() -> {
                        completedAuctions = FXCollections.observableArrayList();
                        completedAuctions.addAll(auctions);
                        tvCompletedAuctions.setItems(completedAuctions);
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de conexión");
                        alert.setHeaderText(null);
                        alert.setContentText("Ocurrió un error al cargar las "
                            + "subastas compradas, por favor intente más tarde");
                        alert.showAndWait();
                    });
                }
            }
        );
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

    @FXML
    private void imgSearchAuctionClick(MouseEvent event) {
        loadCompletedAuctions();
    }

    @FXML
    private void imgCopyEmailClick(MouseEvent event) {
        
    }

    @FXML
    private void imgCopyPhoneNumberClick(ContextMenuEvent event) {
        
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) tfAuctionToSearch.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/MainMenuView.fxml"));
        baseStage.setTitle("Menu principal");
        baseStage.show();
    }
}
