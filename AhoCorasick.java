
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;


/**
 * Implements the Business Logic of Aho Corasick.
 * @author joachimhagege
 */
public class AhoCorasick {
    private TrieBuilder trieBuilder;
    private Trie trie;

    public AhoCorasick(String namesCsv) {
        this.trieBuilder = Trie.builder().caseInsensitive().removeOverlaps().onlyWholeWords();
        loadNames(namesCsv);
    }

    // Loads the list of most frequent names from a CSV file.
    private void loadNames(String namesCsv) {
        BufferedReader bufferedReader = null;

        try {
            String name;
            bufferedReader = new BufferedReader(new FileReader(namesCsv));

            // How to read file in java line by line?
            while ((name = bufferedReader.readLine()) != null) {
                this.trieBuilder.addKeyword(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            // We buid the trie for the AhoCorasick algorithm
            this.trie = this.trieBuilder.build();
        }
    }
    
    /**
     * Returns a list of matches for a specific line.
     * @param line
     * @param lineNumber
     * @return 
     */
    public List<Match> lineListOfMatches(String line, int lineNumber) {
        List<Match> lineListOfMatches = new ArrayList<Match>();
        Collection<Emit> acMatches = parseText(line);
        
        for (Emit e : acMatches) {
            lineListOfMatches.add(new Match(e.getKeyword(), lineNumber, e.getStart()));
        }
        
        return lineListOfMatches;
    }

    private Collection<Emit> parseText(String line) {
        return this.trie.parseText(line);
    }

}