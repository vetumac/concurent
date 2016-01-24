package by.vetumac.learn.concurent;

import javafx.util.Duration;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.*;

public class Operations {

    public static void main(String[] args) throws InterruptedException {
        final int N = 10;
        final Account a = new Account(1000);
        final Account b = new Account(2000);
        final Random random = new Random();
        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        final ScheduledExecutorService schedulerService = Executors.newScheduledThreadPool(1);
        final CountDownLatch countDownLatchAB = new CountDownLatch(0);
        final CyclicBarrier cyclicBarrierAB = new CyclicBarrier(1);
        final Instant before;
        final Instant[] after = new Instant[1];
        final CyclicBarrier cyclicBarrierBA = new CyclicBarrier(N, () -> after[0] = Instant.now());

        for (int i = 0; i < N; i++) {
            executorService.submit(new Transfer(a, b, random.nextInt(400), countDownLatchAB, cyclicBarrierAB));
        }

        final CountDownLatch countDownLatchBA = new CountDownLatch(1);

        for (int i = 0; i < N; i++) {
            executorService.submit(new Transfer(b, a, random.nextInt(400), countDownLatchBA, cyclicBarrierBA));
        }

        before = Instant.now();
        countDownLatchBA.countDown();

        schedulerService.scheduleAtFixedRate(() -> {
            System.out.println("A - " + a.getFailCounter());
            System.out.println("B - " + b.getFailCounter());
        }, 1, 1, TimeUnit.SECONDS);

        executorService.shutdown();
        executorService.awaitTermination(1100, TimeUnit.SECONDS);

        System.out.println(new Duration(after[0].toEpochMilli() - before.toEpochMilli()));
    }
}

