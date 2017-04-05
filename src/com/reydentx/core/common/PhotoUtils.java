/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;

/**
 *
 * @author ducnt3
 */
public class PhotoUtils {

        public static byte[] fromBlobString(String blobImage) {
                if (blobImage.startsWith("data:image")) {
                        String imgData = blobImage.substring(blobImage.indexOf(",") + 1, blobImage.length()) + "";
                        return Base64.decodeBase64(imgData);
                }

                return null;
        }

        public static boolean isValidImage(byte[] data) {
                if (isPNG(data)) {
                        return true;
                } else {
                        if (isValidJPEG(data) && !isBlackJPEG(data)) {
                                return true;
                        }
                }
                return false;
        }

        public static boolean isPNG(byte[] data) {
                if (data == null || data.length < 8) {
                        return false;
                }

                if (data[0] == (byte) 0x89 && data[1] == (byte) 0x50 && data[2] == (byte) 0x4E && data[3] == (byte) 0x47 && data[4] == (byte) 0x0D
                        && data[5] == (byte) 0x0A && data[6] == (byte) 0x1A && data[7] == (byte) 0x0A) {
                        return true;
                }
                return false;
        }

        public static boolean isValidJPEG(byte[] data) {
                if (data == null || data.length < 4) {
                        return false;
                }

                int totalBytes = data.length;

                // valid SOI
                if (data[0] == (byte) 0xff && data[1] == (byte) 0xd8) {
                        // valid EOI
                        if (data[totalBytes - 2] == (byte) 0xff && data[totalBytes - 1] == (byte) 0xd9) {
                                return true;
                        }
                        // ignore last 2 bytes
                        if (data[totalBytes - 4] == (byte) 0xff && data[totalBytes - 3] == (byte) 0xd9) {
                                return true;
                        }
                        if (data[2] == (byte) 0xff && data[3] == (byte) 0xe0) { // JPEG File
                                // Interchange
                                // Format
                                return true;
                        }
                }
                return false;
        }

