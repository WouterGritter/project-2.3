package project23.util;

import org.apache.commons.io.output.TeeOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private Logger() {
    }

    public static boolean DEBUG = true;

    private static PrintStream fileStream;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (fileStream != null) {
                info("Closing log-file stream");
                fileStream.flush();
                fileStream.close();
            }
        }));
    }

    public static void setWriteToFile(File file) {
        if (fileStream != null) {
            return;
        }

        if (file.exists()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H-m-s");
            String[] parts = file.getName().split("\\.");
            String targetName = parts[0] + " " + sdf.format(new Date()) + "." + parts[1];

            File target = new File(file.getParentFile(), targetName);

            System.out.println("COPYING " + file + " TO " + targetName);

            try {
                Files.move(file.toPath(), target.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Start the streams
        try {
            fileStream = new PrintStream(new FileOutputStream(file));

            TeeOutputStream newOut = new TeeOutputStream(System.out, fileStream);
            TeeOutputStream newErr = new TeeOutputStream(System.err, fileStream);

            System.setOut(new PrintStream(newOut, true));
            System.setErr(new PrintStream(newErr, true));
        } catch (FileNotFoundException e) {
            warning("Could not start logging to file '" + file.getName() + "'!");
            e.printStackTrace();
        }
    }

    public static void info(String msg) {
        out("INFO", msg);
    }

    public static void debug(String msg) {
        if (DEBUG) {
            out("DEBUG", msg);
        }
    }

    public static void warning(String msg) {
        err("WARN", msg);
    }

    public static void error(String msg) {
        err("ERROR", msg);
    }

    private static void out(String type, String msg) {
        log(System.out, type, msg);
    }

    private static void err(String type, String msg) {
        log(System.err, type, msg);
    }

    private static void log(PrintStream stream, String type, String msg) {
        stream.format("[%tT] [%s] %s%n", System.currentTimeMillis(), type, msg);
    }
}
