package test.buildings;

import main.buildings.Hotel;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class HotelTest {
    private static final int MAX_ROOMS = 5;
    private final Hotel hotel = new Hotel();

    @Test
    void testTryEnterWhenRoomsAvailable() {
        assertTrue(hotel.tryEnter());
        assertEquals(MAX_ROOMS - 1, hotel.getAvailableRooms());
    }

    @Test
    void testTryEnterWhenFull() throws InterruptedException {
        // Occupy all rooms
        for (int i = 0; i < MAX_ROOMS; i++) {
            assertTrue(hotel.tryEnter());
        }
        assertFalse(hotel.tryEnter());
    }

    @Test
    void testLeaveReleasesRoom() {
        hotel.tryEnter();
        hotel.leave();
        assertEquals(MAX_ROOMS, hotel.getAvailableRooms());
    }

    @Test
    void testGetRemainingTimesRemovesExpiredAndReleasesPermits() throws Exception {
        // Acquire room and add expired visit
        hotel.tryEnter();
        addExpiredVisit();

        List<Long> times = hotel.getRemainingTimes();

        assertEquals(MAX_ROOMS, hotel.getAvailableRooms());
        assertTrue(times.isEmpty());
    }

    @Test
    void testGetRemainingTimesReturnsCorrectDurations() {
        hotel.tryEnter();
        long duration = 1000;
        hotel.addVisitDuration(duration);

        List<Long> times = hotel.getRemainingTimes();

        assertEquals(1, times.size());
        long remaining = times.get(0);
        assertTrue(remaining > 0 && remaining <= duration);
    }

    @Test
    void testGetAvailableRooms() {
        assertEquals(MAX_ROOMS, hotel.getAvailableRooms());
        hotel.tryEnter();
        assertEquals(MAX_ROOMS - 1, hotel.getAvailableRooms());
    }

    // Helper methods
    private Map<Long, Long> getActiveVisits() throws Exception {
        Field field = Hotel.class.getDeclaredField("activeVisits");
        field.setAccessible(true);
        return (ConcurrentHashMap<Long, Long>) field.get(hotel);
    }

    private void addExpiredVisit() throws Exception {
        Map<Long, Long> visits = getActiveVisits();
        long threadId = Thread.currentThread().getId();
        visits.put(threadId, System.currentTimeMillis() - 1000);
    }
}
