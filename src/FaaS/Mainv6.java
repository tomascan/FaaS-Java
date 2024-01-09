package FaaS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// ...

public class Mainv6 {

    public static void main(String[] args) {
        Controller controller = new Controller(8, 100);
        controller.setPolicy(new GreedyGroup());

//        // Registro de acciones
//        controller.registerAction("wordCount", Actions.wordCount, 5);
        controller.registerAction("countWords", Actions.countWords, 5);

        // Lista de rutas de archivos
        List<String> filePaths = IntStream.rangeClosed(1, 10)  // Si tienes 10 archivos
                .mapToObj(i -> "/home/tomor0/Escritorio/Universidad/TAP/P1/FaaS-TAP-master/Texts/Text" + i + ".txt")
                .toList();
        List<Map<String, Integer>> wordCountInputs = new ArrayList<>();
        List<Map<String, Integer>> countWordsInputs = new ArrayList<>();

        for (String filePath : filePaths) {
            String text = readFile(filePath);
            int textHash = text.hashCode();
//
//            Map<String, Integer> wordCountInput = new HashMap<>();
//            wordCountInput.put("text", textHash);
//            wordCountInputs.add(wordCountInput);

            Map<String, Integer> countWordsInput = new HashMap<>();
            countWordsInput.put("text", textHash);
            countWordsInputs.add(countWordsInput);
        }
//
//        List<Integer> wordCountResults = controller.invoke("wordCount", wordCountInputs);
        List<Integer> countWordsResults = controller.invoke("countWords", countWordsInputs);


//        // En tu método main o donde estés procesando los resultados
//        Map<String, Integer> reducedWordCount = reduceWordCounts(wordCountResults);
        int totalWordCount = reduceCountWords(countWordsResults);

//        // Imprimir o utilizar los resultados de WordCount y CountWords
//        System.out.println("Word Counts: " + reducedWordCount);
        System.out.println("Total Word Count: " + totalWordCount);
    }





    private static String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    private static Map<String, Integer> reduceWordCounts(List<Map<String, Integer>> allWordCounts) {
        Map<String, Integer> reducedResult = new HashMap<>();
        for (Map<String, Integer> wordCounts : allWordCounts) {
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                reducedResult.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        return reducedResult;
    }

    // Método para reducir los resultados de CountWords
    private static int reduceCountWords(List<Integer> countWordsResults) {
        int totalWords = 0;
        for (int count : countWordsResults) {
            totalWords += count;
        }
        return totalWords;
    }


}