import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Controller {
    final Invoker[] invokers;
    private Policy policy;

    private Map<String, Function<Map<String, Integer>, Integer>> actions = new HashMap<>();
    private Map<String, Integer> actionMemory = new HashMap<>();

    // Constructor
    public Controller(int numberOfInvokers, int invokerMemory) {
        this.invokers = new Invoker[numberOfInvokers];
        for (int i = 0; i < numberOfInvokers; i++) {
            invokers[i] = new Invoker(invokerMemory);
        }
    }

    public void setPolicy(Policy policy){
        this.policy = policy;
    }

    public void registerAction(String actionName, Function<Map<String, Integer>, Integer> action, int memory) {
        actions.put(actionName, action);
        actionMemory.put(actionName, memory); // Almacenar la memoria requerida para la acción
    }

    // Sobrecarga para invocación individual
    public int invoke(String actionName, Map<String, Integer> parameters) {
        return this.invoke(actionName, List.of(parameters)).get(0);
    }

    // Metodo para invocaciones grupales
    public List<Integer> invoke(String actionName, List<Map<String, Integer>> parameters) {
        List<Integer> results = new ArrayList<>();
        if (policy == null) {
            throw new IllegalStateException("Política no establecida");
        }// La política distribuye las acciones y retorna la asignación
        Map<Invoker, List<Map<String, Integer>>> allocation = policy.distributeActions(parameters, Arrays.asList(invokers), actionMemory.getOrDefault(actionName, 0));

        // Verifica la memoria y ejecuta las acciones

        for (Map.Entry<Invoker, List<Map<String, Integer>>> entry : allocation.entrySet()) {
            Invoker invoker = entry.getKey();
            List<Map<String, Integer>> invokerActions = entry.getValue();
            int requiredMemory = invokerActions.size() * actionMemory.getOrDefault(actionName, 0);

            if (!invoker.hasEnoughMemory(requiredMemory)) {
                throw new IllegalStateException("Uno de los Invokers no tiene suficiente memoria");
            }

            for (Map<String, Integer> actionParams : invokerActions) {
                results.add(invoker.executeAction(actions.get(actionName), actionParams, requiredMemory));
            }
        }

        return results;
    }

    // Metodo para invocaciones asincronas y multithreading
    public Future<Integer> invoke_async(String actionName, Map<String, Integer> parameters) {
        int memoryRequired = actionMemory.getOrDefault(actionName, 0);

        for (Invoker invoker : invokers) {
            if (invoker.hasEnoughMemory(memoryRequired)) {
                return invoker.executeActionAsync(actions.get(actionName), parameters, memoryRequired);
            }
        }
        throw new IllegalStateException("No hay suficiente memoria para ejecutar la acción de manera asíncrona");
    }
}

    // Otros métodos según se necesite


