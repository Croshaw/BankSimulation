package me.vlad.banksimulation.core;

import me.vlad.banksimulation.core.human.Client;
import me.vlad.banksimulation.util.DurationUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Request {
    public final static Request NULL = new Request(-1, 0, null, null, null);
    private final int number;
    private final double price;
    private final Client client;
    private final Duration durationService;
    private Duration currentDuration;
    private Duration waitingDuration;
    private final LocalDateTime dateTimeOfCreation;

    public Request(int number, double price, Client client, Duration durationService, LocalDateTime dateTimeOfCreation) {
        this.number = number;
        this.price = price;
        this.client = client;
        this.durationService = durationService;
        this.dateTimeOfCreation = dateTimeOfCreation;
        currentDuration = Duration.ZERO;
        waitingDuration = Duration.ZERO;
    }
    public int getNumber() {
        return number;
    }
    public long service(long secondsStep) {
        Duration tempDur = currentDuration.plusSeconds(secondsStep);
        long dif = tempDur.minus(durationService).getSeconds();
        if(dif < 0) {
            currentDuration = tempDur;
            dif = 0;
        } else {
            currentDuration = durationService;
        }
        return dif;
    }
    public void waiting(long secondsStep) {
        waitingDuration = waitingDuration.plusSeconds(secondsStep);
    }
    public Duration getWaitingDuration() {
        return waitingDuration;
    }
    public Duration getServiceDuration() {
        return durationService;
    }
    public boolean isDone() {
        return durationService.compareTo(currentDuration) == 0;
    }
    public double getPrice() {
        return price;
    }
    public Client getClient() {
        return client;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Заявка № %d от %s\n".formatted(number, dateTimeOfCreation.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        sb.append("Клиент: %s\n".formatted(client.toShortString()));
        sb.append("Время ожидания: %s\n".formatted(DurationUtils.toString(waitingDuration)));
        sb.append("Время обслуживания: %s\n".formatted(DurationUtils.toString(durationService)));
        sb.append("Текущее время обслуживания: %s\n".formatted(DurationUtils.toString(currentDuration)));
        sb.append("Цена: %.2f".formatted(price));
        return sb.toString();
    }
}
