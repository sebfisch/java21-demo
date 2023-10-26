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
import java.util.concurrent.TimeUnit;

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
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private void serve(Socket client) {
        executor.submit(() -> {
            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(client.getInputStream())) //
                    ; PrintWriter writer
                    = new PrintWriter(client.getOutputStream(), true)) {
                writer.println(cache.computeIfAbsent(reader.readLine(), this::commandInfo));
            } finally {
                client.close();
            }

            return null;
        });
    }

    private String commandInfo(String command) {
        System.out.println("requesting info on %s".formatted(command));
        return switch (Request.send(command)) {
            case Response.Ok(var body) ->
                body;
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
