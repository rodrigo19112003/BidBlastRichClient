package bidblastrichclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import lib.ValidationToolkit;
import model.User;
import repositories.IEmptyProcessStatusListener;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;
import repositories.UsersRepository;

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
    private ObservableList<User> usersList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureUsersTable();
        loadUsers();
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
            boolean isRemovable = cellData.getValue().getIsRemovable();
            
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
    
    private void loadUsers() {
        int limit = getLimitFilterValue(),
            offset = getOffsetFilterValue();
        String searchQuery = tfUserToSearch.getText().trim();
        new UsersRepository().getUsersList(
            searchQuery, limit, offset,
            new IProcessStatusListener<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    Platform.runLater(() -> {
                        usersList = FXCollections.observableArrayList();
                        usersList.addAll(users);
                        tvUsers.setItems(usersList);
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de conexión");
                        alert.setHeaderText(null);
                        alert.setContentText("Ocurrió un error al cargar los "
                            + "usuarios del sistema, por favor intente más tarde");
                        alert.showAndWait();
                    });
                }
            }
        );
    }
    
    private void deleteUser(int idProfile) {
        new UsersRepository().deleteUser(
            idProfile, 
            new IEmptyProcessStatusListener() {
                @Override
                public void onSuccess() {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Usuario eliminado con éxito");
                        alert.setHeaderText(null);
                        alert.setContentText("Se eliminó al usuario de forma "
                                + "exitosa");
                        alert.showAndWait();
                        loadUsers();
                    });
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    showGetUsersListError(errorCode);
                }
        });
    }
    
    private void showGetUsersListError(ProcessErrorCodes errorStatus) {
        Platform.runLater(() -> {
            String errorMessage;
            switch(errorStatus) {
                case REQUEST_FORMAT_ERROR:
                    errorMessage = "El usuario ya no puede ser eliminado, porque "
                            + "cambio su actividad en el sistema o porque ya fue "
                            + "eliminado ";
                    break;
                default:
                    errorMessage = "Por el momento no se puede eliminar al usuario, "
                            + "por favor inténtelo más tarde";
            }
        
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error al eliminar al usuario");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
        });
    }
    
    private int getLimitFilterValue() {
        int limit = 10;
        
        String limitValue = tfLimit.getText().trim();
        if(ValidationToolkit.isNumeric(limitValue)) {
            limit = Integer.parseInt(limitValue);
        }
        
        return limit;
    }
    
    private int getOffsetFilterValue() {
        int offset = 0;
        
        String offsetValue = tfOffset.getText().trim();
        if(ValidationToolkit.isNumeric(offsetValue)) {
            offset = Integer.parseInt(offsetValue);
        }
        
        return offset;
    }
    
    private boolean validateFiltersValues() {
        String limit = tfLimit.getText().trim();
        String offset = tfOffset.getText().trim();
        
        boolean isValidLimit = limit.isEmpty() 
            || (ValidationToolkit.isNumeric(limit) && Integer.parseInt(limit) > 0);
        boolean isValidOffset = offset.isEmpty() || ValidationToolkit.isNumeric(offset);
        
        return isValidLimit && isValidOffset;
    }
    
    private void showInvalidFiltersValuesError() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Filtros inválidos");
        alert.setHeaderText(null);
        alert.setContentText("Verifique que los valores ingresados en los campos "
            + "offset y limit sean números enteros no negativos. Tome en cuenta "
            + "que el valor mínimo aceptado de limit es 1");
        alert.showAndWait();
    }
    
    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) tfLimit.getScene().getWindow();
        baseStage.setScene(Navigation.startScene("views/LoginView.fxml"));
        baseStage.setTitle("Inicio de sesión");
        baseStage.show();
    }

    @FXML
    private void imgSearchUserClick(MouseEvent event) {
        boolean validFilters = validateFiltersValues();
        
        if(!validFilters) {
            showInvalidFiltersValuesError();
        } else {
            loadUsers();
        }
    }

    @FXML
    private void btnDeleteUserClick(ActionEvent event) {
        User user = tvUsers.getSelectionModel().getSelectedItem();
        if (user != null) {
            if (user.getIsRemovable()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmación de eliminación de usuario");
                alert.setHeaderText(null);
                alert.setContentText("¿Estás seguro de que deseas eliminar al "
                        + "usuario del sistema?, ya no tendrá acceso");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    deleteUser(user.getId());
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No se puede eliminar este usuario");
                alert.setHeaderText(null);
                alert.setContentText("El usuario seleccionado no se puede eliminar");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Seleccione un usuario");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione un usuario de la lista para eliminarlo");
            alert.showAndWait();
        }
    }
}