package bidblastrichclient.controllers;

import gRPC.Client;
import gRPC.VideoStreamListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import lib.ImageToolkit;
import lib.Navigation;
import model.Auction;
import model.AuctionCategory;
import model.HypermediaFile;
import repositories.AuctionCategoriesRepository;
import repositories.AuctionsRepository;
import repositories.IEmptyProcessStatusListener;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;

public class EvaluateItemViewController implements Initializable, VideoStreamListener {

    @FXML
    private ImageView imgReturnToPreviousPage;
    @FXML
    private HBox hbImageCarrusel;
    @FXML
    private MediaView mvVideoPlayer;
    @FXML
    private Label lblAuctionTitle;
    @FXML
    private Label lblAuctionBasePrice;
    @FXML
    private Label lblAuctionDaysAvailable;
    @FXML
    private Label lblAuctionState;
    @FXML
    private Label lblMiniumBid;
    @FXML
    private Label lblAuctionDescription;
    @FXML
    private ComboBox<Auction> cbxAuctions;
    @FXML
    private Label lblPriceSign;
    @FXML
    private ImageView imgMainHypermediaFile;
    @FXML
    private Label lblBasePriceIcon;
    @FXML
    private ComboBox<AuctionCategory> cbxAuctionCategory;
    @FXML
    private Button btnApprove;
    @FXML
    private Button btnDeny;
    @FXML
    private Label lblComents;
    @FXML
    private TextArea tfComents;
    @FXML
    private Button btnSave;
    @FXML
    private Label lblBasePrices;
    @FXML
    private Label lblPesosIcon;
    @FXML
    private Label lblOpenningDays;
    
