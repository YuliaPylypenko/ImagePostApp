package com.example.BlogWebSite.constant;

public class ValidationConstants {
    public static final String USERNAME_REGEXP =
            "^(?!.*\\.\\.)(?!.*\\.$)(?!.*\\-\\-)"
                    + "(?=[ЄІЇҐЁА-ЯA-Z])"
                    + "[ЄІЇҐЁєіїґёА-Яа-яA-Za-z0-9\\s-'’.\\\"]"
                    + "{1,30}"
                    + "(?<![ЭэЁёъЪЫы])$";

    public static final String PASSWORD_REGEXP = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=])(?!.*\\s).{8,20}$";

    public static final String USERNAME_MESSAGE =
            "The name ${validatedValue} cannot be empty, "
                    + "starts with a number or not a capital letter, "
                    + "ends with dot, "
                    + "contain 2 consecutive dots or dashes and symbols like @#$. "
                    + "Use English or Ukrainian letters, "
                    + "no longer than 30 symbols, "
                    + "the name ${validatedValue} could contain numbers, symbols '’, "
                    + "dot in the middle of the name, dash and whitespaces.";

    public static final String INVALID_EMAIL = "{validation.invalid.email}";
    private ValidationConstants() {
    }
}
