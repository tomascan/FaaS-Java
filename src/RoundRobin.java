import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class RoundRobin implements Policy {
    @Override
    public Map<Invoker, List<Map<String, Integer>>> distributeActions(List<Map<String, Integer>> actions, List<Invoker> invokers, int memoryPerAction) {
        Map<Invoker, List<Map<String, Integer>>> allocation = new HashMap<>();
        int invokerIndex = 0;

        for (Map<String, Integer> action : actions) {
            Invoker currentInvoker = invokers.get(invokerIndex);
            if (!currentInvoker.hasEnoughMemory(memoryPerAction)) {
                throw new IllegalStateException("Uno de los Invokers no tiene suficiente memoria");
            }
            currentInvoker.reserveMemory(memoryPerAction);
            allocation.computeIfAbsent(currentInvoker, k -> new ArrayList<>()).add(action);

            invokerIndex = (invokerIndex + 1) % invokers.size();
        }

        return allocation;
    }
}

