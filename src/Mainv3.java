import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
public class Mainv3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Controller controller = new Controller(4, 2024);
        controller.setPolicy(new RoundRobin());

        // Initialize and register Invokers
        for (int i = 0; i < controller.getInvokers().length; i++) {
            controller.getInvokers()[i].registerObserver(controller);
        }

        // Register actions
        controller.registerAction("sumar", Actions.sumar, 100);
        controller.registerAction("restar", Actions.restar, 256);
        controller.registerAction("multiplicar", Actions.multiplicar, 100);
        controller.registerAction("dividir", Actions.dividir, 256);
        controller.registerAction("dormir", Actions.dormir, 100);

        // Execute group actions
        List<Map<String, Integer>> actions = new ArrayList<>();
        for (int i = 0; i < 35; i++) {
            actions.add(Map.of("time", i*10));
        }
        controller.invoke("dormir", actions);

        // Print action counts for each Invoker
        for (Invoker invoker : controller.getInvokers()) {
            System.out.println("Invoker " + invoker.getId() + " realizó " + invoker.getActionCount() + " acciones.");
        }

        // Analyze and display metrics
        controller.analyzeMetrics();
    }
}
