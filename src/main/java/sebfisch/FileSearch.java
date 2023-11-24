package sebfisch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import sebfisch.util.Partial;

public class FileSearch {

    public static void main(final String[] args) {
        final Path folderPath = Path.of(args[0]);
        final String extension = args[1];
        final Predicate<String> isMatching = Pattern.compile(args[2]).asPredicate();

        try (Stream<Path> files = Files.walk(folderPath)) {
            files
                    .filter(Files::isRegularFile)
                    .filter(filePath -> filePath.toString().endsWith(extension))
                    .map(Path::toAbsolutePath)
                    .map(filePath -> FileMatches.from(filePath, isMatching))
                    .peek(partial -> partial.ifFailure(System.err::println))
                    .mapMulti(Partial<FileMatches, IOException>::ifSuccess)
                    .filter(matches -> !matches.matchingLines().isEmpty())
                    .peek(matches -> System.out.println(matches.filePath()))
                    .map(FileMatches::matchingLines)
                    .mapMulti(List::forEach)
                    .forEach(System.out::println);
        } catch (IOException | UncheckedIOException e) {
            System.err.println(e);
        }
    }

    private record FileMatches(Path filePath, List<String> matchingLines) {

        static Partial<FileMatches, IOException> from(Path filePath, Predicate<String> isMatching) {
            try (Stream<String> lines = Files.lines(filePath)) {
                List<String> matchingLines = lines.filter(isMatching).toList();
                return new Partial.Success<>(new FileMatches(filePath, matchingLines));
            } catch (IOException e) {
                return new Partial.Failure<>(e);
            }
        }
    }
}
