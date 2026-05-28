public class Runway {

    private boolean occupied = false;

    public synchronized void acquire(String planeName) throws InterruptedException {
        while (occupied) {
            System.out.println("[" + Thread.currentThread().getName() + "] "
                    + planeName + " is waiting for runway.");
            wait();
        }

        occupied = true;
        System.out.println("[" + Thread.currentThread().getName() + "] "
                + planeName + " acquired the runway.");
    }

    public synchronized void release(String planeName) {
        occupied = false;
        System.out.println("[" + Thread.currentThread().getName() + "] "
                + planeName + " released the runway.");
        notifyAll();
    }
}
