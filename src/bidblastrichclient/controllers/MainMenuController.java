package bidblastrichclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lib.Navigation;

public class MainMenuController implements Initializable {

    @FXML
    private ImageView imgReturnToPreviousPage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void btnSearchAuctionClick(ActionEvent event) {
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/SearchAuctionView.fxml"));
        baseStage.setTitle("Buscar subasta");
        baseStage.show();
    }

    @FXML
    private void btnCreateAuctionClick(ActionEvent event) {
    }

    @FXML
    private void btnPurchasesClick(ActionEvent event) {
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/CompletedAuctionsListView.fxml"));
        baseStage.setTitle("Subastas Compradas");
        baseStage.show();
    }

    @FXML
    private void btnSalesClick(ActionEvent event) {
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/CreatedAuctionsListView.fxml"));
        baseStage.setTitle("Subastas vendidas");
        baseStage.show();
    }
}
