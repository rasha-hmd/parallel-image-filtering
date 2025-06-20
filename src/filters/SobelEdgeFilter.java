package filters;

public class SobelEdgeFilter implements Filter {
    private final float[][] kernelX = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    private final float[][] kernelY = {
        {-1, -2, -1},
        {0, 0, 0},
        {1, 2, 1}
    };

    @Override
    public float[][] getKernel() {
        // We'll apply both X and Y in processor
        throw new UnsupportedOperationException("Use getXKernel() and getYKernel() for Sobel.");
    }

    public float[][] getXKernel() {
        return kernelX;
    }

    public float[][] getYKernel() {
        return kernelY;
    }

    @Override
    public int getKernelWidth() {
        return 3;
    }

    @Override
    public int getKernelHeight() {
        return 3;
    }

    @Override
    public String getName() {
        return "Sobel Edge Detection";
    }
}
