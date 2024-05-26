package bidblastrichclient.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lib.CurrencyToolkit;
import lib.DateToolkit;
import lib.Navigation;
import model.Auction;
import repositories.AuctionsRepository;
import repositories.IProcessStatusListener;
import repositories.ProcessErrorCodes;

public class SalesStatisticsController implements Initializable {

    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private Label lblFeaturedDay;
    @FXML
    private Label lblTotalAuctions;
    @FXML
    private Label lblAmount;
    @FXML
    private BarChart<String, Number> bcSoldAuctions;
    @FXML
    private PieChart pcSalesAuctionsCategories;
    @FXML
    private Button btnFilterDates;
    @FXML
    private Label lblProfitsEarned;
    private List<Auction> salesAuctionsList;
    private float profitsEarned = 0;
    private final List<String> categories = new ArrayList<>();
    private final List<Integer> categoriesCount = new ArrayList<>();
    private final List<Date> salesDates = new ArrayList<>();
    private  final List<Integer> salesDatesCount = new ArrayList<>();
    private  final List<Float> salesDatesAmounts = new ArrayList<>();
    private String startDate;
    private String endDate;
    @FXML
    private Label lblDatesRangesTitle;
    @FXML
    private Label lblWithoutSalesStatisticsMessage;
    @FXML
    private Label lblFeaturedDayTitle;
    @FXML
    private Label lblBidBlastMessage;
    @FXML
    private Label lblProfitsEarnedTitle;
    @FXML
    private Label lblAuctionCategories;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSalesAuctions();
    }    
    
    private void loadSalesAuctions() {
        disableFilters();
        new AuctionsRepository().getUserSalesAuctionsList(
                startDate, endDate,
                new IProcessStatusListener<List<Auction>>() {
                    @Override
                    public void onSuccess(List<Auction> auctions) {
                        Platform.runLater(() -> {
                            enableFilters();
                            if (auctions.size() != 0) {
                                showStatisticsComponents();
                                hideWithoutSalesStatisticsMessage();
                                salesAuctionsList = auctions;
                                calculateAndShowSalesStatistics();
                                calculateAndShowCategoryStatistics();
                                calculateAndShowFeaturedDay();
                            } else {
                                if (startDate == null && endDate == null) {
                                    hideDatesRanges();
                                } else {
                                    changeWithoutSalesStatisticsMessage();
                                }
                                hideStatisticsComponents();
                                showWithoutSalesStatisticsMessage();
                            }
                        });
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorCode) {
                        Platform.runLater(() -> {
                            hideDatesRanges();
                            hideWithoutSalesStatisticsMessage();
                            hideStatisticsComponents();
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Error de conexión");
                            alert.setHeaderText(null);
                            alert.setContentText("Ocurrió un error al cargar las"
                                    + " estadísticas, inténtelo más tarde");
                            alert.showAndWait();
                        });
                    }
                }
        );
    }
    
    private void calculateAndShowSalesStatistics(){
        bcSoldAuctions.getData().clear();
        for (int i = 0; i < salesAuctionsList.size(); i++) {
            Auction auction = salesAuctionsList.get(i);
            profitsEarned += auction.getLastOffer().getAmount();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(trimString(auction.getTitle(), 8));
            series.getData().add(new XYChart.Data<>(
                "", 
                auction.getLastOffer().getAmount())
            );
            bcSoldAuctions.getData().add(series);
        }
        lblProfitsEarned.setText(CurrencyToolkit.parseToMXN(profitsEarned));
        
         for (XYChart.Series<String, Number> series : bcSoldAuctions.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Tooltip tooltip = new Tooltip("Precio: " + data.getYValue());
                Tooltip.install(data.getNode(), tooltip);
                data.getNode().setOnMouseEntered(
                    event -> data.getNode().setStyle("-fx-opacity: 0.7;"));
                data.getNode().setOnMouseExited(
                    event -> data.getNode().setStyle("-fx-opacity: 1;"));
            }
        }

    }
    
    private String trimString(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        } else {
            return text.substring(0, maxLength);
        }
    }
    
    private void calculateAndShowCategoryStatistics() {
        pcSalesAuctionsCategories.getData().clear();
        for (int i = 0; i < salesAuctionsList.size(); i++) {
            Auction auction = salesAuctionsList.get(i);
            if (!categories.contains(auction.getCategory().getTitle())) {
                categories.add(auction.getCategory().getTitle());
            }
        }
        for (int i = 0; i < categories.size(); i++) {
            int count = 0;
            String category = categories.get(i);
            for (int j = 0; j < salesAuctionsList.size(); j++) {
                Auction auction = salesAuctionsList.get(j);
                if (category.equals(auction.getCategory().getTitle())) {
                    count += 1;
                }
            }
            categoriesCount.add(count);
        }
        
        for (int i = 0; i < categories.size(); i++) {
            PieChart.Data slice = new PieChart.Data(
                categories.get(i), categoriesCount.get(i)
            );
            pcSalesAuctionsCategories.getData().add(slice);
        }
        
        for (PieChart.Data data : pcSalesAuctionsCategories.getData()) {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + data.getPieValue());
            Tooltip.install(data.getNode(), tooltip);
            data.getNode().setOnMouseEntered(event -> data.getNode().setStyle("-fx-opacity: 0.7;"));
            data.getNode().setOnMouseExited(event -> data.getNode().setStyle("-fx-opacity: 1;"));
        }
    }
    
    private void calculateAndShowFeaturedDay() {
        Date bestDate = new Date();
        int totalAuctions = 0;
        float totalAmount = 0;
        for (int i = 0; i < salesAuctionsList.size(); i++) {
            Auction auction = salesAuctionsList.get(i);
            if (!salesDates.contains(auction.getUpdatedDate())) {
                salesDates.add(auction.getUpdatedDate());
            }
        }
        for (int i = 0; i < salesDates.size(); i++) {
            int count = 0;
            float amount = 0;
            Date saleSate = salesDates.get(i);
            for (int j = 0; j < salesAuctionsList.size(); j++) {
                Auction auction = salesAuctionsList.get(j);
                if (saleSate.equals(auction.getUpdatedDate())) {
                    count += 1;
                    amount += auction.getLastOffer().getAmount();
                }
            }
            salesDatesCount.add(count);
            salesDatesAmounts.add(amount);
        }

        float amountMax = 0;
        for (int i = 0; i < salesDates.size(); i++) {
            Date saleSate = salesDates.get(i);
            int auctions = salesDatesCount.get(i);
            float amount = salesDatesAmounts.get(i);
            if (amount > amountMax) {
                bestDate = saleSate;
                totalAuctions = auctions;
                totalAmount = amount;
            }
        }
        
        lblFeaturedDay.setText(DateToolkit.parseToFullDate(bestDate));
        lblTotalAuctions.setText("¡Muchas felicidades! en este día lograste concretar " + 
                totalAuctions + " subastas, generando un gasto total de:");
        lblAmount.setText(CurrencyToolkit.parseToMXN(totalAmount));
    }
    
    private void hideWithoutSalesStatisticsMessage() {
        lblWithoutSalesStatisticsMessage.setVisible(false);
    }
    
    private void showWithoutSalesStatisticsMessage() {
        lblWithoutSalesStatisticsMessage.setVisible(true);
    }
    
    private void hideDatesRanges() {
        lblDatesRangesTitle.setVisible(false);
        dpStartDate.setVisible(false);
        dpEndDate.setVisible(false);
        btnFilterDates.setVisible(false);
    }
    
    private void hideStatisticsComponents() {
        bcSoldAuctions.setVisible(false);
        pcSalesAuctionsCategories.setVisible(false);
        lblFeaturedDayTitle.setVisible(false);
        lblAmount.setVisible(false);
        lblFeaturedDay.setVisible(false);
        lblTotalAuctions.setVisible(false);
        lblAuctionCategories.setVisible(false);
        lblBidBlastMessage.setVisible(false);
        lblProfitsEarned.setVisible(false);
        lblProfitsEarnedTitle.setVisible(false);
    }
    
    private void showStatisticsComponents() {
        bcSoldAuctions.setVisible(true);
        pcSalesAuctionsCategories.setVisible(true);
        lblFeaturedDayTitle.setVisible(true);
        lblAmount.setVisible(true);
        lblFeaturedDay.setVisible(true);
        lblTotalAuctions.setVisible(true);
        lblAuctionCategories.setVisible(true);
        lblBidBlastMessage.setVisible(true);
        lblProfitsEarned.setVisible(true);
        lblProfitsEarnedTitle.setVisible(true);
    }
    
    private void changeWithoutSalesStatisticsMessage() {
        lblWithoutSalesStatisticsMessage.setText("No existen ventas de subastas "
                + "en el rango de fechas seleccionado");
    }
    
    private void enableFilters() {
        dpStartDate.setDisable(false);
        dpEndDate.setDisable(false);
        btnFilterDates.setDisable(false);
    }
    
    private void disableFilters() {
        dpStartDate.setDisable(true);
        dpEndDate.setDisable(true);
        btnFilterDates.setDisable(true);
    }

    @FXML
    private void btnFilterDatesClick(ActionEvent event) {
        if (dpStartDate.getValue() != null && dpEndDate.getValue() != null) {
            LocalDate selectedStartDate = dpStartDate.getValue();
            startDate = DateToolkit.parseISO8601FromLocalDate(selectedStartDate);
            LocalDate selectedEndDate = dpEndDate.getValue().plusDays(1);
            endDate = DateToolkit.parseISO8601FromLocalDate(selectedEndDate);
            profitsEarned = 0;
            categories.clear();
            categoriesCount.clear();
            salesAuctionsList.clear();
            salesDates.clear();
            salesDatesAmounts.clear();
            salesDatesCount.clear();
            loadSalesAuctions();
        }
    }

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
        Stage baseStage = (Stage)pcSalesAuctionsCategories.getScene().getWindow();

        baseStage.setScene(Navigation.startScene("views/CreatedAuctionsListView.fxml"));
        baseStage.setTitle("Subastas Creadas");
        baseStage.show();
    }
}
