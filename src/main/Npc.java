package main;

import main.buildings.Barbershop;
import main.buildings.Cafe;
import main.buildings.Hotel;

import java.util.Random;

public class Npc implements Runnable {
    private int id;
    private Hotel hotel;
    private final Cafe cafe;
    private final Barbershop barbershop;
    private final Random random = new Random();

    public Npc(int id, Hotel hotel, Cafe cafe, Barbershop barbershop) {
        this.id = id;
        this.hotel = hotel;
        this.cafe = cafe;
        this.barbershop = barbershop;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(random.nextInt(4000) + 1000);
                visitRandomBuilding();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void visitRandomBuilding() {
        int choice = random.nextInt(3);
        switch (choice) {
            case 0 -> visitHotel();
            case 1 -> visitCafe();
            case 2 -> visitBarbershop();
        }
    }

    private void visitHotel() {
        long duration = random.nextBoolean() ? 300 : 100;

        if (hotel.tryEnter()) {
            hotel.addVisitDuration(duration);
//            System.out.println("NPC " + id + " вошел в отель на " + duration + "ms");

            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                hotel.leave();
//                System.out.println("NPC " + id + " покинул отель");
            }
        }
    }

    private void visitCafe() {
        long duration = cafe.getRandomServiceTime();

        if (cafe.tryEnter()) {
            cafe.startVisit(duration);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                cafe.endVisit();
            }
        }
    }

    private void visitBarbershop() {
        long duration = barbershop.getRandomServiceTime();
        boolean isFashion = duration == 3000;

        if (barbershop.tryEnter()) {
            barbershop.startService(isFashion);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                barbershop.endService();
            }
        }
    }
}
