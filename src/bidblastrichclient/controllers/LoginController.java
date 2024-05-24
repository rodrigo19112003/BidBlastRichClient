package bidblastrichclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import lib.ValidationToolkit;

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
