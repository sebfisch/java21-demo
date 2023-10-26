package sebfisch.cheat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class Request {

    private static final Object LOCK = new Object();

    public static Response send(final String command) {
        synchronized (LOCK) {
            final HttpRequest request = HttpRequest.newBuilder() //
                    .setHeader("User-Agent", "curl") //
                    .uri(URI.create("https://cheat.sh/" + command + "?qT")) //
                    .build();
            final HttpClient client = HttpClient.newBuilder() //
                    .connectTimeout(Duration.ofSeconds(5)) //
                    .build();

            try {
                TimeUnit.SECONDS.sleep(1); // allow at most one request per second
                final HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

                if (response.statusCode() != HTTP_STATUS_OK) {
                    return new Response.HttpError(response.statusCode());
                }

                return new Response.Ok(response.body());
            } catch (IOException | InterruptedException e) {
                return new Response.CommunicationError(e.getMessage());
            }
        }
    }

    private static final int HTTP_STATUS_OK = 200;

    private Request() {
    }
}