import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BigGroup implements Policy {
    private int groupSize;

    public BigGroup(int groupSize) {
        this.groupSize = groupSize;
    }

    @Override
    public Map<Invoker, List<Map<String, Integer>>> distributeActions(List<Map<String, Integer>> actions, List<Invoker> invokers, int memoryPerAction) {
        Map<Invoker, List<Map<String, Integer>>> allocation = new HashMap<>();
        int actionIndex = 0;

        while (actionIndex < actions.size()) {
            int end = Math.min(actionIndex + groupSize, actions.size());
            List<Map<String, Integer>> groupActions = actions.subList(actionIndex, end);
            int requiredMemoryForGroup = groupActions.size() * memoryPerAction;

            boolean allocated = false;
            for (Invoker inv : invokers) {
                if (inv.hasEnoughMemory(requiredMemoryForGroup)) {
                    allocation.computeIfAbsent(inv, k -> new ArrayList<>()).addAll(groupActions);
                    inv.reserveMemory(requiredMemoryForGroup);
                    allocated = true;
                    actionIndex = end;
                    break;
                }
            }

            if (!allocated) {
                // Si ningún Invoker tiene suficiente memoria, se intenta con grupos más pequeños
                if (groupSize > 1) {
                    groupSize--;
                } else {
                    throw new IllegalStateException("No hay suficientes Invokers con memoria disponible");
                }
            }
        }

        return allocation;
    }
}
