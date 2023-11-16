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
                    STR."communication error: \{msg}";
                case HttpError(var status) ->
                    STR."HTTP response with status: \{status}";
            };
        }
    }

    public record CommunicationError(String message) implements Error {

    }

    public record HttpError(int status) implements Error {

    }
}
