import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        List<Future<Integer>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(25);

        long startTs = System.currentTimeMillis(); // start time

        for (String text : texts) {
            futures.add(executorService.submit(() -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            }));
        }

        Integer maxSize = 0;
        for (Future future : futures) {
            try {
                maxSize = ((Integer) future.get() > maxSize) ? (Integer) future.get() : maxSize;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTs = System.currentTimeMillis(); // end time

        System.out.printf("Max interval: %d\n", maxSize);
        System.out.println("Time: " + (endTs - startTs) + "ms");

        executorService.shutdown();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}