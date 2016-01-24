package by.vetumac.learn.concurent;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * Created by Алексей on 23.01.2016.
 */
public class Transfer implements Callable<Boolean> {


    private final Account accFrom;
    private final Account accTo;
    private final int amount;
    private final Random random;
    private final CountDownLatch countDownLatch;
    private final CyclicBarrier cyclicBarrier;

    public Transfer(Account accFrom, Account accTo, int amount, CountDownLatch countDownLatch, CyclicBarrier cyclicBarrier) {
        this.accFrom = accFrom;
        this.accTo = accTo;
        this.amount = amount;
        this.cyclicBarrier = cyclicBarrier;
        this.countDownLatch = countDownLatch;
        this.random = new Random();
    }

    @Override
    public Boolean call() throws Exception {
        final int WAIT_SEC = 10;

        countDownLatch.await();

        System.out.println("Transfer start");
        Boolean needRepeat = true;

        try {
            while (needRepeat) {
                if (accFrom.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                    System.out.println("Synchronized From");
                    Thread.sleep(random.nextInt(5000));
                    if (accFrom.getBalance() < amount) throw new InsufficiepnFoundException();
                    try {
                        if (accTo.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                            try {
                                System.out.println("Synchronized To");
                                Thread.sleep(random.nextInt(10000));
                                accFrom.withdraw(amount);
                                accTo.deposit(amount);
                                needRepeat = false;
                            } finally {
                                accTo.getLock().unlock();
                            }
                        } else {
                            accFrom.incFailedTransferCount();
                            accTo.incFailedTransferCount();
                        }
                    } finally {
                        accFrom.getLock().unlock();
                    }
                } else {
                    accFrom.incFailedTransferCount();
                    accTo.incFailedTransferCount();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cyclicBarrier.await();

        System.out.println("Transfer complete");
        return true;
    }
}
