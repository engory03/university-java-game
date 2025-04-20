package main.buildings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class Hotel {
    private static final int MAX_ROOMS = 5;
    private final Semaphore rooms = new Semaphore(MAX_ROOMS, true);
    private final Map<Long, Long> activeVisits = new ConcurrentHashMap<>();

    public boolean tryEnter() {
        return rooms.tryAcquire();
    }

    public void addVisitDuration(long duration) {
        activeVisits.put(Thread.currentThread().getId(), System.currentTimeMillis() + duration);
    }

    public void leave() {
        activeVisits.remove(Thread.currentThread().getId());
        rooms.release();
    }

    public List<Long> getRemainingTimes() {
        long now = System.currentTimeMillis();
        List<Long> times = new ArrayList<>();

        activeVisits.entrySet().removeIf(entry -> {
            if (entry.getValue() <= now) {
                rooms.release();
                return true;
            }
            return false;
        });

        activeVisits.values().forEach(endTime ->
                times.add(endTime - now)
        );
        return times;
    }

    public int getAvailableRooms() {
        return rooms.availablePermits();
    }
}
