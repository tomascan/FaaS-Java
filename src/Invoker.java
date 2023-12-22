import java.util.Map;
import java.util.function.Function;

public class Invoker {
    private int memory;

    public Invoker(int mem) {
        this.memory = mem;
    }

    public boolean canHandleAction() {
        // Supongamos que todas las acciones requieren menos memoria que la disponible por ahora.
        return true;
    }

    public int executeAction(Function<Map<String, Integer>, Integer> action, Map<String, Integer> parameters) {
        if (hasEnoughMemory(256)) {
            memory -= 256; // Reservar memoria para la acción
            try {
                return action.apply(parameters);
            } finally {
                memory += 256; // Liberar memoria después de la acción
            }
        } else {
            throw new IllegalStateException("No hay suficiente memoria para ejecutar la acción");
        }
    }
    public boolean hasEnoughMemory(int requiredMemory) {
        return memory >= requiredMemory;
    }
    // Otros métodos según se necesite
}