package FaaS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Controller controller = new Controller(5,2048);
        controller.setPolicy(new BigGroup(3));

        // Registro de acciones
        controller.registerAction("sumar", Actions.sumar, 256);
        controller.registerAction("restar", Actions.restar, 256);
        controller.registerAction("multiplicar", Actions.multiplicar, 256);
        controller.registerAction("dividir", Actions.dividir, 256);
        controller.registerAction("dormir", Actions.dormir, 100);



        // Ejemplos de uso individual
        Function<Map<String, Object>, Object> action = Actions.sumar;
        Map<String, Object> parameters = Map.of("x", 10, "y", 10);
        Object result = action.apply(parameters);
        System.out.println("Suma: " +result);

        System.out.println("Suma: " + controller.invoke("sumar", Map.of("x", 10, "y", 5)));
        System.out.println("Resta: " + controller.invoke("restar", Map.of("x", 10, "y", 5)));
        System.out.println("Multiplicación: " + controller.invoke("multiplicar", Map.of("x", 10, "y", 5)));
        System.out.println("División: " + controller.invoke("dividir", Map.of("x", 10, "y", 5)));


        // Métodos Asíncronos y Futures
        List<Future<Object>> futures = new ArrayList<>();

        // Ejemplos de uso de Invocaciones Asincronas
        futures.add(controller.invoke_async("dormir", Map.of("time", 5000)));
        futures.add(controller.invoke_async("dormir", Map.of("time", 5000)));
        futures.add(controller.invoke_async("dormir", Map.of("time", 5000)));
        futures.add(controller.invoke_async("dormir", Map.of("time", 5000)));
        futures.add(controller.invoke_async("dormir", Map.of("time", 5000)));
        futures.add(controller.invoke_async("dormir", Map.of("time", 5000)));

        for (Future<Object> fut : futures) {
                fut.get();
                System.out.println("Resultado: " + fut.get());
        }


        for(int i = 0; i < controller.invokers.length; i++){
            System.out.println("Invoker " +controller.invokers[i].getId()+ " realizó " + controller.invokers[i].getActionCount() + " acciones.");
        }



        //Policy Managment en grupos de acciones
        List<Map<String, Object>> actions = new ArrayList<>();
        for(int i = 1; i <30; i++){
            actions.add(Map.of("time", i+100));
        }
        controller.invoke("dormir", actions);


        System.out.println("\n1º Politica:");
        for(int i = 0; i < controller.invokers.length; i++){
            System.out.println("Invoker " +controller.invokers[i].getId()+ " realizó " + controller.invokers[i].getActionCount() + " acciones.");
        }

        //Cambio en la Politica
        controller.setPolicy(new GreedyGroup());

        List<Map<String, Object>> actions1 = new ArrayList<>();
        for(int i = 0; i <30; i++){
            actions1.add(Map.of("x", i, "y", i+1));
        }
        controller.invoke("multiplicar", actions1);

        System.out.println("\n2º Politica:");
        for(int i = 0; i < controller.invokers.length; i++){
            System.out.println("Invoker " +controller.invokers[i].getId()+ " realizó " + controller.invokers[i].getActionCount() + " acciones.");
        }
    }
}
