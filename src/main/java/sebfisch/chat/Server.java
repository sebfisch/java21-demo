package sebfisch.chat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public record Server(ServerSocket socket, Collection<User> users)
        implements Closeable {

    @Override
    public void close() throws IOException {
        socket.close();
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
        this(new ServerSocket(0), new ConcurrentLinkedQueue<>());
    }

    private void start() throws IOException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        while (!socket.isClosed()) {
            User user = new User(this, socket.accept());
            users.add(user);
            executor.submit(user);
        }

        executor.shutdown();
    }

    private Stream<User> otherUsers(User user) {
        return users.stream().filter(u -> u != user);
    }

    public record User(Server server, Socket client, PrintWriter writer)
            implements Callable {

        private User(Server server, Socket client) throws IOException {
            this(server, client, new PrintWriter(client.getOutputStream()));
        }

        @Override
        public Void call() throws IOException {
            // receive fake greeting from other users
            server.otherUsers(this).forEach(user -> user.send("hi", writer));
            // send greeting to other users
            send("hi");

            try (Socket socket = client; BufferedReader reader
                    = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                reader.lines().forEach(this::send);
            } finally {
                send("bye");
                server.users.remove(this);
            }

            return null;
        }

        private void send(String msg) {
            server.otherUsers(this).map(User::writer).forEach(other -> send(msg, other));
        }

        private void send(String msg, PrintWriter other) {
            other.println("[%s:%s] %s".formatted(
                    client.getInetAddress().getHostName(),
                    client.getPort(),
                    msg
            ));
            other.flush();
        }
    }
}
