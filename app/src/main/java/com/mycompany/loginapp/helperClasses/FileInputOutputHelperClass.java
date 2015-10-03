package com.mycompany.loginapp.helperClasses;

import com.google.common.io.Files;
import com.mycompany.loginapp.utilities.Utilities;

import java.io.File;

/**
 * Created by Alexander on 06-09-2015.
 */
public class FileInputOutputHelperClass {

    /**
     * Reads the image file and decodes it into an byte array
     */
    public static byte[] readBytesFromImageFile(File imageFile) {
        byte[] data = null;
        try {
            data = Files.toByteArray(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Reads the image file and decodes it into an byte array
     */
    public static void writeBytesToImageFile(byte[] data, File imageFile) {
        try {
            Files.write(data, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
