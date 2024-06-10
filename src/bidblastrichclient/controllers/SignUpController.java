package bidblastrichclient.controllers;

import api.requests.user.UserRegisterBody;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import lib.Navigation;
import lib.ValidationToolkit;
import repositories.UsersRepository;
import repositories.IEmptyProcessStatusListener;
import repositories.ProcessErrorCodes;

public class SignUpController implements Initializable {

    @FXML
    private TextField tfFullName;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPhoneNumber;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private PasswordField tfConfirmPassword;
    @FXML
    private Label lblFullNameError;
    @FXML
    private Label lblEmailError;
    @FXML
    private Label lblPasswordError;
    @FXML
    private Label lblConfirmPasswordError;
    @FXML
    private ImageView imvAvatar;
    @FXML
    private ImageView eyeIconPassword;
    @FXML
    private ImageView eyeIconConfirmPassword;
    @FXML
    private Button btnRegister;
    private String avatarBase64;
    @FXML
    private Label lblPasswordRules;
    @FXML
    private Label lblConfirmPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblFullNameError.setVisible(false);
        lblEmailError.setVisible(false);
        lblPasswordError.setVisible(false);
        lblConfirmPasswordError.setVisible(false);
        lblPasswordRules.setVisible(false);

        tfPassword.setOnMouseClicked(event -> showPasswordRules());
        tfPassword.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                hidePasswordRules();
            }
        });
        tfPassword.textProperty().addListener((observable, oldValue, newValue) -> updatePasswordRules(newValue));

        tfPhoneNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfPhoneNumber.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        setFieldMaxLength(tfFullName, 80);
        setFieldMaxLength(tfEmail, 60);
        setFieldMaxLength(tfPassword, 15);
        setFieldMaxLength(tfConfirmPassword, 15);
        setFieldMaxLength(tfPhoneNumber, 10);
    }

    private void showPasswordRules() {
        lblPasswordRules.setVisible(true);
        moveElementsDown();
    }

    private void hidePasswordRules() {
        lblPasswordRules.setVisible(false);
        moveElementsUp();
    }

    private void updatePasswordRules(String password) {
        String rules = "• Al menos una letra mayúscula\n" +
                       "• Al menos un número\n" +
                       "• Extensión entre 10 y 15 caracteres";

        boolean containsUpperCase = password.matches(".*[A-Z].*");
        boolean containsNumber = password.matches(".*\\d.*");
        boolean correctLength = password.length() >= 10 && password.length() <= 15;

        rules = updateRuleState(rules, containsUpperCase, "• Al menos una letra mayúscula", "✔ Al menos una letra mayúscula");
        rules = updateRuleState(rules, containsNumber, "• Al menos un número", "✔ Al menos un número");
        rules = updateRuleState(rules, correctLength, "• Extensión entre 10 y 15 caracteres", "✔ Extensión entre 10 y 15 caracteres");

        lblPasswordRules.setText(rules);
    }

    private String updateRuleState(String rules, boolean isValid, String originalText, String updatedText) {
        if (isValid) {
            return rules.replace(originalText, updatedText);
        }
        return rules;
    }

    private void moveElementsDown() {
        lblConfirmPassword.setLayoutY(lblConfirmPassword.getLayoutY() + 50);
        tfConfirmPassword.setLayoutY(tfConfirmPassword.getLayoutY() + 50);
        eyeIconConfirmPassword.setLayoutY(eyeIconConfirmPassword.getLayoutY() + 50);
        lblConfirmPasswordError.setLayoutY(lblConfirmPasswordError.getLayoutY() + 50);
        btnRegister.setLayoutY(btnRegister.getLayoutY() + 50);
    }

    private void moveElementsUp() {
        lblConfirmPassword.setLayoutY(lblConfirmPassword.getLayoutY() - 50);
        tfConfirmPassword.setLayoutY(tfConfirmPassword.getLayoutY() - 50);
        eyeIconConfirmPassword.setLayoutY(eyeIconConfirmPassword.getLayoutY() - 50);
        lblConfirmPasswordError.setLayoutY(lblConfirmPasswordError.getLayoutY() - 50);
        btnRegister.setLayoutY(btnRegister.getLayoutY() - 50);
    }

    private void setFieldMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(newValue.substring(0, maxLength));
            }
        });
    }

    @FXML
    private void btnRegisterNewAccountClick(MouseEvent event) {
        String fullName = tfFullName.getText();
        String email = tfEmail.getText();
        String phoneNumber = tfPhoneNumber.getText();
        String password = tfPassword.getText();
        String confirmPassword = tfConfirmPassword.getText();

        if (validateFields(fullName, email, password, confirmPassword)) {
            UserRegisterBody body = new UserRegisterBody(fullName, email, phoneNumber, avatarBase64, password);
            new UsersRepository().createUser(
                body, 
                new IEmptyProcessStatusListener() {
                    @Override
                    public void onSuccess() {
                        Platform.runLater(() -> showConfirmationDialog());
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorStatus) {
                        showSignUpError(errorStatus);
                    }
            });
        }
    }

    @FXML
    private void btnChooseAvatarClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imvAvatar.setImage(image);

            if (isValidImageSize(selectedFile)) {
                avatarBase64 = convertImageToBase64(selectedFile);
                avatarBase64 = "UNE8RUNC29U83NRCUNWCQWNUECIQURWICNQUROICQUWCRNCIOWUQRCQOIRINUQCIORUCOINQUROUCQOURNOICQ";
            } else {
                showAlert("La imagen seleccionada es demasiado grande.");
            }
        }
    }

    private boolean validateFields(String fullName, String email, String password, String confirmPassword) {
        boolean isValid = true;

        if (fullName.isEmpty()) {
            tfFullName.setStyle("-fx-border-color: #ff1700; -fx-border-radius: 5;");
            lblFullNameError.setText("El nombre completo es obligatorio.");
            lblFullNameError.setVisible(true);
            isValid = false;
        } else {
            tfFullName.setStyle(null);
            lblFullNameError.setVisible(false);
        }

        if (email.isEmpty() || !ValidationToolkit.isValidEmail(email)) {
            tfEmail.setStyle("-fx-border-color: #ff1700; -fx-border-radius: 5;");
            lblEmailError.setText("El correo electrónico no es válido.");
            lblEmailError.setVisible(true);
            isValid = false;
        } else {
            tfEmail.setStyle(null);
            lblEmailError.setVisible(false);
        }

        if (password.isEmpty()) {
            tfPassword.setStyle("-fx-border-color: #ff1700; -fx-border-radius: 5;");
            lblPasswordError.setText("La contraseña es obligatoria.");
            lblPasswordError.setVisible(true);
            isValid = false;
        } else if (!ValidationToolkit.isValidPassword(password)) {
            tfPassword.setStyle("-fx-border-color: #ff1700; -fx-border-radius: 5;");
            lblPasswordError.setText("La contraseña no es válida.");
            lblPasswordError.setVisible(true);
            isValid = false;
        } else {
            tfPassword.setStyle(null);
            lblPasswordError.setVisible(false);
        }

        if (confirmPassword.isEmpty()) {
            tfConfirmPassword.setStyle("-fx-border-color: #ff1700; -fx-border-radius: 5;");
            lblConfirmPasswordError.setText("Confirmar la contraseña es obligatorio.");
            lblConfirmPasswordError.setVisible(true);
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tfConfirmPassword.setStyle("-fx-border-color: #ff1700; -fx-border-radius: 5;");
            lblConfirmPasswordError.setText("Las contraseñas no coinciden.");
            lblConfirmPasswordError.setVisible(true);
            isValid = false;
        } else {
            tfConfirmPassword.setStyle(null);
            lblConfirmPasswordError.setVisible(false);
        }

        return isValid;
    }

    private boolean isValidImageSize(File imageFile) {
        final double maxSizeInMB = 0.5;
        double fileSizeInMB = imageFile.length() / (1024.0 * 1024.0);
        return fileSizeInMB <= maxSizeInMB;
    }

    private String convertImageToBase64(File imageFile) {
        try {
            byte[] fileContent = new byte[(int) imageFile.length()];
            try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
                fileInputStream.read(fileContent);
            }
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void togglePasswordVisibility(PasswordField passwordField) {
        if (passwordField.getText().isEmpty()) {
            return;
        }
        TextField textField = new TextField(passwordField.getText());
        textField.setStyle(passwordField.getStyle());
        textField.setPrefHeight(passwordField.getPrefHeight());
        textField.setPrefWidth(passwordField.getPrefWidth());
        textField.setFont(passwordField.getFont());

        AnchorPane parent = (AnchorPane) passwordField.getParent();
        int index = parent.getChildren().indexOf(passwordField);
        parent.getChildren().remove(passwordField);
        parent.getChildren().add(index, textField);

        textField.setOnMouseClicked(event -> togglePasswordVisibilityBack(textField, passwordField));
    }

    private void togglePasswordVisibilityBack(TextField textField, PasswordField passwordField) {
        passwordField.setText(textField.getText());
        AnchorPane parent = (AnchorPane) textField.getParent();
        int index = parent.getChildren().indexOf(textField);
        parent.getChildren().remove(textField);
        parent.getChildren().add(index, passwordField);
    }

    private void showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro exitoso");
        alert.setHeaderText(null);
        alert.setContentText("Tu cuenta ha sido creada exitosamente, inicia sesión para comenzar en el mundo de las subastas.");
        alert.showAndWait();

        Platform.runLater(this::redirectToLogin);
    }

    private void showSignUpError(ProcessErrorCodes errorStatus) {
        Platform.runLater(() -> {
            String errorMessage;
            switch(errorStatus) {
                case REQUEST_FORMAT_ERROR:
                    errorMessage = "El email ya se encuentra en uso";
                    break;
                default:
                    errorMessage = "Por el momento no se puede registrar, "
                            + "por favor inténtelo más tarde";
            }
        
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error al registrarse");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void redirectToLogin() {
        Stage baseStage = (Stage) tfEmail.getScene().getWindow();
        baseStage.setScene(Navigation.startScene("views/LoginView.fxml"));
        baseStage.setTitle("Iniciar sesión");
        baseStage.show();
    }

    @FXML
    private void btnReturnLogInClick(MouseEvent event) {
        redirectToLogin();
    }
}

