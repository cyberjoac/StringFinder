import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author joachimhagege
 */
public class BigIdStringFinder {

    private final String fileName;

    // 10 sounds like a reasonable number of threads.
    private static final int NUMBER_OF_THREADS = 10;

    private List<List<Match>> listOfMatches;
    private AhoCorasick ahoCorasick;
    private Aggregator aggregator;
    private ExecutorService executorService;
    private Collection<Future<List<Match>>> futures;

    /**
     * Creates a BigId object responsible for our main flow.
     *
     * @param fileName
     */
    public BigIdStringFinder(String fileName) {
        this.fileName = fileName;
        this.ahoCorasick = new AhoCorasick("list-of-names.csv");
        this.listOfMatches = new ArrayList<>();
        this.aggregator = new Aggregator();
        // Our Executor service will be responsible for distributing the Matchers 
        // accross the workers.
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        
        // We need to keep a reference to the "futures" of the threads to wait 
        // for their completion.
        this.futures = new LinkedList<Future<List<Match>>>();
    }

    public void findEnglishMostCommonNames() {
        try {
            BufferedReader bufferedReader
                    = new BufferedReader(new FileReader(fileName));

            String line = null;
            int lineNumber = 1;
            ArrayList<String> thousandLines = new ArrayList<String>();
            
            while ((line = bufferedReader.readLine()) != null) {
                // Read 1000 lines or less
                thousandLines.add(line);
                lineNumber++;
 
                if (lineNumber % 1000 == 0) {
                    startAsynchronousMatcher(thousandLines, lineNumber - 1000);
                    // Initialize for the next thousandLines.
                    thousandLines = new ArrayList<>();
                }
            }
            // Line number = 4544 - 4544 % 1000 = 4000
            startAsynchronousMatcher(thousandLines, lineNumber - lineNumber % 1000);

            waitForAllMatchersCompletion();

            // Once all the threads are done, go here.
            aggregator.computeAggregatedList(listOfMatches);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException e) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        // We need to shutdown all the threads of the Executor Service.
        finally {
            executorService.shutdown();
        }
        aggregator.printAggregatedListOfNames();
    }
    
    private void startAsynchronousMatcher(ArrayList<String> thousandLines, int lineNumber) {
        // .clone is necessary for thread-safe operation.
        Matcher matcher = new Matcher((ArrayList<String>) thousandLines.clone(), lineNumber, ahoCorasick);
        // Executes in a new thread for efficiency and save 
        // the future for polling its completion.
        futures.add(executorService.submit(matcher));     
    }

    /**
     * Wait until all the futures finish completing their task.
     * future.get() is blocking. 
     * There is no efficiency consideration because our program needs to wait
     * for completion of all the tasks at this stage.
     */
    private void waitForAllMatchersCompletion() {
        for (Future<List<Match>> future : futures) {
            try {
                this.listOfMatches.add(future.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BigIdStringFinder stringFinder = new BigIdStringFinder("big.txt");
        stringFinder.findEnglishMostCommonNames();
    }
}