package bidblastrichclient.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lib.Navigation;
import lib.Session;

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

    @FXML
    private void btnModifyUserClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bidblastrichclient/views/UserFormView.fxml"));
            Parent root = loader.load();
            UserFormController controller = loader.getController();

            controller.setUserInformation(Session.getInstance().getUser(), true);
            Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();
            baseStage.setScene(new Scene(root));
            baseStage.setTitle("Modificar usuario");
            baseStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
