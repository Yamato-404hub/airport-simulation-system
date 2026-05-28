public class Gate {

    private int gateNumber;
    private boolean occupied = false;

    public Gate(int gateNumber) {
        this.gateNumber = gateNumber;
    }

    public synchronized boolean tryUse() {
        if (occupied) {
            return false;
        }

        occupied = true;
        return true;
    }

    public synchronized void release(String planeName) {
        occupied = false;
        System.out.println("[" + Thread.currentThread().getName() + "] "
                + planeName + " undocked from Gate-" + gateNumber + ".");
        notifyAll();
    }

    public synchronized boolean isOccupied() {
        return occupied;
    }

    @Override
    public String toString() {
        return "Gate-" + gateNumber;
    }
}