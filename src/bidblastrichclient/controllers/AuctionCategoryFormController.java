package bidblastrichclient.controllers;

import api.requests.auctioncategories.AuctionCategoryBody;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lib.Navigation;
import lib.ValidationToolkit;
import model.AuctionCategory;
import repositories.AuctionCategoriesRepository;
import repositories.IEmptyProcessStatusListener;
import repositories.ProcessErrorCodes;

public class AuctionCategoryFormController implements Initializable {

    @FXML
    private TextField tfTitle;
    @FXML
    private TextArea tfDescription;
    @FXML
    private TextArea tfKeywords;
    @FXML
    private Label lblTitleError;
    @FXML
    private Label lblDescriptionError;
    @FXML
    private Label lblKeywordsError;
    private int idAuctionCategory;
    private String title;
    private String description;
    private String keywords;
    private boolean isEdition;
    @FXML
    private Button btnSaveTitle;
    @FXML
    private Label lblFormTitle;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hideErrorMessages();
        applyRestrictionsOnFields();
    }    
    
    public void setAuctionCategoryInformation(AuctionCategory category, boolean isEdition) {
        if (category != null) {
            this.idAuctionCategory = category.getId();
            this.title = category.getTitle();
            this.description = category.getDescription();
            this.keywords = category.getKeywords();
            this.isEdition = isEdition;
        }
        
        if (isEdition) {
            loadAuctionCategoryInformationOnView();
            lblFormTitle.setText("Modificar categoría");
            btnSaveTitle.setText("Modificar categoría");
        } else {
            lblFormTitle.setText("Registrar categoría");
            btnSaveTitle.setText("Registrar categoría");
        }
    }
    
    private void loadAuctionCategoryInformationOnView() {
        tfTitle.setText(title);
        tfDescription.setText(description);
        tfKeywords.setText(keywords);
    }

    private void hideErrorMessages() {
        lblTitleError.setVisible(false);
        lblDescriptionError.setVisible(false);
        lblKeywordsError.setVisible(false);
    }
    
    private void applyRestrictionsOnFields() {
        setFieldMaxLength(tfTitle, 60);
    }
    
    private void setFieldMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(newValue.substring(0, maxLength));
            }
        });
    }
    
    private boolean verifyTextFields() {
        boolean areValid = 
                !tfTitle.getText().trim().isEmpty()
                && !tfDescription.getText().trim().isEmpty()
                && !tfKeywords.getText().trim().isEmpty()
                && ValidationToolkit.areValidKeywords(tfKeywords.getText().trim());
        
        return areValid;
    }
    
    private void showTextFieldsErrorsMessage() {
        if (tfTitle.getText().isEmpty()) {
            lblTitleError.setVisible(true);
        }
        if (tfDescription.getText().isEmpty()) {
            lblDescriptionError.setVisible(true);
        }
        if (tfKeywords.getText().isEmpty() 
                || !ValidationToolkit.areValidKeywords(tfKeywords.getText())) {
            lblKeywordsError.setVisible(true);
        }
    }

    private void btnGoToAuctionCategoriesListClick(MouseEvent event) {
        Stage baseStage = (Stage) tfDescription.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/ProductCategoryView.fxml"));
        baseStage.setTitle("Modificar categoría de producto");
        baseStage.show();
    }

    @FXML
    private void btnCancelModifyAuctionCategoryClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de cancelación");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que deseas cancelar el guardado "
                + "de la información de la categoría?, los cambios no se "
                + "guardarán y se perderán");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            redirectToPreviousPage();
        }
    }

    @FXML
    private void btnSaveAuctionCategoryClick(ActionEvent event) {
        hideErrorMessages();
        if (verifyTextFields()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de guardado de información");
            alert.setHeaderText(null);
            alert.setContentText("¿Estás seguro de que deseas guardar la información "
                    + "ingresada?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (isEdition) {
                    modifyAuctionCategory();
                } else {
                    registerAuctionCategory();
                }
            }
        } else {
            showTextFieldsErrorsMessage();
        }
    }
    
    private void registerAuctionCategory() {
        new AuctionCategoriesRepository().registerAuctionCategory(
            new AuctionCategoryBody(
                tfTitle.getText().trim(),
                tfDescription.getText().trim(),
                tfKeywords.getText().trim()
            ),
            new IEmptyProcessStatusListener() {
                @Override
                public void onSuccess() {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Categoría registrada exitosamente");
                        alert.setHeaderText(null);
                        alert.setContentText("Se registró la información de la "
                                + "categoría de forma correcta");
                        alert.showAndWait();
                        redirectToPreviousPage();
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    showLoginError(errorCode);
                }
            }
        );
    }
    
    private void modifyAuctionCategory() {
        new AuctionCategoriesRepository().updateAuctionCategory(
           idAuctionCategory,
            new AuctionCategoryBody(
                tfTitle.getText(),
                tfDescription.getText(),
                tfKeywords.getText()
            ),
            new IEmptyProcessStatusListener() {
                @Override
                public void onSuccess() {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Categoría modificada exitosamente");
                        alert.setHeaderText(null);
                        alert.setContentText("Se actualizó la información de la "
                                + "categoría de forma correcta");
                        alert.showAndWait();
                        redirectToPreviousPage();
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    showLoginError(errorCode);
                }
            }
        );
    }
    
    private void showLoginError(ProcessErrorCodes errorStatus) {
        Platform.runLater(() -> {
            String errorMessage;
            switch(errorStatus) {
                case REQUEST_FORMAT_ERROR:
                    errorMessage = "Titulo de categoría ya registrado";
                    break;
                default:
                    errorMessage = "Por el momento no se puede guardar la información "
                            + "de la categoría, por favor inténtelo más tarde";
            }
        
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error al guardar la categoría");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
        });
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de regreso a ventana previa");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que deseas regresar a la ventana "
                + "previa?, los cambios no se guardarán y se perderán");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            redirectToPreviousPage();
        }
    }
    
    private void redirectToPreviousPage() {
        Stage baseStage = (Stage) tfDescription.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/AuctionsCategoriesListView.fxml"));
        baseStage.setTitle("Categorías de subastas");
        baseStage.show();
    }
}