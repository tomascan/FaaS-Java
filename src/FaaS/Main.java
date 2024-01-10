package FaaS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Controller controller = new Controller(4,2048);
        controller.setPolicy(new UniformGroup(8));

        // Registro de acciones
        controller.registerAction("sumar", Actions.sumar, 100);
        controller.registerAction("restar", Actions.restar, 256);
        controller.registerAction("multiplicar", Actions.multiplicar, 100);
        controller.registerAction("dividir", Actions.dividir, 256);
        controller.registerAction("dormir", Actions.dormir, 50);

//        // Ejemplos de uso individual
        System.out.println("Suma: " + controller.invoke("sumar", Map.of("x", 6, "y", 2)));
        System.out.println("Resta: " + controller.invoke("restar", Map.of("x", 6, "y", 2)));
        System.out.println("Multiplicación: " + controller.invoke("multiplicar", Map.of("x", 6, "y", 2)));
        System.out.println("División: " + controller.invoke("dividir", Map.of("x", 6, "y", 2)));

//        // Ejemplo de dormir
        System.out.println("Dormir: Iniciando");
        int a = (int) controller.invoke("dormir", Map.of("time", 3000));
        System.out.println("Dormir: Terminado: "+ a);

//        // Ejemplo de uso grupal
        List<Map<String, Object>> input = Arrays.asList(
                Map.of("x", 6, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8)
        );
        List<Object> result = controller.invoke("restar", input);
        System.out.println("Restas:"+result.toString());
        List<Object> result1 = controller.invoke("dividir", input);
        System.out.println("Divisiones:"+result1.toString());

//        // Ejemplos de uso de Invocaciones Asincronas
        Future<Object> fut1 = controller.invoke_async("dormir", Map.of("time", 5000));
        Future<Object> fut2 = controller.invoke_async("dormir", Map.of("time", 5000));
        Future<Object> fut3 = controller.invoke_async("dormir", Map.of("time", 5000));
        Future<Object> fut4 = controller.invoke_async("sumar", Map.of("x", 2, "y", 2));

            fut1.get();
            fut2.get();
            fut3.get();
            fut4.get();

            System.out.println("Todas las acciones han terminadas en " + (int)fut1.get()/1000 +" s");



        //Policy Managment en grupos de acciones
        List<Map<String, Object>> actions = new ArrayList<>();
        List<Map<String, Object>> actions1 = new ArrayList<>();
        for(int i = 0; i <18; i++){
            actions.add(Map.of("x", i, "y", i+1));
            actions1.add(Map.of("x", i, "y", i+1));
        }

        //Invocar las acciones y distribuirlas entre los Invokers
        controller.invoke("sumar", actions);
        controller.invoke("multiplicar", actions1);

        //Mostrar el contador de acciones realizadas por cada FaaS.Invoker
        for(int i = 0; i < controller.invokers.length; i++){
            System.out.println("Invoker " +controller.invokers[i].getId()+ " realizó " + controller.invokers[i].getActionCount() + " acciones.");
        }
    }
}
