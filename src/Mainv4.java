import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Mainv4 {
    public static void main(String[] args) {
        // Crea una instancia del controlador
        Controller controller = new Controller(4, 2024);
        controller.setPolicy(new RoundRobin());

        // Registra los observadores para los Invokers
        for (int i = 0; i < controller.getInvokers().length; i++) {
            controller.getInvokers()[i].registerObserver(controller);
        }

        // Registro de acciones
        controller.registerAction("sumar", Actions.sumar, 100);
        controller.registerAction("restar", Actions.restar, 256);
        controller.registerAction("multiplicar", Actions.multiplicar, 100);
        controller.registerAction("dividir", Actions.dividir, 256);
        controller.registerAction("dormir", Actions.dormir, 200);
        controller.registerAction("factorial", Actions.factorial, 100);


//        //         Ejemplo de uso grupal
//        List<Map<String, Integer>> input = Arrays.asList(
//                Map.of("x", 6, "y", 3),
//                Map.of("x", 9, "y", 1),
//                Map.of("x", 8, "y", 8)
//        );
//        List<Integer> result = controller.invoke("restar", input);
//        System.out.println("Restas:"+result.toString());
//        List<Integer> result1 = controller.invoke("dividir", input);
//        System.out.println("Divisiones:"+result1.toString());
//
//
//        List<Map<String, Integer>> input1 = Arrays.asList(
//                Map.of("time", 5000),
//                Map.of("time", 989),
//                Map.of("time", 8)
//        );
//        List<Integer>  result2 = controller.invoke("dormir",input1);
//
//        // Analiza y muestra métricas si es necesario
//        controller.analyzeMetrics();
//
//        // Aplicar MemoizationDecorator a una acción, por ejemplo, 'sumar'
//        Function<Map<String, Integer>, Integer> sumarMemoized = new MemoizationDecorator(Actions.sumar, controller);
//        controller.registerAction("sumar", sumarMemoized, 100);
//
//        // Realizar invocaciones repetidas con los mismos parámetros para 'sumar'
//        Map<String, Integer> sumParams = Map.of("x", 5, "y", 3);
//        int sumResult1 = controller.invoke("sumar", sumParams);
//        System.out.println("Resultado de sumar (1ª vez): " + sumResult1);
//
//        Map<String, Integer> sumParams1 = Map.of("x", 10, "y", 2);
//        int sumResult2 = controller.invoke("sumar", sumParams1);
//        System.out.println("Resultado de sumar (2ª vez, debería ser de caché): " + sumResult2);

        // Aplica solo el TimerDecorator
        Function<Map<String, Integer>, Integer> factorialConCronometro = new TimerDecorator(Actions.factorial);
        controller.registerAction("factorialConCronometro", factorialConCronometro, 100);

        // Aplica solo el MemoizationDecorator
        Function<Map<String, Integer>, Integer> factorialMemoized = new MemoizationDecorator(Actions.factorial, controller);
        controller.registerAction("factorialMemoized", factorialMemoized, 100);

        // Aplica ambos, Timer y Memoization
        Function<Map<String, Integer>, Integer> factorialAmbos = new MemoizationDecorator(new TimerDecorator(Actions.factorial), controller);
        controller.registerAction("factorialAmbos", factorialAmbos, 100);



        // Realizar algunas invocaciones
        Map<String, Integer> factorialParams = Map.of("number", 25);

        for (int i = 0; i < 10; i++) {
            int resultado = controller.invoke("factorialAmbos", factorialParams);
            System.out.println("Resultado del factorial (Invocación " + (i + 1) + "): " + resultado);
        }
//
//        controller.invoke("factorialConCronometro", factorialParams); // Solo cronometraje
//        controller.invoke("factorialMemoized", factorialParams); // Solo memoización
//        controller.invoke("factorialAmbos", factorialParams); // Ambos
//

        controller.printCache();
    }
}