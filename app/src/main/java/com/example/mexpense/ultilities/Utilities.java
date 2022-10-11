package com.example.mexpense.ultilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utilities {

    private final static DateTimeFormatter android_format = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    private final static DateTimeFormatter sql_format = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_DATABASE);

    public static String convertDate(String source, boolean androidToSql) {
        if(androidToSql){
            LocalDate date = LocalDate.parse(source, android_format);
            return sql_format.format(date);
        }
        else{
            LocalDate date = LocalDate.parse(source, sql_format);
            return android_format.format(date);
        }
    }

    public static void hideInput(Activity activity, View view){
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(manager.isAcceptingText()){
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static byte[] bitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
