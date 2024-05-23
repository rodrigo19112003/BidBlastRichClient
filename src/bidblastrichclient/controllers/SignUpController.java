package bidblastrichclient.controllers;

import api.IEmptyProcessStatusListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import repositories.AccountRepository;
import repositories.ProcessErrorCodes;
import requests.register.UserRegisterBody;


public class SignUpController implements Initializable {

    @FXML
    private TextField tfFullName;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPhoneNumber;
    @FXML
    private TextField tfPassword;
    @FXML
    private Button btnRegister;
    private AccountRepository accountRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        accountRepository = new AccountRepository();
    }    

    @FXML
    private void btnTegisterNewAccount(MouseEvent event) {
        String fullName = tfFullName.getText();
        String email = tfEmail.getText();
        String phoneNumber = tfPhoneNumber.getText();
        String password = tfPassword.getText();

        UserRegisterBody body = new UserRegisterBody(fullName, email, phoneNumber, null, password);

        accountRepository.createAccount(body, new IEmptyProcessStatusListener() {
            @Override
            public void onSuccess() {
                System.out.println("Account created successfully!");
            }

            @Override
            public void onError(ProcessErrorCodes errorStatus) {
                System.err.println("Error creating account");
            }
        });
    }
}
    

