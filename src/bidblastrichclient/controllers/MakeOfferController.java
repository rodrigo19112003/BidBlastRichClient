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
import javafx.stage.Stage;
import lib.Navigation;

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
    
    private int idAuction;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void setIdAuction(int idAuction) {
        this.idAuction = idAuction;
        System.out.println(idAuction);
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
        
    }
}
