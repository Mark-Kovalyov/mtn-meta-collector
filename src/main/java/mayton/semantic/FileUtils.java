package mayton.semantic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.String.format;

public class FileUtils {

    @Nullable
    public static String getFileExtensionOrNull(@NotNull String filename) {
        int index = filename.lastIndexOf(".");
        return index == -1 ? null : filename.substring(index);
    }

    @NotNull
    public static String escapeControl(@NotNull String arg) {
        StringBuilder s = new StringBuilder(arg.length());
        for (int i = 0; i < arg.length(); i++) {
            char c = arg.charAt(i);
            if ((int) c < 32) {
                s.append(format("\\x%02X", (int) c));
            } else {
                s.append(c);
            }
        }
        return s.toString();
    }


}
