package bidblastrichclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import model.AuctionCategory;

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
    private String title;
    private String description;
    private String keywords;
    private Boolean isEdition;
    @FXML
    private Button btnSaveTitle;
    @FXML
    private Label lblFormTitle;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hideErrorMessages();
    }    
    
    public void setAuctionCategoryInformation(AuctionCategory category, boolean isEdition) {
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

    @FXML
    private void btnGoToAuctionCategoriesListClick(MouseEvent event) {
        // TODO Redirección a la lista de categorías
    }

    @FXML
    private void btnCancelModifyAuctionCategoryClick(ActionEvent event) {
    }

    @FXML
    private void btnSaveAuctionCategoryClick(ActionEvent event) {
    }
    
}
