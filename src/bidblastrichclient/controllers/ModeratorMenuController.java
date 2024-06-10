package bidblastrichclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lib.Navigation;

public class ModeratorMenuController implements Initializable {

    @FXML
    private ImageView imgReturnToPreviousPage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void btnAssessAuctionClick(ActionEvent event) {
    }

    @FXML
    private void btnAuctionCategoriesListClick(ActionEvent event) {
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/AuctionsCategoriesListView.fxml"));
        baseStage.setTitle("Categorias de subastas");
        baseStage.show();
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();
        
        baseStage.setScene(Navigation.startScene("views/LoginView.fxml"));
        baseStage.setTitle("Inicio de sesión");
        baseStage.show();
    }
}