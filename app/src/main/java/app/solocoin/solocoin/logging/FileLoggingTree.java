package app.solocoin.solocoin.logging;

import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {


    private static final String LOG_TAG = FileLoggingTree.class.getSimpleName();

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
//        try {
//            String path = "Log";
//            String fileNameTimeStamp = new SimpleDateFormat("dd-MM-yyyy",
//                    Locale.getDefault()).format(new Date());
//            String logTimeStamp = new SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
//                    Locale.getDefault()).format(new Date());
//            String fileName = fileNameTimeStamp + ".html";
//
//            // Create file
//            File file  = generateFile(path, fileName,fileNameTimeStamp,logTimeStamp);
//
//            // If file created or exists save logs
//            if (file != null) {
//                FileWriter writer = new FileWriter(file, true);
//
//                if (null!=t){
//                    writer.append("<p style=\"background:lightgray;\"><strong "
//                            + "style=\"background:lightblue;\">&nbsp&nbsp")
//                            .append(logTimeStamp)
//                            .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
//                            .append(tag)
//                            .append("</strong> - ")
//                            .append(message)
//                            .append("</p>")
//                            .append(t.toString())
//                    ;
//                }
//                writer.append("<p style=\"background:lightgray;\"><strong "
//                        + "style=\"background:lightblue;\">&nbsp&nbsp")
//                        .append(logTimeStamp)
//                        .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
//                        .append(tag)
//                        .append("</strong> - ")
//                        .append(message)
//                        .append("</p>")
//               ;
//                writer.flush();
//                writer.close();
//            }
//        } catch (Exception e) {
//            Timber.e( "Error while logging into file : " + e );
//        }
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.getLineNumber();
    }

    /*  Helper method to create file*/
    @Nullable
    private static File generateFile(@NonNull String path, @NonNull String fileName, String fileNameTimeStamp, String logTimeStamp) {
        File file = null;
        if (isExternalStorageAvailable()) {
            File root =  new File( Environment.getExternalStorageDirectory() ,"/Solocoin/Logs");

            boolean dirExists = true;

            if (!root.exists()) {
                dirExists = root.mkdirs();

            }

            if (dirExists) {
                file = new File(root, fileName);
                if (getFileSizeMegaBytes(file)>5){
                    String fileNam = fileNameTimeStamp+"(backup)"+logTimeStamp;
                    String tempfileName = fileNam + ".html";
                    File tempFile = new File(root, tempfileName);
                    file.renameTo(tempFile);
                }else {
                    file = new File(root, fileName);
                }
            }
        }

        return file;

    }


    /* Helper method to determine if external storage is available*/
    private static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState());

    }

    private static double getFileSizeMegaBytes(File file) {
        return (double) (file.length() / (1024 * 1024));
    }
}