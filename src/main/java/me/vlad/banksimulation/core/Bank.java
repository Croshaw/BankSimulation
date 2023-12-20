package me.vlad.banksimulation.core;

import me.vlad.banksimulation.core.human.Clerk;
import me.vlad.banksimulation.core.human.Client;
import me.vlad.banksimulation.util.DurationUtils;
import me.vlad.banksimulation.util.NumberUtils;
import me.vlad.banksimulation.util.Pair;

import java.time.*;
import java.util.*;

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
    private final HashMap<LocalDate, Integer> numberOfLostClients;
    public Bank(int maxQueueLength, int maxClerksCount) {
        this.dateTimeOfCreation = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
        this.schedule = new HashMap<>();
        schedule.put(DayOfWeek.MONDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        schedule.put(DayOfWeek.TUESDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        schedule.put(DayOfWeek.WEDNESDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        schedule.put(DayOfWeek.THURSDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        schedule.put(DayOfWeek.FRIDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        schedule.put(DayOfWeek.SATURDAY, new Pair<>(LocalTime.of(10, 0, 0), Duration.ofHours(6)));
        currentDuration = Duration.ZERO;
        clerks = new HashSet<>();
        this.maxQueueLength = maxQueueLength;
        this.maxClerksCount = maxClerksCount;
        queue = new ArrayDeque<>();
        maxAndMinQueueLength = new HashMap<>();
        maxAndMinQueueLength.put(dateTimeOfCreation.toLocalDate(), new Pair<>(Integer.MAX_VALUE, Integer.MIN_VALUE));
        reputation = 1;
        number = 1;
        infoBoard = new InfoBoard();
        numberOfLostClients = new HashMap<>();
        numberOfLostClients.put(getCurrentDateTime().toLocalDate(), 0);
    }
    public boolean hire(Clerk clerk) {
        if(clerks.size() < maxClerksCount)
            return clerks.add(clerk);
        return false;
    }
    public boolean clientAlreadyInQueue(Client client) {
        for(var req : queue) {
            if(req.getClient() == client)
                return true;
        }
        return false;
    }
    public boolean isQueueFull() {
        return queue.size() == maxQueueLength;
    }
    public boolean registerRequest(Duration serviceDuration, Client client, double price) {
        if(queue.size() == maxQueueLength) {
            numberOfLostClients.put(getCurrentDateTime().toLocalDate(), numberOfLostClients.getOrDefault(getCurrentDateTime().toLocalDate(), 0) + 1);
            reputation = Math.max(reputation-0.05, 0);
            return false;
        }
        queue.offer(new Request(number, price, client, serviceDuration, getCurrentDateTime()));
        if(!maxAndMinQueueLength.containsKey(getCurrentDateTime().toLocalDate()))
            maxAndMinQueueLength.put(getCurrentDateTime().toLocalDate(), new Pair<>(Integer.MAX_VALUE, Integer.MIN_VALUE));
        var pair = maxAndMinQueueLength.get(getCurrentDateTime().toLocalDate());
        pair.setFirst(Math.min(pair.getFirst(), queue.size()));
        pair.setSecond(Math.max(pair.getSecond(), queue.size()));
        number++;
        return true;
    }
    public boolean requestToGetRequest(Clerk clerk) {
        if(queue.isEmpty())
            return false;
        infoBoard.work(clerk, queue.poll());
        return true;
    }
    private void freeQueue() {
        while(!queue.isEmpty()) {
            numberOfLostClients.put(getCurrentDateTime().toLocalDate(), numberOfLostClients.getOrDefault(getCurrentDateTime().toLocalDate(), 0) + 1);
            queue.poll();
        }
        number = 1;
    }
    private long workByClerk(long secondsStep, Clerk clerk) {
        return clerk.work(secondsStep, this);
    }
    public void work(long secondsStep) {
        ArrayList<Clerk> tempClerks = new ArrayList<>(clerks);
        Collections.shuffle(tempClerks);
        HashMap<Clerk, Long> remainders = new HashMap<>();
        queue.forEach(x -> x.waiting(secondsStep));
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
            clerks.forEach(Clerk::finalizeWork);
            freeQueue();
        } else if(!isWork(prevDateTime) && isWork(curDateTime)) {
            var tempSchedule = schedule.get(prevDateTime.getDayOfWeek());
            long dif = Math.abs(Duration.between(tempSchedule.getFirst(), prevDateTime).toSeconds());
            work(dif);
        } else {
            freeQueue();
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
    public Duration getCurrentDuration() {
        return currentDuration;
    }
    public int getMaxClerksCount() {
        return maxClerksCount;
    }
    public int getNumberRequest() {
        return number;
    }
    public InfoBoard getInfoBoard() {
        return infoBoard;
    }
    public double getReputation() {
        if(!isWork())
            return 0;
        double tempRep = reputation;

        if(queue.size() > 6)
            tempRep *= (double) 1 /(queue.size()-5);
        var curDateTime = getCurrentDateTime();
        tempRep *= ((double) 1 /(DayOfWeek.SUNDAY.getValue() - curDateTime.getDayOfWeek().getValue()));
        var sched = schedule.get(curDateTime.getDayOfWeek());
        tempRep *= NumberUtils.convertValueFromRangeToNewRange(0, sched.getSecond().toSeconds(), 0.1, 2, Duration.between(curDateTime.toLocalTime(), sched.getFirst().plus(sched.getSecond())).toSeconds());
        return tempRep;
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
    public int getNumberOfLostClientsByDate(LocalDate date) {
        return numberOfLostClients.getOrDefault(date, 0);
    }
    public int getTotalNumberOfLostClients() {
        int number = 0;
        for(var lost : numberOfLostClients.values())
            number+=lost;
        return number;
    }
    public Duration getAverageRequestWaitingDuration() {
        Duration duration = Duration.ZERO;
        for(var clerk : clerks)
            duration = duration.plus(clerk.getAverageRequestWaitingDuration());
        if(clerks.isEmpty())
            return Duration.ZERO;
        return duration.dividedBy(clerks.size());
    }
    public Duration getAverageEmploymentOfClerk(Clerk clerk) {
        return clerk.getAverageEmployment();
    }
    public Duration getAverageEmployment() {
        Duration duration = Duration.ZERO;
        for(var clerk : clerks)
            duration = duration.plus(clerk.getAverageEmployment());
        if(clerks.isEmpty())
            return Duration.ZERO;
        return duration.dividedBy(clerks.size());
    }
    public double getTotalProfitByClerkAndDate(Clerk clerk, LocalDate date) {
        return clerk.getProfitByDate(date);
    }
    public double getTotalProfitByDate(LocalDate date) {
        double profit = 0;
        for(var clerk : clerks) {
            profit += clerk.getProfitByDate(date);
        }
        return profit;
    }
    public double getTotalProfitByClerk(Clerk clerk) {
        return clerk.getTotalProfit();
    }
    public double getTotalProfit() {
        double profit = 0;
        for(var clerk : clerks) {
            profit += clerk.getTotalProfit();
        }
        return profit;
    }
    public HashSet<Clerk> getClerks() {
        return clerks;
    }
    public Queue<Request> getQueue() {
        return queue;
    }
    public int getQueueLimit() {
        return maxQueueLength;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Банк\n");
        sb.append("Кол-во обслуженных клинетов: %d\n".formatted(getTotalNumberOfServedClients()));
        sb.append("Кол-во потерянных клиентов: %d\n".formatted(getTotalNumberOfLostClients()));
        sb.append("Максимальная длина очереди: %d\n".formatted(getMaxQueueLength()));
        sb.append("Минимальная длина очереди: %d\n".formatted(getMinQueueLength()));
        sb.append("Средняя длина очереди: %d\n".formatted(getAverageQueueLength()));
        sb.append("Среднее время ожидания клиентов: %s\n".formatted(DurationUtils.toString(getAverageRequestWaitingDuration())));
        sb.append("Средняя занятость клерков: %s\n".formatted(DurationUtils.toString(getAverageEmployment())));
        sb.append("Полученная прибыль: %.02f\n".formatted(getTotalProfit()));
        sb.append("Репутация: %f".formatted(getReputation()));
        return sb.toString();
    }
}
