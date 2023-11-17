package sebfisch.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import sebfisch.util.Partial;

public class SrcFileSearch {

    record FileMatches(Path fileName, List<String> matchingLines) {

        static Partial<FileMatches, IOException> from(Path fileName, Predicate<String> isMatching) {
            try (Stream<String> lines = Files.lines(fileName)) {
                List<String> matchingLines = lines.filter(isMatching).toList();
                return new Partial.Success<>(new FileMatches(fileName, matchingLines));
            } catch (IOException e) {
                return new Partial.Failure<>(e);
            }
        }
    }

    public static void main(final String[] args) {
        final Path srcPath = Path.of("src");
        final String regExp = "sealed interface";
        final Predicate<String> isMatching = Pattern.compile(regExp).asPredicate();

        try (Stream<Path> srcFiles = Files.walk(srcPath).filter(Files::isRegularFile)) {
            srcFiles
                    .map(Path::toAbsolutePath)
                    .map(file -> FileMatches.from(file, isMatching))
                    .peek(partial -> partial.ifFailure(System.err::println))
                    .mapMulti(Partial<FileMatches, IOException>::ifSuccess)
                    .filter(matches -> !matches.matchingLines().isEmpty())
                    .peek(matches -> System.out.println(matches.fileName()))
                    .map(FileMatches::matchingLines)
                    .mapMulti(List::forEach)
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
