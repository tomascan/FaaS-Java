package FaaS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Mainv3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Controller controller = new Controller(4, 2024);
        controller.setPolicy(new RoundRobin());

        // Initialize and register Invokers
        for (int i = 0; i < controller.getInvokers().length; i++) {
            controller.getInvokers()[i].registerObserver(controller);
        }

        // Registar acciones
        controller.registerAction("sumar", Actions.sumar, 100);
        controller.registerAction("restar", Actions.restar, 256);
        controller.registerAction("multiplicar", Actions.multiplicar, 100);
        controller.registerAction("dividir", Actions.dividir, 256);
        controller.registerAction("dormir", Actions.dormir, 150);

        // Asignar grupo acciones
        List<Map<String, Object>> actions = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            actions.add(Map.of("time", i*10)); // El sleep va incrementando levemente
        }

        //Ejecutar grupo de acciones
        controller.invoke("dormir", actions);

        // Printear Resultados
        for (Invoker invoker : controller.getInvokers()) {
            System.out.println("FaaS.Invoker " + invoker.getId() + " realizó " + invoker.getActionCount() + " acciones.");
        }

        controller.analyzeMetrics();
    }
}


