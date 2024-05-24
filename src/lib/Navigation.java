package lib;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import bidblastrichclient.BidBlastRichClient;
import java.io.IOException;

public class Navigation {
    public static Scene startScene(String ruta) {
        Scene scene = null;
        
        try {
            Parent view = FXMLLoader.load(BidBlastRichClient.class.getResource(ruta));
            scene = new Scene(view);
        } catch (IOException ex) {
            System.err.println("ERROR, " + ex.toString());
        }
        
        return scene;
    }
}
