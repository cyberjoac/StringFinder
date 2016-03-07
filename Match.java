/**
 *
 * @author joachimhagege
 */
public class Match {
    private final String nameMatched;
    private final int lineOffsetOfMatch;
    private final int charOffsetOfMatch;
    
    public Match(String nameMatched, int lineOffset, int charOffset) {
        this.nameMatched = firstLetterCaps(nameMatched);
        this.lineOffsetOfMatch = lineOffset;
        this.charOffsetOfMatch = charOffset;
    }
    
    private String firstLetterCaps(String name) {
        String firstLetter = name.substring(0,1).toUpperCase();
        String restLetters = name.substring(1).toLowerCase();
        return firstLetter + restLetters;
    }
    
    public String stringPosition() {
        return "(lineOffset=" + lineOffsetOfMatch 
                + ", charOffset=" + charOffsetOfMatch + ")";
    }
    
    public String getName() {
        return nameMatched;
    }
    
    public int getLineOffset() {
        return lineOffsetOfMatch;
    }
    
    public int getCharOffset() {
        return charOffsetOfMatch;
    }
    
    @Override
    public String toString() {
        return stringPosition(); //To change body of generated methods, choose Tools | Templates.
    }    
}