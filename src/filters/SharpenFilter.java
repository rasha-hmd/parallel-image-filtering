package filters;

public class SharpenFilter implements Filter {
    private final float[][] kernel = {
        {0, -1, 0},
        {-1, 5, -1},
        {0, -1, 0}
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
        return "Sharpen";
    }
}
