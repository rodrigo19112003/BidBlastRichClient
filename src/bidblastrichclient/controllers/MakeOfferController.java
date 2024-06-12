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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import lib.CurrencyToolkit;
import lib.DateToolkit;
import lib.ImageToolkit;
import lib.Navigation;
import lib.ValidationToolkit;
import model.Auction;
import model.HypermediaFile;
import repositories.AuctionsRepository;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;

public class MakeOfferController implements Initializable, VideoStreamListener {

    @FXML
    private HBox hbImageCarrusel;
    @FXML
    private Label lblTimeLeft;
    @FXML
    private Label lblAuctionTitle;
    @FXML
    private ImageView imgMainHypermediaFile;
    @FXML
    private MediaView mvVideoPlayer;
    @FXML
    private Label lblPriceTitle;
    @FXML
    private Label lblAuctionPrice;
    @FXML
    private Label lblMinimumBidText;
    @FXML
    private Label lblMinimumBidValue;
    @FXML
    private TextField tfOffer;
    @FXML
    private Label lblOfferError;
    @FXML
    private Label lblAuctionDescription;
    
    private int idAuction;
    private Auction auction;
    private List<HypermediaFile> hypermediaFiles;
    private final List<byte[]> videoFragments = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private int currentFragmentIndex = 0;
    private Client gRPCClient;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void setIdAuction(int idAuction) {
        this.idAuction = idAuction;
        loadAuction();
    }
    
    private void loadAuction(){
        new AuctionsRepository().getAuctionById(
            idAuction,
            new IProcessStatusListener<Auction>() {
                @Override
                public void onSuccess(Auction auction) {
                    Platform.runLater(() -> {
                        showAuctionInformation(auction);
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
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
    
    private void showAuctionInformation(Auction auction) {
        this.auction = auction;
        this.hypermediaFiles = auction.getMediaFiles();
        
        lblAuctionTitle.setText(auction.getTitle());
        lblTimeLeft.setText("Se cierra el " + DateToolkit.parseToFullDateWithHour(auction.getClosesAt()));
        
        String auctionPrice = CurrencyToolkit.parseToMXN(auction.getBasePrice());
        if(auction.getLastOffer() != null) {
            lblPriceTitle.setText("Última oferta");
            auctionPrice = CurrencyToolkit.parseToMXN(auction.getLastOffer().getAmount());
        }
        lblAuctionPrice.setText(auctionPrice);
        
        lblAuctionDescription.setText(auction.getDescription());
        
        if(auction.getMinimumBid() > 0) {
            lblMinimumBidText.setText("El subastador estableció una puja mínima de");
            lblMinimumBidValue.setText(CurrencyToolkit.parseToMXN(auction.getMinimumBid()));
            lblMinimumBidValue.setVisible(true);
        }
        
        showImagesOnCarrusel();
    }
    
    private void showImagesOnCarrusel(){
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

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) hbImageCarrusel.getScene().getWindow();
        baseStage.setScene(Navigation.startScene("views/SearchAuctionView.fxml"));
        baseStage.setTitle("Buscar subasta");
        baseStage.show();
    }

    @FXML
    private void btnMakeOfferClick(ActionEvent event) {
        boolean isValidOffer = validateOfferAmount();
        
        cleanOfferErrorMessage();
        if(!isValidOffer) {
            showInvalidOfferErrorMessage();
        } else {
            makeOffer();
        }
    }
    
    private void cleanOfferErrorMessage() {
        tfOffer.setStyle("-fx-border-color: #000000; -fx-border-radius: 5;");
        lblOfferError.setVisible(false);
    }
    
    private void showInvalidOfferErrorMessage() {
        String rawOffer = tfOffer.getText().trim();
        float previousOffer = auction.getLastOffer() != null
            ? auction.getLastOffer().getAmount()
            : auction.getBasePrice();
        float minimumBid = auction.getMinimumBid();
        
        String errorMessage = "";
        if(!ValidationToolkit.isPositiveFloat(rawOffer)) {
            errorMessage = "Esta oferta no tiene un formato válido";
        }
        
        if(errorMessage.isEmpty() && Float.parseFloat(rawOffer) <= previousOffer) {
            errorMessage = "Esta oferta no supera la mejor oferta previa";
        }
        
        if(errorMessage.isEmpty() && Float.parseFloat(rawOffer) - previousOffer < minimumBid) {
            errorMessage = "Debe superar la puja mínima establecida";
        }
        
        if(!errorMessage.isEmpty()) {
            tfOffer.setStyle("-fx-border-color: #ff1700; -fx-border-radius: 5;");
            lblOfferError.setVisible(true);
            lblOfferError.setText(errorMessage);
        }
    }
    
    private void makeOffer() {
        
    }
    
    private boolean validateOfferAmount() {
        String rawOffer = tfOffer.getText().trim();
        float previousOffer = auction.getLastOffer() != null
            ? auction.getLastOffer().getAmount()
            : auction.getBasePrice();
        float minimumBid = auction.getMinimumBid();
        
        return ValidationToolkit.isPositiveFloat(rawOffer)
            && Float.parseFloat(rawOffer) > previousOffer
            && Float.parseFloat(rawOffer) - previousOffer >= minimumBid;
    }
}
