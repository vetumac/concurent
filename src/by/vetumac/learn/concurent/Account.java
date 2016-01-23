package by.vetumac.learn.concurent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Алексей on 23.01.2016.
 */
public class Account {

    private int balance;
    private Lock lock;

    private AtomicInteger failCounter;

    public Lock getLock() {
        return lock;
    }

    public Account(int initBalance) {
        this.balance = initBalance;
        this.lock = new ReentrantLock();
        this.failCounter = new AtomicInteger();
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void incFailedTransferCount() {
        failCounter.incrementAndGet();
    }

    public int getBalance() {
        return balance;
    }

    public AtomicInteger getFailCounter() {
        return failCounter;
    }
}
