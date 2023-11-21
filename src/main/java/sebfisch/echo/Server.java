package sebfisch.echo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.StringTemplate.STR;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public record Server(
        ServerSocket socket, ExecutorService executor, RecentlyAccessed<String> recentMessages)
        implements Closeable {

    public static void main(String[] args) {
        try (Server server = new Server()) {
            server.handleInput();
            server.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Server   {
        System.out.println(STR."listening on port \{socket.getLocalPort()}");
    }

    private Server() throws IOException {
        this(new ServerSocket(0),
                Executors.newVirtualThreadPerTaskExecutor(),
                new RecentlyAccessed<>(20)
        );
    }

    private void handleInput() {
        executor.submit(() -> {
            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(System.in))//
                    ; PrintWriter writer
                    = new PrintWriter(System.out, true)) {
                reader.lines().forEach(unused -> {
                    System.out.println("recent messages:");
                    recentMessages.elements().forEach(writer::println);
                });
            }
            return null;
        });
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
                reader.lines().forEach(msg -> {
                    recentMessages.add(msg);
                    writer.println(msg);
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
