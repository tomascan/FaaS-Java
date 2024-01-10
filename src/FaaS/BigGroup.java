package FaaS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Implementación de la política de distribución de acciones que agrupa varias acciones para su ejecución.
 * Esta política intenta agrupar un número específico de acciones y asignarlas a un invocador que tenga suficiente memoria.
 */
public class BigGroup implements Policy {
    private int groupSize;

    /**
     * Constructor para FaaS.BigGroup.
     *
     * @param groupSize El tamaño del grupo de acciones a agrupar para cada invocador.
     */
    public BigGroup(int groupSize) {
        this.groupSize = groupSize;
    }

    /**
     * Distribuye las acciones entre los invocadores disponibles, agrupándolas según el tamaño de grupo definido.
     * Si un invocador no tiene suficiente memoria, intenta con grupos más pequeños.
     *
     * @param actions         Lista de acciones a distribuir.
     * @param invokers        Lista de invocadores disponibles.
     * @param memoryPerAction Memoria requerida por cada acción.
     * @return Mapa que asocia cada invocador con una lista de acciones.
     * @throws IllegalStateException Si no se pueden asignar todas las acciones debido a la falta de memoria.
     */
    @Override
    public Map<Invoker, List<Map<String, Object>>> distributeActions(List<Map<String, Object>> actions, List<Invoker> invokers, int memoryPerAction) {
        Map<Invoker, List<Map<String, Object>>> allocation = new HashMap<>();
        int actionIndex = 0;

        while (actionIndex < actions.size()) {
            int end = Math.min(actionIndex + groupSize, actions.size());
            List<Map<String, Object>> groupActions = actions.subList(actionIndex, end);
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
                // Si ningún FaaS.Invoker tiene suficiente memoria, se intenta con grupos más pequeños
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
