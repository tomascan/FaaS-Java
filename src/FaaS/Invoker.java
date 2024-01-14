package FaaS;

import java.util.ArrayList;
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
    private final AtomicInteger memory; //Memoria del Invoker controlada de manera atómica para multithreading
    private int actionCount = 0; //Cantidad de acciones ejecutadas
    private final ExecutorService executor; //Ejecutor para manejar aciones asincronas (Thread Pool)
    private final List<Observer> observers = new ArrayList<>(); //Lista de observers que apuntaran al controller


    /**
     * Constructor del Invoker.
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
     * Obtiene el número de acciones ejecutadas por este Invoker.
     *
     * @return Número de acciones ejecutadas.
     */
    public int getActionCount() {
        return actionCount;
    }


    /**
     * Obtiene la cantidad actual de memoria disponible en el Invoker.
     *
     * @return Cantidad de memoria disponible.
     */
    public int getMemory() {
        return memory.get();
    }


    // ------------------ MEMORIA -------------------------
    /**
     * Establece una cantidad de memoria para el Invoker.
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
//        System.out.println("Invoker "+ getId()+". Disponible: "+ getMemory());
        return memory.get() >= requiredMemory;
    }


    /**
     * Reserva una cantidad específica de memoria para una operación.
     *
     * @param memoryToReserve Cantidad de memoria a reservar.
     */
    public void reserveMemory(int memoryToReserve) {
//        System.out.println("Invoker "+ getId()+" Reservando memoria: "+memoryToReserve);
        memory.addAndGet(-memoryToReserve);
//        System.out.println("\tDespues de reservar. Memoria Disponible: "+ getMemory());

    }

    /**
     * Libera una cantidad específica de memoria previamente reservada.
     *
     * @param memoryToRelease Cantidad de memoria a liberar.
     */
    public void releaseMemory(int memoryToRelease) {
//        System.out.println("Invoker "+ getId()+" Liberando memoria: "+memoryToRelease);
        memory.addAndGet(memoryToRelease);
//        System.out.println("\tDespues de liberar, Memoria Disponible: "+ getMemory());

    }

    // ---------------------- EXECUTE ------------------------

    /**
     * Ejecuta una acción de manera sincrónica.
     *
     * @param action         Función que representa la acción a ejecutar.
     * @param parameters     Parámetros necesarios para la acción.
     * @param memoryRequired Cantidad de memoria requerida para ejecutar la acción.
     * @return Resultado de la acción ejecutada.
     */
    public Object executeAction(Function<Map<String, Object>, Object> action, Map<String, Object> parameters, int memoryRequired) {
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = action.apply(parameters);
        } finally {
            releaseMemory(memoryRequired); // Release memory after action
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime; // Calculate execution time

            Metric metric = new Metric(this.id, executionTime, memoryRequired);
            notifyObservers(metric); // Notify the Controller if its defined
        }

        actionCount++; // Increase action count
        return result;
    }


    /**
     * Ejecuta una acción de manera asincrona.
     *
     * @param action         Función que representa la acción a ejecutar.
     * @param parameters     Parámetros necesarios para la ejecución de la acción.
     * @param memoryRequired Cantidad de memoria requerida para ejecutar la acción.
     * @return Future representando el resultado pendiente de la acción.
     * @throws IllegalStateException Si no hay suficiente memoria para ejecutar la acción.
     */
    public Future<Object> executeActionAsync(Function<Map<String, Object>, Object> action, Map<String, Object> parameters, int memoryRequired) {
            return executor.submit(() -> {
                try {
                    actionCount++;
                    return action.apply(parameters);
                } finally {
                    releaseMemory(memoryRequired);
                }
            });
        }



    //OBSERVER -------------------------------------------------------
    /**
     * Registra un observador para recibir actualizaciones sobre métricas.
     *
     * @param observer El observador a registrar.
     */
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Elimina un observador de la lista de observadores registrados.
     *
     * @param observer El observador a eliminar.
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifica a todos los observadores registrados con la métrica proporcionada.
     *
     * @param metric La métrica a enviar a los observadores.
     */
    public void notifyObservers(Metric metric) {
        for (Observer observer : observers) {
            observer.updateMetrics(metric);
        }
    }
}