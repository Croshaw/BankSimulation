package me.vlad.banksimulation.core.human;

import me.vlad.banksimulation.core.Bank;

import java.time.Duration;

public class Client extends People {
    public Client(String surname, String name, String patronymic) {
        super(surname, name, patronymic);
    }
    public void comeToBank(Bank bank, Duration serviceDuration, double price) {
        bank.registerRequest(serviceDuration, this, price);
    }
}
