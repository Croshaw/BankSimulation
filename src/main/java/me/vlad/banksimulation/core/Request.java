package me.vlad.banksimulation.core;

import me.vlad.banksimulation.core.human.Client;

import java.time.Duration;
import java.time.LocalDateTime;

public class Request {
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
}
