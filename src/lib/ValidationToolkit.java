package lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationToolkit {
    public static boolean isValidEmail(String text) {
        boolean isValidEmail = false;

        if (text != null) {
            String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

            Pattern pattern = Pattern.compile(emailPattern);
            Matcher matcher = pattern.matcher(text);

            isValidEmail = matcher.matches();
        }

        return isValidEmail;
    }
}
