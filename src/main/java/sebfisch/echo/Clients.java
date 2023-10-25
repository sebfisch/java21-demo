package sebfisch.echo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public record Clients(
        String host, int port, int clientCount, int messageCount,
        ExecutorService executor, SplittableRandom random) implements Closeable {

    public static void main(String[] args) {
        try (Clients clients = new Clients(args)) {
            clients.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void sleepRandomly(SplittableRandom rnd, int origin, int bound)
            throws InterruptedException {
        Thread.sleep(rnd.nextInt(origin, bound));
    }

    Clients(String[] args) {
        this(args[0],
                Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])
        );
    }

    Clients(String host, int port, int clientCount, int messageCount) {
        this(host, port, clientCount, messageCount,
                Executors.newVirtualThreadPerTaskExecutor(),
                new SplittableRandom()
        );
    }

    private void start() throws IOException {
        IntStream.range(0, clientCount).forEach(clientNum -> {
            executor.submit(() -> {
                runClient(clientNum);
                return null;
            });
        });
    }

    public void runClient(int clientNum) throws IOException, InterruptedException {
        try (Socket socket
                = new Socket(host, port) //
                ; PrintWriter writer
                = new PrintWriter(socket.getOutputStream(), true) //
                ; BufferedReader reader
                = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            final SplittableRandom rnd = random.split();
            sleepRandomly(rnd, 0, 1000);
            for (int messageNum = 0; messageNum < messageCount; messageNum++) {
                final String message = "%s - Client %d: Message %d".formatted(
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                        clientNum,
                        messageNum
                );
                writer.println(message);
                assert message.equals(reader.readLine());
                sleepRandomly(rnd, 0, 2000);
            }
        }
    }

    @Override
    public void close() {
        try {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            executor.shutdownNow();
        }
    }
}
