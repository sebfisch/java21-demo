package sebfisch.cheat;

public sealed interface Response {

    public record Ok(String body) implements Response {

    }

    public record Timeout() implements Response {
    }

    public sealed interface Error extends Response permits CommunicationError, HttpError {

        default String message() {
            return switch (this) {
                case CommunicationError(var msg) ->
                    "communication error: %s".formatted(msg);
                case HttpError(var status) ->
                    "HTTP response with status: %s".formatted(status);
            };
        }
    }

    public record CommunicationError(String message) implements Error {

    }

    public record HttpError(int status) implements Error {

    }
}
