package by.vetumac.learn.concurent;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Operations {

    public static void main(String[] args) throws InterruptedException {
        final Account a = new Account(1000);
        final Account b = new Account(2000);
        final Random random = new Random();
        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        final ScheduledExecutorService schedulerService = Executors.newScheduledThreadPool(1);

        for (int i = 0; i < 10; i++) {
            executorService.submit(new Transfer(a, b, random.nextInt(400)));
        }

        schedulerService.scheduleAtFixedRate(() -> {
            System.out.println("A - " + a.getFailCounter());
            System.out.println("B - " + b.getFailCounter());
        }, 1, 1, TimeUnit.SECONDS);

        executorService.shutdown();
        executorService.awaitTermination(11, TimeUnit.SECONDS);

    }
}

