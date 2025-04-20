package main.buildings;

import java.util.concurrent.*;
import java.util.*;
import java.util.stream.Collectors;

public class Barbershop {
    private static final int MAX_HAIRDRESSERS = 2;
    private final Semaphore seats = new Semaphore(MAX_HAIRDRESSERS, true);
    private final ConcurrentHashMap<Long, Boolean> activeServices = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public boolean tryEnter() {
        return seats.tryAcquire();
    }

    public void startService(boolean isFashionCut) {
        activeServices.put(Thread.currentThread().getId(), isFashionCut);
    }

    public void endService() {
        activeServices.remove(Thread.currentThread().getId());
        seats.release();
    }

    public List<String> getActiveServices() {
        return activeServices.entrySet().stream()
                .map(entry -> entry.getValue() ? "Модная стрижка" : "Простая стрижка")
                .collect(Collectors.toList());
    }

    public int getAvailableSeats() {
        return seats.availablePermits();
    }

    public long getRandomServiceTime() {
        return random.nextBoolean() ? 1000 : 3000;
    }
}
