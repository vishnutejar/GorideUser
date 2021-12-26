package com.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;



import com.goride.user.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class  OtpView extends LinearLayout {
    private EditText currentlyFocusedEditText;
    private List<EditText> editTexts = new ArrayList<>();
    private int length;
    private OtpListener otpListener;
    private String stylesOtp;

    public OtpView(Context context) {
        super(context);
        init(null);
    }

    public OtpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OtpView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray styles = getContext().obtainStyledAttributes(attrs, R.styleable.OtpView);
        styleEditTexts(styles);
        styles.recycle();
    }

    /**
     * Get an instance of the present otp
     */
    private String makeOTP() {
        StringBuilder stringBuilder = new StringBuilder();
        for (EditText editText : editTexts) {
            stringBuilder.append(editText.getText());
        }
        return stringBuilder.toString();
    }

    /**
     * Checks if all four fields have been filled
     *
     * @return length of OTP
     */
    public boolean hasValidOTP() {
        return makeOTP().length() == length;
    }

    /**
     * Returns the present otp entered by the user
     *
     * @return OTP
     */
    public String getOTP() {
        return makeOTP();
    }

    /**
     * Sets the listener that will be used to call onOtpEntered when the user has completed entering the otp
     */
    public void setListener(OtpListener otpListener) {
        this.otpListener = otpListener;
    }

    /**
     * Used to set the OTP. More of cosmetic value than functional value
     *
     * @param otp Send the four digit otp
     */
    public void setOTP(String otp) {
        if (otp != null) {
            if (otp.isEmpty()) {
                for (int i = 0; i < editTexts.size(); i++) {
                    editTexts.get(i).setText("");
                }
                currentlyFocusedEditText = editTexts.get(0);
                currentlyFocusedEditText.setEnabled(true);
                currentlyFocusedEditText.requestFocus();
            } else if (otp.length() != length) {
                throw new IllegalArgumentException("Otp Size is different from the OtpView size");
            } else {
                for (int i = 0; i < editTexts.size(); i++) {
                    editTexts.get(i).setText(String.valueOf(otp.charAt(i)));
                }
                currentlyFocusedEditText = editTexts.get(length - 1);
                currentlyFocusedEditText.setEnabled(true);
                currentlyFocusedEditText.requestFocus();

            }
        }
    }

    private void styleEditTexts(TypedArray styles) {
        length = styles.getInt(R.styleable.OtpView_length, 4);
        generateViews(styles);
    }

    private void generateViews(TypedArray styles) {
        if (length > 0) {
            int width = (int) styles.getDimension(R.styleable.OtpView_width, getPixels(48));
            int height = (int) styles.getDimension(R.styleable.OtpView_height, getPixels(48));
            int space = (int) styles.getDimension(R.styleable.OtpView_space, getPixels(0));
            int spaceLeft = (int) styles.getDimension(R.styleable.OtpView_space_left, getPixels(4));
            int spaceRight = (int) styles.getDimension(R.styleable.OtpView_space_right, getPixels(4));
            int spaceTop = (int) styles.getDimension(R.styleable.OtpView_space_top, getPixels(4));
            int spaceBottom = (int) styles.getDimension(R.styleable.OtpView_space_bottom, getPixels(4));
            LayoutParams params =
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (space > 0) {
                int spaceInPx = getPixels(space);
                params.setMargins(spaceInPx, spaceInPx, spaceInPx, spaceInPx);
            } else {
                params.setMargins(getPixels(spaceLeft), getPixels(spaceRight), getPixels(spaceTop),
                        getPixels(spaceBottom));
            }
            InputFilter[] filter = new InputFilter[]{getFilter(), new InputFilter.LengthFilter(1)};
            int textColor = styles.getColor(R.styleable.OtpView_android_textColor, Color.BLACK);
            int backgroundColor =
                    styles.getColor(R.styleable.OtpView_text_background_color, Color.TRANSPARENT);
            int inputType = styles.getInt(R.styleable.OtpView_android_inputType,
                    EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
            int hintColor = styles.getColor(R.styleable.OtpView_hint_color, Color.GRAY);
            String hint = styles.getString(R.styleable.OtpView_hint);
            stylesOtp = styles.getString(R.styleable.OtpView_otp);
            for (int i = 0; i < length; i++) {
                EditText editText = new EditText(getContext());
                editText.setId(i);
                editText.setSingleLine();
                editText.setWidth(width);
                editText.setHeight(height);
                editText.setGravity(Gravity.CENTER_HORIZONTAL);
                editText.setMaxLines(1);
                editText.setFilters(filter);
                editText.setLayoutParams(params);
                if (backgroundColor != Color.TRANSPARENT) {
                    editText.setBackgroundColor(backgroundColor);
                } else {
                    editText.getBackground().mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
                }
                editText.setHintTextColor(hintColor);
                editText.setHint(hint);
                editText.setTextColor(textColor);
                editText.setInputType(inputType);
                setFocusListener(editText);
                setOnTextChangeListener(editText);
                setOnKeyListener(editText);
                editText.setEnabled(false);

                addView(editText, i);
                editTexts.add(editText);
            }
            currentlyFocusedEditText = editTexts.get(0);
            currentlyFocusedEditText.setEnabled(true);
            currentlyFocusedEditText.requestFocus();


            setOTP(stylesOtp);
        } else {
            throw new IllegalStateException("Please specify the length of the otp view");
        }
    }

    private void setOnKeyListener(EditText editText) {
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DEL) {

                    if (currentlyFocusedEditText != null && currentlyFocusedEditText.length() == 0) {
                        if (currentlyFocusedEditText == editTexts.get(0)) {
                           /* if (imm != null) {
                                //imm.hideSoftInputFromWindow(getWindowToken(), 0);
                            }*/
                        } else {
                            int leftFocusPosition = 0;
                            for (int i = 0; i < editTexts.size(); i++) {
                                if (currentlyFocusedEditText == editTexts.get(i)) {
                                    leftFocusPosition = i - 1;
                                }
                            }
                            final EditText editText1 = editTexts.get(leftFocusPosition);
                            editText1.setEnabled(true);
                            editText1.requestFocus();
                        }
                        return true;

                    }
                }
                return false;
            }
        });
    }

    private InputFilter getFilter() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {
                for (int i = start; i < end; ++i) {
                    if (!Pattern.compile(
                            "[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*")
                            .matcher(String.valueOf(source.charAt(i)))
                            .matches()) {
                        return "";
                    }
                }
                return null;
            }
        };
    }

    private int getPixels(int valueInDp) {
        Resources r = getResources();
        float px =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, r.getDisplayMetrics());
        return (int) px;
    }

    private void setFocusListener(EditText editText) {
        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentlyFocusedEditText = (EditText) v;
                    currentlyFocusedEditText.setEnabled(true);
                    currentlyFocusedEditText.setSelection(currentlyFocusedEditText.getText().length());
                    updateEnabledState();
                }

            }
        };
        editText.setOnFocusChangeListener(onFocusChangeListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void disableKeypad() {
        OnTouchListener touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm =
                        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        };
        for (EditText editText : editTexts) {
            editText.setOnTouchListener(touchListener);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void enableKeypad() {
        OnTouchListener touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        };
        for (EditText editText : editTexts) {
            editText.setOnTouchListener(touchListener);
        }
    }

    public EditText getCurrentFoucusedEditText() {
        return currentlyFocusedEditText;
    }

    private void setOnTextChangeListener(final EditText editText) {

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int editTextLength = currentlyFocusedEditText.getText().toString().length();
                InputMethodManager imm =
                        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (editTextLength == 0) {
                    if (currentlyFocusedEditText == editTexts.get(0)) {
                        if (imm != null) {
                            //imm.hideSoftInputFromWindow(getWindowToken(), 0);
                        }
                    } else {
                        int leftFocusPosition = 0;
                        for (int i = 0; i < editTexts.size(); i++) {
                            if (currentlyFocusedEditText == editTexts.get(i)) {
                                leftFocusPosition = i - 1;
                            }
                        }
                        final EditText editText1 = editTexts.get(leftFocusPosition);
                        editText1.setEnabled(true);
                        editText1.requestFocus();

                    }
                } else if (editTextLength == 1) {
                    if (currentlyFocusedEditText == editTexts.get(editTexts.size() - 1)) {
                        if (imm != null) {
                            //  imm.hideSoftInputFromWindow(getWindowToken(), 0);
                        }
                    } else {
                        int rightFocusPosition = 0;
                        for (int i = 0; i < editTexts.size(); i++) {
                            if (currentlyFocusedEditText == editTexts.get(i)) {
                                rightFocusPosition = i + 1;
                            }
                        }
                        final EditText editText1 = editTexts.get(rightFocusPosition);
                        editText1.setEnabled(true);
                        editText1.requestFocus();
                    }
                }
                if (otpListener != null && getOTP().length() == length) {
                    otpListener.onOtpEntered(getOTP());
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
    }

    public void simulateDeletePress() {
        currentlyFocusedEditText.setText("");
    }

    public interface OtpListener {
        void onOtpEntered(String otp);
    }

    private void updateEnabledState() {
        for (int i = 0; i < length; i++) {
            if (currentlyFocusedEditText != editTexts.get(i)) {
                editTexts.get(i).setEnabled(false);
            }
        }
        currentlyFocusedEditText.selectAll();

    }
}