package sebfisch.cheat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public record Clients(String host, int port, int clientCount,
        List<String> frequentCommands, List<String> lessFrequentCommands,
        ExecutorService executor, SplittableRandom random)
        implements Closeable {

    public static void main(String[] args) {
        try (Clients clients = new Clients(args)) {
            clients.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private Clients(String[] args) throws IOException {
        this(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    private Clients(String host, int port, int clientCount) throws IOException {
        this(host, port, clientCount,
                UnixCommands.readFrequent(), UnixCommands.readLessFrequent(),
                Executors.newVirtualThreadPerTaskExecutor(), new SplittableRandom());
    }

    private void start() throws IOException {
        IntStream.range(0, clientCount).forEach(unused -> executor.submit(this::runClient));
    }

    public void runClient() {
        try (Socket socket
                = new Socket(host, port) //
                ; BufferedReader reader
                = new BufferedReader(new InputStreamReader(socket.getInputStream())) //
                ; PrintWriter writer
                = new PrintWriter(socket.getOutputStream(), true)) {
            writer.println(randomCommand());
            reader.lines().forEach(System.out::println);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private String randomCommand() {
        final List<String> commandList
                = random.nextInt(10) < 8
                ? frequentCommands
                : lessFrequentCommands;

        return commandList.get(random.nextInt(commandList.size()));
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
