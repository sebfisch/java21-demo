package sebfisch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// shuf -r -i 1-10 -n 100000 | ./java.sh sebfisch.SleepSort
public record SleepSort(ExecutorService executor) {

    public static void main(String[] args) {
        new SleepSort().sortInputs();
    }

    private SleepSort() {
        // this(Executors.newFixedThreadPool(10));
        // this(Executors.newThreadPerTaskExecutor(Thread::new));
        this(Executors.newVirtualThreadPerTaskExecutor());
    }

    private void sortInputs() {
        try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {
            stdin.lines().map(Integer::parseInt).forEach(num -> {
                executor.submit(() -> {
                    TimeUnit.SECONDS.sleep(num);
                    System.out.println(num);
                    return null;
                });
            });

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (IOException | InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
