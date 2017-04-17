package classes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * used to validate password on registration
 */
public class PasswordValidator{

    private Pattern pattern;
    private Matcher matcher;

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)" + // Checks for number
                    "(?=.*[A-z])" + // Checks for letter
                    "(?=.*[*?~!@-_#$%£])" + //Checks for special character
                    ".{8,16})"; // Checks length

    public PasswordValidator(){
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    public boolean validate(final String password){

        matcher = pattern.matcher(password);
        return matcher.matches();

    }
}
