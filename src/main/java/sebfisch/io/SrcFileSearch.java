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
                return new IO.Failure<>(e);
            }
        }

        void print() {
            System.out.println(fileName);
            matchingLines.forEach(System.out::println);
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
                    .mapMulti(IO<FileMatches>::onSuccess)
                    .filter(matches -> !matches.matchingLines().isEmpty())
                    .forEach(FileMatches::print);
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
