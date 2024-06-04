package bidblastrichclient.controllers;

import api.requests.auctioncategories.AuctionCategoryBody;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    }    
    
    public void setAuctionCategoryInformation(AuctionCategory category, boolean isEdition) {
        this.idAuctionCategory = category.getId();
        this.title = category.getTitle();
        this.description = category.getDescription();
        this.keywords = category.getKeywords();
        this.isEdition = isEdition;
        
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
    
    private boolean verifyTextFields() {
        boolean areValid = 
                !tfTitle.getText().isEmpty() &&
                !tfDescription.getText().isEmpty() &&
                !tfKeywords.getText().isEmpty() &&
                ValidationToolkit.areValidKeywords(tfKeywords.getText());
        
        System.out.println(areValid);
        
        return areValid;
    }
    
    private void showTextFieldsErrorsMessage() {
        if (tfTitle.getText().isEmpty()) {
            lblTitleError.setVisible(true);
        }
        if (tfDescription.getText().isEmpty()) {
            lblDescriptionError.setVisible(true);
        }
        if (tfKeywords.getText().isEmpty() || !ValidationToolkit.areValidKeywords(tfKeywords.getText())) {
            lblKeywordsError.setVisible(true);
        }
    }

    @FXML
    private void btnGoToAuctionCategoriesListClick(MouseEvent event) {
        Stage baseStage = (Stage) tfDescription.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/ProductCategoryView.fxml"));
        baseStage.setTitle("Modificar categoría de producto");
        baseStage.show();
    }

    @FXML
    private void btnCancelModifyAuctionCategoryClick(ActionEvent event) {
        Stage baseStage = (Stage) tfDescription.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/ProductCategoryView.fxml"));
        baseStage.setTitle("Modificar categoría de producto");
        baseStage.show();
    }

    @FXML
    private void btnSaveAuctionCategoryClick(ActionEvent event) {
        hideErrorMessages();
        isEdition = true;
        if (verifyTextFields()) {
            if (isEdition) {
                idAuctionCategory = 1;
                modifyAuctionCategory();
            } else {
                registerAuctionCategory();
            }
        } else {
            showTextFieldsErrorsMessage();
        }
    }
    
    private void registerAuctionCategory() {
        // TODO
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
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de conexión");
                        alert.setHeaderText(null);
                        alert.setContentText("Ocurrió un error al modificar la"
                                + " categoría, inténtelo más tarde");
                        alert.showAndWait();
                    });
                }
            }
        );
    }
    
    private void goToAuctionCategoriesList() {
        Stage baseStage = (Stage) tfDescription.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/ProductCategoryView.fxml"));
        baseStage.setTitle("Modificar categoría de producto");
        baseStage.show();
    }
}
