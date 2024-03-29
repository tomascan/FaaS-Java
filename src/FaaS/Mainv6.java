package FaaS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Mainv6 {
    public static void main(String[] args) throws IOException {
        Controller controller = new Controller(10, 20000);
        controller.setPolicy(new RoundRobin());

        // Registro de acciones
        controller.registerAction("wordCount", Actions.wordCount, 100);
        controller.registerAction("countWords", Actions.countWords, 100);

        // Lista de rutas de archivos
        List<String> filePaths = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> "/home/tomor0/Escritorio/Universidad/TAP/P1/FaaS-TAP-master/Texts/Text" + i + ".txt")
                .toList();

        List<Map<String, Object>> inputs = new ArrayList<>();

        for (String filePath : filePaths) {
            String text = readFile(filePath);

            Map<String, Object> input = new HashMap<>();
            input.put("text", text);
            inputs.add(input);
        }

        // Decorar con TimerDecorator para ver el tiempo de lectura de cada fichero
        Function<Map<String, Object>, Object> wordCountTimer= new TimerDecorator(Actions.countWords);
        controller.registerAction("wordCountTimer", wordCountTimer, 100);
        // Usa las versiones decoradas de las funciones (Los Maps decorados no se pueden reducir)
        controller.invoke("wordCountTimer", inputs);


        // Invocar las acciones
        List<Object> wordCountResults = controller.invoke("wordCount", inputs);
        List<Object> countWordsResults = controller.invoke("countWords", inputs);

        // Reducir los resultados
        Map<String, Integer> reducedWordCount = reduceWordCount(wordCountResults); //Palabra - Concurrencias
        int totalWordCount = reduceCountWords(countWordsResults); //Total de palabras del texto

        // Imprimir los resultados
        System.out.println("Word Counts: " + reducedWordCount);
        System.out.println("Total Word Count: " + totalWordCount);


        for (Invoker invoker : controller.getInvokers()) {
            System.out.println("FaaS.Invoker " + invoker.getId() + " realizó " + invoker.getActionCount() + " acciones.");
        }

    }




    private static String readFile(String filePath) throws IOException {
            return new String(Files.readAllBytes(Paths.get(filePath)));

    }


    private static Map<String, Integer> reduceWordCount(List<Object> allWordCounts) {
        Map<String, Integer> reducedResult = new HashMap<>();
        for (Object word : allWordCounts) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> wordCounts = (Map<String, Integer>) word;
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                reducedResult.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        return reducedResult;
    }


    // Método para reducir los resultados de CountWords
    private static int reduceCountWords(List<Object> countWordsResults) {
        int totalWords = 0;
        for (Object count : countWordsResults) {
            if (count instanceof Integer) {
                totalWords += (Integer) count;
            } else {
                throw new IllegalArgumentException("Todos los elementos de countWordsResults deben ser de tipo Integer.");
            }
        }
        return totalWords;
    }


}