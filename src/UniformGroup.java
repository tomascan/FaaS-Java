import java.util.ArrayList;
import java.util.List;

public class UniformGroup implements Policy {
    @Override
    public List<Integer> distributeFunctions(int totalFunctions, List<Integer> availableInvokers) {
        List<Integer> functionsPerInvoker = new ArrayList<>();
        int numInvokers = availableInvokers.size();
        int functionsPerInvokerCount = totalFunctions / numInvokers;
        int remainingFunctions = totalFunctions % numInvokers;

        for (int i = 0; i < numInvokers; i++) {
            int functions = functionsPerInvokerCount + (i < remainingFunctions ? 1 : 0);
            functionsPerInvoker.add(functions);
        }

        return functionsPerInvoker;
    }
}
