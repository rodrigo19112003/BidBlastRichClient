package bidblastrichclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import model.AuctionCategory;
import repositories.AuctionCategoriesRepository;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;

public class SearchAuctionController implements Initializable {

    @FXML
    private TextField tfSearchQuery;
    @FXML
    private ComboBox<AuctionCategory> cbCategories;
    @FXML
    private ComboBox<?> cbPriceRanges;
    @FXML
    private TextField tfLimit;
    @FXML
    private TextField tfOffset;
    @FXML
    private Pane pnAuctionsList;
    
    private ObservableList<AuctionCategory> auctionCategories;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoadAllAuctionCategories();
    }
    
    private void LoadAllAuctionCategories() {
        new AuctionCategoriesRepository().getAuctionCategories(
            new IProcessStatusListener<List<AuctionCategory>>() {
                @Override
                public void onSuccess(List<AuctionCategory> categories) {
                    Platform.runLater(() -> {
                        auctionCategories = FXCollections.observableArrayList();
                        auctionCategories.addAll(categories);
                        cbCategories.setItems(auctionCategories);
                        
                        cbCategories.setDisable(false);
                    });
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
                        
                        cbCategories.setDisable(true);
                    });
                }
            }
        );
    }

    @FXML
    private void btnSearchClick(ActionEvent event) {
    }
}
