import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Controller {
    private Invoker[] invokers;

    private Map<String, Function<Map<String, Integer>, Integer>> actions = new HashMap<>();
    private Map<String, Integer> actionMemory = new HashMap<>();

    public Controller(int numberOfInvokers, int invokerMemory) {
        this.invokers = new Invoker[numberOfInvokers];
        for (int i = 0; i < numberOfInvokers; i++) {
            invokers[i] = new Invoker(invokerMemory);
        }
    }

    public void registerAction(String actionName, Function<Map<String, Integer>, Integer> action, int memory) {
        actions.put(actionName, action);
        actionMemory.put(actionName, memory); // Almacenar la memoria requerida para la acción
    }

    // Sobrecarga para invocación individual
    public int invoke(String actionName, Map<String, Integer> parameters) {
        return this.invoke(actionName, List.of(parameters)).get(0);
    }
    public List<Integer> invoke(String actionName, List<Map<String, Integer>> parameters) {
        List<Integer> results = new ArrayList<>();
        int memoryPerAction = actionMemory.getOrDefault(actionName, 0);
        int requiredMemory = memoryPerAction * parameters.size(); // Asumimos que cada acción necesita 256MB

        for (Invoker invoker : invokers) {
            if (invoker.hasEnoughMemory(requiredMemory)) {
                for (Map<String, Integer> param : parameters) {
                    results.add(invoker.executeAction(actions.get(actionName), param, requiredMemory));
                }
                return results;
            }
        }
        throw new IllegalStateException("No hay suficiente memoria para ejecutar el grupo de invocaciones");
    }


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


