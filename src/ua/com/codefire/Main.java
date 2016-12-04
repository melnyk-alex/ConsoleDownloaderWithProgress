package ua.com.codefire;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final int PROGRESS_SIZE = 60;

    public static void main(String[] args) throws InterruptedException {
        List<String> downloadingList = new ArrayList<>();
        downloadingList.add("http://ichef.bbci.co.uk/naturelibrary/images/ic/credit/640x395/m/mo/mountain/mountain_1.jpg");
        downloadingList.add("http://www.hd-wallpapersdownload.com/download/images-of-tigers-in-the-wild-2560x1440/");

        //http://www.hd-wallpapersdownload.com/download/images-of-tigers-in-the-wild-2560x1440/
//        String downloadAddress = "http://ichef.bbci.co.uk/naturelibrary/images/ic/credit/640x395/m/mo/mountain/mountain_1.jpg";

        if (args.length > 0) {
            downloadingList.addAll(Arrays.asList(args));
        } else {
            System.out.println("Nothing to download, please specify addresses...");
        }

        int index = 1;

        for (String downloadAddress : downloadingList) {
            try {
                URL url = new URL(downloadAddress);

                URLConnection conn = url.openConnection();
                long length = conn.getContentLengthLong();

                InputStream inputStream = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(String.format("image_%05d.jpg", index++));

                long downloaded = 0L;
                // _tb - time begin
                // _tc - time complete
                long time_begin = 0;

                // 8KB
                byte[] buffer = new byte[8192];

                time_begin = System.currentTimeMillis();
                double bandwidth = 0.0;

                while (inputStream.available() >= 0) {
                    int read = inputStream.read(buffer);
                    downloaded += read;

                    long time = System.currentTimeMillis() - time_begin;

                    if (read < 0) {
                        break;
                    }

                    bandwidth = ((double) read / time);

                    // Clear period
                    time_begin = System.currentTimeMillis();

                    fos.write(buffer, 0, read);
                    fos.flush();

                    if (length < 0) {
                        System.out.printf("\rDOWNLOADED: (%8.2f KiB/s) %6s%% [%08d bytes]", bandwidth, "n/a  ", downloaded);
                    } else {
                        double progress = ((double) downloaded * 100 / length);
                        int curr = (int) (progress * PROGRESS_SIZE / 100);

                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < curr; i++) {
                            sb.append("=");
                        }

                        if (curr < PROGRESS_SIZE - 1) {
                            sb.append(">");
                        }


                        System.out.printf("\r[%-" + PROGRESS_SIZE + "s] %6.2f%% (%.2f KiB/s)", sb, progress, bandwidth);

//                        System.out.printf("\rDOWNLOADED: (%8.2f KiB/s) %6.2f%% [%08d bytes]", bandwidth, progress, downloaded);
                    }

                    Thread.sleep(500);
                }

                System.out.printf("\n%s\nFile downloaded!\nTotal: %d bytes\n\n", downloadAddress, downloaded);

                inputStream.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
