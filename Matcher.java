import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author joachimhagege
 */
public class Matcher implements Runnable {
    private final ArrayList<String> lines;
    private final int startingLine;
    private final Pattern regexPattern;
    private java.util.regex.Matcher regexMatcher;
    
    private ArrayList<Match> partialListOfMatches;
    
    /**
     * Gets at most 1000 lines of text as input and searches for matches of a 
     * given set of strings. 
     * @param lines
     * @param startingLine
     */
    public Matcher (ArrayList<String> lines, int startingLine) {
        this.lines = lines;
        this.startingLine = startingLine;
        this.regexPattern = Pattern.compile(BigIdStringFinder.listOfNamesRegex, Pattern.CASE_INSENSITIVE);
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
        regexMatcher = regexPattern.matcher(line);
        while (regexMatcher.find()) {
            
            int charOffset = regexMatcher.start();
            String nameMatched = regexMatcher.group();
            partialListOfMatches.add(new Match(nameMatched, lineNumber, charOffset));
        }
    }

    public ArrayList<Match> getMatches() {
        return partialListOfMatches;
    }

    @Override
    public void run() {
        computeMatches();
    }
}