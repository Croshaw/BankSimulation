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
    private int number;
    public Bank(LocalDateTime dateTimeOfCreation, HashMap<DayOfWeek, Pair<LocalTime, Duration>> schedule, int maxQueueLength, int maxClerksCount) {
        this.dateTimeOfCreation = dateTimeOfCreation;
        this.schedule = schedule;
        this.maxQueueLength = maxQueueLength;
        this.maxClerksCount = maxClerksCount;
        queue = new SynchronousQueue<>();
        maxAndMinQueueLength = new HashMap<>();
        maxAndMinQueueLength.put(dateTimeOfCreation.toLocalDate(), new Pair<>(Integer.MAX_VALUE, Integer.MIN_VALUE));
        reputation = 1;
        number = 1;
        infoBoard = new InfoBoard();
    }
    public boolean hire(Clerk clerk) {
        return clerks.add(clerk);
    }
    public boolean registerRequest(Request request) {
        if(queue.size() == maxQueueLength) {
            reputation = Math.max(reputation-0.05, 0);
            return false;
        }
        queue.offer(request);
        number++;
        return true;
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
            number = 1;
        } else if(!isWork(prevDateTime) && isWork(curDateTime)) {
            var tempSchedule = schedule.get(prevDateTime.getDayOfWeek());
            long dif = Math.abs(Duration.between(tempSchedule.getFirst(), prevDateTime).toSeconds());
            work(dif);
        } else {
            if(number != 1)
                number = 1;
        }
    }
    public boolean isWork() {
        return isWork(getCurrentDateTime());
    }
    public boolean isWork(LocalDateTime time) {
        if(!schedule.containsKey(time.getDayOfWeek()))
            return false;
        var tempSchedule = schedule.get(time.getDayOfWeek());
        return time.toLocalTime().isAfter(tempSchedule.getFirst()) && time.toLocalTime().isBefore(tempSchedule.getFirst().plus(tempSchedule.getSecond()));
    }
    public LocalDateTime getCurrentDateTime() {
        return dateTimeOfCreation.plus(currentDuration);
    }
    public int getNumberRequest() {
        return number;
    }
    public double getReputation() {
        return reputation;
    }
    public int getMinQueueLengthByDate(LocalDate date) {
        if(!maxAndMinQueueLength.containsKey(date))
            return 0;
        return maxAndMinQueueLength.get(date).getFirst();
    }
    public int getMinQueueLength() {
        int min = Integer.MAX_VALUE;
        for(var date : maxAndMinQueueLength.keySet()) {
            min = Math.min(min, getMinQueueLengthByDate(date));
        }
        return min;
    }
    public int getMaxQueueLengthByDate(LocalDate date) {
        if(!maxAndMinQueueLength.containsKey(date))
            return 0;
        return maxAndMinQueueLength.get(date).getSecond();
    }
    public int getMaxQueueLength() {
        int max = Integer.MIN_VALUE;
        for(var date : maxAndMinQueueLength.keySet()) {
            max = Math.max(max, getMaxQueueLengthByDate(date));
        }
        return max;
    }
    public int getAverageQueueLengthByDate(LocalDate date) {
        if(!maxAndMinQueueLength.containsKey(date))
            return 0;
        var pair = maxAndMinQueueLength.get(date);
        return (pair.getFirst() + pair.getSecond()) / 2;
    }
    public int getAverageQueueLength() {
        if(maxAndMinQueueLength.isEmpty())
            return 0;
        int avg = 0;
        for(var date : maxAndMinQueueLength.keySet()) {
            avg += getAverageQueueLengthByDate(date);
        }
        return avg / maxAndMinQueueLength.size();
    }
    public int getNumberOfServedClientsByClerkAndDate(Clerk clerk, LocalDate date) {
        return clerk.getNumberOfServedClientsByDate(date);
    }
    public int getNumberOfServedClientsByDate(LocalDate date) {
        int number = 0;
        for(var clerk : clerks) {
            number += clerk.getNumberOfServedClientsByDate(date);
        }
        return number;
    }
    public int getNumberOfServedClientsByClerk(Clerk clerk) {
        return clerk.getNumberOfServedClients();
    }
    public int getTotalNumberOfServedClients() {
        int number = 0;
        for(var clerk : clerks) {
            number += clerk.getNumberOfServedClients();
        }
        return number;
    }
    public int getNumberOfLostClientsByClerkAndDate(Clerk clerk, LocalDate date) {
        return clerk.getNumberOfLostClientsByDate(date);
    }
    public int getNumberOfLostClientsByDate(LocalDate date) {
        int number = 0;
        for(var clerk : clerks) {
            number += clerk.getNumberOfLostClientsByDate(date);
        }
        return number;
    }
    public int getNumberOfLostClientsByClerk(Clerk clerk) {
        return clerk.getNumberOfLostClients();
    }
    public int getTotalNumberOfLostClients() {
        int number = 0;
        for(var clerk : clerks) {
            number += clerk.getNumberOfLostClients();
        }
        return number;
    }

}
