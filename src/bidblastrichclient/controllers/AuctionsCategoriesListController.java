package bidblastrichclient.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lib.Navigation;
import model.AuctionCategory;
import repositories.AuctionCategoriesRepository;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;

public class AuctionsCategoriesListController implements Initializable {

    @FXML
    private ImageView imgReturnToPreviousPage;
    @FXML
    private TableView<AuctionCategory> tvAuctionCategories;
    @FXML
    private TableColumn<AuctionCategory, String> colCategoryTitle;
    @FXML
    private TableColumn<AuctionCategory, String> colCategoryDescription;
    @FXML
    private TableColumn<AuctionCategory, String> colKeyWords;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colCategoryTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategoryDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colKeyWords.setCellValueFactory(new PropertyValueFactory<>("keywords"));
        loadAuctionCategories();
    }
    private void loadAuctionCategories() {
        AuctionCategoriesRepository repository = new AuctionCategoriesRepository();
        repository.getAuctionCategories(new IProcessStatusListener<List<AuctionCategory>>() {
            @Override
            public void onSuccess(List<AuctionCategory> auctionCategories) {
                Platform.runLater(() -> tvAuctionCategories.getItems().setAll(auctionCategories));
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de conexión");
                        alert.setHeaderText(null);
                        alert.setContentText("Ocurrió un error al cargar las "
                            + "categorías, por favor intente más tarde");
                        alert.showAndWait();
                    });
            }
        });
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/ModeratorMenuView.fxml"));
        baseStage.setTitle("Categorias de subastas");
        baseStage.show();
    }

    @FXML
    private void btnNewCategoryClick(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bidblastrichclient/views/AuctionCategoryFormView.fxml"));
            Parent root = loader.load();
            AuctionCategoryFormController controller = loader.getController();

            controller.setAuctionCategoryInformation(null, false);
            Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();
            baseStage.setScene(new Scene(root));
            baseStage.setTitle("Registrar categoría de subastas");
            baseStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void btnEditClick(ActionEvent event) {
        AuctionCategory category = tvAuctionCategories.getSelectionModel().getSelectedItem();
        
        if (category != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bidblastrichclient/views/AuctionCategoryFormView.fxml"));
                Parent root = loader.load();
                AuctionCategoryFormController controller = loader.getController();

                controller.setAuctionCategoryInformation(category, true);
                Stage baseStage = (Stage) imgReturnToPreviousPage.getScene().getWindow();
                baseStage.setScene(new Scene(root));
                baseStage.setTitle("Modificar categoría de subastas");
                baseStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Seleccione una categoría");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione una categoría de la lista para poder "
                    + "editarla");
            alert.showAndWait();
        }
    }
}
