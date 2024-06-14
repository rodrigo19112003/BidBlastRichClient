package bidblastrichclient.controllers;

import grpc.Client;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
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
import repositories.IEmptyProcessStatusListener;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;
import grpc.IVideoStreamListener;

public class OffersOnAuctionController implements Initializable, IVideoStreamListener {

    @FXML
    private MediaView mvVideoPlayer;
    @FXML
    private TableView<Offer> tvOffersMade;
    @FXML
    private TableColumn<Offer, Image> colAvatar;
    @FXML
    private TableColumn<Offer, String> colPurchaserName;
    @FXML
    private TableColumn<Offer, String> colCreationDate;
    @FXML
    private TableColumn<Offer, String> colOffer;
    @FXML
    private Label lblAuctionTitle;
    @FXML
    private Label lblTimeLeft;
    @FXML
    private HBox hbImageCarrusel;
    @FXML
    private TextField tfOffset;
    @FXML
    private TextField tfLimit;
    
    private MediaPlayer mediaPlayer;
    private final List<byte[]> videoFragments = new ArrayList<>();;
    private int currentFragmentIndex = 0;
    private Client gRPCClient;
    private int idAuction;
    @FXML
    private ImageView imgMainHypermediaFile;
    private List<HypermediaFile> hypermediaFiles;
    private ObservableList<Offer> offersList;

