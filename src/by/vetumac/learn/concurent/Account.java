package by.vetumac.learn.concurent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Алексей on 23.01.2016.
 */
public class Account {

    private int balance;
    private Lock lock;

    public Lock getLock() {
        return lock;
    }

    public Account(int initBalance) {
        this.balance = initBalance;
        this.lock = new ReentrantLock();
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public int getBalance() {
        return balance;
    }
}
