import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BigGroup implements Policy {
    private final int groupSize;


    public BigGroup(int groupSize) {
        this.groupSize = groupSize;
    }

    @Override
    public Map<Invoker, List<Map<String, Integer>>> distributeActions(List<Map<String, Integer>> actions, List<Invoker> invokers, int memoryPerAction) {
        Map<Invoker, List<Map<String, Integer>>> allocation = new HashMap<>();
        int actionIndex = 0;

        while (actionIndex < actions.size()) {
            boolean assigned = false;
            for (Invoker inv : invokers) {
                int actionsCountForThisGroup = Math.min(groupSize, actions.size() - actionIndex);
                int requiredMemoryForGroup = actionsCountForThisGroup * memoryPerAction;

                if (inv.hasEnoughMemory(requiredMemoryForGroup)) {
                    List<Map<String, Integer>> groupActions = new ArrayList<>(actions.subList(actionIndex, actionIndex + actionsCountForThisGroup));
                    allocation.computeIfAbsent(inv, k -> new ArrayList<>()).addAll(groupActions);
                    actionIndex += actionsCountForThisGroup;
                    assigned = true;
                    break;
                }
            }
            if (!assigned) {
                throw new IllegalStateException("No hay suficientes Invokers con memoria disponible");
            }
        }
        return allocation;
    }
}
