package bidblastrichclient.controllers;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class ModeratorMenuController implements Initializable {

    @FXML
    private ImageView imgReturnToPreviousPage;
    @FXML
    private Label lblUserName;
    @FXML
    private Circle crclUserAvatar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showUserProfileInformation();
    }
    
    private void showUserProfileInformation() {
        User user = Session.getInstance().getUser();
        
        lblUserName.setText(user.getFullName());
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
    private void btnAssessAuctionClick(ActionEvent event) {
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/EvaluateItemView.fxml"));
        baseStage.setTitle("Evaluar subastas");
        baseStage.show();
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