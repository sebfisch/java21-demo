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

public record Proxy(
        ServerSocket socket, ExecutorService executor)
        implements Closeable {

    public static void main(String[] args) {
        try (Proxy server = new Proxy()) {
            server.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Proxy  {
        System.out.println("listening on port %s".formatted(socket.getLocalPort()));
    }

    private Proxy() throws IOException {
        this(new ServerSocket(0),
                Executors.newVirtualThreadPerTaskExecutor()
        );
    }

    private void start() throws IOException {
        while (!socket.isClosed()) {
            serve(socket.accept());
        }
    }

    private void serve(Socket client) {
        executor.submit(() -> {
            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(client.getInputStream())) //
                    ; PrintWriter writer
                    = new PrintWriter(client.getOutputStream(), true)) {
                reader.lines().forEach(command -> {
                    switch (Request.send(command)) {
                        case Response.Ok(var body) ->
                            writer.println(body);
                        case Response.Error e ->
                            writer.println(e.message());
                    }
                });
            } finally {
                client.close();
            }

            return null;
        });
    }

    @Override
    public void close() throws IOException {
        socket.close();
        executor.shutdown();
    }
}