        public static boolean isBlackJPEG(byte[] data) {
                try {
                        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data));
                        return isBlackJPEG(bi);
                } catch (Exception e) {
                }

                return true;
        }

        public static boolean isBlackJPEG(BufferedImage bi) {
                if (bi != null) {
                        int height = bi.getHeight();
                        int width = bi.getWidth();

                        for (int x = 0; x < width; x++) {
                                for (int y = 0; y < height; y++) {
                                        if (bi.getRGB(x, y) != -16777216) {
                                                return false;
                                        }
                                }
                        }
                }

                return true;
        }

        public static byte[] resizeImageScaleCrop(InputStream data, int img_width, int img_height, boolean isPNG) {
                BufferedImage originalImage;
                try {
                        originalImage = ImageIO.read(data);
                        Dimension origDimentsion = new Dimension(originalImage.getWidth(), originalImage.getHeight());
                        Dimension fitDimentsion = new Dimension(img_width, img_height);

                        int flag = testThresholdMinWidthHeightImage(origDimentsion, fitDimentsion);
                        // size Anh dang: MinW < W < 720, MinH < H < 720.
                        if (flag == -1) {
                                data.reset();
                                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                                byte[] read = new byte[2048];
                                int i = 0;
                                while ((i = data.read(read)) > 0) {
                                        byteArray.write(read, 0, i);
                                }
                                data.close();
                                return byteArray.toByteArray();
                        } else if (flag == 0) {
                                // size Anh dang: MinW < W < 720 < H || MinH < H < 720 < W
                                double ratioWidth = (origDimentsion.width * 1.0) / fitDimentsion.width;
                                double ratioHeight = (origDimentsion.height * 1.0) / fitDimentsion.height;
                                if (ratioWidth < ratioHeight) {
                                        // fit width, crop height image.
                                        int yH = 0;
                                        if (origDimentsion.height > fitDimentsion.height) {
                                                yH = (origDimentsion.height - fitDimentsion.height) / 2;
                                        }
                                        return cropBufferedImage(isPNG, originalImage, 0, yH, origDimentsion.width, fitDimentsion.height);
                                } else {
                                        // fit height, crop width image.
                                        int xW = 0;
                                        if (origDimentsion.width > fitDimentsion.width) {
                                                xW = (origDimentsion.width - fitDimentsion.width) / 2;
                                        }
                                        return cropBufferedImage(isPNG, originalImage, xW, 0, fitDimentsion.width, origDimentsion.height);
                                }
                        } else {
                                // size Anh dang: 720 < W,H.
                                // Scale Image.
                                double ratioWidth = (origDimentsion.width * 1.0) / fitDimentsion.width;
                                double ratioHeight = (origDimentsion.height * 1.0) / fitDimentsion.height;
                                if (ratioWidth < ratioHeight) {
                                        // fit width, crop height image.
                                        int new_width = fitDimentsion.width;
                                        int new_height = (int) (origDimentsion.height / ratioWidth);
                                        int yH = 0;
                                        if (new_height > fitDimentsion.height) {
                                                yH = (new_height - fitDimentsion.height) / 2;
                                        }

                                        byte[] scaleByteImage = scaleBufferedImage(isPNG, originalImage, 0, 0, new_width, new_height);
                                        if (scaleByteImage != null && scaleByteImage.length > 0) {
                                                InputStream isImg = new ByteArrayInputStream(scaleByteImage);
                                                BufferedImage scaleBufferedImage = ImageIO.read(isImg);

                                                // Crop width image.
                                                return cropBufferedImage(isPNG, scaleBufferedImage, 0, yH, fitDimentsion.width, fitDimentsion.height);
                                        }
                                } else {
                                        // fit height, crop width image.
                                        int new_width = (int) (origDimentsion.width / ratioHeight);
                                        int new_height = fitDimentsion.height;
                                        int xW = 0;
                                        if (new_width > fitDimentsion.width) {
                                                xW = (new_width - fitDimentsion.width) / 2;
                                        }

                                        byte[] scaleByteImage = scaleBufferedImage(isPNG, originalImage, 0, 0, new_width, new_height);
                                        if (scaleByteImage != null && scaleByteImage.length > 0) {
                                                InputStream isImg = new ByteArrayInputStream(scaleByteImage);
                                                BufferedImage scaleBufferedImage = ImageIO.read(isImg);

                                                // Crop height image.
                                                return cropBufferedImage(isPNG, scaleBufferedImage, xW, 0, fitDimentsion.width, fitDimentsion.height);
                                        }
                                }
                        }
                } catch (Exception ex) {
                }
                return null;
        }

        public static byte[] scaleBufferedImage(boolean isPNG, BufferedImage originalImage, int x, int y, int width, int height) {
                byte[] imageFinal = null;
                try {
                        if (originalImage != null) {
                                int type = (originalImage.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
                                BufferedImage resizedImage = new BufferedImage(width, height, type);
                                Graphics2D g = resizedImage.createGraphics();
                                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                                g.drawImage(originalImage, x, y, width, height, null);
                                g.dispose();

                                if (isPNG) {
                                        ImageIO.write(resizedImage, "png", outstream);
                                } else {
                                        ImageIO.write(resizedImage, "jpg", outstream);
                                }
                                imageFinal = outstream.toByteArray();
                        }
                } catch (IOException ex) {
                }
                return imageFinal;
        }

        public static byte[] cropBufferedImage(boolean isPNG, BufferedImage originalImage, int x, int y, int width, int height)
                throws IOException {
                byte[] imageFinal = null;
                if (originalImage != null) {
                        // step 1: crop
                        BufferedImage thumbnail = Scalr.crop(originalImage, x, y, width, height, Scalr.OP_ANTIALIAS);

                        // step 2. convert BufferedImage to byte[].
                        String extension = "";
                        if (isPNG == true) {
                                extension = "png";
                        } else {
                                extension = "jpg";
                        }
                        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                        ImageIO.write(thumbnail, extension, outstream);
                        outstream.flush();
                        imageFinal = outstream.toByteArray();
                        outstream.close();
                }
                return imageFinal;
        }

        private static int testThresholdMinWidthHeightImage(Dimension imgSize, Dimension boundary) {
                int rs = -1;
                if (imgSize.width <= boundary.width && imgSize.height <= boundary.height) {
                        rs = -1;
                } else if ((imgSize.width <= boundary.width && imgSize.height > boundary.height)
                        || (imgSize.width > boundary.width && imgSize.height <= boundary.height)) {
                        rs = 0;
                } else if (imgSize.width > boundary.width && imgSize.height > boundary.height) {
                        rs = 1;
                }
                return rs;
        }

        public static byte[] converImagePNGtoJPEG(InputStream data) {
                byte[] imageFinal = null;
                try {
                        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                        BufferedImage bufferedImage = ImageIO.read(data);

                        // create a blank, RGB, same width and height, and a white background
                        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                                BufferedImage.TYPE_INT_RGB);
                        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
                        ImageIO.write(newBufferedImage, "JPEG", outstream);
                        imageFinal = outstream.toByteArray();
                        outstream.close();
                } catch (Exception ex) {
                }

                return imageFinal;
        }

        public static byte[] resizeImage(ByteArrayInputStream data, int img_width, int img_height, boolean isPNG) {
                BufferedImage originalImage;
                try {
                        originalImage = ImageIO.read(data);
                        Dimension origDimentsion = new Dimension(originalImage.getWidth(), originalImage.getHeight());
                        Dimension fitDimentsion = new Dimension(img_width, img_height);

                        // Dimension dimentsion = getScaledDimension(origDimentsion, fitDimentsion);
                        Dimension dimentsion = fitDimentsion;
                        if (origDimentsion.width != dimentsion.width || origDimentsion.height != dimentsion.height) {

                                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                                BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT, dimentsion.width,
                                        dimentsion.height, Scalr.OP_ANTIALIAS);

                                if (isPNG) {
                                        ImageIO.write(resizedImage, "png", outstream);
                                } else {
                                        ImageIO.write(resizedImage, "jpg", outstream);
                                }

                                return outstream.toByteArray();
                        } else {
                                data.reset();
                                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                                byte[] read = new byte[2048];
                                int i = 0;
                                while ((i = data.read(read)) > 0) {
                                        byteArray.write(read, 0, i);
                                }
                                data.close();
                                return byteArray.toByteArray();
                        }
                } catch (Exception ex) {
                }
                return null;
        }

        public static byte[] waterMarkJPG(String baseImagePath, String waterMartPath) {
                try {
                        File origFile = new File(baseImagePath);
                        ImageIcon icon = new ImageIcon(origFile.getPath());
                        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(),
                                icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics graphics = bufferedImage.getGraphics();
                        graphics.drawImage(icon.getImage(), 0, 0, null);

                        ImageIcon png = new ImageIcon(waterMartPath);
                        graphics.drawImage(png.getImage(), 0, 0, null);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "jpg", baos);
                        baos.flush();
                        byte[] imageInByte = baos.toByteArray();

                        return imageInByte;
                } catch (IOException e) {
                        e.printStackTrace();
                }

                return null;
        }
}
