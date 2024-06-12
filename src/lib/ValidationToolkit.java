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
    
    public static boolean areValidKeywords(String keywords){
        boolean areValidKeywords = false;

        if (keywords != null) {
            String keywordsPattern = "^(\\w+\\s*,\\s*){2,}\\w+$";

            Pattern pattern = Pattern.compile(keywordsPattern);
            Matcher matcher = pattern.matcher(keywords);

            areValidKeywords = matcher.matches();

        }

        return  areValidKeywords;
    }
    
    public static boolean isNumeric(String text) {
        boolean isNumeric = false;
        
        if (text != null) {
            String numericPattern = "^[0-9]+$";

            Pattern pattern = Pattern.compile(numericPattern);
            Matcher matcher = pattern.matcher(text);

            isNumeric = matcher.matches();
        }
        
        return isNumeric;
    }
    
    public static boolean isValidPassword(String password) {
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()\\-+=¿?_].*");
        boolean validLength = password.length() >= 8;
        return hasUppercase && hasNumber && hasSpecialChar && validLength;
    }
    
    public static boolean isPositiveFloat(String text) {
        boolean isFloat = false;
        
        if (text != null) {
            String floatPattern = "^([0-9]+([.][0-9])?|[.][0-9])$";
            
            Pattern pattern = Pattern.compile(floatPattern);
            Matcher matcher = pattern.matcher(text);

            isFloat = matcher.matches();
        }
        
        return isFloat;
    }
}
