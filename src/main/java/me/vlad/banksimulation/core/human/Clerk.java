package me.vlad.banksimulation.core.human;

import me.vlad.banksimulation.core.Bank;
import me.vlad.banksimulation.core.Request;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Clerk extends People {
    private double dailySalary;
    private final LocalDateTime dateTimeOfHire;
    private Request currentTask;
    private Duration currentDuration;
    private final HashMap<LocalDate, Duration> employment;
    private final HashMap<LocalDate, ArrayList<Request>> completedRequests;
    public Clerk(String surname, String name, String patronymic, double dailySalary, LocalDateTime dateTimeOfHire) {
        super(surname, name, patronymic);
        this.dailySalary = dailySalary;
        this.dateTimeOfHire = dateTimeOfHire;
        currentDuration = Duration.ZERO;
        currentTask = Request.NULL;
        employment = new HashMap<>();
        employment.put(dateTimeOfHire.toLocalDate(), Duration.ZERO);
        completedRequests = new HashMap<>();
        completedRequests.put(dateTimeOfHire.toLocalDate(), new ArrayList<>());
    }
    public long work(long secondsStep, Bank bank) {
        if(currentTask == Request.NULL || currentTask.isDone()) {
            if(currentTask != Request.NULL) {
                if(!completedRequests.containsKey(getCurrentDate()))
                    completedRequests.put(getCurrentDate(), new ArrayList<>());
                completedRequests.get(getCurrentDate()).add(currentTask);
                currentTask = Request.NULL;
            }
            if(!takeRequest(bank)) {
                return 0;
            } else {
                currentTask.waiting(secondsStep*-1);
            }
        }
        long remainder = currentTask.service(secondsStep);
        currentDuration = currentDuration.plusSeconds(secondsStep - remainder);
        employment.put(getCurrentDate(), employment.getOrDefault(getCurrentDate(), Duration.ZERO).plusSeconds(secondsStep - remainder));
        return remainder;
    }
    public boolean takeRequest(Bank bank) {
        return bank.requestToGetRequest(this);
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
    public int getNumberOfServedClientsByDate(LocalDate date) {
        if(!completedRequests.containsKey(date))
            return 0;
        return completedRequests.get(date).size();
    }
    public int getNumberOfServedClients() {
        int number = 0;
        for(var date : completedRequests.keySet()) {
            number += completedRequests.get(date).size();
        }
        return number;
    }
    public Duration getAverageRequestWaitingDuration() {
        Duration duration = Duration.ZERO;
        int size = 0;
        for(var requestList : completedRequests.values()) {
            size += requestList.size();
            for(var request : requestList) {
                duration = duration.plus(request.getWaitingDuration());
            }
        }
        if(size == 0)
            return duration;
        return duration.dividedBy(size);
    }
    public Duration getAverageEmployment() {
        Duration duration = Duration.ZERO;
        int size = 0;
        for(var dur : employment.values()) {
            duration = duration.plus(dur);
            size++;
        }
        if(size == 0)
            return duration;
        return duration.dividedBy(size);
    }
    public double getProfitByDate(LocalDate date) {
        double profit = dailySalary;
        for(var request : completedRequests.get(date)) {
            profit += request.getPrice();
        }
        return profit;
    }
    public double getTotalProfit() {
        double profit = 0;
        for(var date : completedRequests.keySet()) {
            profit += getProfitByDate(date);
        }
        return profit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Клерк %s\n".formatted(super.toShortString()));
        sb.append("Дневная зарплата %.2f\n".formatted(dailySalary));
        sb.append("Дата найма: %s\n".formatted(dateTimeOfHire.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        sb.append("Общая прибыль: %.2f\n".formatted(getTotalProfit()));
        sb.append("Кол-во обслуженных клиентов: %d\n".formatted(getNumberOfServedClients()));
        sb.append("Текущая заявка: %s".formatted(currentTask == Request.NULL ? "нет" : currentTask.toString().replace("\n", "\n\t")));
        return sb.toString();
    }
}