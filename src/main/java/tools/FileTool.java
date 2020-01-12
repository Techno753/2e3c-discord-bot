package tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

/**
 * Manages general file download and access
 */
public final class FileTool {

    /**
     * Downloads an image from a URL
     * @param src URL source of image
     * @param dest Where to save image including file name
     * @return  1 - Image successfully downloaded
     *          -2 - Error downloading image
     */
    public static int downloadFileByURL(String src, String dest) {
        try {
            // https://cdn.discordapp.com/attachments/99218035843301376/665864870331416597/586280686298857502.png
            URL url = new URL(src);

            // Try to get image data as bytearray
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (InputStream inputStream = url.openStream()) {
                int n;
                byte [] buffer = new byte[1024];
                while ((n = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, n);
                }
            } catch (Exception e) {
                return -2;  // Error getting data from image URL
            }

            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(output.toByteArray());
            } catch (Exception e) {
                return -3;  // Error writing image data to file
            }
            return 1;
        } catch (Exception e) {
            System.out.println("Error downloading file.");
            System.out.println(e);
            return -1;
        }
    }

    public static File getLocalImage(String src) {
        try {
            return new File(src);
        } catch (Exception e) {
            System.out.println("File not found.");
        }
        return null;
    }

    public static int deleteFile(String src) {
        File file = getLocalImage(src);
        if (file.delete()) {
            return 1;   // File successfully deleted
        }
        return -1;  // Failed to delete file
    }
}
