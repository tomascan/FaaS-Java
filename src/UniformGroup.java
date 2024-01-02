import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Política de distribución de acciones que asigna un número uniforme de acciones a cada invocador.
 * Esta política intenta asignar un grupo de tamaño igual de acciones a cada invocador hasta que se hayan asignado todas las acciones.
 */
public class UniformGroup implements Policy {
    private final int groupSize;

    /**
     * Constructor para UniformGroup.
     *
     * @param groupSize El tamaño del grupo de acciones que se asignará uniformemente a cada invocador.
     */
    public UniformGroup(int groupSize) {
        this.groupSize = groupSize;
    }

    /**
     * Distribuye las acciones entre los invocadores de manera que cada uno reciba un número igual de acciones, hasta un máximo definido.
     * Si un invocador no tiene suficiente memoria, se pasa al siguiente invocador.
     *
     * @param actions         Lista de acciones a distribuir.
     * @param invokers        Lista de invocadores disponibles.
     * @param memoryPerAction Memoria requerida por cada acción.
     * @return                Mapa que asocia cada invocador con una lista de acciones asignadas.
     * @throws IllegalStateException Si todos los invocadores alcanzan su capacidad máxima y no pueden manejar más acciones.
     */
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
            if (!currentInvoker.hasEnoughMemory(memoryPerAction)) {
                invokerIndex = (invokerIndex + 1) % invokers.size();
                currentInvoker = invokers.get(invokerIndex);
                if (!currentInvoker.hasEnoughMemory(memoryPerAction)) {
                    throw new IllegalStateException("Uno de los Invokers no tiene suficiente memoria");
                }
            }
            currentInvoker.reserveMemory(memoryPerAction);
            allocation.computeIfAbsent(currentInvoker, k -> new ArrayList<>()).add(action);
            currentCount++;
        }

        return allocation;
    }
}
