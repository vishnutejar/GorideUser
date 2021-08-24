package com.fonts;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by ubuntu on 28/12/17.
 */

public class FontUtils {

    private static FontUtils instance;
    private HashMap<String, Typeface> fontsMap;

    public static FontUtils getInstance(){
        if (instance==null){
            instance=new FontUtils();
        }
        return instance;
    }

    private void makeHashMap(){
        if (fontsMap==null){
            fontsMap=new HashMap<>();
        }
    }

    public  Typeface getFont(Context context, String font){
        if (context==null || font==null || font.trim().isEmpty()){
            return null;
        }
        makeHashMap();
        if (fontsMap.containsKey(font)){
            return fontsMap.get(font);
        }
        else {
            Typeface typeface=Typeface.createFromAsset(context.getAssets(), font);
            fontsMap.put(font, typeface);
            return typeface;
        }
    }
}
