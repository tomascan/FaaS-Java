import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class GreedyGroup implements Policy {
    @Override
    public Map<Invoker, List<Map<String, Integer>>> distributeActions(List<Map<String, Integer>> actions, List<Invoker> invokers, int memoryPerAction) {
        Map<Invoker, List<Map<String, Integer>>> allocation = new HashMap<>();
        int invokerIndex = 0;

        for (Map<String, Integer> action : actions) {
            if (invokerIndex >= invokers.size()) {
                throw new IllegalStateException("No hay suficientes Invokers con memoria disponible");
            }

            Invoker currentInvoker = invokers.get(invokerIndex);
            if (!currentInvoker.hasEnoughMemory(memoryPerAction)) {
                invokerIndex++; // Avanzar al siguiente Invoker
                if (invokerIndex < invokers.size()) {
                    currentInvoker = invokers.get(invokerIndex);
                } else {
                    continue;
                }
            }

            allocation.computeIfAbsent(currentInvoker, k -> new ArrayList<>()).add(action);
            currentInvoker.reserveMemory(memoryPerAction); // Asumiendo que este método ajusta la memoria disponible
        }

        // Liberar la memoria reservada en los Invokers al final
        for (Invoker invoker : allocation.keySet()) {
            invoker.releaseMemory(memoryPerAction * allocation.get(invoker).size());
        }

        return allocation;
    }
}
