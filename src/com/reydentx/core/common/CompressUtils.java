/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author ducnt3
 */
public class CompressUtils {
    
    public static byte[] gzipString(String str) throws IOException {
        try {
            if (str == null || str.length() == 0) {
                return null;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream(str.length());
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());

            gzip.close();
            out.close();

            return out.toByteArray(); // I would return compressedBytes instead String
        } finally {
        }
    }

    public static String fromGzip(byte[] bytes) {
        try {
            if (bytes == null || bytes.length == 0) {
                return "";
            }

            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
            BufferedReader bf = new BufferedReader(new InputStreamReader(gis));
            String outStr = "";
            String line;
            while ((line = bf.readLine()) != null) {
                outStr += line;
            }
            return outStr;
        } catch (Exception ex) {
            return "";
        }
    }

}
