package parallel;

import filters.Filter;
import processor.ImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.RecursiveAction;

public class ParallelFilterTask extends RecursiveAction {
    private final BufferedImage input;
    private final BufferedImage output;
    private final Filter filter;
    private final int startY;
    private final int endY;
    private final int threshold;

    public ParallelFilterTask(BufferedImage input, BufferedImage output, Filter filter,
                              int startY, int endY, int threshold) {
        this.input = input;
        this.output = output;
        this.filter = filter;
        this.startY = startY;
        this.endY = endY;
        this.threshold = threshold;
    }

    @Override
    protected void compute() {
        if (endY - startY <= threshold) {
            applyFilter();
        } else {
            int mid = (startY + endY) / 2;
            ParallelFilterTask upper = new ParallelFilterTask(input, output, filter, startY, mid, threshold);
            ParallelFilterTask lower = new ParallelFilterTask(input, output, filter, mid, endY, threshold);
            invokeAll(upper, lower);
        }
    }

    private void applyFilter() {
        int width = input.getWidth();
        int height = input.getHeight();
        int kWidth = filter.getKernelWidth();
        int kHeight = filter.getKernelHeight();
        int kHalfW = kWidth / 2;
        int kHalfH = kHeight / 2;
    
        if (filter instanceof filters.SobelEdgeFilter sobel) {
            float[][] kernelX = sobel.getXKernel();
            float[][] kernelY = sobel.getYKernel();
    
            for (int y = Math.max(startY, kHalfH); y < Math.min(endY, height - kHalfH); y++) {
                for (int x = kHalfW; x < width - kHalfW; x++) {
                    float gxR = 0, gxG = 0, gxB = 0;
                    float gyR = 0, gyG = 0, gyB = 0;
    
                    for (int ky = -kHalfH; ky <= kHalfH; ky++) {
                        for (int kx = -kHalfW; kx <= kHalfW; kx++) {
                            int pixel = input.getRGB(x + kx, y + ky);
                            Color color = new Color(pixel);
    
                            float wx = kernelX[ky + kHalfH][kx + kHalfW];
                            float wy = kernelY[ky + kHalfH][kx + kHalfW];
    
                            gxR += wx * color.getRed();
                            gxG += wx * color.getGreen();
                            gxB += wx * color.getBlue();
    
                            gyR += wy * color.getRed();
                            gyG += wy * color.getGreen();
                            gyB += wy * color.getBlue();
                        }
                    }
    
                    int r = (int) Math.min(255, Math.hypot(gxR, gyR));
                    int g = (int) Math.min(255, Math.hypot(gxG, gyG));
                    int b = (int) Math.min(255, Math.hypot(gxB, gyB));
    
                    output.setRGB(x, y, new Color(r, g, b).getRGB());
                }
            }
        } else {
            // Default filter processing
            float[][] kernel = filter.getKernel();
    
            for (int y = Math.max(startY, kHalfH); y < Math.min(endY, height - kHalfH); y++) {
                for (int x = kHalfW; x < width - kHalfW; x++) {
                    float r = 0, g = 0, b = 0;
    
                    for (int ky = -kHalfH; ky <= kHalfH; ky++) {
                        for (int kx = -kHalfW; kx <= kHalfW; kx++) {
                            int pixel = input.getRGB(x + kx, y + ky);
                            Color color = new Color(pixel);
                            float weight = kernel[ky + kHalfH][kx + kHalfW];
    
                            r += weight * color.getRed();
                            g += weight * color.getGreen();
                            b += weight * color.getBlue();
                        }
                    }
    
                    int newR = Math.min(255, Math.max(0, Math.round(r)));
                    int newG = Math.min(255, Math.max(0, Math.round(g)));
                    int newB = Math.min(255, Math.max(0, Math.round(b)));
    
                    output.setRGB(x, y, new Color(newR, newG, newB).getRGB());
                }
            }
        }
    }
    
}
