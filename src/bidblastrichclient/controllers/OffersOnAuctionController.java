package bidblastrichclient.controllers;

import gRPC.Client;
import gRPC.VideoStreamListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
import lib.DateToolkit;
import lib.ImageToolkit;
import model.Auction;
import model.HypermediaFile;
import model.Offer;
import repositories.AuctionsRepository;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;

public class OffersOnAuctionController implements Initializable, VideoStreamListener {

    @FXML
    private MediaView mvVideoPlayer;
    @FXML
    private TableView<Offer> tvOffersMade;
    @FXML
    private TableColumn<Offer, Image> colAvatar;
    @FXML
    private TableColumn<Offer, String> colPurchaserName;
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
    private Client client;
    private int idAuction;
    @FXML
    private ImageView imgMainHypermediaFile;
    List<HypermediaFile> hypermediaFiles;

   @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAuction();
    }
    
    public void setIdAuction(int idAuction){
        this.idAuction = idAuction;
    }
    
    private void loadAuction(){
        idAuction = 7;
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
    
    private void loadImagesOnCarrusel(){
        List<Map.Entry<Image, Integer>> images = new ArrayList<>();
        
        for (HypermediaFile file: hypermediaFiles) {
            String content = file.getContent();
            if (!content.isEmpty()) {
                Image image = ImageToolkit.decodeBase64ToImage(content);
                images.add(new AbstractMap.SimpleEntry<>(image, file.getId()));
            } else {
                File imageFile = new File("src/bidblastrichclient/resources/Video.JPG");
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
    }

    @FXML
    private void btnBlockPurchaserClick(ActionEvent event) {
    }

    @FXML
    private void btnLoadOffersClick(ActionEvent event) {
    }
}