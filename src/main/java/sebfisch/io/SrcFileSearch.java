package sebfisch.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SrcFileSearch {

    record FileMatches(Path fileName, List<String> matchingLines) {

        static IO<FileMatches> from(Path fileName, Predicate<String> match) {
            try (Stream<String> lines = Files.lines(fileName)) {
                return new IO.Result<>(new FileMatches(fileName, lines.filter(match).toList()));
            } catch (IOException e) {
                return new IO.Error<>(e);
            }
        }
    }

    public static void main(final String[] args) {
        final Path srcPath = Path.of("src");
        final String regExp = "public static[^=]*\\(";
        final Predicate<String> containsMatch = Pattern.compile(regExp).asPredicate();

        try (Stream<Path> javaFiles = walkJavaFiles(srcPath)) {
            javaFiles
                    .map(Path::toAbsolutePath)
                    .map(file -> FileMatches.from(file, containsMatch))
                    .peek(io -> io.ifError(e -> System.err.println(e.getMessage())))
                    .mapMulti(IO<FileMatches>::ifResult)
                    .filter(matches -> !matches.matchingLines().isEmpty())
                    .peek(matches -> System.out.println(matches.fileName()))
                    .map(FileMatches::matchingLines)
                    .mapMulti(List::forEach)
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static Stream<Path> walkJavaFiles(final Path root) throws IOException {
        return Files.walk(root) //
                .filter(Files::isReadable) //
                .filter(path -> path.toString().endsWith(".java"));
    }
}
