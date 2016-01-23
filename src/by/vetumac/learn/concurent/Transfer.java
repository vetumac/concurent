package by.vetumac.learn.concurent;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Алексей on 23.01.2016.
 */
public class Transfer implements Callable<Boolean> {


    private final Account accFrom;
    private final Account accTo;
    private final int amount;
    private final Random random;

    public Transfer(Account accFrom, Account accTo, int amount) {
        this.accFrom = accFrom;
        this.accTo = accTo;
        this.amount = amount;
        this.random = new Random();
    }

    @Override
    public Boolean call() throws Exception {
        final int WAIT_SEC = 10;

        System.out.println("Transfer start");

        try {
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
                        } finally {
                            accTo.getLock().unlock();
                        }
                    } else {
                        accFrom.incFailedTransferCount();
                        accTo.incFailedTransferCount();
                        return false;
                    }
                } finally {
                    accFrom.getLock().unlock();
                }
            } else {
                accFrom.incFailedTransferCount();
                accTo.incFailedTransferCount();
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Transfer complete");
        return true;
    }
}
