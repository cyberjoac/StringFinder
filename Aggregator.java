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

    public void computeAggregatedList(List<List<Match>> listOfMatches) {
        for (List<Match> matchList : listOfMatches) {
            for (Match match : matchList) {
                String nameMatched = match.getName();
                
                aggregatedListOfMatches.putIfAbsent(nameMatched, new ArrayList<Match>());
                List<Match> nameFinalMatches = aggregatedListOfMatches.get(nameMatched);
                nameFinalMatches.add(match);
            }
        }
    }

    public void printAggregatedListOfNames() {
        for (Map.Entry<String, ArrayList<Match>> entry : aggregatedListOfMatches.entrySet()) {
            System.out.print(entry.getKey() + "-->");
            System.out.print(entry.getValue().toString() + "\n\n");
        }
    }
}