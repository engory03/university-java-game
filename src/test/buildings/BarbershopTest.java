package test.buildings;

import main.buildings.Barbershop;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class BarbershopTest {
    private static final int MAX_HAIRDRESSERS = 2;
    private final Barbershop barbershop = new Barbershop();

    @Test
    void testTryEnterWhenSeatsAvailable() {
        assertTrue(barbershop.tryEnter());
        assertEquals(MAX_HAIRDRESSERS - 1, barbershop.getAvailableSeats());
    }

    @Test
    void testTryEnterWhenFull() {
        occupyAllSeats();
        assertFalse(barbershop.tryEnter());
    }

    @Test
    void testStartServiceAddsEntry() throws Exception {
        barbershop.tryEnter();
        barbershop.startService(true);

        ConcurrentHashMap<Long, Boolean> services = getActiveServices();
        assertFalse(services.isEmpty());
        assertTrue(services.containsValue(true));
    }

    @Test
    void testEndServiceRemovesEntryAndReleasesSeat() throws Exception {
        barbershop.tryEnter();
        barbershop.startService(false);

        barbershop.endService();

        assertEquals(MAX_HAIRDRESSERS, barbershop.getAvailableSeats());
        assertTrue(getActiveServices().isEmpty());
    }

    @Test
    void testGetAvailableSeats() {
        assertEquals(MAX_HAIRDRESSERS, barbershop.getAvailableSeats());
        barbershop.tryEnter();
        assertEquals(MAX_HAIRDRESSERS - 1, barbershop.getAvailableSeats());
    }

    @Test
    void testGetRandomServiceTimeReturnsValidValues() {
        for (int i = 0; i < 100; i++) {
            long time = barbershop.getRandomServiceTime();
            assertTrue(time == 1000 || time == 3000);
        }
    }

    // Helper methods
    private void occupyAllSeats() {
        for (int i = 0; i < MAX_HAIRDRESSERS; i++) {
            barbershop.tryEnter();
        }
    }

    private ConcurrentHashMap<Long, Boolean> getActiveServices() throws Exception {
        Field field = Barbershop.class.getDeclaredField("activeServices");
        field.setAccessible(true);
        return (ConcurrentHashMap<Long, Boolean>) field.get(barbershop);
    }

    private void addTestService(boolean isFashion) throws Exception {
        ConcurrentHashMap<Long, Boolean> services = getActiveServices();
        services.put(Thread.currentThread().getId(), isFashion);
    }
}
