import java.util.ArrayList;

public class AirTrafficController {

    private Runway runway;
    private Gate[] gates;
    private RefuelTruck refuelTruck;

    private int planesOnGround = 0;
    private final int MAX_PLANES_ON_GROUND = 3;

    private boolean emergencyWaiting = false;
    private String emergencyPlaneName = null;

    private int planesServed = 0;
    private int totalPassengersBoarded = 0;

    private ArrayList<Long> waitingTimes = new ArrayList<>();

    public AirTrafficController(Runway runway, Gate[] gates, RefuelTruck refuelTruck) {
        this.runway = runway;
        this.gates = gates;
        this.refuelTruck = refuelTruck;
    }

    public synchronized Gate requestLanding(String planeName, boolean emergency, long requestTime)
            throws InterruptedException {

        if (emergency) {
            emergencyWaiting = true;
            emergencyPlaneName = planeName;

            System.out.println("[ATC] Emergency landing request received from " + planeName + ".");
            notifyAll();
        }

        while (planesOnGround >= MAX_PLANES_ON_GROUND ||
                (emergencyWaiting && !planeName.equals(emergencyPlaneName))) {

            if (planesOnGround >= MAX_PLANES_ON_GROUND) {
                System.out.println("[ATC] Landing denied for " + planeName + ". Airport is full.");
            } else {
                System.out.println("[ATC] " + planeName + " is waiting because emergency plane has priority.");
            }

            wait();
        }

        Gate gate = findFreeGate();

        planesOnGround++;

        long waitingTime = System.currentTimeMillis() - requestTime;
        waitingTimes.add(waitingTime);

        if (emergency && planeName.equals(emergencyPlaneName)) {
            emergencyWaiting = false;
            emergencyPlaneName = null;
        }

        System.out.println("[ATC] Landing approved for " + planeName + ".");
        System.out.println("[ATC] " + gate + " assigned to " + planeName + ".");
        System.out.println("[ATC] Planes on ground: " + planesOnGround);

        return gate;
    }

    private Gate findFreeGate() {
        for (Gate gate : gates) {
            if (gate.tryUse()) {
                return gate;
            }
        }

        // This should not happen because max planes on ground is 3
        return null;
    }

    public synchronized void requestTakeoff(String planeName) {
        System.out.println("[ATC] Takeoff approved for " + planeName + ".");

        planesOnGround--;
        planesServed++;

        System.out.println("[ATC] " + planeName + " left airport ground.");
        System.out.println("[ATC] Planes on ground: " + planesOnGround);

        notifyAll();
    }

    public synchronized void addPassengers(int passengers) {
        totalPassengersBoarded += passengers;
    }

    public synchronized void printFinalReport() {
        System.out.println("\n=== Final ATC Report ===");

        boolean allGatesEmpty = true;

        for (Gate gate : gates) {
            if (gate.isOccupied()) {
                allGatesEmpty = false;
            }
            System.out.println(gate + " empty: " + !gate.isOccupied());
        }

        System.out.println("All gates empty: " + allGatesEmpty);

        long min = Long.MAX_VALUE;
        long max = 0;
        long total = 0;

        for (long time : waitingTimes) {
            if (time < min) {
                min = time;
            }

            if (time > max) {
                max = time;
            }

            total += time;
        }

        long average = 0;

        if (!waitingTimes.isEmpty()) {
            average = total / waitingTimes.size();
        }

        System.out.println("Planes served: " + planesServed);
        System.out.println("Passengers boarded: " + totalPassengersBoarded);
        System.out.println("Minimum waiting time: " + min + " ms");
        System.out.println("Maximum waiting time: " + max + " ms");
        System.out.println("Average waiting time: " + average + " ms");
    }

    public Runway getRunway() {
        return runway;
    }

    public RefuelTruck getRefuelTruck() {
        return refuelTruck;
    }
}