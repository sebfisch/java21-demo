package sebfisch.cheat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sebfisch.util.MostRecentlyUsed;

public record Proxy(
        ServerSocket socket, ExecutorService executor, MostRecentlyUsed<String, String> cache)
        implements Closeable {

    public static void main(String[] args) {
        try (Proxy server = new Proxy()) {
            server.handleInput();
            server.start();
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public Proxy   {
        System.out.println("listening on port %s".formatted(socket.getLocalPort()));
    }

    private Proxy() throws IOException {
        this(new ServerSocket(0),
                Executors.newVirtualThreadPerTaskExecutor(),
                new MostRecentlyUsed<>(40)
        );
    }

    private void handleInput() {
        executor.submit(() -> {
            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(System.in))//
                    ; PrintWriter writer
                    = new PrintWriter(System.out, true)) {
                reader.lines().forEach(ignored -> {
                    System.out.println("%d recent commands:".formatted(cache.entries().size()));
                    cache.entries().keySet().forEach(writer::println);
                });
            }
            return null;
        });
    }

    private void start() throws IOException, InterruptedException {
        while (!socket.isClosed()) {
            serve(socket.accept());
        }
    }

    private void serve(Socket client) {
        System.out.println("connection from port %d".formatted(client.getPort()));
        executor.submit(() -> {
            System.out.println("serving connection from port %d".formatted(client.getPort()));
            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(client.getInputStream())) //
                    ; PrintWriter writer
                    = new PrintWriter(client.getOutputStream(), true)) {
                writer.println(cache.computeIfAbsent(reader.readLine(), this::requestCommandInfo));
            } finally {
                client.close();
            }

            return null;
        });
    }

    private String requestCommandInfo(String command) {
        return switch (Request.send(command)) {
            case Response.Ok(var body) ->
                body;
            case Response.Timeout() ->
                requestCommandInfo(command); // try again
            case Response.Error e ->
                e.message();
        };
    }

    @Override
    public void close() throws IOException {
        socket.close();
        executor.shutdown();
    }
}
