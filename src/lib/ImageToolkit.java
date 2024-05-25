package lib;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class ImageToolkit {
    public static Image decodeBase64ToImage(String base64Content) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Content);
        return new Image(new ByteArrayInputStream(imageBytes));
    }
}
