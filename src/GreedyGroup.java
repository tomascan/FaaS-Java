import java.util.ArrayList;
import java.util.List;

public class GreedyGroup implements Policy {
    @Override
    public List<Integer> distributeFunctions(int totalFunctions, List<Integer> availableInvokers) {
        List<Integer> functionsPerInvoker = new ArrayList<>();
        int numInvokers = availableInvokers.size();
        int remainingFunctions = totalFunctions;

        while (remainingFunctions > 0) {
            for (int i = 0; i < numInvokers && remainingFunctions > 0; i++) {
                int functions = Math.min(remainingFunctions, availableInvokers.get(i));
                functionsPerInvoker.add(functions);
                remainingFunctions -= functions;
            }
        }

        return functionsPerInvoker;
    }
}
