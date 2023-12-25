import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mainv2 {

    public static void main(String[] args) throws Exception {
        Controller controller = new Controller(4, 100);
        controller.setPolicy(new RoundRobin());
        controller.registerObserver();

        // Registro de acciones
        controller.registerAction("sumar", Actions.sumar, 5);
        controller.registerAction("restar", Actions.restar, 5);
        controller.registerAction("multiplicar", Actions.multiplicar, 5);
        controller.registerAction("dividir", Actions.dividir, 5);
        controller.registerAction("dormir", Actions.dormir, 10);

        // Procesar una serie de archivos
        processFiles(controller, 1, 10);

        //Mostrar el contador de acciones realizadas por cada Invoker
        for(int i = 0; i < controller.invokers.length; i++){
            System.out.println("Invoker " + (i + 1) + " realizó " + controller.invokers[i].getActionCount() + " acciones.");
        }

        // Calcular y mostrar métricas
        controller.analyzeMetrics();
    }

    private static void processFiles(Controller controller, int currentFile, int totalFiles) throws IOException {
        if (currentFile >= totalFiles) {
            return; // Caso base: Todos los archivos han sido procesados
        }

        String filePath = "/home/tomor0/Escritorio/Universidad/TAP/P1/FaaS-TAP-master/Ficheros/Ex" + currentFile + ".txt";
        List<String> fileContents = readFiles(filePath);
        for (String content : fileContents) {
            splitActions(content, controller);
        }

        processFiles(controller, currentFile + 1, totalFiles); // Llamada recursiva para el siguiente archivo
    }

    private static void splitActions(String content, Controller controller) {
        System.out.println("Contenido leído: " + content);

        // Procesamiento de la línea como antes
        String[] parts = content.split("\\s+", 2);
        String actionName = parts[0];
        System.out.println("Nombre de la acción: " + actionName);

        Map<String, Integer> params = new HashMap<>();
        if (parts.length > 1) {
            String[] keyValuePairs = parts[1].split("\\s+");
            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0].trim(), Integer.parseInt(keyValue[1].trim()));
                }
            }
        }

        List<Integer> result = controller.invoke(actionName, Arrays.asList(params));
        System.out.println("Resultado de la acción '" + actionName + "': " + result);
    }

    private static List<String> readFiles(String path) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            return stream.collect(Collectors.toList());
        }
    }
}
