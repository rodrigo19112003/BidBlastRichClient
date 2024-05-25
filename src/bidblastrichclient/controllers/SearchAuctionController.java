package bidblastrichclient.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import repositories.AuctionsRepository;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;
import model.Auction;

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
        loadAllAuctionCategories();
        loadAuctions();
    }
    
    private void loadAllAuctionCategories() {
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
    
    private void loadAuctions() {
        int minimumPrice = 0, 
            maximumPrice = Integer.MAX_VALUE,
            limit = 10,
            offset = 0;
        String searchQuery = "",
            categories = "";
        /*PriceRange priceFilter = priceFilterSelected.getValue();
        if(priceFilter != null) {
            if(priceFilter.getMinimumAmount() != Float.NEGATIVE_INFINITY) {
                minimumPrice = (int)priceFilter.getMinimumAmount();
            }

            if(priceFilter.getMaximumAmount() != Float.POSITIVE_INFINITY) {
                maximumPrice = (int)priceFilter.getMaximumAmount();
            }
        }*/

        new AuctionsRepository().getAuctionsList(
            searchQuery, limit, offset,
            //ApiFormatter.parseToPlainMultiValueParam(categoryFiltersSelected.getValue()),
            categories,
            minimumPrice, maximumPrice,
            new IProcessStatusListener<List<Auction>>() {
                @Override
                public void onSuccess(List<Auction> auctions) {
                    System.out.println("Las subastas se cargaron correctamente");
                    System.out.println("Total: " + auctions.size());
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    System.out.println("Error en la carga inicial de subastas");
                }
            }
        );
    }

    @FXML
    private void btnSearchClick(ActionEvent event) {
    }
}
