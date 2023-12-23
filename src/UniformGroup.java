import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniformGroup implements Policy {
    private final int groupSize;

    public UniformGroup(int groupSize) {
        this.groupSize = groupSize;
    }

    @Override
    public Map<Invoker, List<Map<String, Integer>>> distributeActions(List<Map<String, Integer>> actions, List<Invoker> invokers, int memoryPerAction) {
        Map<Invoker, List<Map<String, Integer>>> allocation = new HashMap<>();
        int currentCount = 0;
        int invokerIndex = 0;

        for (Map<String, Integer> action : actions) {
            if (currentCount == groupSize) {
                currentCount = 0;
                invokerIndex = (invokerIndex + 1) % invokers.size();
            }
            Invoker currentInvoker = invokers.get(invokerIndex);
            allocation.computeIfAbsent(currentInvoker, k -> new ArrayList<>()).add(action);
            currentCount++;
        }

        return allocation;
    }
}
