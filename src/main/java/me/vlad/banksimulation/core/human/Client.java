package me.vlad.banksimulation.core.human;

import me.vlad.banksimulation.core.Bank;
import me.vlad.banksimulation.core.Request;

import java.time.Duration;

public class Client extends People {
    public Client(String surname, String name, String patronymic) {
        super(surname, name, patronymic);
    }

    public void comeToBank(Bank bank, Duration serviceDuration, double price) {
        Request request = new Request(bank.getNumberRequest(), price, this, serviceDuration, bank.getCurrentDateTime());
        bank.registerRequest(request);
    }
}
