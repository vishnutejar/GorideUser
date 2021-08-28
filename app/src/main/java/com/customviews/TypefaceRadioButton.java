package com.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.fonts.FontUtils;
import com.quickzetuser.R;



/**
 * CustomRadioButton
 * Use for set custom font from .xml file
 */
public class  TypefaceRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {


    public TypefaceRadioButton(Context context) {
        super(context);
    }

    public TypefaceRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        inIt(context, attrs);
    }

    public TypefaceRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inIt(context, attrs);
    }

    /*@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TypefaceRadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inIt(context, attrs);
    }
*/
    /**
     * Initialize and set all required param from this method
     * parsing custom style tags here
     *
     * @param context
     * @param attrs
     */
    private void inIt(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            if (attrs != null) {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TypefaceRadioButton);
                String fontName = a.getString(R.styleable.TypefaceRadioButton_custom_font);
                Typeface typeface = FontUtils.getInstance().getFont(context, fontName);
                if (typeface != null) {
                    setTypeface(typeface);
                }
            }
        }
    }
}
