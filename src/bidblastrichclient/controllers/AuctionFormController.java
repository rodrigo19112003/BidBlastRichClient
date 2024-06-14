package bidblastrichclient.controllers;

import api.requests.auctions.AuctionCreateBody;
import gRPC.Client;
import gRPC.GrpcClientCallback;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lib.Navigation;
import model.AuctionState;
import model.HypermediaFile;
import repositories.AuctionsRepository;
import repositories.IEmptyProcessStatusListener;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;

public class AuctionFormController implements Initializable {

    @FXML
    private TextField lblAuctionTitle;
    @FXML
    private TextField tfAuctionDescription;
    @FXML
    private TextField tfAuctionBasePrice;
    @FXML
    private TextField tfAuctionMiniumBid;
    @FXML
    private TextField tfAuctionDaysAvailable;
    @FXML
    private VBox vboxUploadedFiles;
    @FXML
    private Label lblUploadMedia;
    @FXML
    private ComboBox<AuctionState> cbAuctionState;

    private List<HypermediaFile> uploadedMediaFiles = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAuctionStates();
        tfAuctionBasePrice.addEventFilter(KeyEvent.KEY_TYPED, this::validateNumericInput);
        tfAuctionMiniumBid.addEventFilter(KeyEvent.KEY_TYPED, this::validateNumericInput);
        tfAuctionDaysAvailable.addEventFilter(KeyEvent.KEY_TYPED, this::validateNumericInput);
        setFieldLimits();
    }

    private void setFieldLimits() {
        addTextLimiter(lblAuctionTitle, 40); 
        addTextLimiter(tfAuctionDescription, 100); 
        addTextLimiter(tfAuctionBasePrice, 5); 
        addTextLimiter(tfAuctionMiniumBid, 5); 
        addTextLimiter(tfAuctionDaysAvailable, 5); 
    }

    private void loadAuctionStates() {
        AuctionsRepository auctionsRepository = new AuctionsRepository();
        auctionsRepository.getAuctionStates(new IProcessStatusListener<List<AuctionState>>() {
            @Override
            public void onSuccess(List<AuctionState> auctionStates) {
                Platform.runLater(() -> {
                    cbAuctionState.getItems().setAll(auctionStates);
                });
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                showAlert("Error al cargar los estados de las subastas");
            }
        });
    }

    private void validateNumericInput(KeyEvent event) {
        if (!event.getCharacter().matches("[0-9.]")) {
            event.consume();
        }
    }

    private void addTextLimiter(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(oldValue);
            }
        });
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) lblAuctionTitle.getScene().getWindow();
        baseStage.setScene(Navigation.startScene("views/MainMenuView.fxml"));
        baseStage.setTitle("Menu principal");
        baseStage.show();
    }

    @FXML
    private void btnUploadHipermedia(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona archivos para subir");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg"),
                new FileChooser.ExtensionFilter("Videos", "*.avi")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                try {
                    if (isValidFile(file)) {
                        Label fileLabel = new Label(file.getName());
                        vboxUploadedFiles.getChildren().add(fileLabel);
                        uploadedMediaFiles.add(new HypermediaFile(file.getName(), convertFileToBase64(file), Files.probeContentType(file.toPath())));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isValidFile(File file) throws IOException {
        String mimeType = Files.probeContentType(file.toPath());
        long fileSize = Files.size(file.toPath());

        if (mimeType != null && mimeType.startsWith("image")) {
            if (fileSize > 2 * 1024 * 1024) {
                showAlert("El archivo de imagen " + file.getName() + " excede el tamaño máximo permitido de 2 MB.");
                return false;
            }
            return true;
        } else if (mimeType != null && mimeType.equals("video/avi")) {
            if (fileSize > 5 * 1024 * 1024) {
                showAlert("El archivo de video " + file.getName() + " excede el tamaño máximo permitido de 5 MB.");
                return false;
            }
            return true;
        }

        showAlert("Tipo de archivo no permitido: " + file.getName());
        return false;
    }

    private String convertFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void btnSaveAuctionClick(ActionEvent event) {
        boolean isValid = true;

        if (lblAuctionTitle.getText().isEmpty()) {
            lblAuctionTitle.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            lblAuctionTitle.setStyle(null);
        }

        if (cbAuctionState.getValue() == null) {
            cbAuctionState.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            cbAuctionState.setStyle(null);
        }

        if (tfAuctionDescription.getText().isEmpty()) {
            tfAuctionDescription.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            tfAuctionDescription.setStyle(null);
        }

        if (tfAuctionBasePrice.getText().isEmpty()) {
            tfAuctionBasePrice.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (Double.parseDouble(tfAuctionBasePrice.getText()) < 0) {
            showAlert("El precio base de la subasta no puede ser negativo.");
            isValid = false;
        } else {
            tfAuctionBasePrice.setStyle(null);
        }
        if (tfAuctionDaysAvailable.getText().isEmpty()) {
            tfAuctionDaysAvailable.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (Integer.parseInt(tfAuctionDaysAvailable.getText()) <= 0) {
            showAlert("Los días disponibles deben ser mayores a cero.");
            isValid = false;
        } else {
            tfAuctionDaysAvailable.setStyle(null);
        }

        if (vboxUploadedFiles.getChildren().isEmpty()) {
            lblUploadMedia.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else {
            lblUploadMedia.setStyle(null);
        }

        if (isValid) {
            createAuction();
        } else {
            showAlert("Por favor, completa todos los campos obligatorios.");
        }
    }

    private void createAuction() {
        String title = lblAuctionTitle.getText();
        String description = tfAuctionDescription.getText();
        double basePrice = Double.parseDouble(tfAuctionBasePrice.getText());
        Double minimumBid = tfAuctionMiniumBid.getText().isEmpty() ? null : Double.parseDouble(tfAuctionMiniumBid.getText());
        int daysAvailable = Integer.parseInt(tfAuctionDaysAvailable.getText());
        AuctionState selectedState = cbAuctionState.getSelectionModel().getSelectedItem();
        int itemConditionId = selectedState.getId_item_condition();

        if (itemConditionId == -1) {
            showAlert("Por favor selecciona un estado de subasta válido.");
            return;
        }

        List<HypermediaFile> imageFiles = new ArrayList<>();
        List<HypermediaFile> videoFiles = new ArrayList<>();

        for (HypermediaFile file : uploadedMediaFiles) {
        if (file.getMimeType().startsWith("image")) {
            imageFiles.add(file);
        } else if (file.getMimeType().equals("video/mp4")) {
            videoFiles.add(file);
        }
        }
        
        AuctionCreateBody auctionBody = new AuctionCreateBody(
            title, description, basePrice, minimumBid, daysAvailable, itemConditionId, imageFiles
        );

        new AuctionsRepository().createAuction(auctionBody, new IEmptyProcessStatusListener() {
            @Override
            public void onSuccess() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Subasta creada");
                    alert.setHeaderText(null);
                    alert.setContentText("La subasta ha sido creada con éxito");
                    alert.showAndWait().ifPresent(response -> {
                        redirectToPreviousPage();
                    });
                });

                for (HypermediaFile videoFile : videoFiles) {
                    sendVideoByGrpc(videoFile, auctionBody.getIdItemCondition());
                }
            }

            @Override
            public void onError(ProcessErrorCodes errorStatus) {
                Platform.runLater(() -> {
                    showAlert("No se pudo crear la subasta. Por favor, intenta de nuevo.");
                });
            }
        });
    }

    private void sendVideoByGrpc(HypermediaFile videoFile, int auctionId) {
    System.out.println("Preparando para enviar el video por gRPC: " + videoFile.getName());
    Client grpcClient = new Client();
    grpcClient.uploadVideo(videoFile, auctionId, new GrpcClientCallback() {
        @Override
        public void onSuccess() {
            System.out.println("Video cargado correctamente");
        }

        @Override
        public void onError(Throwable error) {
            System.err.println("Error al cargar el video: " + error.getMessage());
        }
    });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void redirectToPreviousPage() {
        Stage baseStage = (Stage) lblAuctionTitle.getScene().getWindow();
        baseStage.setScene(Navigation.startScene("views/MainMenuView.fxml"));
        baseStage.setTitle("Menu principal");
        baseStage.show();
    }

    @FXML
    private void btnCancelCreateAuctionClick(ActionEvent event) {
        redirectToPreviousPage();
    }
}

