package FaaS;

import java.util.Map;
import java.util.function.Function;

public class Mainv4 {
    public static void main(String[] args) {
        // Crea una instancia del controlador
        Controller controller = new Controller(4, 2024);
        controller.setPolicy(new UniformGroup(3));

        // Registra los observadores para los Invokers
//        for (int i = 0; i < controller.getInvokers().length; i++) {
//            controller.getInvokers()[i].registerObserver(controller);
//        }

        // Registro de acciones
        controller.registerAction("sumar", Actions.sumar, 100);
        controller.registerAction("restar", Actions.restar, 256);
        controller.registerAction("multiplicar", Actions.multiplicar, 100);
        controller.registerAction("dividir", Actions.dividir, 256);
        controller.registerAction("dormir", Actions.dormir, 200);
        controller.registerAction("factorial", Actions.factorial, 100);

        // Registrar solo el MemoizationDecorator para factorial
        Function<Map<String, Object>, Object> factorialMemoized = new MemoizationDecorator("Factorial", Actions.factorial, controller);

        //Sin Registrar la Acción (Para casos Individuales)
        Map<String, Object> params = Map.of("number", 5);
        Object firstResult = factorialMemoized.apply(params);
        System.out.println("Resultado del factorial: " + firstResult);

        //Registando la acción
        controller.registerAction("factorialMemoized", factorialMemoized, 100);

        // Registrar TimerDecorator para factorial
        Function<Map<String, Object>, Object> factorialCronometro = new TimerDecorator(Actions.factorial);
        controller.registerAction("factorialCronometro", factorialCronometro, 100);

        // Registrar ambos, Timer y Memoization
        Function<Map<String, Object>, Object> factorialAmbos = new MemoizationDecorator("Timer-Factorial", new TimerDecorator(Actions.factorial), controller);
        controller.registerAction("factorialAmbos", factorialAmbos, 100);




        Map<String, Object> factorialParams1 = Map.of("number", 25);
        for (int i = 0; i < 10; i++) {
            int resultado1 = (int) controller.invoke("factorialAmbos", factorialParams1);
            System.out.println("Resultado del factorial (Invocación " + (i + 1) + "): " + resultado1);
        }

        controller.printCache();


        Map<String, Object> factorialParams = Map.of("number", 10);
        controller.invoke("factorialAmbos", factorialParams); // Ambos
        controller.invoke("factorialCronometro", factorialParams); // Solo cronometraje
        controller.invoke("factorialMemoized", factorialParams); // Solo memoización

        controller.printCache();

    }
}
