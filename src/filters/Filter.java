package filters;

public interface Filter {
    float[][] getKernel();
    int getKernelWidth();
    int getKernelHeight();
    String getName();
}
