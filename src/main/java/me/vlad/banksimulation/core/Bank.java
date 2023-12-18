package me.vlad.banksimulation.core;

import me.vlad.banksimulation.core.human.Clerk;
import me.vlad.banksimulation.util.Pair;

import java.time.*;
import java.util.*;
import java.util.concurrent.SynchronousQueue;

public class Bank {
    private final LocalDateTime dateTimeOfCreation;
    private Duration currentDuration;
    private HashSet<Clerk> clerks;
    private final HashMap<DayOfWeek, Pair<LocalTime, Duration>> schedule;
    private final Queue<Request> queue;
    private final HashMap<LocalDate, Pair<Integer, Integer>> maxAndMinQueueLength;
    private double reputation;
    private final int maxQueueLength;
    private final int maxClerksCount;
    private final InfoBoard infoBoard;
    public Bank(LocalDateTime dateTimeOfCreation, HashMap<DayOfWeek, Pair<LocalTime, Duration>> schedule, int maxQueueLength, int maxClerksCount) {
        this.dateTimeOfCreation = dateTimeOfCreation;
        this.schedule = schedule;
        this.maxQueueLength = maxQueueLength;
        this.maxClerksCount = maxClerksCount;
        queue = new SynchronousQueue<>();
        maxAndMinQueueLength = new HashMap<>();
        maxAndMinQueueLength.put(dateTimeOfCreation.toLocalDate(), new Pair<>(Integer.MAX_VALUE, Integer.MIN_VALUE));
        reputation = 1;
        infoBoard = new InfoBoard();
    }
    public boolean hire(Clerk clerk) {
        return clerks.add(clerk);
    }
    public void registerRequest(Request request) {
        queue.offer(request);
    }
    private long workByClerk(long secondsStep, Clerk clerk) {
        return clerk.work(secondsStep, this);
    }
    public void work(long secondsStep) {
        ArrayList<Clerk> tempClerks = new ArrayList<>(clerks);
        Collections.shuffle(tempClerks);
        HashMap<Clerk, Long> remainders = new HashMap<>();
        for(var clerk : tempClerks) {
            long remainder = workByClerk(secondsStep, clerk);
            if(remainder > 0) {
                remainders.put(clerk, remainder);
            }
        }
        while(!remainders.isEmpty()) {
            ArrayList<Clerk> toRem = new ArrayList<>();
            for(var clerk : remainders.keySet()) {
                long secStep = remainders.get(clerk);
                if(secStep == 0)
                    continue;
                long remainder = workByClerk(secStep, clerk);
                if(remainder > 0)
                    remainders.put(clerk, remainder);
                else
                    toRem.add(clerk);
            }
            toRem.forEach(remainders::remove);
        }
    }
    public void life(long secondsStep) {
        LocalDateTime prevDateTime = getCurrentDateTime();
        currentDuration = currentDuration.plusSeconds(secondsStep);
        LocalDateTime curDateTime = getCurrentDateTime();
        if(isWork(prevDateTime) && isWork(curDateTime)) {
            work(secondsStep);
        } else if(isWork(prevDateTime) && !isWork(curDateTime)) {
            var tempSchedule = schedule.get(prevDateTime.getDayOfWeek());
            long dif = Math.abs(Duration.between(tempSchedule.getFirst().plus(tempSchedule.getSecond()), prevDateTime).toSeconds());
            work(dif);
        } else if(!isWork(prevDateTime) && isWork(curDateTime)) {
            var tempSchedule = schedule.get(prevDateTime.getDayOfWeek());
            long dif = Math.abs(Duration.between(tempSchedule.getFirst(), prevDateTime).toSeconds());
            work(dif);
        }
    }

    public boolean isWork() {
        return isWork(getCurrentDateTime());
    }
    public boolean isWork(LocalDateTime time) {
        var tempSchedule = schedule.get(time.getDayOfWeek());
        return time.toLocalTime().isAfter(tempSchedule.getFirst()) && time.toLocalTime().isBefore(tempSchedule.getFirst().plus(tempSchedule.getSecond()));
    }
    public LocalDateTime getCurrentDateTime() {
        return dateTimeOfCreation.plus(currentDuration);
    }
}
