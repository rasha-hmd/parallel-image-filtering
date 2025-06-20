package filters;

public class GaussianBlurFilter implements Filter {
    private final float[][] kernel = {
        {1/16f, 2/16f, 1/16f},
        {2/16f, 4/16f, 2/16f},
        {1/16f, 2/16f, 1/16f}
    };

    @Override
    public float[][] getKernel() {
        return kernel;
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
        return "Gaussian Blur";
    }
}
