package processor;

public class Timer {
    private long startTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public double stopAndGetSeconds() {
        return (System.nanoTime() - startTime) / 1e9;
    }
}
