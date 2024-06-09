package bidblastrichclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import lib.ImageToolkit;
import lib.Navigation;
import model.User;

public class UsersListController implements Initializable {

    @FXML
    private TextField tfLimit;
    @FXML
    private TextField tfOffset;
    @FXML
    private TableView<User> tvUsers;
    @FXML
    private TableColumn<User, Image> colUserAvatar;
    @FXML
    private TableColumn<User, String> colFullName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colPhoneNumber;
    @FXML
    private TableColumn<User, String> colRole;
    @FXML
    private TableColumn<User, String> colPossibilityOfElimination;
    @FXML
    private TextField tfUserToSearch;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    private void configureUsersTable() {
        colFullName.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            
            return new SimpleStringProperty(
                user != null ? user.getFullName() : "NA"
            );
        });
        colEmail.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getEmail();
            
            return new SimpleStringProperty(
                email != null ? email : "NA"
            );
        });
        colPhoneNumber.setCellValueFactory(cellData -> {
            String phoneNumber = cellData.getValue().getPhoneNumber();
            
            return new SimpleStringProperty(
                phoneNumber != null && !phoneNumber.isEmpty() ? phoneNumber : "NA"
            );
        });
        colRole.setCellValueFactory(cellData -> {
            List<String> roles = cellData.getValue().getRoles();
            String rolesString = String.join("\n", roles);
            
            return new SimpleStringProperty(
                rolesString
            );
        });
        colPossibilityOfElimination.setCellValueFactory(cellData -> {
            boolean isRemovable = cellData.getValue().isIsRemovable();
            
            return new SimpleStringProperty(
                isRemovable ? "Se puede eliminar" : "No se puede eliminar"
            );
        });
        
        configureUserAvatarColumn();
    }
    
    private void configureUserAvatarColumn() {
        colUserAvatar.setCellValueFactory(cellData -> {
            String userAvatar = 
                cellData.getValue().getAvatar().isEmpty() 
                    ? null 
                    : cellData.getValue().getAvatar();
            
            if (userAvatar != null) {
                Image jfxImage = 
                    ImageToolkit.decodeBase64ToImage(userAvatar);
                return new javafx.beans.property.SimpleObjectProperty<>(jfxImage);
            }
            return new javafx.beans.property.SimpleObjectProperty<>(null);
        });

        colUserAvatar.setCellFactory(new Callback<TableColumn<User, Image>, TableCell<User, Image>>() {
            @Override
            public TableCell<User, Image> call(TableColumn<User, Image> param) {
                return new TableCell<User, Image>() {
                    private final ImageView imageView = new ImageView();

                    @Override
                    protected void updateItem(Image item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            imageView.setImage(item);
                            imageView.setFitWidth(30);
                            imageView.setFitHeight(30 * item.getHeight() / item.getWidth());
                            setGraphic(imageView);
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                };
            }
        });
    }
    
    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) tfLimit.getScene().getWindow();
        baseStage.setScene(Navigation.startScene("views/LoginView.fxml"));
        baseStage.setTitle("Inicio de sesión");
    }

    @FXML
    private void imgSearchUserClick(MouseEvent event) {
        
    }

    @FXML
    private void btnDeleteUserClick(ActionEvent event) {
        
    }
    
}
