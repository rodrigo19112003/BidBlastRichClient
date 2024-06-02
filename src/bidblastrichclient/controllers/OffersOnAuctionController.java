package bidblastrichclient.controllers;

import gRPC.Client;
import gRPC.VideoStreamListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import model.Offer;

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
    
    private MediaPlayer mediaPlayer;
    private final List<byte[]> videoFragments = new ArrayList<>();;
    private int currentFragmentIndex = 0;
    private Client client;

   @Override
    public void initialize(URL url, ResourceBundle rb) {
        client = new Client(this);
        int videoId = 2;
        client.streamVideo(videoId);
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
            System.out.println("Transmisión de video completada");
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
}