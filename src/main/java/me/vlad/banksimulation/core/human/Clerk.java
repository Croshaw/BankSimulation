package me.vlad.banksimulation.core.human;

import me.vlad.banksimulation.core.Bank;
import me.vlad.banksimulation.core.Request;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Clerk extends People {
    private double dailySalary;
    private final LocalDateTime dateTimeOfHire;
    private Request currentTask;
    private Duration currentDuration;
    private final HashMap<LocalDate, Duration> employment;
    private final HashMap<LocalDate, ArrayList<Request>> completedRequests;
    private final HashMap<LocalDate, ArrayList<Request>> lostsRequests;
    public Clerk(String surname, String name, String patronymic, double dailySalary, LocalDateTime dateTimeOfHire) {
        super(surname, name, patronymic);
        this.dailySalary = dailySalary;
        this.dateTimeOfHire = dateTimeOfHire;
        currentDuration = Duration.ZERO;
        /*TODO Переделать
        currentTask = null;*/
        employment = new HashMap<>();
        employment.put(dateTimeOfHire.toLocalDate(), Duration.ZERO);
        completedRequests = new HashMap<>();
        completedRequests.put(dateTimeOfHire.toLocalDate(), new ArrayList<>());
        lostsRequests = new HashMap<>();
        lostsRequests.put(dateTimeOfHire.toLocalDate(), new ArrayList<>());
    }
    public long work(long secondsStep, Bank bank) {
        if(currentTask == null || currentTask.isDone()) {
            if(currentTask != null) {
                if(!completedRequests.containsKey(getCurrentDate()))
                    completedRequests.put(getCurrentDate(), new ArrayList<>());
                completedRequests.get(getCurrentDate()).add(currentTask);
            }
            takeRequest(bank);
        }
        long remainder = currentTask.service(secondsStep);
        currentDuration = currentDuration.plusSeconds(secondsStep - remainder);
        employment.put(getCurrentDate(), employment.getOrDefault(getCurrentDate(), Duration.ZERO).plusSeconds(secondsStep - remainder));
        return remainder;
    }
    public void takeRequest(Bank bank) {
        //TODO Дописать
    }
    public void takeRequest(Request request) {
        currentTask = request;
    }
    private LocalDate getCurrentDate() {
        return getCurrentDateTime().toLocalDate();
    }
    private LocalDateTime getCurrentDateTime() {
        return dateTimeOfHire.plus(currentDuration);
    }
}