    private MediaPlayer mediaPlayer;
    private final List<byte[]> videoFragments = new ArrayList<>();
    private int currentFragmentIndex = 0;
    private Client client;
    private List<HypermediaFile> hypermediaFiles;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblPriceSign.setVisible(false);
        lblBasePriceIcon.setVisible(false);
        btnSave.setVisible(false);
        lblComents.setVisible(false);
        tfComents.setVisible(false);
        loadAuctions();
        loadAuctionCategories();
        cbxAuctions.setOnAction(event -> {
            Auction selectedAuction = cbxAuctions.getSelectionModel().getSelectedItem();
            if (selectedAuction != null) {
                recoverAuctionDetails(selectedAuction);
                lblPriceSign.setVisible(true);
                lblBasePriceIcon.setVisible(true);
            }
        });
        tfComents.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() <= 500) {
                return change;
            } else {
                return null;
            }  
        }));
    }        
     private void loadImagesOnCarrusel(){
        hbImageCarrusel.getChildren().clear();
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
        
        for (HypermediaFile file : hypermediaFiles) {
            if (file.getId() == image.getValue()) {
                content = file.getContent();
            }
        }
        
        if (!content.isEmpty()) {
            if (Client.getChannelStatus() && client != null) {
                videoFragments.clear();
                client.shutdown();
                client = null;
            }
            if (mediaPlayer != null && 
                    mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                mediaPlayer.stop();
                mediaPlayer = null;
            }
            imgMainHypermediaFile.setVisible(true);
            imgMainHypermediaFile.setImage(image.getKey());
            imgMainHypermediaFile.setPreserveRatio(true);
        } else {
            mvVideoPlayer.setVisible(true);
            client = new Client(this);
            client.streamVideo(image.getValue());
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

    private void loadAuctions() {
    new AuctionsRepository().getPublishedAuctions(new IProcessStatusListener<List<Auction>>() {
        @Override
            public void onSuccess(List<Auction> auctions) {
                Platform.runLater(() -> {
                    if (auctions != null && !auctions.isEmpty()) {
                        System.out.println("Subastas recuperadas: " + auctions.size());
                    } else {
                        System.out.println("No se recuperaron subastas.");
                    }
                    cbxAuctions.setItems(FXCollections.observableArrayList(auctions));
                });
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                Platform.runLater(() -> {
                    System.err.println("Error al cargar las subastas: " + errorCode);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error al cargar subastas");
                    alert.setHeaderText(null);
                    alert.setContentText("No se pudieron cargar las subastas publicadas. Intente de nuevo más tarde.");
                    alert.showAndWait();
                });
            }
        });
    }
               
    private void recoverAuctionDetails(Auction auction) {
        lblAuctionTitle.setText(auction.getTitle());
        lblAuctionBasePrice.setText(String.valueOf(auction.getBasePrice()));
        lblMiniumBid.setText(String.valueOf(auction.getMinimumBid()));
        lblAuctionDescription.setText(auction.getDescription());
        lblAuctionDaysAvailable.setText(String.valueOf(auction.getDaysAvailable()));
        lblAuctionState.setText(auction.getItemCondition());
        hypermediaFiles = auction.getMediaFiles();
        loadImagesOnCarrusel();
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        if (Client.getChannelStatus() && client != null) {
            videoFragments.clear();
            client.shutdown();
            client = null;
        }
        if (mediaPlayer != null && mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/ModeratorMenuView.fxml"));
        baseStage.setTitle("Menu principal");
        baseStage.show();
    }
    private void loadAuctionCategories() {
        new AuctionCategoriesRepository().getAuctionCategories(new IProcessStatusListener<List<AuctionCategory>>() {
            @Override
            public void onSuccess(List<AuctionCategory> categories) {
                Platform.runLater(() -> {
                    cbxAuctionCategory.setItems(FXCollections.observableArrayList(categories));
                });
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error al cargar categorías");
                    alert.setHeaderText(null);
                    alert.setContentText("No se pudieron cargar las categorías de subastas. Intente de nuevo más tarde.");
                    alert.showAndWait();
                });
            }
        });
    }

    @FXML
    private void btnChooseCategoryClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bidblastrichclient/views/AuctionCategoryFormView.fxml"));
            Parent root = loader.load();
            AuctionCategoryFormController controller = loader.getController();

            controller.setAuctionCategoryInformation(null, false);
            Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();
            baseStage.setScene(new Scene(root));
            baseStage.setTitle("Registrar categoría de subastas");
            baseStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void btnAproveClick(ActionEvent event) {
        Auction selectedAuction = cbxAuctions.getSelectionModel().getSelectedItem();
        AuctionCategory selectedCategory = cbxAuctionCategory.getSelectionModel().getSelectedItem();

        if (selectedAuction != null && selectedCategory != null) {
            System.out.println("Aprobando subasta con ID: " + selectedAuction.getId() + " y categoría ID: " + selectedCategory.getId());

            new AuctionsRepository().approveAuction(
                selectedAuction.getId(),
                selectedCategory.getId(),
                new IEmptyProcessStatusListener() {
                    @Override
                    public void onSuccess() {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Subasta aprobada");
                            alert.setHeaderText(null);
                            alert.setContentText("La subasta ha sido aprobada con éxito.");
                            alert.showAndWait().ifPresent(response -> {
                                navigateToMainMenu();
                            });
                            System.out.println("Subasta aprobada con éxito.");
                        });
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorCode) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error al aprobar subasta");
                            alert.setHeaderText(null);
                            alert.setContentText("No se pudo aprobar la subasta. Intente de nuevo más tarde.");
                            alert.showAndWait();
                            System.err.println("Error al aprobar la subasta: " + errorCode);
                        });
                    }
                }
            );
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("Por favor seleccione una subasta y una categoría para aprobar.");
                alert.showAndWait();
                System.err.println("No se seleccionó una subasta o una categoría.");
            });
        }
    }

    private void navigateToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bidblastrichclient/views/ModeratorMenuView.fxml"));
            Parent root = loader.load();
            Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();
            baseStage.setScene(new Scene(root));
            baseStage.setTitle("Menu principal");
            baseStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void btnDenyClick(ActionEvent event) {
        Auction selectedAuction = cbxAuctions.getSelectionModel().getSelectedItem();

        if (selectedAuction != null) {
            lblAuctionTitle.setVisible(false);
            lblAuctionBasePrice.setVisible(false);
            lblAuctionDaysAvailable.setVisible(false);
            lblAuctionState.setVisible(false);
            lblMiniumBid.setVisible(false);
            lblAuctionDescription.setVisible(false);
            cbxAuctions.setVisible(false);
            lblPriceSign.setVisible(false);
            lblBasePriceIcon.setVisible(false);
            btnApprove.setVisible(false);
            btnDeny.setVisible(false);
            lblBasePrices.setVisible(false);
            lblOpenningDays.setVisible(false);
            lblPesosIcon.setVisible(false);
            
            lblComents.setVisible(true);
            tfComents.setVisible(true);
            btnSave.setVisible(true);
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("Por favor seleccione una subasta para denegar.");
                alert.showAndWait();
            });
        }
    }   
    @FXML
    private void btnSaveCommentsClick(ActionEvent event) {
        Auction selectedAuction = cbxAuctions.getSelectionModel().getSelectedItem();
        String comments = tfComents.getText();

        if (selectedAuction != null && comments != null && !comments.isEmpty()) {
            new AuctionsRepository().rejectAuction(
                selectedAuction.getId(),
                comments,
                new IEmptyProcessStatusListener() {
                    @Override
                    public void onSuccess() {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Subasta rechazada");
                            alert.setHeaderText(null);
                            alert.setContentText("La subasta ha sido rechazada con éxito.");
                            alert.showAndWait();
                            Stage baseStage = (Stage) tfComents.getScene().getWindow();
                            baseStage.setScene(Navigation.startScene("views/ModeratorMenuView.fxml"));
                            baseStage.setTitle("Menu principal");
                            baseStage.show();
                        });
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorCode) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error al rechazar subasta");
                            alert.setHeaderText(null);
                            alert.setContentText("No se pudo rechazar la subasta. Intente de nuevo más tarde.");
                            alert.showAndWait();
                        });
                    }
                }
            );
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("Por favor seleccione una subasta y ingrese comentarios.");
                alert.showAndWait();
            });
        }
    }
}