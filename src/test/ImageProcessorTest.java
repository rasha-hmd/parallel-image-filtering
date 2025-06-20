package test;

import filters.*;
import processor.ImageProcessor;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageProcessorTest {

    private final String testImagePath = "input.png";

    @Test
    public void testGaussianBlur() throws Exception {
        runFilterTest(new GaussianBlurFilter(), "GaussianBlur");
    }

    @Test
    public void testSharpen() throws Exception {
        runFilterTest(new SharpenFilter(), "Sharpen");
    }

    @Test
    public void testSobelEdge() throws Exception {
        runFilterTest(new SobelEdgeFilter(), "SobelEdge");
    }

    private void runFilterTest(Filter filter, String name) throws Exception {
        File imgFile = new File(testImagePath);
        assertTrue(imgFile.exists(), "Test image not found: " + testImagePath);

        BufferedImage input = ImageProcessor.loadImage(testImagePath);

        BufferedImage seq = ImageProcessor.applySequential(input, filter);
        BufferedImage parManual = ImageProcessor.applyParallel(input, filter, 100);
        BufferedImage parAuto = ImageProcessor.applyParallel(input, filter, Math.max(20, input.getHeight() / 12));

        assertTrue(imagesEqual(seq, parManual), "Manual parallel differs from sequential in " + name);
        assertTrue(imagesEqual(seq, parAuto), "Auto-tuned parallel differs from sequential in " + name);
    }

    private boolean imagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) return false;
        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}
