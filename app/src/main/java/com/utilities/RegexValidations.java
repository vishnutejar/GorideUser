package com.utilities;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidations {
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String PHONE_REGEX = "^[+]?[0-9]{10,10}$";
    private static final String VEHICLE_NUMBER_REGEX = "^[A-Z]{2}\\s[0-9]{2}\\s[A-Z]{2}\\s[0-9]{4}$";
    // private static final String PHONE_MSG = "+### ### ####";
    // Error Messages
    private static final String REQUIRED_MSG = "Required";
    private static final String MANDATORY_FIELD = "Please fill Mandatory fields.";
    private static final String INVALID_NAME_MSG = "Name should be 3 to 20 alphabetic characters only.";
    private static final String EMPTY_NAME_MSG = "Please enter name.";
    private static final String passwordlength_MSG = "Password length must between 4 to 20 ! ";
    private static final String EMAIL_MSG = "Please fill valid email id";
    private static final String VEHICLE_EMPTY_MSG = "Please enter vehicle number";
    private static final String VEHICLE_INVALID_MSG = "Please enter valid vehicle number";
    private static final String Phone_MSG = "Phone Number should be 10 digits";
    private static final String EMAIL_EMPTY_MSG = "Please enter email id";
    private static final String PHONE_EMPTY_MSG = "Please enter phone number";
    private static final String Password_MSG = "Password";
    public static final String PASSWORD_CONFIRM_MSG = "Please enter password to confirm";
    public static final String PASSWORD_VALIDATION_MSG = "You must be provide at least 6 to 30 characters for password.";
    public static final String PASSWORD_EMPTY_MSG = "Please enter password";
    private static final String Conform_MSG = "Confirm Password";
    private static final String ConformPasswordNotMatch_MSG = "Confirm Password Does Not Match";
    public static final int PASSWORD_LENGTH = 6;

    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, EMAIL_EMPTY_MSG, required);
    }

    public static boolean isValidVehicleNumber(EditText editText, boolean required) {
        return isValid(editText, VEHICLE_NUMBER_REGEX, VEHICLE_INVALID_MSG, VEHICLE_EMPTY_MSG, required);
    }

    // call this method when you need to check phone number validation

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex,
                                  String errMsg, String emptyMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError(emptyMsg);
            return false;
        }


        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }


        return true;
    }


   /* public static boolean isValidPassword(EditText password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password.getText().toString());
        return matcher.matches();

    }*/

    public static boolean isValidPassword(EditText editText) {
        boolean status = true;
        if (editText.getText().toString().length() < PASSWORD_LENGTH) {
            status = false;
            if (editText.getText().toString().length() == 0) {
                editText.setError(RegexValidations.PASSWORD_EMPTY_MSG);
            } else {
                editText.setError(RegexValidations.PASSWORD_VALIDATION_MSG);
            }
        }
        return status;
    }

    public static boolean isValidMobile(String phone, EditText editText) {
        boolean check = false;
        String text = editText.getText().toString().trim();
        if (!Pattern.matches("[a-zA-Z]+", text)) {
            if (phone.length() < 6 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
                editText.setError("Phone Number Not Valid");
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }


    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(MANDATORY_FIELD);
            return false;
        }

        return true;

    }

    public static boolean isValidName(EditText editText) {
        String text = editText.getText().toString();
        editText.setError(null);

        // length 0 means there is no text
        if (TextUtils.isEmpty(text)) {
            editText.setError(EMPTY_NAME_MSG);
            return false;
        }
        if (text.length() < 3 || text.length() > 20) {
            editText.setError(INVALID_NAME_MSG);
            return false;
        }

        return true;

    }

    public static boolean hasnull(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {

            return false;
        }

        return true;

    }


    public static boolean hasTextView(TextView textView) {

        String text = textView.getText().toString().trim();
        textView.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            textView.setError(REQUIRED_MSG);
            return false;
        }

        return true;

    }

    public static boolean conpaireText(EditText editTextTo,
                                       EditText editTextCompaireWith) {

        String To = editTextTo.getText().toString().trim();
        String from = editTextCompaireWith.getText().toString().trim();
        editTextTo.setError(null);
        editTextCompaireWith.setError(null);

        // length 0 means there is no text
        if (To.length() == 0 || from.length() == 0) {
            editTextTo.setError(Password_MSG);
            editTextCompaireWith.setError(Conform_MSG);
            return false;
        } else {
            if (To.length() > 5 && To.length() < 20 || from.length() > 5
                    && from.length() < 20) {

            } else {
                editTextTo.setError(passwordlength_MSG);
                editTextCompaireWith.setError(passwordlength_MSG);
                return false;
            }

            if (To.equals(from)) {
                return true;
            } else {

                editTextCompaireWith.setError(ConformPasswordNotMatch_MSG);
                return false;
            }
        }

    }


    public static boolean mobileno(EditText editTextTo
    ) {

        String To = editTextTo.getText().toString().trim();

        editTextTo.setError(null);


        // length 0 means there is no text
        if (To.length() == 0) {
            editTextTo.setError("Mobile");

            return false;
        } else {
            if (To.length() == 10) {

            } else {
                editTextTo.setError("Mobile No. length must have 10");

                return false;
            }

        }
        return false;

    }


    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValid(editText, PHONE_REGEX, Phone_MSG, PHONE_EMPTY_MSG, required);
    }

    public static boolean compairePhone(EditText editTextTo) {
        Boolean isvalid = false;
        String To = editTextTo.getText().toString().trim();
        editTextTo.setError(null);
        // length 0 means there is no text
        if (To.length() == 0) {
            editTextTo.setError(REQUIRED_MSG);
            return isvalid = false;
        } else {
            if (To.length() < 20) {
                isvalid = true;
            } else {
                editTextTo.setError(Phone_MSG);

                return isvalid = false;
            }
        }
        return isvalid;
    }

    public static String getFirst10Words(String arg) {
        Pattern pattern = Pattern.compile("([\\S]+\\s*){1,5}");
        Matcher matcher = pattern.matcher(arg);
        matcher.find();
        return matcher.group();
    }

}
