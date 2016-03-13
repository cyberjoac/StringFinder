import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joachimhagege
 */
public class Aggregator {

    /**
     * We could have used a ConcurrentHashMap which would have been thread-safe,
     * but not efficient. Timothy ­­> [[lineOffset=13000, charOffset=19775],
     * [lineOffset=13000, charOffset=42023]] James --> [[lineOffset=12,
     * charOffset=455]] ...
     */
    private Map<String, ArrayList<Match>> aggregatedListOfMatches;

    /**
     * The aggregator ­ aggregates the results from all the matchers and prints
     * the results.
     */
    public Aggregator() {
        this.aggregatedListOfMatches = new HashMap<>();
    }

    public void computeAggregatedList(List<Matcher> matchers) {
        for (Matcher matcher : matchers) {
            List<Match> matches = matcher.getMatches();
            for (Match match : matches) {
                String nameMatched = match.getName();
                
                aggregatedListOfMatches.putIfAbsent(nameMatched, new ArrayList<Match>());
                List<Match> nameFinalMatches = aggregatedListOfMatches.get(nameMatched);
                nameFinalMatches.add(match);
            }
        }
    }

    public String aggregatedListOfNames() {
        String toReturn = "";
        for (Map.Entry<String, ArrayList<Match>> entry : aggregatedListOfMatches.entrySet()) {
            toReturn += entry.getKey() + "-->";
            toReturn += entry.getValue().toString();
            toReturn += "\n\n";
        }
        return toReturn;
    }
}