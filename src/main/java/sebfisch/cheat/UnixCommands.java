package sebfisch.cheat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class UnixCommands {

    public static List<String> readFrequent() throws IOException {
        return readResource("/commands/frequent.txt");
    }

    public static List<String> readLessFrequent() throws IOException {
        return readResource("/commands/less_frequent.txt");
    }

    private static List<String> readResource(String resourcePath) throws IOException {
        InputStream input = UnixCommands.class.getResourceAsStream(resourcePath);
        if (input == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            return reader.lines().toList();
        }
    }

    private UnixCommands() {
    }
}
