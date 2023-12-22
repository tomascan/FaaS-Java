import java.util.List;

public interface Policy {
    List<Integer> distributeFunctions(int totalFunctions, List<Integer> availableInvokers);
}
