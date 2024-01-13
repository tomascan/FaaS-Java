package FaaS;

import java.lang.reflect.Proxy;
import java.util.Map;

public class Mainv5 {
    public static void main(String[] args) {
        Controller controller = new Controller(4, 2024);
        controller.setPolicy(new GreedyGroup());

        // Initialize and register Invokers
//        for (int i = 0; i < controller.getInvokers().length; i++) {
//            controller.getInvokers()[i].registerObserver(controller);
//        }

        // Register actions
        controller.registerAction("sumar", Actions.sumar, 100);
        controller.registerAction("restar", Actions.restar, 256);
        controller.registerAction("multiplicar", Actions.multiplicar, 100);
        controller.registerAction("dividir", Actions.dividir, 256);
        controller.registerAction("dormir", Actions.dormir, 100);
        controller.registerAction("factorial", Actions.factorial, 100);

        // Crea la instancia del proxy
        IActions actionsProxy = (IActions) Proxy.newProxyInstance(
                IActions.class.getClassLoader(),
                new Class<?>[] { IActions.class },
                new ActionInvocationHandler(controller)
        );

        // Utiliza el proxy para invocar acciones sin necesidad de Controller
        Map<String, Object> paramsSumar = Map.of("x", 5, "y", 3);
        Integer resultadoSumar = actionsProxy.sumar(paramsSumar);
        System.out.println("Resultado de sumar: " + resultadoSumar);

        Map<String, Object> paramsFactorial = Map.of("number", 5);
        Integer resultadoFactorial = actionsProxy.factorial(paramsFactorial);
        System.out.println("Resultado del factorial: " + resultadoFactorial);

//        System.out.println(controller.getMetrics());

    }
}
