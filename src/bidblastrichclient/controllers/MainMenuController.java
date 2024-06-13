package bidblastrichclient.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lib.Navigation;
import lib.Session;
import model.User;

public class MainMenuController implements Initializable {

    @FXML
    private ImageView imgReturnToPreviousPage;
    @FXML
    private Button btnSales;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserRole;
    
    private final String AUCTIONEER_ROLE = "AUCTIONEER";
    @FXML
    private Circle crclUserAvatar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showAuctioneerOptions();
        showUserProfileInformation();
    }
    
    private void showAuctioneerOptions() {
        List<String> userRoles = Session.getInstance().getUser().getRoles();
        
        if(userRoles.contains(AUCTIONEER_ROLE)) {
            btnSales.setDisable(false);
        }
    }
    
    private void showUserProfileInformation() {
        User user = Session.getInstance().getUser();
        lblUserName.setText(user.getFullName());
        
        if(user.getRoles().contains(AUCTIONEER_ROLE)) {
            lblUserRole.setText("Subastador");
        }
        
        showUserAvatar(user);
    }
    
    private void showUserAvatar(User user) {
        String avatarBase64 = user.getAvatar();
        Image avatarImage;
        
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(avatarBase64);
            avatarImage = new Image(new ByteArrayInputStream(decodedBytes));
        } else {
            avatarImage = new Image(getClass().getResourceAsStream("/bidblastrichclient/resources/Avatar.png"));
        }
        
        crclUserAvatar.setFill(new ImagePattern(avatarImage));
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
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/AuctionFormView.fxml"));
        baseStage.setTitle("Crear subasta");
        baseStage.show();
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

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();
        
        baseStage.setScene(Navigation.startScene("views/LoginView.fxml"));
        baseStage.setTitle("Inicio de sesión");
        baseStage.show();
    }
}