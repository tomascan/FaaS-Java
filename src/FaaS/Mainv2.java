package FaaS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mainv2 {

    public static void main(String[] args) throws Exception {
        Controller controller = new Controller(8, 100);
        controller.setPolicy(new RoundRobin());

        // Initialize and register Invokers
        for (int i = 0; i < controller.getInvokers().length; i++) {
            controller.getInvokers()[i].registerObserver(controller);
        }

        // Registro de acciones
        controller.registerAction("sumar", Actions.sumar, 5);
        controller.registerAction("restar", Actions.restar, 5);
        controller.registerAction("multiplicar", Actions.multiplicar, 5);
        controller.registerAction("dividir", Actions.dividir, 5);
        controller.registerAction("dormir", Actions.dormir, 5);
        controller.registerAction("factorial", Actions.factorial, 5);
        controller.registerAction("wordCount", Actions.wordCount, 100);
        controller.registerAction("countWords", Actions.countWords, 100);

        // Procesar una serie de archivos
        List<Map<String, Object>> allActions = new ArrayList<>();
        processFiles(allActions, 1, 10);
        controller.invokeFile(allActions);

        //Mostrar el contador de acciones realizadas por cada Invoker
        for(int i = 0; i < controller.invokers.length; i++){
            System.out.println("Invoker " + (i + 1) + " realizó " + controller.invokers[i].getActionCount() + " acciones.");
        }

        // Calcular y mostrar métricas
        controller.analyzeMetrics();
    }






    private static void processFiles(List<Map<String, Object>> allActions, int currentFile, int totalFiles) throws IOException {
        if (currentFile > totalFiles) {
            return; // Caso base: Todos los archivos han sido procesados
        }

        String filePath = "/home/tomor0/Escritorio/Universidad/TAP/P1/FaaS-TAP-master/Ficheros/Ex" + currentFile + ".txt";
        List<String> fileContents = readFiles(filePath);
        for (String content : fileContents) {
            Map<String, Object> actionData = splitActions(content);
            if (actionData != null) {
                allActions.add(actionData);
            }
        }

        processFiles(allActions, currentFile + 1, totalFiles);
    }




    private static Map<String, Object> splitActions(String content) {
        // Comprobar si la línea está vacía o solo contiene espacios en blanco
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        String[] parts = content.split("\\s+", 2);
        // Comprobar si hay suficientes partes para una acción y sus parámetros
        if (parts.length < 2) {
            return null;
        }

        String actionName = parts[0];
        Map<String, Object> params = new HashMap<>();
        // Divide los parámetros en pares clave-valor
        String[] keyValuePairs = parts[1].split("\\s+");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                // Intenta convertir a Integer, si falla, asume que es String
                try {
                    params.put(key, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    // Elimina las comillas si es necesario
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    params.put(key, value);
                }
            }
        }

        Map<String, Object> actionData = new HashMap<>();
        actionData.put("actionName", actionName);
        actionData.put("parameters", params);

        return actionData;
    }


    private static List<String> readFiles(String path) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            return stream.collect(Collectors.toList());
        }
    }
}
