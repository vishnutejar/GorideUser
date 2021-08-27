package com.quickzetuser.ui.utilities;

import android.text.TextUtils;
import android.widget.EditText;

import com.quickzetuser.appBase.AppBaseActivity;

import java.util.regex.Pattern;


/**
 * @author Sunil kumar Yadav
 * @Since 21/5/18
 */
public class Validate {
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String EMAIL_MSG_EMPTY = "Please enter your email id.";
    private static final String EMAIL_MSG_INVALID = "You have entered an invalid email address.";

    private static final String MOBILE_REGEX = "^[+]?[0-9]{7,15}$";
    private static final String MOBILENUMBER_MSG_EMPTY = "Please enter your mobile number.";
    private static final String MOBILENUMBER_VALIDATION = "Mobile number should contain 10 digits.";
    private static final String MOBILENUMBER_MSG_INVALID = "You have entered an invalid mobile number.";

    private static final String EMAIL_OR_MOBILE_MSG_EMPTY = "Please enter your valid email / mobile number.";

    private static final String PASSWORD_MSG_EMPTY = "Please enter your password.";
    private static final String PASSWORD_MSG_INVALID = "Your password can't start or end with a blank space.";
    private static final String PASSWORD_MSG_INVALID_LENGTH = "You must be provide at least 6 to 30 characters for password.";

    private static final String CONFIRM_PASSWORD_MSG_EMPTY = "Please enter your confirm password.";
    private static final String CONFIRM_PASSWORD_MSG_INVALID = "Password and confirm password does not match.";

    private static final String OTP_MSG_EMPTY = "Please enter your OTP.";
    private static final String OTP_MSG_INVALID = "You have entered an invalid OTP.";

    private static final String FIRST_NAME_MSG_EMPTY = "Please enter your first name.";
    private static final String FIRST_NAME_MSG_INVALID = "Your first name can't start or end with a blank space.";
    private static final String FIRST_NAME_MSG_INVALID_LENGTH = "First name should be 3 to 20 Alphabetic Characters only.";

    private static final String LAST_NAME_MSG_EMPTY = "Please enter your last name.";
    private static final String LAST_NAME_MSG_INVALID = "Your last name can't start or end with a blank space.";
    private static final String LAST_NAME_MSG_INVALID_LENGTH = "Last name should be 3 to 20 Alphabetic Characters only.";


    private AppBaseActivity context;

    public Validate(AppBaseActivity context) {
        this.context = context;
    }

    public boolean validEmailOrMobile(EditText editText) {
        String emailOrMobile = editText.getText().toString().trim();
        if (emailOrMobile.length() == 0) {
            showErrorMessage(EMAIL_OR_MOBILE_MSG_EMPTY);
            return false;
        }

        if (TextUtils.isDigitsOnly(emailOrMobile)) {
            return validMobileNumber(editText);
        }
        return validEmailAddress(editText);
    }

    public boolean validEmailAddress(EditText editText) {
        String email = editText.getText().toString().trim();
        if (email.length() == 0) {
            showErrorMessage(EMAIL_MSG_EMPTY);
            return false;
        }
        return validEmailPattern(editText);
    }

    public boolean validEmailPattern(EditText editText) {
        String email = editText.getText().toString().trim();
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            showErrorMessage(EMAIL_MSG_INVALID);
            return false;
        }
        return true;
    }

    public boolean validMobileNumber(EditText editText) {
        String mobileNumber = editText.getText().toString().trim();
        if (mobileNumber.length() == 0) {
            showErrorMessage(MOBILENUMBER_MSG_EMPTY);
            return false;
        }
        if (mobileNumber.length() < 10 || mobileNumber.length()>10) {
            showErrorMessage(MOBILENUMBER_VALIDATION);
            return false;
        }

        return validMobilePattern(editText);
    }

    public boolean validMobilePattern(EditText editText){
        String mobileNumber = editText.getText().toString().trim();

        if (!Pattern.matches(MOBILE_REGEX, mobileNumber)) {
            showErrorMessage(MOBILENUMBER_MSG_INVALID);
            return false;
        }
        return true;
    }

    public boolean validPassword(EditText editText) {
        String password = editText.getText().toString();
        if (password.length() == 0) {
            showErrorMessage(PASSWORD_MSG_EMPTY);
            return false;
        }
        if (!password.equals(password.trim())) {
            showErrorMessage(PASSWORD_MSG_INVALID);
            return false;
        }
        return true;
    }

    public boolean validPasswordLength(EditText editText) {
        String password = editText.getText().toString();
        if (password.length() == 0) {
            showErrorMessage(PASSWORD_MSG_EMPTY);
            return false;
        }
        if (!password.equals(password.trim())) {
            showErrorMessage(PASSWORD_MSG_INVALID);
            return false;
        }
        if (password.length() < 6 || password.length() > 30) {
            showErrorMessage(PASSWORD_MSG_INVALID_LENGTH);
            return false;
        }
        return true;
    }

    public boolean validConfirmPassword(String password, EditText editText) {
        String confirmPassword = editText.getText().toString();
        if (confirmPassword.length() == 0) {
            showErrorMessage(CONFIRM_PASSWORD_MSG_EMPTY);
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showErrorMessage(CONFIRM_PASSWORD_MSG_INVALID);
            return false;
        }
        return true;
    }

    public boolean validOtp(String otp, int length) {
        if (otp.length() == 0) {
            showErrorMessage(OTP_MSG_EMPTY);
            return false;
        }
        if (otp.length() < length) {
            showErrorMessage(OTP_MSG_INVALID);
            return false;
        }
        return true;
    }

    public boolean validFirstName(EditText editText) {
        String firstName = editText.getText().toString();
        if (firstName.length() == 0) {
            showErrorMessage(FIRST_NAME_MSG_EMPTY);
            return false;
        }
        if (!firstName.equals(firstName.trim())) {
            showErrorMessage(FIRST_NAME_MSG_INVALID);
            return false;
        }
        if (firstName.length() < 3 || firstName.length() > 20) {
            showErrorMessage(FIRST_NAME_MSG_INVALID_LENGTH);
            return false;
        }
        return true;
    }

    public boolean validLastName(EditText editText) {
        String lastName = editText.getText().toString();
        if (lastName.length() == 0) {
            showErrorMessage(LAST_NAME_MSG_EMPTY);
            return false;
        }
        if (!lastName.equals(lastName.trim())) {
            showErrorMessage(LAST_NAME_MSG_INVALID);
            return false;
        }
        if (lastName.length() < 3 || lastName.length() > 20) {
            showErrorMessage(LAST_NAME_MSG_INVALID_LENGTH);
            return false;
        }
        return true;
    }

    public void showErrorMessage(String msg) {
        if (context == null) return;
        context.showErrorMessage(msg);
    }
}