   @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureOffersTable();
    }
    
    public void setIdAuction(int idAuction){
        this.idAuction = idAuction;
        loadAuction();
        loadOffers();
    }
    
    private void configureOffersTable() {
        colPurchaserName.setCellValueFactory(cellData -> {
            User customer = cellData.getValue().getCustomer();
            
            return new SimpleStringProperty(
                customer != null ? customer.getFullName() : "NA"
            );
        });
        colCreationDate.setCellValueFactory(cellData -> {
            Date purchaseDate = cellData.getValue().getCreationDate();
            
            return new SimpleStringProperty(
                DateToolkit.parseToFullDateWithHour(purchaseDate)
            );
        });
        colOffer.setCellValueFactory(cellData -> {
            float offer = cellData.getValue().getAmount();
            
            return new SimpleStringProperty(
                    "$" + offer
            );
        });
        configureCustomerAvatarColumn();
    }
    
    private void configureCustomerAvatarColumn() {
        colAvatar.setCellValueFactory(cellData -> {
            String customerAvatar = 
                cellData.getValue().getCustomer().getAvatar() == null 
                    ? null 
                    : cellData.getValue().getCustomer().getAvatar();
            
            if (customerAvatar != null) {
                Image jfxImage = 
                    ImageToolkit.decodeBase64ToImage(customerAvatar);
                return new javafx.beans.property.SimpleObjectProperty<>(jfxImage);
            }
            return new javafx.beans.property.SimpleObjectProperty<>(null);
        });

        colAvatar.setCellFactory(new Callback<TableColumn<Offer, Image>, TableCell<Offer, Image>>() {
            @Override
            public TableCell<Offer, Image> call(TableColumn<Offer, Image> param) {
                return new TableCell<Offer, Image>() {
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
    
    private void loadAuction(){
        new AuctionsRepository().getAuctionById(
            idAuction,
            new IProcessStatusListener<Auction>() {
                @Override
                public void onSuccess(Auction auction) {
                    Platform.runLater(() -> {
                        lblAuctionTitle.setFont(new Font("Sytem", 18));
                        lblAuctionTitle.setText(auction.getTitle());
                        lblTimeLeft.setText("Se cierra el " + DateToolkit.parseToFullDateWithHour(auction.getClosesAt()));
                        hypermediaFiles = auction.getMediaFiles();
                        loadImagesOnCarrusel();
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de conexión");
                        alert.setHeaderText(null);
                        alert.setContentText("Ocurrió un error al cargar la "
                            + "subasta, por favor intente más tarde");
                        alert.showAndWait();
                    });
                }
            }
        );
    }
    
    private void loadOffers() {
        int limit = getLimitFilterValue(),
            offset = getOffsetFilterValue();
        new AuctionsRepository().getUserAuctionOffersByAuctionId(
            idAuction, limit, offset,
            new IProcessStatusListener<List<Offer>>() {
                @Override
                public void onSuccess(List<Offer> offers) {
                    Platform.runLater(() -> {
                        offersList = FXCollections.observableArrayList();
                        offersList.addAll(offers);
                        tvOffersMade.setItems(offersList);
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de conexión");
                        alert.setHeaderText(null);
                        alert.setContentText("Ocurrió un error al cargar las "
                            + "ofertas, por favor intente más tarde");
                        alert.showAndWait();
                    });
                }
            }
        );
    }
    
    private void blockUser(int idProfile) {
        new AuctionsRepository().blockUserInAnAuctionAndDeleteHisOffers(
            idAuction, idProfile,
            new IEmptyProcessStatusListener() {
                @Override
                public void onSuccess() {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Usuario bloqueado con éxito");
                        alert.setHeaderText(null);
                        alert.setContentText("Se bloqueó al usuario de forma "
                                + "exitosa");
                        alert.showAndWait();
                        loadOffers();
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de conexión");
                        alert.setHeaderText(null);
                        alert.setContentText("Ocurrió un error al bloquear el usuario");
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
    
    private void loadImagesOnCarrusel(){
        List<Map.Entry<Image, Integer>> images = new ArrayList<>();
        
        for (HypermediaFile file: hypermediaFiles) {
            String content = file.getContent();
            if (!content.isEmpty()) {
                Image image = ImageToolkit.decodeBase64ToImage(content);
                images.add(new AbstractMap.SimpleEntry<>(image, file.getId()));
            } else {
                File imageFile = new File("src/bidblastrichclient/resources/Video.png");
                Image image = new Image(imageFile.toURI().toString());
                images.add(new AbstractMap.SimpleEntry<>(image, file.getId()));
            }
        }
        
        for (Map.Entry<Image, Integer> image : images) {
            ImageView thumbnail = new ImageView(image.getKey());
            thumbnail.setFitWidth(80);
            thumbnail.setFitHeight(60);
            thumbnail.setOnMouseClicked(event -> showFileInMainView(image));
            hbImageCarrusel.getChildren().add(thumbnail);
        }

        if (!images.isEmpty()) {
            showFileInMainView(images.get(0));
        }
    }
    
    private void showFileInMainView(Map.Entry<Image, Integer> image) {
        imgMainHypermediaFile.setVisible(false);
        mvVideoPlayer.setVisible(false);
        String content = "";
        
        if (Client.getChannelStatus() && gRPCClient != null) {
            videoFragments.clear();
            gRPCClient.shutdown();
            gRPCClient = null;
        }
        if (mediaPlayer != null && 
                mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        
        for (HypermediaFile file : hypermediaFiles) {
            if (file.getId() == image.getValue()) {
                content = file.getContent();
            }
        }
        
        if (!content.isEmpty()) {
            imgMainHypermediaFile.setVisible(true);
            imgMainHypermediaFile.setImage(image.getKey());
            imgMainHypermediaFile.setPreserveRatio(true);
        } else {
            mvVideoPlayer.setVisible(true);
            gRPCClient = new Client(this);
            gRPCClient.streamVideo(image.getValue());
        }
    }

    @Override
    public void onVideoChunkReceived(byte[] videoChunk) {
        videoFragments.add(videoChunk);
        if (currentFragmentIndex == 0) {
            playVideo();
        }
    }

    @Override
    public void onVideoFetchError(Throwable error) {
        Platform.runLater(() -> {
            System.out.println("Error al obtener el fragmento de video: " + error.getMessage());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error al cargar el video");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar el video, inténtalo de nuevo");
            alert.showAndWait();
        });
    }

    @Override
    public void onVideoFetchComplete() {
        Platform.runLater(() -> {
            System.out.println("Transmision de video completada");
        });
    }

    private void playVideo() {
        if (videoFragments.isEmpty()) {
            return;
        }

        byte[] videoChunk = videoFragments.get(currentFragmentIndex);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(videoChunk);

        String tempFilePath = createTempFileFromInputStream(inputStream);
        if (tempFilePath == null) {
            System.out.println("Error al crear el archivo multimedia temporal");
            return;
        }

        Media media = new Media(new File(tempFilePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mvVideoPlayer.setMediaPlayer(mediaPlayer);
        mediaPlayer.setOnEndOfMedia(this::playNextFragment);
        mediaPlayer.play();
    }

    private String createTempFileFromInputStream(InputStream inputStream) {
        try {
            File tempFile = File.createTempFile("tempVideo", ".avi");
            tempFile.deleteOnExit();
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            inputStream.close();
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playNextFragment() {
        currentFragmentIndex++;
        if (currentFragmentIndex < videoFragments.size()) {
            playVideo();
        } else {
            mediaPlayer.stop();
            currentFragmentIndex = 0;
            playVideo();
        }
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        if (Client.getChannelStatus() && gRPCClient != null) {
            videoFragments.clear();
            gRPCClient.shutdown();
            gRPCClient = null;
        }
        if (mediaPlayer != null && 
                mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        
        Stage baseStage = (Stage) tfLimit.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/CreatedAuctionsListView.fxml"));
        baseStage.setTitle("Ofertas sobre subasta");
        baseStage.show();
    }

    @FXML
    private void btnBlockPurchaserClick(ActionEvent event) {
        Offer offer = tvOffersMade.getSelectionModel().getSelectedItem();
        if (offer != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de bloqueo de usuario");
            alert.setHeaderText(null);
            alert.setContentText("¿Estás seguro de que deseas bloquear al usuario?, se "
                    + "eliminarán sus ofertas y ya no podrá ofertar en esta subasta.");
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                blockUser(offer.getCustomer().getId());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Seleccione una oferta");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione una oferta de la lista para bloquear"
                    + " al usuario que la realizó");
            alert.showAndWait();
        }
    }

    @FXML
    private void btnLoadOffersClick(ActionEvent event) {
        boolean validFilters = validateFiltersValues();
        
        if(!validFilters) {
            showInvalidFiltersValuesError();
        } else {
            loadOffers();
        }
    }
}