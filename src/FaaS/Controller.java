package FaaS;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Clase FaaS.Controller que gestiona la invocación de acciones utilizando múltiples invocadores.
 * Permite la registración y ejecución de acciones, tanto de manera sincrónica como asincrónica,
 * y administra la distribución de las acciones entre los invocadores según una política definida.
 */
public class Controller implements Observer{
    final Invoker[] invokers; //Lista de Invokers
    private Policy policy; //FaaS.Policy Manager

    // Mapa que asocia nombres de acción con sus respectivas funciones.
    private final Map<String, Function<Map<String, Object>, Object>> actions = new HashMap<>();
    private final Map<String, Integer> actionMemory = new HashMap<>(); // Mapa almacena la memoria requerida para cada acción.

    private final List<Metric> metrics = new ArrayList<>();

    /**
     * Constructor de FaaS.Controller.
     * Inicializa un array de invocadores con la capacidad de memoria especificada.
     *
     * @param numberOfInvokers Número de invocadores a crear.
     * @param invokerMemory    Memoria asignada a cada invocador.
     */
    public Controller(int numberOfInvokers, int invokerMemory) {
        this.invokers = new Invoker[numberOfInvokers];
        for (int i = 0; i < numberOfInvokers; i++) {
            invokers[i] = new Invoker(i, invokerMemory);
        }
    }


    /**
     * Establece la política de distribución de acciones.
     *
     * @param policy Objeto FaaS.Policy a establecer.
     */
    public void setPolicy(Policy policy){
        this.policy = policy;
    }


    /**
     * Registra una nueva acción con su nombre, función y memoria requerida.
     *
     * @param actionName Nombre de la acción.
     * @param action     Función que implementa la acción.
     * @param memory     Memoria requerida para la acción.
     */
    public void registerAction(String actionName, Function<Map<String, Object>, Object> action, int memory) {
        actions.put(actionName, action);
        actionMemory.put(actionName, memory); // Almacenar la memoria requerida para la acción
    }


    public Invoker[] getInvokers() {
        return invokers;
    }


    /**
     * Realiza una sobrecarga para una acción individual.
     *
     * @param actionName  Nombre de la acción a invocar.
     * @param parameters  Parámetros requeridos para la acción.
     * @return            Resultado de la invocación de la acción.
     */
    public Object invoke(String actionName, Map<String, Object> parameters) {
        return this.invoke(actionName, List.of(parameters)).get(0);
    }

    /**
     * Realiza invocaciones grupales de una acción.
     *
     * @param actionName  Nombre de la acción a invocar.
     * @param parameters  Lista de mapas de parámetros para cada invocación.
     * @return            Lista de resultados de las invocaciones.
     */
    public  List<Object> invoke(String actionName, List<Map<String, Object>> parameters) {
        List<Object> results = new ArrayList<>();
        if (policy == null) {
            throw new IllegalStateException("Política no establecida");
        }// La política distribuye las acciones y retorna la asignación

        Map<Invoker, List<Map<String, Object>>> allocation = policy.distributeActions(parameters, Arrays.asList(invokers), actionMemory.getOrDefault(actionName, 0));
        // Ejecuta las acciones
        for (Map.Entry<Invoker, List<Map<String, Object>>> entry : allocation.entrySet()) {
            Invoker invoker = entry.getKey();
            List<Map<String, Object>> invokerActions = entry.getValue();
//            int requiredMemory = invokerActions.size() * actionMemory.getOrDefault(actionName, 0);

            for (Map<String, Object> actionParams : invokerActions) {
                results.add(invoker.executeAction(actions.get(actionName), actionParams, actionMemory.getOrDefault(actionName, 0)));
            }
        }
        return results;
    }

    /**
     * Realiza una invocación asincrónica de la acción especificada.
     *
     * @param actionName Nombre de la acción a invocar.
     * @param parameters Parámetros requeridos para la acción.
     * @return Future representando el resultado pendiente de la invocación.
     * @throws IllegalStateException Si no hay suficiente memoria para ejecutar la acción.
     */
    public Future<Object> invoke_async(String actionName, Map<String, Object> parameters) {
        int memoryRequired = actionMemory.getOrDefault(actionName, 0);

        for (Invoker invoker : invokers) {
            if (invoker.hasEnoughMemory(memoryRequired)) {
                return invoker.executeActionAsync(actions.get(actionName), parameters, memoryRequired);
            }
        }
        throw new IllegalStateException("No hay suficiente memoria para ejecutar la acción de manera asíncrona");
    }



