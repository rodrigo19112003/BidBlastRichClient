/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package bidblastrichclient.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaView;

/**
 * FXML Controller class
 *
 * @author dnava
 */
public class EvaluateItemViewController implements Initializable {

    @FXML
    private ImageView imgReturnToPreviousPage;
    @FXML
    private HBox hbImageCarrusel;
    @FXML
    private MediaView mvVideoPlayer;
    @FXML
    private Label lblAuctionTitle;
    @FXML
    private Label lblAuctionTitle2;
    @FXML
    private Label lblAuctionBasePrice;
    @FXML
    private Label lblAuctionTitle11;
    @FXML
    private Label lblAuctionTitle12;
    @FXML
    private Label lblAuctionTitle21;
    @FXML
    private Label lblAuctionTitle211;
    @FXML
    private Label lblAuctionTitle2111;
    @FXML
    private Label lblAuctionDaysAvailable;
    @FXML
    private Label lblAuctionState;
    @FXML
    private Label lblAuctionTitle131;
    @FXML
    private Label lblMiniumBid;
    @FXML
    private Label lblAuctionTitle13111;
    @FXML
    private Label lblAuctionTitle21111;
    @FXML
    private ComboBox<?> cbxCategory;
    @FXML
    private Label lblAuctionTitle211111;
    @FXML
    private Label lblAuctionDescription;
    @FXML
    private Label lblAuctionTitle211112;
    @FXML
    private ComboBox<?> cbxAuctions;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void imgReturnToPreviousPageClick(MouseEvent event) {
    }

    @FXML
    private void btnChooseCategoryClick(ActionEvent event) {
    }

    @FXML
    private void btnAproveClick(ActionEvent event) {
    }

    @FXML
    private void btnDenyClick(ActionEvent event) {
    }
    
}
