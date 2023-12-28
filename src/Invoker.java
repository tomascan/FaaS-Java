import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final int id;
    private final ExecutorService executor; //Ejecutor para manejar aciones asincronas
    private final AtomicInteger memory; //Memoria del Invoker controlada de manera atómica para multithreading
    private int actionCount = 0; //Cantidad de acciones ejecutadas

    private Controller controller;
    private List<Observer> observers = new ArrayList<>(); //Lista de observers que apuntaran al controller

    private Map<String, Map<Map<String, Integer>, Integer>> cache = new HashMap<>(); // cache para el Decorator


    /**
     * Constructor de Invoker.
     * Inicializa el Invoker con una cantidad específica de memoria y un servicio de ejecución.
     *
     * @param id  Número de identidad del Invoker creado
     * @param mem Cantidad inicial de memoria disponible.
     */
    public Invoker(int id, int mem) {
        this.id = id;
        this.memory = new AtomicInteger(mem);
        this.executor = Executors.newCachedThreadPool();
    }

    public int getId() {
        return id;
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
     * @param action         Función que representa la acción a ejecutar.
     * @param parameters     Parámetros necesarios para la ejecución de la acción.
     * @param memoryRequired Cantidad de memoria requerida para ejecutar la acción.
     * @return Resultado de la acción ejecutada.
     */
    public int executeAction(Function<Map<String, Integer>, Integer> action, Map<String, Integer> parameters, int memoryRequired) {
        long startTime = System.currentTimeMillis(); // Start time
        reserveMemory(memoryRequired); // Reserve memory for the action
        int result = 0;

        try {
            result = action.apply(parameters);
        } finally {
            releaseMemory(memoryRequired); // Release memory after action
            long endTime = System.currentTimeMillis(); // End time
            long executionTime = endTime - startTime; // Calculate execution time

            Metric metric = new Metric(this.id, executionTime, memoryRequired);
            notifyObservers(metric); // Notify the Controller
        }

        actionCount++; // Increase action count
        return result;
    }


    /**
     * Ejecuta una acción de manera asincrónica.
     *
     * @param action         Función que representa la acción a ejecutar.
     * @param parameters     Parámetros necesarios para la ejecución de la acción.
     * @param memoryRequired Cantidad de memoria requerida para ejecutar la acción.
     * @return Future representando el resultado pendiente de la acción.
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


    //OBSERVER -------------------------------------------------------

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Metric metric) {
        for (Observer observer : observers) {
            observer.updateMetrics(metric);
        }
    }


// DECORATOR ---------------------------------------------------------------

    public Integer getCachedResult(String actionName, Map<String, Integer> parameters) {
        return cache.getOrDefault(actionName, new HashMap<>()).get(parameters);
    }

    public void cacheResult(String actionName, Map<String, Integer> parameters, Integer result) {
        cache.computeIfAbsent(actionName, k -> new HashMap<>()).put(parameters, result);
    }

}