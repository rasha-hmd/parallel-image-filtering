import filters.*;
import processor.ImageProcessor;
import processor.Timer;

import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) throws Exception {
        String inputPath = "input.png";
        String outputPathSeq = "output_seq.png";
        String outputPathPar = "output_par.png";
        int threshold = 100; // row threshold for Fork/Join split

        Filter filter = new GaussianBlurFilter(); // Change to SharpenFilter or SobelEdgeFilter if needed

        BufferedImage input = ImageProcessor.loadImage(inputPath);

        Timer timer = new Timer();

        System.out.println("Applying Sequential Filter...");
        timer.start();
        BufferedImage resultSeq = ImageProcessor.applySequential(input, filter);
        double timeSeq = timer.stopAndGetSeconds();
        ImageProcessor.saveImage(resultSeq, outputPathSeq);
        System.out.println("Sequential Time: " + timeSeq + " seconds");

        System.out.println("Applying Parallel Filter...");
        timer.start();
        BufferedImage resultPar = ImageProcessor.applyParallel(input, filter, threshold);
        double timePar = timer.stopAndGetSeconds();
        ImageProcessor.saveImage(resultPar, outputPathPar);
        System.out.println("Parallel Time: " + timePar + " seconds");

        System.out.printf("Speedup: %.2fx%n", timeSeq / timePar);
    }
}
