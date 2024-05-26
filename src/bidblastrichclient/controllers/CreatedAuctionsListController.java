package bidblastrichclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import model.Auction;

public class CreatedAuctionsListController implements Initializable {

    @FXML
    private TextField tfAuctionToSearch;
    @FXML
    private TableView<Auction> tvProposedAuctions;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnProposed;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnProposed;
    @FXML
    private TableColumn<Auction, String> colProposedTime;
    @FXML
    private TableColumn<Auction, String> colBasePrice;
    @FXML
    private TableColumn<Auction, String> colMinimumBid;
    @FXML
    private TableView<Auction> tvPublishedAuctions;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnPublished;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnPublished;
    @FXML
    private TableColumn<Auction, String> colTimeLeft;
    @FXML
    private TableColumn<Auction, String> colLastOffer;
    @FXML
    private TableView<Auction> tvSoldAuctions;
    @FXML
    private TableColumn<Auction, Image> colAuctioneerAvatar;
    @FXML
    private TableColumn<Auction, String> colAuctioneerFullName;
    @FXML
    private TableColumn<Auction, String> colEmail;
    @FXML
    private TableColumn<Auction, String> colPhoneNumber;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnSold;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnSold;
    @FXML
    private TableColumn<Auction, String> colSaleDate;
    @FXML
    private TableColumn<Auction, String> colPrice;
    @FXML
    private TableView<Auction> tvClosedAuctions;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnClosed;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnClosed;
    @FXML
    private TableColumn<Auction, String> colDeadline;
    @FXML
    private TableColumn<Auction, String> colStateMessage;
    @FXML
    private TableView<Auction> tvRejectedAuctions;
    @FXML
    private TableColumn<Auction, Image> colAuctionImageOnRejected;
    @FXML
    private TableColumn<Auction, String> colAuctionTitleOnRejected;
    @FXML
    private TableColumn<Auction, String> colRejectionDate;
    @FXML
    private TableColumn<Auction, String> colComments;
    @FXML
    private TextField tfLimit;
    @FXML
    private TextField tfOffset;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void btnConsultSalesStatisticsClick(ActionEvent event) {
    }

    @FXML
    private void imgSearchAuctionClick(MouseEvent event) {
    }

    @FXML
    private void btnSeeOffersMade(ActionEvent event) {
    }

    @FXML
    private void imgCopyEmailClick(MouseEvent event) {
    }

    @FXML
    private void imgCopyPhoneNumberClick(MouseEvent event) {
    }
    
}
