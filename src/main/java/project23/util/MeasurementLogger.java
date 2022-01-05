package project23.util;

import project23.framework.player.MinimaxAIPlayer;

import java.io.*;

public class MeasurementLogger {

    private MeasurementLogger() {
    }

    private static final File LOG_FOLDER = new File("measurements/");

    static {
        if (!LOG_FOLDER.exists()) {
            LOG_FOLDER.mkdir();
        }

        for (File file : LOG_FOLDER.listFiles()) {
            file.delete();
        }
    }

    public static void logTime(MinimaxAIPlayer.AIDifficulty difficulty, long ms) {
        try {
            File file = new File(LOG_FOLDER, "time-" + difficulty + ".txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            writer.write(String.valueOf(ms));
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void logDepth(MinimaxAIPlayer.AIDifficulty difficulty, int depth) {
        try {
            File file = new File(LOG_FOLDER, "depth-" + difficulty + ".txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            writer.write(String.valueOf(depth));
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
