import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author joachimhagege
 */
public class Matcher implements Callable<List<Match>> {
    private final ArrayList<String> lines;
    private final int startingLine;
    private AhoCorasick ahoCorasick;
   
    private List<Match> partialListOfMatches;
    
    /**
     * Gets at most 1000 lines of text as input and searches for matches of a 
     * given set of strings. 
     * @param lines
     * @param startingLine
     * @param ahoCorasick
     */
    public Matcher (ArrayList<String> lines, int startingLine, AhoCorasick ahoCorasick) {
        this.lines = lines;
        this.startingLine = startingLine;
        this.ahoCorasick = ahoCorasick;
        this.partialListOfMatches = new ArrayList<Match>();
    }
    
    private void computeMatches() {
        int currentLineNumber = startingLine;
        for (String line : lines) {
            findLineMatches(line, currentLineNumber);
            currentLineNumber++;
        }
    }
    
    private void findLineMatches(String line, int lineNumber) {
        partialListOfMatches.addAll(ahoCorasick.lineListOfMatches(line, lineNumber));
    }

    @Override
    public List<Match> call() throws Exception {
        computeMatches();
        return partialListOfMatches;
    }
}