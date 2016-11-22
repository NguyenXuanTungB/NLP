package com.example.framgia.natuallanguageprocessing.util;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import com.example.framgia.natuallanguageprocessing.R;

import java.util.Locale;

/**
 * Created by framgia on 21/11/2016.
 */
public class MethodUtil {
    public static boolean checkString(String inSubString, String inChap){
        String subString= inSubString.trim();
        String chap=inChap.trim();
        String subArray[]= subString.split(" ");
        int index=0;
        String chapArray[]= chap.split(" ");
        for(int i=0;i<chapArray.length;i++){
            if (subArray[index].equals(chapArray[i])){
                index++;
            }
            if (index==subArray.length)
                return true;
        }
        return false;
    }

}
