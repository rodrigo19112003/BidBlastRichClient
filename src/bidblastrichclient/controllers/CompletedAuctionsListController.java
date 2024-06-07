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
    private TableColumn<Auction, String> colAuctionTitle;
    @FXML
    private TableColumn<Auction, Image> colAuctioneerAvatar;
    @FXML
    private TableColumn<Auction, String> colAuctioneerFullName;
    @FXML
    private TableColumn<Auction, String> colEmail;
    @FXML
    private TableColumn<Auction, String> colPhoneNumber;
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
        colEmail.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getAuctioneer().getEmail();
            
            return new SimpleStringProperty(
                email != null ? email : "NA"
            );
        });
        colPhoneNumber.setCellValueFactory(cellData -> {
            String phoneNumber = cellData.getValue().getAuctioneer().getPhoneNumber();
            
            return new SimpleStringProperty(
                phoneNumber != null && !phoneNumber.isEmpty() ? phoneNumber : "NA"
            );
        });
        colPrice.setCellValueFactory(cellData -> {
            float price = cellData.getValue().getLastOffer().getAmount();
            
            return new SimpleStringProperty(
                    "$" + price
            );
        });
        configureAuctioneerAvatarColumn();
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
    
    private void configureAuctioneerAvatarColumn() {
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

        colAuctioneerAvatar.setCellFactory(new Callback<TableColumn<Auction, Image>, TableCell<Auction, Image>>() {
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
        boolean validFilters = validateFiltersValues();
        
        if(!validFilters) {
            showInvalidFiltersValuesError();
        } else {
            loadCompletedAuctions();
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
        Auction auction = tvCompletedAuctions.getSelectionModel().getSelectedItem();
        if(auction != null){
            String email = auction.getAuctioneer().getEmail();
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
        Auction auction = tvCompletedAuctions.getSelectionModel().getSelectedItem();
        if(auction != null){
            if (auction.getAuctioneer().getPhoneNumber() != null &&
                    !auction.getAuctioneer().getPhoneNumber().isEmpty()) {
                String phoneNumber = auction.getAuctioneer().getPhoneNumber();
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
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) tfAuctionToSearch.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/MainMenuView.fxml"));
        baseStage.setTitle("Menu principal");
        baseStage.show();
    }
}
