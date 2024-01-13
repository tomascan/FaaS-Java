import FaaS.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ControllerTest {

    private Controller controller;


    @BeforeEach
    public void setUp() {
        controller = new Controller(4, 1000);
        controller.setPolicy(new RoundRobin());

        controller.registerAction("sumar", Actions.sumar, 100);
        controller.registerAction("dormir", Actions.dormir, 100);

    }

    @Test
    public void testRegisterAction() {
        Function<Map<String, Object>, Object> testAction = params -> params.get("parametros");
        controller.registerAction("testAction", testAction, 100);
    }

    @Test
    public void testInvokeAction() {
        controller.registerAction("testAction", params -> "Test", 100);
        Object result = controller.invoke("testAction", new HashMap<>());
        assertEquals("Test", result);
    }



    //POLITICAS
    @Test
    public void testRoundRobin() {
        controller.setPolicy(new RoundRobin());

        List<Map<String, Object>> actions = new ArrayList<>();
        for(int i = 0; i <20; i++){
            actions.add(Map.of("x", i, "y", i+1));
        }
        controller.invoke("sumar", actions);

        int accionesPorInvoker = 5; // Esperado: 5 acciones por Invoker para 4 Invokers
        Invoker[] invokers = controller.getInvokers();
        for (Invoker invoker : invokers) {
            assertEquals(accionesPorInvoker, invoker.getActionCount());
        }


    }
    @Test
    public void testUniformGroup() {
        controller.setPolicy(new UniformGroup(3));

        List<Map<String, Object>> actions = new ArrayList<>();
        for(int i = 0; i <21; i++){
            actions.add(Map.of("x", i, "y", i+1));
        }
        controller.invoke("sumar", actions);

        int[] accionesEsperadas = {6, 6, 6, 3};
        Invoker[] invokers = controller.getInvokers();

        for (int i = 0; i < invokers.length; i++) {
            assertEquals(accionesEsperadas[i], invokers[i].getActionCount());
        }
    }


    // OBSERVER
    @Test
    public void testObserver() {

        for (Invoker invoker : controller.getInvokers()) {
            invoker.registerObserver(controller);
        }
        // Ejecutar una acción
        controller.invoke("dormir", Map.of("time", 2000));

        assertFalse(controller.getMetrics().isEmpty());
    }
    @Test
    public void testMetricUpdate() {
        Metric testMetric = new Metric(1, 1000, 100); // id Invoker, tiempo de ejecución, uso de memoria
        controller.updateMetrics(testMetric);

        assertEquals(testMetric, controller.getMetrics().get(0));
    }


    // DECORATOR

    @Test
    public void testMemoizationDecorator() {
        Function<Map<String, Object>, Object> sumarMemoized = new MemoizationDecorator("sumar", Actions.sumar, controller);
        Map<String, Object> params = Map.of("x", 9, "y", 10);

        Object firstResult = sumarMemoized.apply(params);

        assertNotNull(controller.getCachedResult("sumar", params)); //El resultado se guarda en caché

        Map<String, Object> params1 = Map.of("x", 9, "y", 10);
        Object secondResult = sumarMemoized.apply(params1);

        assertEquals(firstResult, secondResult);
    }

    @Test
    public void testTimerDecorator() {
        Function<Map<String, Object>, Object> sumarTimer = new TimerDecorator(Actions.sumar);
        Map<String, Object> params = Map.of("x", 2, "y", 2);


        Object result = sumarTimer.apply(params);

        assertEquals(4, result);
    }


    // PROXY

    @Test
    public void testProxyInvocation() {
        Map<String, Object> params = Map.of("x", 10, "y", 10);

        IActions actionsProxy = (IActions) Proxy.newProxyInstance(
                IActions.class.getClassLoader(),
                new Class<?>[]{IActions.class},
                new ActionInvocationHandler(controller)
        );

        Integer resultado = actionsProxy.sumar(params);

        assertNotNull(resultado);
        assertEquals(20, resultado);
    }

}

