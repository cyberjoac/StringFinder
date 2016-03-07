
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author joachimhagege
 */
public class BigIdStringFinder {

    // Regex for matching the 50 most frequent English names.
    // \b at beginning and end for matching exactly the name (not valid if letters
    // following of before.
    // /i Ignore case
    // /g Global search
    public static final String listOfNamesRegex = "\\b(James|John|Robert|"
            + "Michael|William|David|Richard|Charles|Joseph|Thomas|Christopher|"
            + "Daniel|Paul|Mark|Donald|George|Kenneth|Steven|Edward|Brian|"
            + "Ronald|Anthony|Kevin|Jason|Matthew|Gary|Timothy|Jose|Larry|"
            + "Jeffrey|Frank|Scott|Eric|Stephen|Andrew|Raymond|Gregory|Joshua|"
            + "Jerry|Dennis|Walter|Patrick|Peter|Harold|Douglas|Henry|Carl|"
            + "Arthur|Ryan|Roger)\\b";

    private final String fileName;

    // 10 sounds like a reasonable number of threads.
    private static final int NUMBER_OF_THREADS = 10;

    private ArrayList<Matcher> matchers;
    private Aggregator aggregator;
    private ExecutorService executorService;
    private Collection<Future<?>> futures;

    /**
     * Creates a BigId object responsible for our main flow.
     *
     * @param fileName
     */
    public BigIdStringFinder(String fileName) {
        this.fileName = fileName;
        this.matchers = new ArrayList<Matcher>();
        this.aggregator = new Aggregator();
        // Our Executor service will be responsible for distributing the Matchers 
        // accross the workers.
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        
        // We need to keep a reference to the "futures" of the threads to wait 
        // for their completion.
        this.futures = new LinkedList<Future<?>>();

    }

    public String findEnglishMostCommonNames() {
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
            aggregator.computeAggregatedList(matchers);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException e) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        // We need to shutdown all the threads of the Executor Service.
        finally {
            executorService.shutdown();
        }
        return aggregator.aggregatedListOfNames();
    }
    
    private void startAsynchronousMatcher(ArrayList<String> thousandLines, int lineNumber) {
        // .clone is necessary for thread-safe operation.
        Matcher matcher = new Matcher((ArrayList<String>) thousandLines.clone(), lineNumber - 1000);
        // Executes in a new thread for efficiency and save 
        // the future for polling its completion.
        futures.add(executorService.submit(matcher));
        // Saves a reference of all the matchers.
        matchers.add(matcher);        
    }

    /**
     * Wait until all the futures finish completing their task.
     * future.get() is blocking. 
     * There is no efficiency consideration because our program needs to wait
     * for completion of all the tasks at this stage.
     */
    private void waitForAllMatchersCompletion() {
        for (Future<?> future : futures) {
            try {
                future.get();
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
        String englishCommonNames = stringFinder.findEnglishMostCommonNames();
        System.out.println(englishCommonNames);
    }
}