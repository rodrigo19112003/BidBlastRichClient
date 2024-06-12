package bidblastrichclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaView;

public class MakeOfferController implements Initializable {

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
    private Label lblPriceTitle11;
    @FXML
    private Label lblMinimumBidText;
    @FXML
    private Label lblMinimumBidValue;
    @FXML
    private TextField tfOffer;
    @FXML
    private Label lblOfferError;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
    }

    @FXML
    private void btnMakeOfferClick(ActionEvent event) {
    }
}
