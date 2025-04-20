package test.buildings;

import main.buildings.Cafe;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CafeTest {
    private static final int MAX_SLOTS = 12;
    private final Cafe cafe = new Cafe();

    @Test
    void testTryEnterWhenSlotsAvailable() {
        assertTrue(cafe.tryEnter());
        assertEquals(MAX_SLOTS - 1, cafe.getAvailableSlots());
    }

    @Test
    void testTryEnterWhenFull() {
        consumeAllSlots();
        assertFalse(cafe.tryEnter());
    }

    @Test
    void testStartVisitAddsActiveEntry() throws Exception {
        cafe.tryEnter();
        long duration = 1000;

        cafe.startVisit(duration);
        List<Long> durations = cafe.getActiveDurations();

        assertEquals(1, durations.size());
        assertTrue(durations.get(0) > 0);
    }

    @Test
    void testEndVisitReleasesSlotAndRemovesEntry() throws Exception {
        cafe.tryEnter();
        cafe.startVisit(1000);

        cafe.endVisit();

        assertEquals(MAX_SLOTS, cafe.getAvailableSlots());
        assertTrue(getActiveVisits().isEmpty());
    }

    @Test
    void testGetActiveDurationsFiltersExpired() throws Exception {
        cafe.tryEnter();
        addExpiredVisit();

        List<Long> durations = cafe.getActiveDurations();

        assertTrue(durations.isEmpty());
    }

    @Test
    void testGetAvailableSlots() {
        assertEquals(MAX_SLOTS, cafe.getAvailableSlots());
        cafe.tryEnter();
        assertEquals(MAX_SLOTS - 1, cafe.getAvailableSlots());
    }

    @Test
    void testGetRandomServiceTimeReturnsValidValues() {
        for (int i = 0; i < 100; i++) {
            long time = cafe.getRandomServiceTime();
            assertTrue(time == 90000 || time == 180000);
        }
    }

    private void consumeAllSlots() {
        for (int i = 0; i < MAX_SLOTS; i++) {
            cafe.tryEnter();
        }
    }

    private ConcurrentHashMap<Long, Long> getActiveVisits() throws Exception {
        Field field = Cafe.class.getDeclaredField("activeVisits");
        field.setAccessible(true);
        return (ConcurrentHashMap<Long, Long>) field.get(cafe);
    }

    private void addExpiredVisit() throws Exception {
        ConcurrentHashMap<Long, Long> visits = getActiveVisits();
        long threadId = Thread.currentThread().getId();
        visits.put(threadId, System.currentTimeMillis() - 1000);
    }
}
