package bidblastrichclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class SearchAuctionController implements Initializable {

    @FXML
    private TextField tfSearchQuery;
    @FXML
    private ComboBox<?> cbCategories;
    @FXML
    private ComboBox<?> cbPriceRanges;
    @FXML
    private TextField tfLimit;
    @FXML
    private TextField tfOffset;
    @FXML
    private Pane pnAuctionsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void btnSearchClick(ActionEvent event) {
    }
}
