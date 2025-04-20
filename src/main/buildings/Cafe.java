package main.buildings;

import java.util.concurrent.*;
import java.util.*;
import java.util.stream.Collectors;

public class Cafe {
    private static final int MAX_SLOTS = 12;
    private final Semaphore slots = new Semaphore(MAX_SLOTS, true);
    private final ConcurrentHashMap<Long, Long> activeVisits = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public boolean tryEnter() {
        return slots.tryAcquire();
    }

    public void startVisit(long duration) {
        activeVisits.put(Thread.currentThread().getId(), System.currentTimeMillis() + duration);
    }

    public void endVisit() {
        activeVisits.remove(Thread.currentThread().getId());
        slots.release();
    }

    public List<Long> getActiveDurations() {
        long now = System.currentTimeMillis();
        return activeVisits.values().stream()
                .map(endTime -> endTime - now)
                .filter(remaining -> remaining > 0)
                .collect(Collectors.toList());
    }

    public int getAvailableSlots() {
        return slots.availablePermits();
    }

    public long getRandomServiceTime() {
        return random.nextBoolean() ? 90000 : 180000; // 1.5мин и 3мин для теста
    }
}