    //INVOCAR ACCIONES AGRUPADAS DESDE FICHEROS
    public void invokeFile(List<Map<String, Object>> actionDataList) {
        if (policy == null) {
            throw new IllegalStateException("Política no establecida");
        }

        // Agrupar acciones por nombre
        Map<String, List<Map<String, Object>>> groupedActions = new HashMap<>();
        for (Map<String, Object> actionData : actionDataList) {
            String actionName = (String) actionData.get("actionName");
            if (actionData.get("parameters") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> parameters = (Map<String, Object>) actionData.get("parameters");
                groupedActions.computeIfAbsent(actionName, k -> new ArrayList<>()).add(parameters);
            }
        }

        // Imprimir las acciones agrupadas
        System.out.println("Acciones agrupadas por nombre:");
        for (String actionName : groupedActions.keySet()) {
            System.out.println("Nombre de la acción: " + actionName + ", Cantidad de acciones: " + groupedActions.get(actionName).size());
        }

        // Invocar acciones agrupadas
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupedActions.entrySet()) {
            String actionName = entry.getKey();
            List<Map<String, Object>> paramsList = entry.getValue();

            System.out.println("Invocando acciones para: " + actionName);

            // Ejecutar acciones agrupadas aplicando la política
            List<Object> results = invoke(actionName, paramsList);
            // Imprimir resultados de la invocación
            System.out.println("Resultados de la acción '" + actionName + "': " + results);
        }
    }




    //--------------------------------------------------OBSERVER------------------------------------------

    /**
     * Actualiza las métricas del sistema cada vez que un invocador completa una acción.
     * Este método es llamado automáticamente por los invocadores.
     *
     * @param metric La métrica generada por el invocador después de ejecutar una acción.
     */
    @Override
    public void updateMetrics(Metric metric) {
        metrics.add(metric);
        System.out.println("Metric received: " + metric);
    }


    /**
     * Analiza y muestra estadísticas basadas en las métricas recogidas, como el tiempo promedio,
     * máximo y mínimo de ejecución, y el uso promedio de memoria por invocador.
     */
    public void analyzeMetrics() {
        System.out.println("\nMétricas principales:");

        // Calcula y muestra el tiempo promedio de ejecución de todas las acciones
        double avgTime = metrics.stream()
                .mapToDouble(Metric::getExecutionTime)
                .average()
                .orElse(0.0);
        System.out.println("\tTiempo promedio de ejecución: " + avgTime + " ms");

        // Calcula y muestra el tiempo máximo y mínimo de ejecución de todas las acciones
        long maxTime = metrics.stream()
                .mapToLong(Metric::getExecutionTime)
                .max()
                .orElse(0L);
        System.out.println("\tTiempo máximo de ejecución: " + maxTime + " ms");

        long minTime = metrics.stream()
                .mapToLong(Metric::getExecutionTime)
                .min()
                .orElse(0L);
        System.out.println("\tTiempo mínimo de ejecución: " + minTime + " ms");

        // Calcula y muestra el tiempo total de ejecución de todas las acciones
        long totalExecutionTime = metrics.stream()
                .mapToLong(Metric::getExecutionTime)
                .sum();
        System.out.println("\tTiempo total de ejecución: " + totalExecutionTime + " ms");

        // Calcula y muestra la utilización total de memoria de cada Invoker
        Map<Integer, Integer> avgMemoryUsagePerInvoker = metrics.stream()
                .collect(Collectors.groupingBy(Metric::getId,
                        Collectors.summingInt(Metric::getMemoryUsed)));

        avgMemoryUsagePerInvoker.forEach((invokerId, avgMemoryUsage) -> System.out.println("\tInvoker " + invokerId + " - Memoria Total: " + avgMemoryUsage + " MB"));
    }




    // DECORATOR --------------------------------------------
    private final Map<String, Map<Map<String, Object>, Object>> cache = new HashMap<>();

    /**
     * Obtiene un resultado previamente almacenado en la caché, si está disponible.
     *
     * @param actionName Nombre de la acción.
     * @param parameters Parámetros de la acción.
     * @return El resultado almacenado en caché, o null si no está disponible.
     */
    public Integer getCachedResult(String actionName, Map<String, Object> parameters) {
        return (Integer) cache.getOrDefault(actionName, new HashMap<>()).get(parameters);
    }

    /**
     * Almacena un resultado en la caché para su uso futuro.
     *
     * @param actionName Nombre de la acción.
     * @param parameters Parámetros de la acción.
     * @param result     El resultado de la acción.
     */
    public void cacheResult(String actionName, Map<String, Object> parameters, Integer result) {
        cache.computeIfAbsent(actionName, k -> new HashMap<>()).put(parameters, result);
    }


    /**
     * Imprime el contenido actual de la caché, mostrando las acciones, sus parámetros y resultados.
     */
    public void printCache() {
        System.out.println("\n  Contenido de la caché:");
        for (Map.Entry<String, Map<Map<String, Object>, Object>> entry : cache.entrySet()) {
            String actionName = entry.getKey();
            Map<Map<String, Object>, Object> cacheMap = entry.getValue();
            System.out.println("\t\tAcción: " + actionName);
            cacheMap.forEach((params, result) -> System.out.println(" \t\t\tParámetros: " + params + " -> Resultado: " + result));
        }
    }

}

