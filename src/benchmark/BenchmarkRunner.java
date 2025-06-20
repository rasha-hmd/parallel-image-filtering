package benchmark;

import filters.GaussianBlurFilter;
import filters.Filter;
import processor.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        String imagePath = "benchmark_input.png"; // Use a large image (e.g., 1920x1080 or larger)
        BufferedImage input = ImageProcessor.loadImage(imagePath);
        Filter filter = new GaussianBlurFilter(); // Change to SharpenFilter or SobelEdgeFilter if needed
        int threshold = Math.max(20, input.getHeight() / 12);

        double baselineTime = 0;
        String csvPath = "benchmark.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            writer.println("Threads,Time_Seconds,Speedup");

            for (int threads : List.of(1, 2, 4, 8)) {
                ForkJoinPool pool = new ForkJoinPool(threads);
                long start = System.nanoTime();

                BufferedImage result = pool
                        .submit(() -> ImageProcessor.applyParallel(input, filter, threshold))
                        .get();

                long end = System.nanoTime();
                double timeSeconds = (end - start) / 1e9;

                if (threads == 1) {
                    baselineTime = timeSeconds;
                }

                double speedup = baselineTime / timeSeconds;

                System.out.printf("Threads: %d, Time: %.3fs, Speedup: %.2fx%n", threads, timeSeconds, speedup);
                writer.printf("%d,%.3f,%.2f%n", threads, timeSeconds, speedup);
            }
        }

        System.out.println("Benchmark complete and results saved to benchmark.csv");
        System.out.println("Waiting 15 seconds for VisualVM...");
        Thread.sleep(15000);

    }
}
