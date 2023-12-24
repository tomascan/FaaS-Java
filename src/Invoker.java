import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;


/**
 * Clase Invoker que administra la ejecución de acciones, tanto sincrónicas como asincrónicas,
 * gestionando la memoria requerida para cada acción y manteniendo un contador de acciones ejecutadas.
 */
public class Invoker {
    private final ExecutorService executor; //Ejecutor para manejar aciones asincronas
    private final AtomicInteger memory; //Memoria del Invoker controlada de manera atómica para multithreading
    private int actionCount = 0; //Cantidad de acciones ejecutadas

    /**
     * Constructor de Invoker.
     * Inicializa el Invoker con una cantidad específica de memoria y un servicio de ejecución.
     *
     * @param mem Cantidad inicial de memoria disponible.
     */
    public Invoker(int mem) {
        this.memory = new AtomicInteger(mem);
        this.executor = Executors.newCachedThreadPool();
    }


    /**
     * Obtiene la cantidad actual de memoria disponible en el Invoker.
     *
     * @return Cantidad de memoria disponible.
     */
    public int getMemory() {
        return memory.get();
    }

    /**
     * Obtiene el número de acciones ejecutadas por este Invoker.
     *
     * @return Número de acciones ejecutadas.
     */
    public int getActionCount() {
        return actionCount;
    }


    /**
     * Establece una nueva cantidad de memoria disponible en el Invoker.
     *
     * @param mem Nueva cantidad de memoria a establecer.
     */
    public void setMemory(int mem) {
        this.memory.set(mem);
    }

    /**
     * Verifica si el Invoker tiene suficiente memoria disponible para una operación.
     *
     * @param requiredMemory Cantidad de memoria requerida para la operación.
     * @return true si hay suficiente memoria disponible, false en caso contrario.
     */
    public boolean hasEnoughMemory(int requiredMemory) {
        return memory.get() >= requiredMemory;
    }


    /**
     * Reserva una cantidad específica de memoria para una operación.
     *
     * @param memoryToReserve Cantidad de memoria a reservar.
     */
    public void reserveMemory(int memoryToReserve) {
        memory.addAndGet(-memoryToReserve);
    }

    /**
     * Libera una cantidad específica de memoria previamente reservada.
     *
     * @param memoryToRelease Cantidad de memoria a liberar.
     */
    public void releaseMemory(int memoryToRelease) {
        memory.addAndGet(memoryToRelease);
    }


    /**
     * Ejecuta una acción de manera sincrónica.
     *
     * @param action          Función que representa la acción a ejecutar.
     * @param parameters      Parámetros necesarios para la ejecución de la acción.
     * @param memoryRequired  Cantidad de memoria requerida para ejecutar la acción.
     * @return                Resultado de la acción ejecutada.
     */
    public int executeAction(Function<Map<String, Integer>, Integer> action, Map<String, Integer> parameters, int memoryRequired) {
            actionCount++; //Al reservar memoria para una accion se suma tambien la accion a la cuenta del Invoker
                return action.apply(parameters);
    }


    /**
     * Ejecuta una acción de manera asincrónica.
     *
     * @param action          Función que representa la acción a ejecutar.
     * @param parameters      Parámetros necesarios para la ejecución de la acción.
     * @param memoryRequired  Cantidad de memoria requerida para ejecutar la acción.
     * @return                Future representando el resultado pendiente de la acción.
     * @throws IllegalStateException Si no hay suficiente memoria para ejecutar la acción.
     */
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



}