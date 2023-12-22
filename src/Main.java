import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller(1, 1024);

        // Registro de acciones
        controller.registerAction("sumar", Actions.sumar, 256);
        controller.registerAction("restar", Actions.restar, 256);
        controller.registerAction("multiplicar", Actions.multiplicar, 256);
        controller.registerAction("dividir", Actions.dividir, 256);
        controller.registerAction("dormir", Actions.dormir, 256);

        // Ejemplos de uso
        System.out.println("Suma: " + controller.invoke("sumar", Map.of("x", 6, "y", 2)));
        System.out.println("Resta: " + controller.invoke("restar", Map.of("x", 6, "y", 2)));
        System.out.println("Multiplicación: " + controller.invoke("multiplicar", Map.of("x", 6, "y", 2)));
        System.out.println("División: " + controller.invoke("dividir", Map.of("x", 6, "y", 2)));

        // Ejemplo de dormir
        System.out.println("Dormir: Iniciando");
        controller.invoke("dormir", Map.of("time", 1000));
        System.out.println("Dormir: Terminado");



        List<Map<String, Integer>> input = Arrays.asList(
                Map.of("x", 6, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8)
        );


        List<Integer> result = controller.invoke("sumar", input);
        System.out.println("Sumas:"+result.toString());
        List<Integer> result1 = controller.invoke("dividir", input);
        System.out.println("Divisiones:"+result1.toString());


    }
}