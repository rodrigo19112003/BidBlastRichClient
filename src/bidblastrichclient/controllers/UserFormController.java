package bidblastrichclient.controllers;

import api.requests.user.UserRegisterBody;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import lib.ImageToolkit;
import lib.Navigation;
import lib.ValidationToolkit;
import model.User;
import repositories.UsersRepository;
import repositories.IEmptyProcessStatusListener;
import repositories.ProcessErrorCodes;

public class UserFormController implements Initializable {
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
    private Button btnSaveTitle;
    @FXML
    private Label lblCreateUserTitle;
    @FXML
    private Label lblPhoneNumberError;
    @FXML
    private Label lblConfirmPassword;
    private String avatarBase64;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String avatar;
    private boolean isEdition;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hideErrorMessages();
        applyRestrictionsOnFields();
    }
    
    public void setUserInformation(User user, boolean isEdition) {
        if (user != null) {
            this.fullName = user.getFullName();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.avatar = user.getAvatar();
            this.isEdition = isEdition;
        }
        
        if (isEdition) {
            loadUserInformationOnView();
            lblCreateUserTitle.setText("Edita tu información");
            btnSaveTitle.setText("Actualizar");
        } else {
            lblCreateUserTitle.setText("¡Crea una cuenta!");
            btnSaveTitle.setText("Registrarme");
        }
    }
    
    private void loadUserInformationOnView() {
        tfFullName.setText(fullName);
        tfEmail.setText(email);
        tfPhoneNumber.setText(phoneNumber != null ? phoneNumber : "");
        Image image = ImageToolkit.decodeBase64ToImage(avatar);
        imvAvatar.setImage(image);
        avatarBase64 = avatar;
    }
    
    private void hideErrorMessages() {
        lblFullNameError.setVisible(false);
        lblEmailError.setVisible(false);
        lblPhoneNumberError.setVisible(false);
        lblPasswordError.setVisible(false);
        lblConfirmPasswordError.setVisible(false);
    }
    
    private void applyRestrictionsOnFields() {
        setFieldMaxLength(tfFullName, 255);
        setFieldMaxLength(tfEmail, 60);
        setFieldMaxLength(tfPhoneNumber, 10);
    }

    private void setFieldMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(newValue.substring(0, maxLength));
            }
        });
    }

    @FXML
    private void btnChooseAvatarClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser
                        .ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            if (isValidImageSize(selectedFile)) {
                Image image = new Image(selectedFile.toURI().toString());
                imvAvatar.setImage(image);
                avatarBase64 = convertImageToBase64(selectedFile);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Imagen demasiado grande");
                alert.setHeaderText(null);
                alert.setContentText("La imagen supera los 500 kb permitidos");
                alert.showAndWait();
            }
        }
    }

    private boolean verifyTextFields() {
        boolean areValid = false;
        
        if (isEdition) {
            areValid = 
                !tfFullName.getText().trim().isEmpty()&&
                !tfEmail.getText().trim().isEmpty() &&
                ValidationToolkit.isValidEmail(tfEmail.getText());
            if (!tfPassword.getText().trim().isEmpty()) {
                areValid = areValid && 
                    ValidationToolkit.isValidPassword(tfPassword.getText().trim()) &&
                    tfPassword.getText().trim().equals(tfConfirmPassword.getText().trim());
            }
        } else {
            areValid = 
                !tfFullName.getText().trim().isEmpty()&&
                !tfEmail.getText().trim().isEmpty() &&
                ValidationToolkit.isValidEmail(tfEmail.getText()) &&
                !tfPassword.getText().trim().isEmpty() &&
                ValidationToolkit.isValidPassword(tfPassword.getText().trim()) &&
                tfPassword.getText().trim().equals(tfConfirmPassword.getText().trim());
        }
        
        if (!tfPhoneNumber.getText().trim().isEmpty()) {
            areValid = areValid && ValidationToolkit.isNumeric(tfPhoneNumber.getText().trim());
        }        
        
        return areValid;
    }
    
    private void showTextFieldsErrorsMessage() {
        if (tfFullName.getText().trim().isEmpty()) {
            lblFullNameError.setVisible(true);
        }
        if (tfEmail.getText().trim().isEmpty() || 
                !ValidationToolkit.isValidEmail(tfEmail.getText().trim())) {
            lblEmailError.setVisible(true);
        }
        if (!tfPhoneNumber.getText().trim().isEmpty() && 
                !ValidationToolkit.isNumeric(tfPhoneNumber.getText().trim())) {
            lblPhoneNumberError.setVisible(true);
        }
        if (isEdition) {
            if (!tfPassword.getText().trim().isEmpty() &&
                    !ValidationToolkit.isValidPassword(tfPassword.getText().trim())) {
                lblPasswordError.setVisible(true);
                if (!tfPassword.getText().trim().equals(tfConfirmPassword.getText().trim())) {
                    lblConfirmPasswordError.setText("Confirme su contraseña, debe ser la misma");
                    lblConfirmPassword.setVisible(true);
                }
            }
            if (!tfConfirmPassword.getText().trim().isEmpty() &&
                    tfPassword.getText().trim().isEmpty()) {
                lblConfirmPasswordError.setText("Ha hecho una confirmación de contraseña "
                        + "para una inexistente");
                lblConfirmPassword.setVisible(true);
            }
        } else {
            if (tfPassword.getText().trim().isEmpty() ||
                    !ValidationToolkit.isValidPassword(tfPassword.getText().trim())) {
                lblPasswordError.setVisible(true);
            }
            if (tfConfirmPassword.getText().trim().isEmpty() ||
                    !ValidationToolkit.isValidPassword(tfConfirmPassword.getText().trim())) {
                lblConfirmPasswordError.setVisible(true);
            }
            if (!tfConfirmPassword.getText().trim().equals(tfPassword.getText().trim())) {
                lblConfirmPasswordError.setVisible(true);
            }
        }
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
    
    private void createUser() {
        if (verifyTextFields()) {
            new UsersRepository().createUser(
                new UserRegisterBody(
                    tfFullName.getText().trim(),
                    tfEmail.getText().trim(),
                    tfPhoneNumber.getText().trim(),
                    avatarBase64,
                    tfPassword.getText().trim()
                ), 
                new IEmptyProcessStatusListener() {
                    @Override
                    public void onSuccess() {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Se creó tu cuenta en BidBlast");
                            alert.setHeaderText(null);
                            alert.setContentText("Se registró tu información de "
                                    + "forma correcta, ya puedes iniciar sesión");
                            alert.showAndWait();
                            redirectToPreviousPage(false);
                        });
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorStatus) {
                        showSignUpError(errorStatus);
                    }
                }
            );
        }
    }
    
    private void updateUser(){
        if (verifyTextFields()) {
            new UsersRepository().updateUser(
                new UserRegisterBody(
                    tfFullName.getText().trim(),
                    tfEmail.getText().trim(),
                    tfPhoneNumber.getText().trim().isEmpty() ? null : tfPhoneNumber.getText().trim(),
                    avatarBase64,
                    tfPassword.getText().trim().isEmpty() ? null : tfPassword.getText().trim()
                ), 
                new IEmptyProcessStatusListener() {
                    @Override
                    public void onSuccess() {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Se auctualizó tu información");
                            alert.setHeaderText(null);
                            alert.setContentText("Se auctualizó tu información de "
                                    + "forma correcta, será redirigido al inicio "
                                    + "de sesión para que vuelva a ingresar");
                            alert.showAndWait();
                            redirectToPreviousPage(true);
                        });
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorStatus) {
                        showSignUpError(errorStatus);
                    }
                }
            );
        }
    }
    
    private void redirectToPreviousPage(boolean isUpdated) {
        Stage baseStage = (Stage) tfFullName.getScene().getWindow();

        if (isEdition && !isUpdated) {
            baseStage.setScene(Navigation.startScene("views/MainMenuView.fxml"));
            baseStage.setTitle("Menú principal");
            baseStage.show();
        } else {
            baseStage.setScene(Navigation.startScene("views/LoginView.fxml"));
            baseStage.setTitle("Inicio de sesión");
            baseStage.show();
        }
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

    @FXML
    private void btnSaveUserClick(ActionEvent event) {
        hideErrorMessages();
        if (verifyTextFields()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de guardado de información");
            alert.setHeaderText(null);
            alert.setContentText("¿Estás seguro de que deseas guardar la información "
                    + "ingresada?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (isEdition) {
                    updateUser();
                } else {
                    createUser();
                }
            }
        } else {
            showTextFieldsErrorsMessage();
        }
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de regreso a ventana previa");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que deseas regresar a la ventana "
                + "previa?, los cambios no se guardarán y se perderán");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            redirectToPreviousPage(false);
        }
    }
}