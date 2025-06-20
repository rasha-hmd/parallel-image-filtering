package processor;

import filters.*;
import parallel.ParallelFilterTask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.util.concurrent.ForkJoinPool;

public class ImageProcessor {
    public static BufferedImage loadImage(String path) throws Exception {
        return ImageIO.read(new File(path));
    }

    public static void saveImage(BufferedImage img, String path) throws Exception {
        ImageIO.write(img, "png", new File(path));
    }

    public static BufferedImage applySequential(BufferedImage input, Filter filter) {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
        int kWidth = filter.getKernelWidth();
        int kHeight = filter.getKernelHeight();
        int kHalfW = kWidth / 2;
        int kHalfH = kHeight / 2;
    
        if (filter instanceof filters.SobelEdgeFilter sobel) {
            float[][] kernelX = sobel.getXKernel();
            float[][] kernelY = sobel.getYKernel();
    
            for (int y = kHalfH; y < height - kHalfH; y++) {
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
            // fallback to default filter handling
            float[][] kernel = filter.getKernel();
            for (int y = kHalfH; y < height - kHalfH; y++) {
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
    
        return output;
    }
    

    public static BufferedImage applyParallel(BufferedImage input, Filter filter, int threshold) {
        BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new ParallelFilterTask(input, output, filter, 0, input.getHeight(), threshold));
        return output;
    }
}
