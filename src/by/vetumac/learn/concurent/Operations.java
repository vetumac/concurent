package by.vetumac.learn.concurent;

import java.util.concurrent.TimeUnit;

public class Operations {

    public static void main(String[] args) {
        final Account a = new Account(1000);
        final Account b = new Account(2000);

        new Thread(() -> {
            transfer(a, b, 500);
        }).start();

        transfer(b, a, 500);
    }

    public static void transfer(Account accFrom, Account accTo, int amount) {
        final int WAIT_SEC = 10;

        System.out.println("Transfer start");

        if (accFrom.getBalance() < amount) throw new InsufficiepnFoundException();

        try {
            if (accFrom.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                System.out.println("Synchronized From");
                try {
                    if (accTo.getLock().tryLock(WAIT_SEC, TimeUnit.SECONDS)) {
                        try {
                            System.out.println("Synchronized To");
                            accFrom.withdraw(amount);
                            accTo.deposit(amount);
                        } finally {
                            accTo.getLock().unlock();
                        }
                    } else {
                        throw new WaitLockException();
                    }
                } finally {
                    accFrom.getLock().unlock();
                }
            } else {
                throw new WaitLockException();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Transfer complete");
    }


}

