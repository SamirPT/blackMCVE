package tech.peller.mrblackandroidwatch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Sam (salyasov@gmail.com) on 31.05.2018
 */
public class Logger {
    public static String readAndSaveLog() {
        StringBuilder log = new StringBuilder();
        try {
            Process logcat = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
            BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()),4*1024);
            String line;
            String separator = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) {
                log.append("ANDROID_WATCH_LOG ");
                log.append(line);
                log.append(separator);
            }
        } catch (Exception e) {
            String separator = System.getProperty("line.separator");
            log.append("ANDROID_WATCH_LOG_LOGGER_EXCEPTION ");
            log.append(e.getMessage());
            log.append(separator);
            e.printStackTrace();
        }

        clearLog();

        return log.toString();
    }

    public static void clearLog() {
        try {
            new ProcessBuilder()
                .command("logcat", "-c")
                .redirectErrorStream(true)
                .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
