public class RefuelTruck {

    private boolean busy = false;

    public synchronized void useTruck(String planeName) throws InterruptedException {
        while (busy) {
            System.out.println("[" + Thread.currentThread().getName() + "] "
                    + planeName + " is waiting for refuel truck.");
            wait();
        }

        busy = true;

        System.out.println("[" + Thread.currentThread().getName() + "] "
                + "Refuel truck is refueling " + planeName + ".");

        Thread.sleep(1500);

        System.out.println("[" + Thread.currentThread().getName() + "] "
                + "Refueling completed for " + planeName + ".");

        busy = false;
        notifyAll();
    }
}