package sebfisch.echo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public record Server(ServerSocket socket, ExecutorService executor)
        implements Closeable {

    @Override
    public void close() throws IOException {
        socket.close();
        executor.shutdown();
    }

    public static void main(String[] args) {
        try (Server server = new Server()) {
            server.start();
        } catch (IOException | UncheckedIOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Server  {
        System.out.println("listening on port %s".formatted(socket.getLocalPort()));
    }

    private Server() throws IOException {
        this(new ServerSocket(0), Executors.newVirtualThreadPerTaskExecutor());
    }

    private void start() throws IOException {
        while (!socket.isClosed()) {
            serve(socket.accept());
        }
    }

    private void serve(Socket client) {
        executor.submit(() -> {
            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                reader.lines().forEach(System.out::println);
            } finally {
                client.close();
            }

            return null;
        });
    }
}
