package FaaS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Política de distribución de acciones que asigna acciones a invocadores de forma codiciosa.
 * Esta política intenta llenar cada invocador con tantas acciones como sea posible antes de pasar al siguiente.
 */
public class GreedyGroup implements Policy {
    /**
     * Distribuye las acciones entre los invocadores de forma codiciosa.
     * Asigna acciones a un invocador hasta que se queda sin memoria, y luego pasa al siguiente.
     *
     * @param actions         Lista de acciones a distribuir.
     * @param invokers        Lista de invocadores disponibles.
     * @param memoryPerAction Memoria requerida por cada acción.
     * @return Mapa que asocia cada invocador con una lista de acciones asignadas.
     * @throws IllegalStateException Si no hay suficientes invocadores con memoria disponible para todas las acciones.
     */
    @Override
    public Map<Invoker, List<Map<String, Object>>> distributeActions(List<Map<String, Object>> actions, List<Invoker> invokers, int memoryPerAction) {
        Map<Invoker, List<Map<String, Object>>> allocation = new HashMap<>();
        int invokerIndex = 0;

        for (Map<String, Object> action : actions) {
            Invoker currentInvoker = invokers.get(invokerIndex);
            while (!currentInvoker.hasEnoughMemory(memoryPerAction)) {
                invokerIndex++;
                if (invokerIndex >= invokers.size()) {
                    throw new IllegalStateException("No hay suficientes Invokers con memoria disponible");
                }
                currentInvoker = invokers.get(invokerIndex);
            }

            allocation.computeIfAbsent(currentInvoker, k -> new ArrayList<>()).add(action);
            currentInvoker.reserveMemory(memoryPerAction);
        }
        
        return allocation;
    }
}
