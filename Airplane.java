import java.util.Random;

public class Airplane extends Thread {

    private int planeNumber;
    private AirTrafficController atc;
    private boolean emergency;

    private int passengersBoarded;
    private long requestTime;

    private Random random = new Random();

    public Airplane(int planeNumber, AirTrafficController atc, boolean emergency) {
        this.planeNumber = planeNumber;
        this.atc = atc;
        this.emergency = emergency;

        setName("Plane-" + planeNumber);
    }

    @Override
    public void run() {
        try {
            requestTime = System.currentTimeMillis();

            if (emergency) {
                print("Requesting emergency landing because of low fuel.");
            } else {
                print("Requesting landing.");
            }

            Gate gate = atc.requestLanding(getName(), emergency, requestTime);

            atc.getRunway().acquire(getName());
            print("Landing on runway.");
            Thread.sleep(1000);
            print("Landed.");
            atc.getRunway().release(getName());

            print("Coasting to " + gate + ".");
            Thread.sleep(700);

            print("Docked at " + gate + ".");

            int passengersOut = random.nextInt(51);
            print(passengersOut + " passengers are disembarking.");
            Thread.sleep(1000);

            print("Starting gate operations.");

            Thread refuelThread = new Thread(() -> {
                try {
                    atc.getRefuelTruck().useTruck(getName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, getName() + "-Refuel");

            Thread cleaningThread = new Thread(() -> {
                try {
                    print("Cleaning and refilling supplies.");
                    Thread.sleep(1500);
                    print("Cleaning and supplies completed.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, getName() + "-Cleaning");

            Thread boardingThread = new Thread(() -> {
                try {
                    passengersBoarded = random.nextInt(51);
                    print(passengersBoarded + " passengers are boarding.");
                    Thread.sleep(1500);
                    print("Boarding completed.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, getName() + "-Boarding");

            // These 3 jobs happen at the same time
            refuelThread.start();
            cleaningThread.start();
            boardingThread.start();

            refuelThread.join();
            cleaningThread.join();
            boardingThread.join();

            print("All gate operations completed.");

            gate.release(getName());

            print("Coasting to runway for takeoff.");
            Thread.sleep(700);

            print("Requesting takeoff.");

            atc.getRunway().acquire(getName());
            atc.requestTakeoff(getName());

            print("Taking off.");
            Thread.sleep(1000);

            print("Takeoff completed.");
            atc.getRunway().release(getName());

            atc.addPassengers(passengersBoarded);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void print(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + getName() + ": " + message);
    }
}