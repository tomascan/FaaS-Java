import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Controller controller = new Controller(1, 1024);

        // Registro de acciones
        controller.registerAction("sumar", Actions.sumar, 340);
        controller.registerAction("restar", Actions.restar, 256);
        controller.registerAction("multiplicar", Actions.multiplicar, 256);
        controller.registerAction("dividir", Actions.dividir, 256);
        controller.registerAction("dormir", Actions.dormir, 256);

        // Ejemplos de uso individual
        System.out.println("Suma: " + controller.invoke("sumar", Map.of("x", 6, "y", 2)));
        System.out.println("Resta: " + controller.invoke("restar", Map.of("x", 6, "y", 2)));
        System.out.println("Multiplicación: " + controller.invoke("multiplicar", Map.of("x", 6, "y", 2)));
        System.out.println("División: " + controller.invoke("dividir", Map.of("x", 6, "y", 2)));

        // Ejemplo de dormir
        System.out.println("Dormir: Iniciando");
        controller.invoke("dormir", Map.of("time", 1000));
        System.out.println("Dormir: Terminado");

        // Ejemplo de uso grupal
        List<Map<String, Integer>> input = Arrays.asList(
                Map.of("x", 6, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8)
        );
        List<Integer> result = controller.invoke("restar", input);
        System.out.println("Restas:"+result.toString());
        List<Integer> result1 = controller.invoke("dividir", input);
        System.out.println("Divisiones:"+result1.toString());


        // Ejemplos de uso de Invocaciones Asincronas
        Future<Integer> fut1 = controller.invoke_async("dormir", Map.of("time", 1000));
        Future<Integer> fut2 = controller.invoke_async("dormir", Map.of("time", 2000));
        Future<Integer> fut3 = controller.invoke_async("dormir", Map.of("time", 5000));
        Future<Integer> fut4 = controller.invoke_async("sumar", Map.of("x", 2, "y", 2));

            fut1.get();
            fut2.get();
            fut3.get();
            fut4.get();

        System.out.println("Todas las acciones han terminado --> "+fut4.get());




    }

}
