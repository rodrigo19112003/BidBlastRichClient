package bidblastrichclient.controllers;

import api.requests.authentication.UserCredentialsBody;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lib.ValidationToolkit;
import repositories.AuthenticationRepository;
import repositories.IEmptyProcessStatusListener;
import repositories.ProcessErrorCodes;

public class LoginController implements Initializable {

    @FXML
    private TextField tfEmail;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label lblEmailError;
    @FXML
    private Label lblPasswordError;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void btnLoginClick(ActionEvent event) {
        cleanErrorFields();
        boolean areFieldsValid = validateFields();
        
        if(areFieldsValid) {
            login();
        } else {
            highlightFieldsWithErrors();
        }
    }
    
    private void cleanErrorFields() {
        tfEmail.setStyle("-fx-border-color: #000000; -fx-border-radius: 5;");
        lblEmailError.setVisible(false);
        
        pfPassword.setStyle("-fx-border-color: #000000; -fx-border-radius: 5;");
        lblPasswordError.setVisible(false);
    }
    
    private boolean validateFields() {
        String email = tfEmail.getText().trim();
        String password = pfPassword.getText().trim();
        
        return ValidationToolkit.isValidEmail(email) 
            && !password.isEmpty();
    }
    
    private void login() {
        String email = tfEmail.getText().trim();
        String password = pfPassword.getText().trim();
        
        new AuthenticationRepository().login(
            new UserCredentialsBody(email, password),
            new IEmptyProcessStatusListener() {
                @Override
                public void onSuccess() {
                    redirectToMenu();
                }

                @Override
                public void onError(ProcessErrorCodes errorStatus) {
                    showLoginError(errorStatus);
                }
            }
        );
    }
    
    private void redirectToMenu() {
        System.out.println("Redireccionando a menú principal");
    }
    
    private void showLoginError(ProcessErrorCodes errorStatus) {
        Platform.runLater(() -> {
            String errorMessage;
            switch(errorStatus) {
                case REQUEST_FORMAT_ERROR:
                    errorMessage = "Por favor verifique sus credenciales";
                    break;
                default:
                    errorMessage = "Por el momento no es posible iniciar sesión, "
                        + "por favor intente más tarde";
            }
        
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error al iniciar sesión");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
        });
    }
    
    private void highlightFieldsWithErrors() {
        String email = tfEmail.getText().trim();
        if(!ValidationToolkit.isValidEmail(email)) {
            tfEmail.setStyle("-fx-border-color: #ff1700; -fx-border-radius: 5;");
            lblEmailError.setVisible(true);
        }
        
        String password = pfPassword.getText().trim();
        if(password.isEmpty()) {
            pfPassword.setStyle("-fx-border-color: #ff1700; -fx-border-radius: 5;");
            lblPasswordError.setVisible(true);
        }
    }

    @FXML
    private void btnRegisterClick(ActionEvent event) {
    }
}
