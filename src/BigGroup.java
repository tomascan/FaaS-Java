//import java.util.ArrayList;
//import java.util.List;
//
//public class BigGroup implements Policy {
//    @Override
//    public List<Integer> distributeActions(int totalFunctions, List<Integer> availableInvokers) {
//        List<Integer> functionsPerInvoker = new ArrayList<>();
//        int numInvokers = availableInvokers.size();
//        int groupSize = 6; // Tamaño del grupo deseado
//
//        while (totalFunctions > 0) {
//            for (int i = 0; i < numInvokers && totalFunctions > 0; i++) {
//                int functions = Math.min(groupSize, totalFunctions);
//                functionsPerInvoker.add(functions);
//                totalFunctions -= functions;
//            }
//        }
//
//        return functionsPerInvoker;
//    }
//}
