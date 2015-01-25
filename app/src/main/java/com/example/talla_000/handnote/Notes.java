package com.example.talla_000.handnote;

import android.provider.BaseColumns;

import java.io.Serializable;
import java.sql.Blob;

/**
 * Created by talla_000 on 11/4/2014.
 */
public class Notes implements BaseColumns,Serializable {

    private static String Name;
    private static String Content;

    public static int getLength() {
        if(Content!=null) {
            return Content.length();
        } else{
            return 0;
        }
    }

    public static void setLength(int length) {
        Notes.length = length;
    }

    public static int length;


    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }



}
