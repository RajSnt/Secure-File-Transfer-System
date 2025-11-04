package Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtil {
    public static byte[] readFileToBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public static void writeBytesToFile(byte[] data, File file) throws IOException {
        Files.write(file.toPath(), data);
    }

    public static boolean ensureParentDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            return parent.mkdirs();
        }
        return true;
    }
}
