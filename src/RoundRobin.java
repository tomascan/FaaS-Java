import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Política de distribución de acciones que implementa el algoritmo Round Robin.
 * Esta política asigna acciones a invocadores de forma secuencial y equitativa.
 */
public class RoundRobin implements Policy {
    /**
     * Distribuye las acciones entre los invocadores disponibles utilizando el algoritmo Round Robin.
     * Cada invocador recibe una acción de forma secuencial. Si un invocador no tiene suficiente memoria, se lanza una excepción.
     *
     * @param actions         Lista de acciones a distribuir.
     * @param invokers        Lista de invocadores disponibles.
     * @param memoryPerAction Memoria requerida por cada acción.
     * @return                Mapa que asocia cada invocador con una lista de acciones asignadas.
     * @throws IllegalStateException Si algún invocador no tiene suficiente memoria.
     */
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

