import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Invoker {
    private final ExecutorService executor;
    private int memory;

    public Invoker(int mem) {
        this.memory = mem;
        this.executor = Executors.newCachedThreadPool();
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


    public Future<Integer> executeActionAsync(Function<Map<String, Integer>, Integer> action, Map<String, Integer> parameters) {
        return executor.submit(() -> {
            // Aquí incluirías la lógica para manejar la memoria
            return action.apply(parameters);
        });
    }

    public boolean hasEnoughMemory(int requiredMemory) {
        return memory >= requiredMemory;
    }




}