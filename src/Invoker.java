import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Invoker {
    private final ExecutorService executor;
    private final AtomicInteger memory;
    private int actionCount = 0;

    public Invoker(int mem) {
        this.memory = new AtomicInteger(mem);
        this.executor = Executors.newCachedThreadPool();
    }


    // Getter para memory
    public int getMemory() {
        return memory.get();
    }

    // Setter para memory
    public void setMemory(int mem) {
        this.memory.set(mem);
    }


    public boolean hasEnoughMemory(int requiredMemory) {
        return memory.get() >= requiredMemory;
    }

    public void reserveMemory(int memoryToReserve) {
        memory.addAndGet(-memoryToReserve);
    }

    public void releaseMemory(int memoryToRelease) {
        memory.addAndGet(memoryToRelease);
    }


    public int executeAction(Function<Map<String, Integer>, Integer> action, Map<String, Integer> parameters, int memoryRequired) {
        if (hasEnoughMemory(memoryRequired)) {
            reserveMemory(memoryRequired); // Reservar la memoria necesaria
            actionCount++; //Al reservar memoria para una accion se suma tambien la accion a la cuenta del Invoker
            try {
                return action.apply(parameters);
            } finally {
                releaseMemory(memoryRequired); // Liberar la memoria después de la acción
            }
        } else {
            throw new IllegalStateException("No hay suficiente memoria para ejecutar la acción");
        }
    }



    public Future<Integer> executeActionAsync(Function<Map<String, Integer>, Integer> action, Map<String, Integer> parameters, int memoryRequired) {
        return executor.submit(() -> {
            if (hasEnoughMemory(memoryRequired)) {
                reserveMemory(memoryRequired);
                try {
                    return action.apply(parameters);
                } finally {
                    releaseMemory(memoryRequired);
                }
            } else {
                throw new IllegalStateException("No hay suficiente memoria para ejecutar la acción de manera asíncrona");
            }
        });
    }

    public int getActionCount() {
        return actionCount;
    }



}