import tester.Tester;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

// represents a set of helpful functions for the connections game
class Utils {

  // counts how many words in each category have been selected
  public int countSelected(ArrayList<ACategory> categories) {
    int c = 0;
    for (int i = 0; i < categories.size(); i++) {
      for (int j = 0; j < categories.get(i).words.size(); j++) {
        if (categories.get(i).words.get(j).selected()) {
          c++;
        }
      }
    }
    return c;
  }

  // makes an array of words into a string
  String wordsToString(ArrayList<AWord> words) {
    String s = "";
    for (int i = 0; i < words.size(); i++) {
      s += words.get(i).word + ", ";
    }

    if (s.length() >= 2) {
      return s.substring(0, s.length() - 2);
    }
    else {
      return s;
    }

  }

  // counts how many categories are scored
  public int countScored(ArrayList<ACategory> categories) {
    int c = 0;
    for (int i = 0; i < categories.size(); i++) {
      c += categories.get(i).scored();
    }
    return c;
  }
}

class UtilsExamples {
  Utils u = new Utils();

  ArrayList<AWord> words0 = new ArrayList<AWord>();
  ArrayList<AWord> words1 = new ArrayList<AWord>(Arrays.asList(new UnselectedWord("HORROR"),
      new UnselectedWord("MUSICAL"), new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN")));

  ACategory unselectedUnscored1 = new UnscoredCategory("MOVIE GENRES", Color.yellow, words1);
  ACategory selectedUnscored1 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"), new SelectedWord("MUSICAL"),
          new SelectedWord("ROMANCE"), new SelectedWord("WESTERN"))));
  ACategory unselectedScored1 = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);
  ACategory selectedScored1 = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HOUNDSTOOTH"),
          new SelectedWord("PAISLEY"), new SelectedWord("PLAID"), new SelectedWord("STRIPES"))),
      0);

  ArrayList<ACategory> categories0 = new ArrayList<ACategory>();
  ArrayList<ACategory> categories1 = new ArrayList<ACategory>(
      Arrays.asList(unselectedUnscored1, unselectedScored1));
  ArrayList<ACategory> categories2 = new ArrayList<ACategory>(
      Arrays.asList(selectedUnscored1, selectedScored1));
  ArrayList<ACategory> categories3 = new ArrayList<ACategory>(
      Arrays.asList(selectedScored1, unselectedScored1));
  ArrayList<ACategory> categories4 = new ArrayList<ACategory>(
      Arrays.asList(unselectedUnscored1, selectedUnscored1));

  // Tests the Utils countSelected method
  void testCountSelected(Tester t) {
    // Test on empty
    t.checkExpect(this.u.countSelected(categories0), 0);
    // Test on lists of no selected
    t.checkExpect(this.u.countSelected(categories1), 0);
    // Test on lists of all selected
    t.checkExpect(this.u.countSelected(categories2), 8);
    // Test on list of mixed
    t.checkExpect(this.u.countSelected(categories3), 4);
  }

  // Tests the Utils wordsToString method
  void testWordsToString(Tester t) {
    // Test on empty
    t.checkExpect(this.u.wordsToString(words0), "");
    // Test on non-empty
    t.checkExpect(this.u.wordsToString(words1), "HORROR, MUSICAL, ROMANCE, WESTERN");
  }

  void testCountScored(Tester t) {
    // Test on empty
    t.checkExpect(this.u.countScored(categories0), 0);
    // Test on lists of no scored
    t.checkExpect(this.u.countScored(categories4), 0);
    // Test on lists of all scored
    t.checkExpect(this.u.countScored(categories3), 2);
    // Test on list of mixed
    t.checkExpect(this.u.countScored(categories1), 1);
  }

}