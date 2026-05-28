import java.util.Random;

public class AirportSimulation {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== Asia Pacific Airport Simulation Started ===");

        Runway runway = new Runway();

        Gate[] gates = {
                new Gate(1),
                new Gate(2),
                new Gate(3)
        };

        RefuelTruck refuelTruck = new RefuelTruck();

        AirTrafficController atc = new AirTrafficController(runway, gates, refuelTruck);

        Airplane[] airplanes = new Airplane[6];
        Random random = new Random();
    for (int i = 0; i < airplanes.length; i++) {
        int planeNumber = i + 1;
        boolean emergency = (planeNumber == 6);
        airplanes[i] = new Airplane(planeNumber, atc, emergency);
        int delay;
        if (planeNumber == 6) {
            delay = 300;} else {
            delay = random.nextInt(2001);}
        
        Thread.sleep(delay);
        airplanes[i].start();
}

        // Main thread waits until all airplane threads finish
        for (Airplane airplane : airplanes) {
            airplane.join();
        }

        System.out.println("\n=== All planes have left the airport ===");
        atc.printFinalReport();
    }
}