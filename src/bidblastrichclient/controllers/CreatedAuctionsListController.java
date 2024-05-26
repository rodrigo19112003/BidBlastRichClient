package bidblastrichclient.controllers;

import java.net.URL;
import java.util.Date;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import lib.DateToolkit;
import lib.ImageToolkit;
import lib.Navigation;
import lib.ValidationToolkit;
import model.Auction;
import model.HypermediaFile;
import model.Offer;
import model.User;
import repositories.AuctionsRepository;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;

public class CreatedAuctionsListController implements Initializable {

    @FXML
    private TextField tfAuctionToSearch;
    @FXML
    private TableView<Auction> tvProposedAuctions;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnProposed;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnProposed;
    @FXML
    private TableColumn<Auction, String> colProposedTime;
    @FXML
    private TableColumn<Auction, String> colBasePrice;
    @FXML
    private TableColumn<Auction, String> colMinimumBid;
    @FXML
    private TableView<Auction> tvPublishedAuctions;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnPublished;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnPublished;
    @FXML
    private TableColumn<Auction, String> colTimeLeft;
    @FXML
    private TableColumn<Auction, String> colLastOffer;
    @FXML
    private TableView<Auction> tvSoldAuctions;
    @FXML
    private TableColumn<Auction, Image> colCustomerAvatar;
    @FXML
    private TableColumn<Auction, String> colCustomerFullName;
    @FXML
    private TableColumn<Auction, String> colEmail;
    @FXML
    private TableColumn<Auction, String> colPhoneNumber;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnSold;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnSold;
    @FXML
    private TableColumn<Auction, String> colSaleDate;
    @FXML
    private TableColumn<Auction, String> colPrice;
    @FXML
    private TableView<Auction> tvClosedAuctions;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnClosed;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnClosed;
    @FXML
    private TableColumn<Auction, String> colDeadline;
    @FXML
    private TableColumn<Auction, String> colStateMessage;
    @FXML
    private TableView<Auction> tvRejectedAuctions;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnRejected;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnRejected;
    @FXML
    private TableColumn<Auction, String> colRejectionDate;
    @FXML
    private TableColumn<Auction, String> colComments;
    @FXML
    private TextField tfLimit;
    @FXML
    private TextField tfOffset;
    private ObservableList<Auction> createdAuctions;
    private ObservableList<Auction> proposedAuctions;
    private ObservableList<Auction> publishedAuctions;
    private ObservableList<Auction> rejectedAuctions;
    private ObservableList<Auction> closedAuctions;
    private ObservableList<Auction> soldAuctions;
    private static final String STATE_PROPOSED = "PROPUESTA";
    private static final String STATE_PUBLISHED = "PUBLICADA";
    private static final String STATE_REJECTED= "RECHAZADA";
    private static final String STATE_CLOSED = "CERRADA";
    private static final String STATE_CONCRETE = "CONCRETADA";
    private static final String STATE_FINISHED = "FINALIZADA";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureProposedAuctionsTable();
        configurePublishedAuctionsTable();
        configureSoldAuctionsTable();
        configureClosedAuctionsTable();
        configureRejectedAuctionsTable();
        loadCreatedAuctions();
    }
    
    private void configureProposedAuctionsTable() {
        colAuctionTitleOnProposed.setCellValueFactory(new PropertyValueFactory("title"));
        colProposedTime.setCellValueFactory(cellData -> {
            int proposedTime = cellData.getValue().getDaysAvailable();
            
            return new SimpleStringProperty(
                "Propuesta para " + proposedTime + " días"
            );
        });
        colBasePrice.setCellValueFactory(cellData -> {
            float basePrice = cellData.getValue().getBasePrice();
            
            return new SimpleStringProperty(
                    "$" + basePrice
            );
        });
        colMinimumBid.setCellValueFactory(cellData -> {
            float minimumBid = cellData.getValue().getMinimumBid();
            
            return new SimpleStringProperty(
                "$" + minimumBid
            );
        });
        configureAuctionImageOnProposedColumn();
    }
    
    private void configureAuctionImageOnProposedColumn() {
        colAuctionImageOnProposed.setCellValueFactory(cellData -> {
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

        colAuctionImageOnProposed.setCellFactory(new Callback<TableColumn<Auction, Image>, TableCell<Auction, Image>>() {
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
    
    private void configurePublishedAuctionsTable() {
        colAuctionTitleOnPublished.setCellValueFactory(new PropertyValueFactory("title"));
        colProposedTime.setCellValueFactory(cellData -> {
            int proposedTime = cellData.getValue().getDaysAvailable();
            
            return new SimpleStringProperty(
                "Propuesta para " + proposedTime + " días"
            );
        });
        colTimeLeft.setCellValueFactory(cellData -> {
            Date timeLeft = cellData.getValue().getClosesAt();
            
            return new SimpleStringProperty(
                DateToolkit.parseToFullDateWithHour(timeLeft)
            );
        });
        colLastOffer.setCellValueFactory(cellData -> {
            Offer lastOffer = 
                    cellData.getValue().getLastOffer() == null
                    ? null
                    : cellData.getValue().getLastOffer();
            
            float amountLastOffer;
            if (lastOffer != null) {
                amountLastOffer = lastOffer.getAmount();
            } else {
                amountLastOffer = -1;
            }
            
            return new SimpleStringProperty(
                amountLastOffer != -1 ? "$" + amountLastOffer : "No hay ofertas aún"
            );
        });
        configureAuctionImageOnPublishedColumn();
    }
    
    private void configureAuctionImageOnPublishedColumn() {
        colAuctionImageOnPublished.setCellValueFactory(cellData -> {
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

        colAuctionImageOnPublished.setCellFactory(new Callback<TableColumn<Auction, Image>, TableCell<Auction, Image>>() {
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
    
    private void configureSoldAuctionsTable() {
        colAuctionTitleOnSold.setCellValueFactory(new PropertyValueFactory("title"));
        colSaleDate.setCellValueFactory(cellData -> {
            Date saleSate = cellData.getValue().getUpdatedDate();
            
            return new SimpleStringProperty(
                DateToolkit.parseToFullDateWithHour(saleSate)
            );
        });
        colCustomerFullName.setCellValueFactory(cellData -> {
            User customer = cellData.getValue().getLastOffer().getCustomer();
            
            return new SimpleStringProperty(
                customer != null ? customer.getFullName() : "NA"
            );
        });
        colEmail.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getLastOffer().getCustomer().getEmail();
            
            return new SimpleStringProperty(
                email != null ? email : "NA"
            );
        });
        colPhoneNumber.setCellValueFactory(cellData -> {
            String phoneNumber = cellData.getValue().getLastOffer().getCustomer().getPhoneNumber();
            
            return new SimpleStringProperty(
                phoneNumber != null ? phoneNumber : "NA"
            );
        });
        colPrice.setCellValueFactory(cellData -> {
            float price = cellData.getValue().getLastOffer().getAmount();
            
            return new SimpleStringProperty(
                    "$" + price
            );
        });
        configureCustomerAvatarColumn();
        configureAuctionImageOnSoldColumn();
    }
    
    private void configureAuctionImageOnSoldColumn() {
        colAuctionImageOnSold.setCellValueFactory(cellData -> {
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

        colAuctionImageOnSold.setCellFactory(new Callback<TableColumn<Auction, Image>, TableCell<Auction, Image>>() {
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
    
    private void configureCustomerAvatarColumn() {
        colCustomerAvatar.setCellValueFactory(cellData -> {
            String customerAvatar = 
                cellData.getValue().getLastOffer().getCustomer().getAvatar() == null 
                    ? null 
                    : cellData.getValue().getLastOffer().getCustomer().getAvatar();
            
            if (customerAvatar != null) {
                Image jfxImage = 
                    ImageToolkit.decodeBase64ToImage(customerAvatar);
                return new javafx.beans.property.SimpleObjectProperty<>(jfxImage);
            }
            return new javafx.beans.property.SimpleObjectProperty<>(null);
        });

        colCustomerAvatar.setCellFactory(new Callback<TableColumn<Auction, Image>, TableCell<Auction, Image>>() {
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
    
    private void configureRejectedAuctionsTable() {
        colAuctionTitleOnRejected.setCellValueFactory(new PropertyValueFactory("title"));
        colRejectionDate.setCellValueFactory(cellData -> {
            Date rejectionDate = cellData.getValue().getUpdatedDate();
            
            return new SimpleStringProperty(
                DateToolkit.parseToFullDateWithHour(rejectionDate)
            );
        });
        colComments.setCellValueFactory(cellData -> {
            String comments = cellData.getValue().getReview().getComments();
            
            return new SimpleStringProperty(
                comments
            );
        });
        configureAuctionImageOnRejectedColumn();
    }
    
    private void configureAuctionImageOnRejectedColumn() {
        colAuctionImageOnRejected.setCellValueFactory(cellData -> {
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

        colAuctionImageOnRejected.setCellFactory(new Callback<TableColumn<Auction, Image>, TableCell<Auction, Image>>() {
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
    
    private void configureClosedAuctionsTable() {
        colAuctionTitleOnClosed.setCellValueFactory(new PropertyValueFactory("title"));
        colDeadline.setCellValueFactory(cellData -> {
            Date deadline = cellData.getValue().getUpdatedDate();
            
            return new SimpleStringProperty(
                DateToolkit.parseToFullDateWithHour(deadline)
            );
        });
        colStateMessage.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(
                "No hubo ofertas realizadas"
            );
        });
        configureAuctionImageOnClosedColumn();
    }
    
    private void configureAuctionImageOnClosedColumn() {
        colAuctionImageOnClosed.setCellValueFactory(cellData -> {
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

        colAuctionImageOnClosed.setCellFactory(new Callback<TableColumn<Auction, Image>, TableCell<Auction, Image>>() {
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

    private void loadCreatedAuctions() {
        int limit = getLimitFilterValue(),
            offset = getOffsetFilterValue();
        String searchQuery = tfAuctionToSearch.getText().trim();

        new AuctionsRepository().getCreatedAuctionsList(
            searchQuery, limit, offset,
            new IProcessStatusListener<List<Auction>>() {
                @Override
                public void onSuccess(List<Auction> auctions) {
                    Platform.runLater(() -> {
                        createdAuctions = FXCollections.observableArrayList();
                        proposedAuctions = FXCollections.observableArrayList();
                        publishedAuctions = FXCollections.observableArrayList();
                        rejectedAuctions = FXCollections.observableArrayList();
                        closedAuctions = FXCollections.observableArrayList();
                        soldAuctions = FXCollections.observableArrayList();
                        createdAuctions.addAll(auctions);
                        for (Auction auction: createdAuctions) {
                            if (auction.getAuctionState().equals(STATE_CONCRETE) ||
                                    auction.getAuctionState().equals(STATE_FINISHED)) {
                                soldAuctions.add(auction);
                            }
                            if (auction.getAuctionState().equals(STATE_PROPOSED)) {
                                proposedAuctions.add(auction);
                            }
                            if (auction.getAuctionState().equals(STATE_PUBLISHED)) {
                                publishedAuctions.add(auction);
                            }
                            if (auction.getAuctionState().equals(STATE_REJECTED)) {
                                rejectedAuctions.add(auction);
                            }
                            if (auction.getAuctionState().equals(STATE_CLOSED)) {
                                closedAuctions.add(auction);
                            }
                        }
                        
                        tvProposedAuctions.setItems(proposedAuctions);
                        tvPublishedAuctions.setItems(publishedAuctions);
                        tvRejectedAuctions.setItems(rejectedAuctions);
                        tvClosedAuctions.setItems(closedAuctions);
                        tvSoldAuctions.setItems(soldAuctions);
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
    private void btnConsultSalesStatisticsClick(ActionEvent event) {
        Stage baseStage = (Stage) tfAuctionToSearch.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/SalesStatisticsView.fxml"));
        baseStage.setTitle("Estadísticas de Ventas");
        baseStage.show();
    }

    @FXML
    private void imgSearchAuctionClick(MouseEvent event) {
        boolean validFilters = validateFiltersValues();
        
        if(!validFilters) {
            showInvalidFiltersValuesError();
        } else {
            loadCreatedAuctions();
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
    private void imgCopyEmailClick(MouseEvent event) {
        Auction auction = tvSoldAuctions.getSelectionModel().getSelectedItem();
        if(auction != null){
            String email = auction.getLastOffer().getCustomer().getEmail();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(email);
            clipboard.setContent(content);
            
             Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Correo electrónico copiado");
            alert.setHeaderText(null);
            alert.setContentText("El correo electrónico: " + email + " se ha "
                    + "copiado al portapapeles");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Seleccione una subasta");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione una subasta de la lista para copiar"
                    + " el correo electrónico del subastador");
            alert.showAndWait();
        }
    }

    @FXML
    private void imgCopyPhoneNumberClick(MouseEvent event) {
         Auction auction = tvSoldAuctions.getSelectionModel().getSelectedItem();
        if(auction != null){
            if (auction.getLastOffer().getCustomer().getPhoneNumber() != null) {
                String phoneNumber = auction.getLastOffer().getCustomer().getPhoneNumber();
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(phoneNumber);
                clipboard.setContent(content);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Número de teléfono copiado copiado");
                alert.setHeaderText(null);
                alert.setContentText("El número de teléfono: " + phoneNumber + " se "
                        + "ha copiado al portapapeles");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No tiene un número de teléfono");
                alert.setHeaderText(null);
                alert.setContentText("El subastador no tiene un número de teléfono"
                        + " registrado");
                alert.showAndWait();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Seleccione una subasta");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione una subasta de la lista para copiar"
                    + " el número de teléfono del subastador");
            alert.showAndWait();
        }
    }

    @FXML
    private void btnSeeOffersMadeClick(ActionEvent event) {
        //TODO
    }
}