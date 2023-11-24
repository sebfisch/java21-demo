package sebfisch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileSearch {

    public static void main(final String[] args) {
        final Path folderPath = Path.of(args[0]);
        final String extension = args[1];
        final Predicate<String> isMatching = Pattern.compile(args[2]).asPredicate();

        try (Stream<Path> files = Files.walk(folderPath)) {
            final Iterable<Path> iterableFiles = () -> files
                    .filter(Files::isRegularFile)
                    .filter(filePath -> filePath.toString().endsWith(extension))
                    .iterator();

            for (final Path file : iterableFiles) {
                final Path filePath = file.toAbsolutePath();

                final FileMatches matches;
                try {
                    matches = FileMatches.from(filePath, isMatching);
                } catch (IOException e) {
                    System.err.println(e);
                    continue;
                }

                if (matches.matchingLines().isEmpty()) {
                    continue;
                }

                System.out.println(matches.filePath());

                for (final String line : matches.matchingLines()) {
                    System.out.println(line);
                }
            }
        } catch (IOException | UncheckedIOException e) {
            System.err.println(e);
        }
    }

    private record FileMatches(Path filePath, List<String> matchingLines) {

        static FileMatches from(Path filePath, Predicate<String> isMatching) throws IOException {
            try (Stream<String> lines = Files.lines(filePath)) {
                List<String> matchingLines = lines.filter(isMatching).toList();
                return new FileMatches(filePath, matchingLines);
            }
        }
    }
}
