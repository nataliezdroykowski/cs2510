import tester.*;
import javalib.worldimages.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// represents the main world state of the game
class ConnectionsWorld extends World {

  GameData gameData;
  boolean win = false;

  ConnectionsWorld(GameData gameData) {
    this.gameData = gameData;
  }

  // Creates the scene
  public WorldScene makeScene() {
    return this.gameData.draw(new WorldScene(700, 400));
  }

  // Handles mouse click event
  public void onMouseClicked(Posn pos) {
    this.gameData.select(pos.x, pos.y);
  }

  // Handles key down events
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.gameData = new Database().randomGame();
    }
    else if (key.equals("s") && this.gameData.fourSelected()) {
      this.gameData.score();
      if (this.gameData.endGame()) {
        this.endOfWorld("");
      }
      else if (this.gameData.countScored() == 4) {
        this.win = true;
        this.endOfWorld("");
      }
    }
    else if (key.equals("n")) {
      this.gameData.shuffle();
    }
    else if (key.equals("d")) {
      this.gameData.deselectAll();
    }
  }

  // Handles key down events
  public void fixedOnKeyEvent(String key, Database db, Random rand) {
    if (key.equals("r")) {
      this.gameData = db.fixedRandomGame(rand);
    }
    else if (key.equals("s") && this.gameData.fourSelected()) {
      this.gameData.fixedScore(rand);
      if (this.gameData.endGame()) {
        this.endOfWorld("");
      }
      else if (this.gameData.countScored() == 4) {
        this.win = true;
        this.endOfWorld("");
      }
    }
    else if (key.equals("n")) {
      this.gameData.fixedShuffle(rand);
    }
    else if (key.equals("d")) {
      this.gameData.deselectAll();
    }
  }

  // draws the final state of the world and ends it
  public WorldScene lastScene(String msg) {
    if (this.win) {
      WorldScene ws = this.gameData.drawEnd(new WorldScene(700, 400));
      ws.placeImageXY(
          new TextImage("You won in " + Integer.toString(this.gameData.tries()) + " tries!", 25,
              Color.green),
          335, 340);
      return ws;
    }
    WorldScene ws = this.gameData.drawEnd(new WorldScene(700, 400));
    ws.placeImageXY(new TextImage("You lose!", 25, Color.red), 335, 340);
    return ws;
  }
}

// represents the game data for a game
class GameData {
  ArrayList<ACategory> categories;
  int lives = 4;
  Positions positions;

  GameData(ArrayList<ACategory> categories) {
    this.categories = categories;
    this.positions = new Positions(0);
    this.positions.shuffle();
  }

  // constructor used for testing random
  GameData(ArrayList<ACategory> categories, Random rand) {
    this.categories = categories;
    this.positions = new Positions(0);
    this.positions.fixedShuffle(rand);
  }

  // deselects all selected words
  public void deselectAll() {
    for (int i = 0; i < this.categories.size(); i++) {
      this.categories.get(i).deselectAll();
    }
  }

  // returns how many tries the player has done so far
  public int tries() {
    return 5 - this.lives;
  }

  // draws each category of words onto the game
  WorldScene draw(WorldScene ws) {
    for (int i = 0; i < this.categories.size(); i++) {
      ws = this.categories.get(i).draw(ws, this.positions);
    }
    ws.placeImageXY(new TextImage(Integer.toString(lives), 20, Color.black), 335, 350);
    return ws;
  }

  // draws all of the categories grouped together
  WorldScene drawEnd(WorldScene ws) {
    Utils u = new Utils();

    for (int i = 0; i < this.categories.size(); i++) {
      int scoredBefore = u.countScored(this.categories);
      this.categories.set(i, this.categories.get(i).scoreAll(scoredBefore));
      ws = this.categories.get(i).draw(ws, this.positions);
    }

    return ws;
  }

  // goes through each of the words and calls their "select" method with the given
  // x, y
  public void select(int x, int y) {
    for (int i = 0; i < this.categories.size(); i++) {
      this.categories.get(i).selectWords(x, y, !this.fourSelected());
    }
  }

  // Scores the selected words if possible
  public void score() {
    Utils u = new Utils();
    int scoredBefore = u.countScored(this.categories);

    for (int i = 0; i < this.categories.size(); i++) {
      this.categories.set(i, this.categories.get(i).score(scoredBefore));
    }

    int scoredAfter = u.countScored(this.categories);
    if (scoredBefore != scoredAfter) {
      this.positions = new Positions(scoredAfter);
      this.positions.shuffle();
    }
    else {
      this.lives -= 1;
    }
  }

  // Scores the selected words if possible (used for testing)
  public void fixedScore(Random rand) {
    Utils u = new Utils();
    int scoredBefore = u.countScored(this.categories);

    for (int i = 0; i < this.categories.size(); i++) {
      this.categories.set(i, this.categories.get(i).score(scoredBefore));
    }

    int scoredAfter = u.countScored(this.categories);
    if (scoredBefore != scoredAfter) {
      this.positions = new Positions(scoredAfter);
      this.positions.fixedShuffle(rand);
    }
    else {
      this.lives -= 1;
    }
  }

  // changes the positions of the words
  public void shuffle() {
    this.positions.shuffle();
  }

  // used for testing
  public void fixedShuffle(Random rand) {
    this.positions.fixedShuffle(rand);
  }

  // checks if four words have been selected
  public boolean fourSelected() {
    return new Utils().countSelected(this.categories) >= 4;
  }

  // checks if the player has lost the game
  public boolean endGame() {
    return this.lives == 0;
  }

  // counts how many categories have been guessed correctly so far
  public int countScored() {
    Utils u = new Utils();
    return u.countScored(this.categories);
  }
}

// represents any category of the game
abstract class ACategory {
  ArrayList<AWord> words;
  String description;
  Color color;

  ACategory(String description, Color color, ArrayList<AWord> words) {
    this.description = description;
    this.color = color;
    this.words = words;
  }

  // Scores the category
  abstract ACategory score(int scoredBefore);

  // Scores the category
  abstract public ACategory scoreAll(int i);

  // draws the category's words onto the game
  abstract WorldScene draw(WorldScene ws, Positions pos);

  // makes a word selected
  abstract void selectWords(int x, int y, boolean canSelect);

  // Returns if the category is scored 1 for scored 0 for not
  abstract int scored();

  // deselects all of the words in the category
  public void deselectAll() {
    for (int i = 0; i < this.words.size(); i++) {
      if (this.words.get(i).selected()) {
        this.words.set(i, this.words.get(i).deselect());
      }
    }
  }

}

// represents unscored categories of the game
class UnscoredCategory extends ACategory {

  UnscoredCategory(String description, Color color, ArrayList<AWord> words) {
    super(description, color, words);
  }

  // Scores the category
  public ACategory score(int scoredBefore) {
    if (new Utils().countSelected(new ArrayList<ACategory>(Arrays.asList(this))) == 4) {
      this.deselectWords();
      return new ScoredCategory(this.description, this.color, this.words, scoredBefore);
    }

    return this;
  }

  // Scores the category
  public ACategory scoreAll(int i) {
    this.deselectAll();
    return new ScoredCategory(this.description, this.color, this.words, i);
  }

  // deselects the words in the category
  public void deselectWords() {
    for (int i = 0; i < this.words.size(); i++) {
      this.words.set(i, this.words.get(i).deselect());
    }
  }

  // selects the words in the category
  public void selectWords(int x, int y, boolean canSelect) {
    for (int i = 0; i < this.words.size(); i++) {
      this.words.set(i, this.words.get(i).select(x, y, canSelect));
    }
  }

  // draws the category's words onto the game
  WorldScene draw(WorldScene ws, Positions pos) {
    ArrayList<Integer> positions = pos.getNext();
    for (int i = 0; i < this.words.size(); i++) {
      ws = this.words.get(i).draw(ws, positions.get(i) % 4, positions.get(i) / 4);
    }

    return ws;
  }

  // determined if this category has been scored
  int scored() {
    return 0;
  }
}

// represents categories that have been scored of the game
class ScoredCategory extends ACategory {
  Box catBox;
  final int BOX_WIDTH = 590;
  final int BOX_HEIGHT = 60;
  final int MIDDLE_X = 335;
  int y;

  ScoredCategory(String description, Color color, ArrayList<AWord> words, int pos) {
    super(description, color, words);
    this.y = 65 + 70 * pos;
    this.catBox = new Box(MIDDLE_X, this.y, BOX_WIDTH, BOX_HEIGHT);
  }

  // Scores the category
  public ACategory score(int scoredBefore) {
    return this;
  }

  // Scores the category
  public ACategory scoreAll(int i) {
    return this;
  }

  // selects words in this category
  public void selectWords(int x, int y, boolean canSelect) {
    // does nothing
  }

  // draws the category's words onto the game
  public WorldScene draw(WorldScene ws, Positions pos) {
    ws = this.catBox.draw(ws, this.color);
    ws.placeImageXY(new TextImage(this.description, 15, Color.black), MIDDLE_X, this.y - 5);
    ws.placeImageXY(new TextImage(new Utils().wordsToString(this.words), Color.black), MIDDLE_X,
        this.y + 10);

    return ws;
  }

  // determines if this category has been guessed
  int scored() {
    return 1;
  }
}

// represents a word thats in the game
abstract class AWord {
  String word;
  Box wordBox;
  final int BOX_WIDTH = 140;
  final int BOX_HEIGHT = 60;

  AWord(String word) {
    this.word = word;
  }

  // draws this word onto the world scene
  abstract WorldScene draw(WorldScene ws, int columnIndex, int rowIndex);

  // changes the selection status of this word
  abstract AWord select(int x, int y, boolean canSelect);

  // determines if this word has been selected
  abstract boolean selected();

  // changes this word to be unselected
  AWord deselect() {
    return new UnselectedWord(this.word);
  }
}

// represents a word that hasn't been clicked/selected
class UnselectedWord extends AWord {
  UnselectedWord(String word) {
    super(word);
  }

  UnselectedWord(String word, Box wb) {
    super(word);
    this.wordBox = wb;
  }

  // draws this word onto the world scene
  public WorldScene draw(WorldScene ws, int columnIndex, int rowIndex) {
    int x = 110 + columnIndex % 4 * 150;
    int y = 65 + rowIndex % 4 * 70;
    this.wordBox = new Box(x, y, BOX_WIDTH, BOX_HEIGHT);

    ws = this.wordBox.draw(ws, Color.LIGHT_GRAY);
    ws.placeImageXY(new TextImage(this.word, 15, FontStyle.BOLD, Color.BLACK), x, y);
    return ws;
  }

  // changes the selection status of this word to selected
  public AWord select(int x, int y, boolean canSelect) {
    if (wordBox.clicked(x, y) && canSelect) {
      return new SelectedWord(this.word);
    }
    return this;
  }

  // determines if this word has been selected
  public boolean selected() {
    return false;
  }

}

// represents a word that has been clicked/selected
class SelectedWord extends AWord {
  SelectedWord(String word) {
    super(word);
  }

  SelectedWord(String word, Box wb) {
    super(word);
    this.wordBox = wb;
  }

  // draws this word onto the world scene
  public WorldScene draw(WorldScene ws, int columnIndex, int rowIndex) {
    int x = 110 + columnIndex % 4 * 150;
    int y = 65 + rowIndex % 4 * 70;
    this.wordBox = new Box(x, y, BOX_WIDTH, BOX_HEIGHT);

    ws = this.wordBox.draw(ws, Color.DARK_GRAY);
    ws.placeImageXY(new TextImage(this.word, 15, FontStyle.BOLD, Color.BLACK), x, y);
    return ws;
  }

  // changes the selection status of this word into unselected
  public AWord select(int x, int y, boolean canSelect) {
    if (this.wordBox.clicked(x, y)) {
      return new UnselectedWord(this.word);
    }
    return this;
  }

  // determines if this word has been selected
  public boolean selected() {
    return true;
  }
}

// Represents a box that contains a single word
class Box {
  int x;
  int y;
  int width;
  int height;

  Box(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  // Checks if the clicked location is inside the box
  boolean clicked(int x, int y) {
    return (this.x - this.width / 2 <= x && x <= this.x + this.width / 2)
        && (this.y - this.height / 2 <= y && y <= this.y + this.height / 2);
  }

  // Draws a box
  WorldScene draw(WorldScene ws, Color color) {
    ws.placeImageXY(new RectangleImage(width, height, OutlineMode.SOLID, color), this.x, this.y);
    return ws;
  }
}

// testing the game
class Examples {
  Box box1 = new Box(100, 100, 140, 60);
  Box box2 = new Box(400, 400, 140, 60);

  AWord word1 = new SelectedWord("hello");
  AWord word2 = new UnselectedWord("world");

  AWord wordBox1 = new SelectedWord("a", this.box1);
  AWord wordBox2 = new SelectedWord("b", this.box2);
  AWord wordBox3 = new UnselectedWord("c", this.box1);
  AWord wordBox4 = new UnselectedWord("d", this.box2);

  AWord selectWordBox1 = new UnselectedWord("a");
  AWord selectWordBox2 = new UnselectedWord("b");
  AWord selectWordBox3 = new SelectedWord("c");
  AWord selectWordBox4 = new SelectedWord("d");

  // Game data example of starting board
  ACategory yellowEx0 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx0 = new UnscoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))));
  ACategory blueEx0 = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
  ACategory purpleEx0 = new UnscoredCategory("CANDY PIECES", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
          new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
  GameData gameEx0 = new GameData(
      new ArrayList<ACategory>(
          Arrays.asList(this.yellowEx0, this.greenEx0, this.blueEx0, this.purpleEx0)),
      new Random(1));

  // Game data example of starting board after deselecting all
  ACategory yellowEx0Deselected = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx0Deselected = new UnscoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))));
  ACategory blueEx0Deselected = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
  ACategory purpleEx0Deselected = new UnscoredCategory("CANDY PIECES", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
          new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
  GameData gameEx0Deselected = new GameData(
      new ArrayList<ACategory>(Arrays.asList(this.yellowEx0Deselected, this.greenEx0Deselected,
          this.blueEx0Deselected, this.purpleEx0Deselected)),
      new Random(1));

  // Game data example with scored categories
  ACategory yellowEx1 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx1 = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);
  ACategory blueEx1 = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
      1);
  ACategory purpleEx1 = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      2);
  GameData gameEx1 = new GameData(
      new ArrayList<ACategory>(
          Arrays.asList(this.yellowEx1, this.greenEx1, this.blueEx1, this.purpleEx1)),
      new Random(1));

  // Game data example with scored categories
  ACategory yellowEx1Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx1Same = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);
  ACategory blueEx1Same = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
      1);
  ACategory purpleEx1Same = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      2);
  GameData gameEx1Same = new GameData(new ArrayList<ACategory>(
      Arrays.asList(this.yellowEx1Same, this.greenEx1Same, this.blueEx1Same, this.purpleEx1Same)),
      new Random(1));

  // Game data example with scored categories after deselecting all
  ACategory yellowEx1Deselected = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx1Deselected = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);
  ACategory blueEx1Deselected = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
      1);
  ACategory purpleEx1Deselected = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      2);
  GameData gameEx1Deselected = new GameData(
      new ArrayList<ACategory>(Arrays.asList(this.yellowEx1Deselected, this.greenEx1Deselected,
          this.blueEx1Deselected, this.purpleEx1Deselected)),
      new Random(1));

  // Game data example with scored categories and selected categories
  ACategory yellowEx3 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
          new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx3 = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);
  ACategory blueEx3 = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
      1);
  ACategory purpleEx3 = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      2);
  GameData gameEx3 = new GameData(
      new ArrayList<ACategory>(
          Arrays.asList(this.yellowEx3, this.greenEx3, this.blueEx3, this.purpleEx3)),
      new Random(1));

  // Game data example with scored categories and selected categories
  ACategory yellowEx3Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
          new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx3Same = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);
  ACategory blueEx3Same = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
      1);
  ACategory purpleEx3Same = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      2);
  GameData gameEx3Same = new GameData(new ArrayList<ACategory>(
      Arrays.asList(this.yellowEx3Same, this.greenEx3Same, this.blueEx3Same, this.purpleEx3Same)),
      new Random(1));

  // Game data example with scored categories and selected categories after
  // deselecting all
  ACategory yellowEx3Deselected = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx3Deselected = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);
  ACategory blueEx3Deselected = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
      1);
  ACategory purpleEx3Deselected = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      2);
  GameData gameEx3Deselected = new GameData(
      new ArrayList<ACategory>(Arrays.asList(this.yellowEx3Deselected, this.greenEx3Deselected,
          this.blueEx3Deselected, this.purpleEx3Deselected)),
      new Random(1));

  // Game data example all categories scored
  ACategory yellowEx4 = new ScoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))),
      0);
  ACategory greenEx4 = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      1);
  ACategory blueEx4 = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
      2);
  ACategory purpleEx4 = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      3);
  GameData gameEx4 = new GameData(
      new ArrayList<ACategory>(
          Arrays.asList(this.yellowEx4, this.greenEx4, this.blueEx4, this.purpleEx4)),
      new Random(1));

  // Game data example 4 selected words
  ACategory yellowEx6 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx6 = new UnscoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HOUNDSTOOTH"),
          new SelectedWord("PAISLEY"), new SelectedWord("PLAID"), new SelectedWord("STRIPES"))));
  ACategory blueEx6 = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
  ACategory purpleEx6 = new UnscoredCategory("CANDY PIECES", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
          new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
  GameData gameEx6 = new GameData(
      new ArrayList<ACategory>(
          Arrays.asList(this.yellowEx6, this.greenEx6, this.blueEx6, this.purpleEx6)),
      new Random(1));

  // Game data example 4 selected words
  ACategory yellowEx6Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx6Same = new UnscoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HOUNDSTOOTH"),
          new SelectedWord("PAISLEY"), new SelectedWord("PLAID"), new SelectedWord("STRIPES"))));
  ACategory blueEx6Same = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
  ACategory purpleEx6Same = new UnscoredCategory("CANDY PIECES", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
          new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
  GameData gameEx6Same = new GameData(new ArrayList<ACategory>(
      Arrays.asList(this.yellowEx6Same, this.greenEx6Same, this.blueEx6Same, this.purpleEx6Same)),
      new Random(1));
  ACategory greenEx6Deselect = new UnscoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("HOUNDSTOOTH"),
          new SelectedWord("PAISLEY"), new SelectedWord("PLAID"), new SelectedWord("STRIPES"))));
  ACategory greenEx6Scored = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);
  GameData gameEx6Deselect = new GameData(new ArrayList<ACategory>(Arrays.asList(this.yellowEx6Same,
      this.greenEx6Deselect, this.blueEx6Same, this.purpleEx6Same)), new Random(1));
  GameData gameEx6Scored = new GameData(new ArrayList<ACategory>(
      Arrays.asList(this.yellowEx6Same, this.greenEx6Scored, this.blueEx6Same, this.purpleEx6Same)),
      new Random(1));

  // Game data example 4 selected words in different categories
  ACategory yellowEx7 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
          new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx7 = new UnscoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HOUNDSTOOTH"),
          new SelectedWord("PAISLEY"), new UnselectedWord("PLAID"), new SelectedWord("STRIPES"))));
  ACategory blueEx7 = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
  ACategory purpleEx7 = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      0);
  GameData gameEx7 = new GameData(
      new ArrayList<ACategory>(
          Arrays.asList(this.yellowEx7, this.greenEx7, this.blueEx7, this.purpleEx7)),
      new Random(1));

  // Game data example 4 selected words in different categories
  ACategory yellowEx7Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
          new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx7Same = new UnscoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HOUNDSTOOTH"),
          new SelectedWord("PAISLEY"), new UnselectedWord("PLAID"), new SelectedWord("STRIPES"))));
  ACategory blueEx7Same = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
  ACategory purpleEx7Same = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      0);
  GameData gameEx7LifeLoss = new GameData(new ArrayList<ACategory>(
      Arrays.asList(this.yellowEx7Same, this.greenEx7Same, this.blueEx7Same, this.purpleEx7Same)),
      new Random(1));

  ACategory yellowEx0Scored = new ScoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))),
      0);
  ACategory greenEx0Scored = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      1);
  ACategory blueEx0Scored = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
      2);
  ACategory purpleEx0Scored = new ScoredCategory(
      "CANDY PIECES", Color.magenta, new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"),
          new UnselectedWord("GOOBER"), new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
      3);

  ACategory yellowEx1Scored = new ScoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))),
      3);

  ACategory yellowEx0Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory greenEx0Same = new UnscoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))));
  ACategory blueEx0Same = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
  ACategory purpleEx0Same = new UnscoredCategory("CANDY PIECES", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
          new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
  GameData gameEx0Same = new GameData(new ArrayList<ACategory>(
      Arrays.asList(this.yellowEx0Same, this.greenEx0Same, this.blueEx0Same, this.purpleEx0Same)),
      new Random(1));

  ACategory yellowEx1SelectHorror = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
          new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory yellowEx1Selected = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
          new UnselectedWord("ROMANCE"), new SelectedWord("WESTERN"))));

  GameData gameEx0Selected = new GameData(new ArrayList<ACategory>(Arrays
      .asList(this.yellowEx1SelectHorror, this.greenEx0Same, this.blueEx0Same, this.purpleEx0Same)),
      new Random(1));

  ACategory greenEx1Selected = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("HOUNDSTOOTH"),
          new SelectedWord("PAISLEY"), new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);

  ACategory yellowEx1SelectedSame = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
          new UnselectedWord("ROMANCE"), new SelectedWord("WESTERN"))));
  ACategory greenEx1SelectedSame = new ScoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("HOUNDSTOOTH"),
          new SelectedWord("PAISLEY"), new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
      0);

  ACategory yellowEx1AllSelected = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"), new SelectedWord("MUSICAL"),
          new SelectedWord("ROMANCE"), new SelectedWord("WESTERN"))));

  ACategory yellowBox = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("HORROR", new Box(110, 135, 140, 60)),
          new UnselectedWord("MUSICAL", new Box(110, 275, 140, 60)),
          new UnselectedWord("ROMANCE", new Box(260, 275, 140, 60)),
          new UnselectedWord("WESTERN", new Box(560, 205, 140, 60)))));

  ACategory yellowBoxSame = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("HORROR", new Box(110, 135, 140, 60)),
          new UnselectedWord("MUSICAL", new Box(110, 275, 140, 60)),
          new UnselectedWord("ROMANCE", new Box(260, 275, 140, 60)),
          new UnselectedWord("WESTERN", new Box(560, 205, 140, 60)))));

  ACategory yellowBoxSelectFirst = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new SelectedWord("HORROR"),
          new UnselectedWord("MUSICAL", new Box(110, 275, 140, 60)),
          new UnselectedWord("ROMANCE", new Box(260, 275, 140, 60)),
          new UnselectedWord("WESTERN", new Box(560, 205, 140, 60)))));

  WorldScene ws = new WorldScene(700, 500);
  WorldScene wsWithBox = this.ws;
  WorldScene wsWithTwoBox = this.ws;

  WorldScene word1Draw = new Box(110, 65, 140, 60).draw(this.ws, Color.DARK_GRAY);
  WorldScene word2Draw = new Box(410, 275, 140, 60).draw(this.ws, Color.LIGHT_GRAY);

  WorldScene yellowDrawStep1 = new Box(110, 135, 140, 60).draw(this.ws, Color.DARK_GRAY);
  WorldScene yellowDrawStep2 = new Box(110, 275, 140, 60).draw(this.yellowDrawStep1,
      Color.LIGHT_GRAY);
  WorldScene yellowDrawStep3 = new Box(260, 275, 140, 60).draw(this.yellowDrawStep2,
      Color.LIGHT_GRAY);
  WorldScene yellowDrawFinal = new Box(560, 205, 140, 60).draw(this.yellowDrawStep3,
      Color.LIGHT_GRAY);

  Positions pos1 = new Positions(0);

  WorldScene ex0RandomWS = new WorldScene(700, 400);
  WorldScene ex1RandomWS = new WorldScene(700, 400);
  WorldScene ex2RandomWS = new WorldScene(700, 400);
  WorldScene ex3RandomWS = new WorldScene(700, 400);
  WorldScene ex4RandomWS = new WorldScene(700, 400);
  WorldScene ex5RandomWS = new WorldScene(700, 400);

  ConnectionsWorld world0 = new ConnectionsWorld(this.gameEx0);
  ConnectionsWorld world1 = new ConnectionsWorld(this.gameEx1);
  ConnectionsWorld world3 = new ConnectionsWorld(this.gameEx3);
  ConnectionsWorld world0Selected = new ConnectionsWorld(this.gameEx0Selected);
  ConnectionsWorld world6 = new ConnectionsWorld(this.gameEx6);
  ConnectionsWorld world7 = new ConnectionsWorld(this.gameEx7);

  Database db1 = new Database();

  void initData() {
    this.yellowEx0 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx0 = new UnscoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))));
    this.blueEx0 = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
    this.purpleEx0 = new UnscoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
    this.gameEx0 = new GameData(
        new ArrayList<ACategory>(
            Arrays.asList(this.yellowEx0, this.greenEx0, this.blueEx0, this.purpleEx0)),
        new Random(1));

    this.yellowEx0Deselected = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx0Deselected = new UnscoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))));
    this.blueEx0Deselected = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
    this.purpleEx0Deselected = new UnscoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
    this.gameEx0Deselected = new GameData(
        new ArrayList<ACategory>(Arrays.asList(this.yellowEx0Deselected, this.greenEx0Deselected,
            this.blueEx0Deselected, this.purpleEx0Deselected)),
        new Random(1));

    this.yellowEx1 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx1 = new ScoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
        0);
    this.blueEx1 = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
        1);
    this.purpleEx1 = new ScoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
        2);
    this.gameEx1 = new GameData(
        new ArrayList<ACategory>(
            Arrays.asList(this.yellowEx1, this.greenEx1, this.blueEx1, this.purpleEx1)),
        new Random(1));

    this.yellowEx1Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx1Same = new ScoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
        0);
    this.blueEx1Same = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
        1);
    this.purpleEx1Same = new ScoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
        2);
    this.gameEx1Same = new GameData(new ArrayList<ACategory>(
        Arrays.asList(this.yellowEx1Same, this.greenEx1Same, this.blueEx1Same, this.purpleEx1Same)),
        new Random(1));

    this.yellowEx1Deselected = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx1Deselected = new ScoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
        0);
    this.blueEx1Deselected = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
        1);
    this.purpleEx1Deselected = new ScoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
        2);
    this.gameEx1Deselected = new GameData(
        new ArrayList<ACategory>(Arrays.asList(this.yellowEx1Deselected, this.greenEx1Deselected,
            this.blueEx1Deselected, this.purpleEx1Deselected)),
        new Random(1));

    this.yellowEx3 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx3 = new ScoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
        0);
    this.blueEx3 = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
        1);
    this.purpleEx3 = new ScoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
        2);
    this.gameEx3 = new GameData(
        new ArrayList<ACategory>(
            Arrays.asList(this.yellowEx3, this.greenEx3, this.blueEx3, this.purpleEx3)),
        new Random(1));

    // Game data example with scored categories and selected categories
    this.yellowEx3Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx3Same = new ScoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
        0);
    this.blueEx3Same = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
        1);
    this.purpleEx3Same = new ScoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
        2);
    this.gameEx3Same = new GameData(new ArrayList<ACategory>(
        Arrays.asList(this.yellowEx3Same, this.greenEx3Same, this.blueEx3Same, this.purpleEx3Same)),
        new Random(1));

    this.yellowEx3Deselected = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx3Deselected = new ScoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
        0);
    this.blueEx3Deselected = new ScoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))),
        1);
    this.purpleEx3Deselected = new ScoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
        2);
    this.gameEx3Deselected = new GameData(
        new ArrayList<ACategory>(Arrays.asList(this.yellowEx3Deselected, this.greenEx3Deselected,
            this.blueEx3Deselected, this.purpleEx3Deselected)),
        new Random(1));

    this.yellowEx0Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx0Same = new UnscoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))));
    this.blueEx0Same = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
    this.purpleEx0Same = new UnscoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
    this.gameEx0Same = new GameData(new ArrayList<ACategory>(
        Arrays.asList(this.yellowEx0Same, this.greenEx0Same, this.blueEx0Same, this.purpleEx0Same)),
        new Random(1));

    this.yellowEx1SelectHorror = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.yellowEx1Selected = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new SelectedWord("WESTERN"))));

    this.gameEx0Selected = new GameData(
        new ArrayList<ACategory>(Arrays.asList(this.yellowEx1SelectHorror, this.greenEx0Same,
            this.blueEx0Same, this.purpleEx0Same)),
        new Random(1));

    this.greenEx1Selected = new ScoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new SelectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
        0);

    // Game data example 4 selected words
    this.yellowEx6 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx6 = new UnscoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(Arrays.asList(new SelectedWord("HOUNDSTOOTH"),
            new SelectedWord("PAISLEY"), new SelectedWord("PLAID"), new SelectedWord("STRIPES"))));
    this.blueEx6 = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
    this.purpleEx6 = new UnscoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
    this.gameEx6 = new GameData(
        new ArrayList<ACategory>(
            Arrays.asList(this.yellowEx6, this.greenEx6, this.blueEx6, this.purpleEx6)),
        new Random(1));

    // Game data example 4 selected words
    this.yellowEx6Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx6Same = new UnscoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(Arrays.asList(new SelectedWord("HOUNDSTOOTH"),
            new SelectedWord("PAISLEY"), new SelectedWord("PLAID"), new SelectedWord("STRIPES"))));
    this.blueEx6Same = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
    this.purpleEx6Same = new UnscoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));
    this.gameEx6Same = new GameData(new ArrayList<ACategory>(
        Arrays.asList(this.yellowEx6Same, this.greenEx6Same, this.blueEx6Same, this.purpleEx6Same)),
        new Random(1));
    this.greenEx6Deselect = new UnscoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("HOUNDSTOOTH"),
            new SelectedWord("PAISLEY"), new SelectedWord("PLAID"), new SelectedWord("STRIPES"))));
    this.greenEx6Scored = new ScoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))),
        0);
    this.gameEx6Deselect = new GameData(new ArrayList<ACategory>(Arrays.asList(this.yellowEx6Same,
        this.greenEx6Deselect, this.blueEx6Same, this.purpleEx6Same)), new Random(1));
    this.gameEx6Scored = new GameData(new ArrayList<ACategory>(Arrays.asList(this.yellowEx6Same,
        this.greenEx6Scored, this.blueEx6Same, this.purpleEx6Same)), new Random(1));
    this.gameEx6Scored.positions = new Positions(1);
    this.gameEx6Scored.positions.fixedShuffle(new Random(1));

    this.yellowEx7 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx7 = new UnscoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new SelectedWord("HOUNDSTOOTH"), new SelectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new SelectedWord("STRIPES"))));
    this.blueEx7 = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
    this.purpleEx7 = new ScoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
        0);
    this.gameEx7 = new GameData(
        new ArrayList<ACategory>(
            Arrays.asList(this.yellowEx7, this.greenEx7, this.blueEx7, this.purpleEx7)),
        new Random(1));

    // Game data example 4 selected words in different categories
    this.yellowEx7Same = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(
            Arrays.asList(new SelectedWord("HORROR"), new UnselectedWord("MUSICAL"),
                new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
    this.greenEx7Same = new UnscoredCategory("PATTERNS", Color.green,
        new ArrayList<AWord>(
            Arrays.asList(new SelectedWord("HOUNDSTOOTH"), new SelectedWord("PAISLEY"),
                new UnselectedWord("PLAID"), new SelectedWord("STRIPES"))));
    this.blueEx7Same = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
            new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
    this.purpleEx7Same = new ScoredCategory("CANDY PIECES", Color.magenta,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
            new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))),
        0);
    this.gameEx7LifeLoss = new GameData(new ArrayList<ACategory>(
        Arrays.asList(this.yellowEx7Same, this.greenEx7Same, this.blueEx7Same, this.purpleEx7Same)),
        new Random(1));
    this.gameEx7LifeLoss.lives = 3;

    this.yellowBox = new UnscoredCategory("MOVIE GENRES", Color.yellow,
        new ArrayList<AWord>(Arrays.asList(new UnselectedWord("HORROR", new Box(110, 135, 140, 60)),
            new UnselectedWord("MUSICAL", new Box(110, 275, 140, 60)),
            new UnselectedWord("ROMANCE", new Box(260, 275, 140, 60)),
            new UnselectedWord("WESTERN", new Box(560, 205, 140, 60)))));

    this.wsWithBox.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.cyan), 100,
        100);
    this.wsWithTwoBox.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.cyan), 100,
        100);
    this.wsWithTwoBox.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.magenta),
        400, 400);

    this.word1Draw.placeImageXY(new TextImage("hello", 15, FontStyle.BOLD, Color.BLACK), 110, 65);

    this.word2Draw.placeImageXY(new TextImage("world", 15, FontStyle.BOLD, Color.BLACK), 410, 275);

    this.yellowDrawStep1.placeImageXY(new TextImage("HORROR", 15, FontStyle.BOLD, Color.BLACK), 110,
        135);
    this.yellowDrawStep2.placeImageXY(new TextImage("MUSICAL", 15, FontStyle.BOLD, Color.BLACK),
        110, 275);
    this.yellowDrawStep3.placeImageXY(new TextImage("ROMANCE", 15, FontStyle.BOLD, Color.BLACK),
        260, 275);
    this.yellowDrawFinal.placeImageXY(new TextImage("WESTERN", 15, FontStyle.BOLD, Color.BLACK),
        560, 205);

    this.ex0RandomWS = new WorldScene(700, 400);
    this.ex1RandomWS = new WorldScene(700, 400);
    this.ex2RandomWS = new WorldScene(700, 400);
    this.ex3RandomWS = new WorldScene(700, 400);
    this.ex4RandomWS = new WorldScene(700, 400);
    this.ex5RandomWS = new WorldScene(700, 400);

    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        410, 205);
    this.ex0RandomWS.placeImageXY(new TextImage("HORROR", 15, FontStyle.BOLD, Color.BLACK), 410,
        205);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        260, 135);
    this.ex0RandomWS.placeImageXY(new TextImage("MUSICAL", 15, FontStyle.BOLD, Color.BLACK), 260,
        135);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 65);
    this.ex0RandomWS.placeImageXY(new TextImage("ROMANCE", 15, FontStyle.BOLD, Color.BLACK), 110,
        65);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 275);
    this.ex0RandomWS.placeImageXY(new TextImage("WESTERN", 15, FontStyle.BOLD, Color.BLACK), 110,
        275);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        560, 65);
    this.ex0RandomWS.placeImageXY(new TextImage("HOUNDSTOOTH", 15, FontStyle.BOLD, Color.BLACK),
        560, 65);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 205);
    this.ex0RandomWS.placeImageXY(new TextImage("PAISLEY", 15, FontStyle.BOLD, Color.BLACK), 110,
        205);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        410, 135);
    this.ex0RandomWS.placeImageXY(new TextImage("PLAID", 15, FontStyle.BOLD, Color.BLACK), 410,
        135);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        560, 135);
    this.ex0RandomWS.placeImageXY(new TextImage("STRIPES", 15, FontStyle.BOLD, Color.BLACK), 560,
        135);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        410, 275);
    this.ex0RandomWS.placeImageXY(new TextImage("FIB", 15, FontStyle.BOLD, Color.BLACK), 410, 275);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 135);
    this.ex0RandomWS.placeImageXY(new TextImage("FICTION", 15, FontStyle.BOLD, Color.BLACK), 110,
        135);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        560, 275);
    this.ex0RandomWS.placeImageXY(new TextImage("LIE", 15, FontStyle.BOLD, Color.BLACK), 560, 275);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        410, 65);
    this.ex0RandomWS.placeImageXY(new TextImage("TALE", 15, FontStyle.BOLD, Color.BLACK), 410, 65);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        260, 205);
    this.ex0RandomWS.placeImageXY(new TextImage("DOT", 15, FontStyle.BOLD, Color.BLACK), 260, 205);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        260, 65);
    this.ex0RandomWS.placeImageXY(new TextImage("GOOBER", 15, FontStyle.BOLD, Color.BLACK), 260,
        65);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        260, 275);
    this.ex0RandomWS.placeImageXY(new TextImage("KISS", 15, FontStyle.BOLD, Color.BLACK), 260, 275);
    this.ex0RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        560, 205);
    this.ex0RandomWS.placeImageXY(new TextImage("WHOPPER", 15, FontStyle.BOLD, Color.BLACK), 560,
        205);
    this.ex0RandomWS.placeImageXY(new TextImage("4", 20, FontStyle.REGULAR, Color.BLACK), 335, 350);

    this.ex1RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        410, 205);
    this.ex1RandomWS.placeImageXY(new TextImage("HORROR", 15, FontStyle.BOLD, Color.BLACK), 410,
        205);
    this.ex1RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        260, 135);
    this.ex1RandomWS.placeImageXY(new TextImage("MUSICAL", 15, FontStyle.BOLD, Color.BLACK), 260,
        135);
    this.ex1RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 65);
    this.ex1RandomWS.placeImageXY(new TextImage("ROMANCE", 15, FontStyle.BOLD, Color.BLACK), 110,
        65);
    this.ex1RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 275);
    this.ex1RandomWS.placeImageXY(new TextImage("WESTERN", 15, FontStyle.BOLD, Color.BLACK), 110,
        275);
    this.ex1RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.GREEN), 335,
        65);
    this.ex1RandomWS.placeImageXY(new TextImage("PATTERNS", 15, FontStyle.REGULAR, Color.BLACK),
        335, 60);
    this.ex1RandomWS.placeImageXY(
        new TextImage("HOUNDSTOOTH, PAISLEY, PLAID, STRIPES", 13, FontStyle.REGULAR, Color.BLACK),
        335, 75);
    this.ex1RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.CYAN), 335,
        135);
    this.ex1RandomWS.placeImageXY(
        new TextImage("SYNONYMS FOR FALSEHOOD", 15, FontStyle.REGULAR, Color.BLACK), 335, 130);
    this.ex1RandomWS.placeImageXY(
        new TextImage("FIB, FICTION, LIE, TALE", 13, FontStyle.REGULAR, Color.BLACK), 335, 145);
    this.ex1RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.MAGENTA),
        335, 205);
    this.ex1RandomWS.placeImageXY(new TextImage("CANDY PIECES", 15, FontStyle.REGULAR, Color.BLACK),
        335, 200);
    this.ex1RandomWS.placeImageXY(
        new TextImage("DOT, GOOBER, KISS, WHOPPER", 13, FontStyle.REGULAR, Color.BLACK), 335, 215);
    this.ex1RandomWS.placeImageXY(new TextImage("4", 20, FontStyle.REGULAR, Color.BLACK), 335, 350);

    this.ex2RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        410, 205);
    this.ex2RandomWS.placeImageXY(new TextImage("HORROR", 15, FontStyle.BOLD, Color.BLACK), 410,
        205);
    this.ex2RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        260, 135);
    this.ex2RandomWS.placeImageXY(new TextImage("MUSICAL", 15, FontStyle.BOLD, Color.BLACK), 260,
        135);
    this.ex2RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 65);
    this.ex2RandomWS.placeImageXY(new TextImage("ROMANCE", 15, FontStyle.BOLD, Color.BLACK), 110,
        65);
    this.ex2RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 275);
    this.ex2RandomWS.placeImageXY(new TextImage("WESTERN", 15, FontStyle.BOLD, Color.BLACK), 110,
        275);
    this.ex2RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.GREEN), 335,
        65);
    this.ex2RandomWS.placeImageXY(new TextImage("PATTERNS", 15, FontStyle.REGULAR, Color.BLACK),
        335, 60);
    this.ex2RandomWS.placeImageXY(
        new TextImage("HOUNDSTOOTH, PAISLEY, PLAID, STRIPES", 13, FontStyle.REGULAR, Color.BLACK),
        335, 75);
    this.ex2RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.CYAN), 335,
        135);
    this.ex2RandomWS.placeImageXY(
        new TextImage("SYNONYMS FOR FALSEHOOD", 15, FontStyle.REGULAR, Color.BLACK), 335, 130);
    this.ex2RandomWS.placeImageXY(
        new TextImage("FIB, FICTION, LIE, TALE", 13, FontStyle.REGULAR, Color.BLACK), 335, 145);
    this.ex2RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.MAGENTA),
        335, 205);
    this.ex2RandomWS.placeImageXY(new TextImage("CANDY PIECES", 15, FontStyle.REGULAR, Color.BLACK),
        335, 200);
    this.ex2RandomWS.placeImageXY(
        new TextImage("DOT, GOOBER, KISS, WHOPPER", 13, FontStyle.REGULAR, Color.BLACK), 335, 215);
    this.ex2RandomWS.placeImageXY(new TextImage("2", 20, FontStyle.REGULAR, Color.BLACK), 335, 350);

    this.ex3RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.DARK_GRAY),
        410, 205);
    this.ex3RandomWS.placeImageXY(new TextImage("HORROR", 15, FontStyle.BOLD, Color.BLACK), 410,
        205);
    this.ex3RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        260, 135);
    this.ex3RandomWS.placeImageXY(new TextImage("MUSICAL", 15, FontStyle.BOLD, Color.BLACK), 260,
        135);
    this.ex3RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 65);
    this.ex3RandomWS.placeImageXY(new TextImage("ROMANCE", 15, FontStyle.BOLD, Color.BLACK), 110,
        65);
    this.ex3RandomWS.placeImageXY(new RectangleImage(140, 60, OutlineMode.SOLID, Color.LIGHT_GRAY),
        110, 275);
    this.ex3RandomWS.placeImageXY(new TextImage("WESTERN", 15, FontStyle.BOLD, Color.BLACK), 110,
        275);
    this.ex3RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.GREEN), 335,
        65);
    this.ex3RandomWS.placeImageXY(new TextImage("PATTERNS", 15, FontStyle.REGULAR, Color.BLACK),
        335, 60);
    this.ex3RandomWS.placeImageXY(
        new TextImage("HOUNDSTOOTH, PAISLEY, PLAID, STRIPES", 13, FontStyle.REGULAR, Color.BLACK),
        335, 75);
    this.ex3RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.CYAN), 335,
        135);
    this.ex3RandomWS.placeImageXY(
        new TextImage("SYNONYMS FOR FALSEHOOD", 15, FontStyle.REGULAR, Color.BLACK), 335, 130);
    this.ex3RandomWS.placeImageXY(
        new TextImage("FIB, FICTION, LIE, TALE", 13, FontStyle.REGULAR, Color.BLACK), 335, 145);
    this.ex3RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.MAGENTA),
        335, 205);
    this.ex3RandomWS.placeImageXY(new TextImage("CANDY PIECES", 15, FontStyle.REGULAR, Color.BLACK),
        335, 200);
    this.ex3RandomWS.placeImageXY(
        new TextImage("DOT, GOOBER, KISS, WHOPPER", 13, FontStyle.REGULAR, Color.BLACK), 335, 215);
    this.ex3RandomWS.placeImageXY(new TextImage("4", 20, FontStyle.REGULAR, Color.BLACK), 335, 350);

    this.ex4RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.YELLOW), 335,
        65);
    this.ex4RandomWS.placeImageXY(new TextImage("MOVIE GENRES", 15, FontStyle.REGULAR, Color.BLACK),
        335, 60);
    this.ex4RandomWS.placeImageXY(
        new TextImage("HORROR, MUSICAL, ROMANCE, WESTERN", 13, FontStyle.REGULAR, Color.BLACK), 335,
        75);
    this.ex4RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.GREEN), 335,
        135);
    this.ex4RandomWS.placeImageXY(new TextImage("PATTERNS", 15, FontStyle.REGULAR, Color.BLACK),
        335, 130);
    this.ex4RandomWS.placeImageXY(
        new TextImage("HOUNDSTOOTH, PAISLEY, PLAID, STRIPES", 13, FontStyle.REGULAR, Color.BLACK),
        335, 145);
    this.ex4RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.CYAN), 335,
        205);
    this.ex4RandomWS.placeImageXY(
        new TextImage("SYNONYMS FOR FALSEHOOD", 15, FontStyle.REGULAR, Color.BLACK), 335, 200);
    this.ex4RandomWS.placeImageXY(
        new TextImage("FIB, FICTION, LIE, TALE", 13, FontStyle.REGULAR, Color.BLACK), 335, 215);
    this.ex4RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.MAGENTA),
        335, 275);
    this.ex4RandomWS.placeImageXY(new TextImage("CANDY PIECES", 15, FontStyle.REGULAR, Color.BLACK),
        335, 270);
    this.ex4RandomWS.placeImageXY(
        new TextImage("DOT, GOOBER, KISS, WHOPPER", 13, FontStyle.REGULAR, Color.BLACK), 335, 285);

    this.ex5RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.YELLOW), 335,
        275);
    this.ex5RandomWS.placeImageXY(new TextImage("MOVIE GENRES", 15, FontStyle.REGULAR, Color.BLACK),
        335, 270);
    this.ex5RandomWS.placeImageXY(
        new TextImage("HORROR, MUSICAL, ROMANCE, WESTERN", 13, FontStyle.REGULAR, Color.BLACK), 335,
        285);
    this.ex5RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.GREEN), 335,
        65);
    this.ex5RandomWS.placeImageXY(new TextImage("PATTERNS", 15, FontStyle.REGULAR, Color.BLACK),
        335, 60);
    this.ex5RandomWS.placeImageXY(
        new TextImage("HOUNDSTOOTH, PAISLEY, PLAID, STRIPES", 13, FontStyle.REGULAR, Color.BLACK),
        335, 75);
    this.ex5RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.CYAN), 335,
        135);
    this.ex5RandomWS.placeImageXY(
        new TextImage("SYNONYMS FOR FALSEHOOD", 15, FontStyle.REGULAR, Color.BLACK), 335, 130);
    this.ex5RandomWS.placeImageXY(
        new TextImage("FIB, FICTION, LIE, TALE", 13, FontStyle.REGULAR, Color.BLACK), 335, 145);
    this.ex5RandomWS.placeImageXY(new RectangleImage(590, 60, OutlineMode.SOLID, Color.MAGENTA),
        335, 205);
    this.ex5RandomWS.placeImageXY(new TextImage("CANDY PIECES", 15, FontStyle.REGULAR, Color.BLACK),
        335, 200);
    this.ex5RandomWS.placeImageXY(
        new TextImage("DOT, GOOBER, KISS, WHOPPER", 13, FontStyle.REGULAR, Color.BLACK), 335, 215);

    this.world0 = new ConnectionsWorld(this.gameEx0);
    this.world1 = new ConnectionsWorld(this.gameEx1);
    this.world3 = new ConnectionsWorld(this.gameEx3);
    this.world0Selected = new ConnectionsWorld(this.gameEx0Selected);
    this.world6 = new ConnectionsWorld(this.gameEx6);
    this.world7 = new ConnectionsWorld(this.gameEx7);

    this.db1 = new Database();
  }

  // TESTS FOR BOX
  // tests Box draw method
  void testBoxDraw(Tester t) {
    this.initData();
    t.checkExpect(this.box1.draw(this.ws, Color.cyan), this.wsWithBox);
    t.checkExpect(this.box2.draw(this.wsWithBox, Color.magenta), this.wsWithTwoBox);
  }

  // tests Box clicked method
  void testClicked(Tester t) {
    this.initData();
    t.checkExpect(this.box1.clicked(50, 50), false);
    t.checkExpect(this.box1.clicked(150, 110), true);
    t.checkExpect(this.box2.clicked(450, 300), false);
    t.checkExpect(this.box2.clicked(420, 380), true);
  }

  // TESTS FOR AWORD
  // tests AWord draw
  void testWordDraw(Tester t) {
    this.initData();
    t.checkExpect(this.word1.draw(this.ws, 0, 0), this.word1Draw);
    t.checkExpect(this.word2.draw(this.ws, 2, 3), this.word2Draw);
  }

  // tests AWord select
  void testSelect(Tester t) {
    this.initData();
    t.checkExpect(this.wordBox1.select(150, 110, false), this.selectWordBox1);
    t.checkExpect(this.wordBox1.select(50, 50, true), this.wordBox1);
    t.checkExpect(this.wordBox1.select(150, 110, true), this.selectWordBox1);
    t.checkExpect(this.wordBox2.select(420, 380, false), this.selectWordBox2);
    t.checkExpect(this.wordBox2.select(50, 50, true), this.wordBox2);
    t.checkExpect(this.wordBox2.select(420, 380, true), this.selectWordBox2);
    t.checkExpect(this.wordBox3.select(150, 110, false), this.wordBox3);
    t.checkExpect(this.wordBox3.select(50, 50, true), this.wordBox3);
    t.checkExpect(this.wordBox3.select(150, 110, true), this.selectWordBox3);
    t.checkExpect(this.wordBox4.select(420, 380, false), this.wordBox4);
    t.checkExpect(this.wordBox4.select(50, 50, true), this.wordBox4);
    t.checkExpect(this.wordBox4.select(420, 380, true), this.selectWordBox4);
  }

  // tests AWord selected
  void testSelected(Tester t) {
    this.initData();
    t.checkExpect(this.word1.selected(), true);
    t.checkExpect(this.word2.selected(), false);
    t.checkExpect(this.wordBox1.selected(), true);
    t.checkExpect(this.wordBox2.selected(), true);
    t.checkExpect(this.wordBox3.selected(), false);
    t.checkExpect(this.wordBox4.selected(), false);
  }

  // tests AWord deselect
  void testDeselect(Tester t) {
    this.initData();
    t.checkExpect(this.word1.deselect(), new UnselectedWord("hello"));
    t.checkExpect(this.word2.deselect(), new UnselectedWord("world"));
    t.checkExpect(this.wordBox1.deselect(), new UnselectedWord("a"));
    t.checkExpect(this.wordBox2.deselect(), new UnselectedWord("b"));
    t.checkExpect(this.wordBox3.deselect(), new UnselectedWord("c"));
    t.checkExpect(this.wordBox4.deselect(), new UnselectedWord("d"));
  }

  // TESTS FOR ACATEGORY
  // tests ACategory score
  void testScore(Tester t) {
    this.initData();

    this.yellowEx0.score(0);
    t.checkExpect(this.yellowEx0, this.yellowEx0Same);

    this.blueEx0.score(2);
    t.checkExpect(this.blueEx0, this.blueEx0Same);

    this.yellowEx1Selected.score(1);
    t.checkExpect(this.yellowEx1Selected, this.yellowEx1SelectedSame);

    this.greenEx1Selected.score(0);
    t.checkExpect(this.greenEx1Selected, this.greenEx1SelectedSame);
  }

  // tests ACategory scoreAll
  void testScoreAll(Tester t) {
    this.initData();
    t.checkExpect(this.yellowEx0.scoreAll(0), this.yellowEx0Scored);
    t.checkExpect(this.greenEx0.scoreAll(1), this.greenEx0Scored);
    t.checkExpect(this.blueEx0.scoreAll(2), this.blueEx0Scored);
    t.checkExpect(this.purpleEx0.scoreAll(3), this.purpleEx0Scored);
    t.checkExpect(this.greenEx1.scoreAll(0), this.greenEx1);
    t.checkExpect(this.blueEx1.scoreAll(1), this.blueEx1);
    t.checkExpect(this.purpleEx1.scoreAll(2), this.purpleEx1);
    t.checkExpect(this.yellowEx1.scoreAll(3), this.yellowEx1Scored);
  }

  // tests ACategory draw
  void testCategoryDraw(Tester t) {
    this.initData();
    t.checkExpect(this.yellowEx1.draw(ws, this.pos1), this.yellowDrawFinal);
    t.checkExpect(this.greenEx1.draw(ws, this.pos1), this.ws);
  }

  // tests ACategory selectedWords
  void testSelectedWords(Tester t) {
    this.initData();
    this.yellowBox.selectWords(100, 100, false);
    t.checkExpect(this.yellowBox, this.yellowBoxSame);

    this.initData();
    this.yellowBox.selectWords(100, 100, true);
    t.checkExpect(this.yellowBox, this.yellowBoxSame);

    this.initData();
    this.yellowBox.selectWords(110, 135, true);
    t.checkExpect(this.yellowBox, this.yellowBoxSelectFirst);

    this.initData();
    this.greenEx1.selectWords(100, 100, false);
    t.checkExpect(this.greenEx1, this.greenEx1Same);

    this.initData();
    this.greenEx1.selectWords(100, 100, true);
    t.checkExpect(this.greenEx1, this.greenEx1Same);

    this.initData();
    this.greenEx1.selectWords(110, 135, true);
    t.checkExpect(this.greenEx1, this.greenEx1Same);

  }

  // tests ACategory scored
  void testScored(Tester t) {
    this.initData();
    t.checkExpect(this.yellowEx0.scored(), 0);
    t.checkExpect(this.greenEx0.scored(), 0);
    t.checkExpect(this.blueEx0.scored(), 0);
    t.checkExpect(this.purpleEx0.scored(), 0);
    t.checkExpect(this.greenEx1.scored(), 1);
    t.checkExpect(this.blueEx1.scored(), 1);
    t.checkExpect(this.purpleEx1.scored(), 1);
    t.checkExpect(this.yellowEx1.scored(), 0);
  }

  // tests ACategory deselectAll
  void testCategoryDeselectAll(Tester t) {
    this.initData();
    this.yellowEx0.deselectAll();
    t.checkExpect(this.yellowEx0, this.yellowEx0Same);

    this.greenEx0.deselectAll();
    t.checkExpect(this.greenEx0, this.greenEx0Same);

    this.blueEx0.deselectAll();
    t.checkExpect(this.blueEx0, this.blueEx0Same);

    this.purpleEx0.deselectAll();
    t.checkExpect(this.purpleEx0, this.purpleEx0Same);

    this.yellowEx1Selected.deselectAll();
    t.checkExpect(this.yellowEx1Selected, this.yellowEx1);

    this.greenEx1Selected.deselectAll();
    t.checkExpect(this.greenEx1Selected, this.greenEx1);
  }

  // TESTS FOR GAMEDATA
  // tests GameData deselectAll
  void testGameDataDeselectAll(Tester t) {
    this.initData();
    // Test on a board with no scored and no selected
    this.gameEx0.deselectAll();
    t.checkExpect(this.gameEx0.categories, Arrays.asList(this.yellowEx0Deselected,
        this.greenEx0Deselected, this.blueEx0Deselected, this.purpleEx0Deselected));
    // Test on a board with no selected words and scored categories
    this.gameEx1.deselectAll();
    t.checkExpect(this.gameEx1.categories, Arrays.asList(this.yellowEx1Deselected,
        this.greenEx1Deselected, this.blueEx1Deselected, this.purpleEx1Deselected));
    // Test on a board with at least 1 selected word and scored categories
    this.gameEx3.deselectAll();
    t.checkExpect(this.gameEx1.categories, Arrays.asList(this.yellowEx3Deselected,
        this.greenEx3Deselected, this.blueEx3Deselected, this.purpleEx3Deselected));
  }

  // tests GameData draw
  void testGameDataDraw(Tester t) {
    this.initData();
    // Test on a starting board
    t.checkExpect(this.gameEx0.draw(new WorldScene(700, 400)), this.ex0RandomWS);
    // Test on a board that has some scored categories
    t.checkExpect(this.gameEx1.draw(new WorldScene(700, 400)), this.ex1RandomWS);
    // Test on a board with lives lost
    this.initData();
    this.gameEx1.lives = 2;
    t.checkExpect(this.gameEx1.draw(new WorldScene(700, 400)), this.ex2RandomWS);
    // Test on a board that has some scored categories and selected words
    t.checkExpect(this.gameEx3.draw(new WorldScene(700, 400)), this.ex3RandomWS);
  }

  // tests GameData tries
  void testTries(Tester t) {
    this.initData();
    // Test on a starting board
    t.checkExpect(this.gameEx0.tries(), 1);
    // Test on a board with 1 life lost
    this.gameEx0.lives = 3;
    t.checkExpect(this.gameEx0.tries(), 2);
    // Test on a board with 2 life lost
    this.gameEx0.lives = 2;
    t.checkExpect(this.gameEx0.tries(), 3);
    // Test on a board with 3 life lost
    this.gameEx0.lives = 1;
    t.checkExpect(this.gameEx0.tries(), 4);
    // Test on a board with 4 life lost
    this.gameEx0.lives = 0;
    t.checkExpect(this.gameEx0.tries(), 5);
  }

  // tests GameData drawEnd
  void testDrawEnd(Tester t) {
    this.initData();
    // Test on a starting board
    t.checkExpect(this.gameEx0.drawEnd(new WorldScene(700, 400)), this.ex4RandomWS);
    // Test on a board with scored categories
    t.checkExpect(this.gameEx1.drawEnd(new WorldScene(700, 400)), this.ex5RandomWS);
    // Test on a board with scored categories and selected words
    t.checkExpect(this.gameEx3.drawEnd(new WorldScene(700, 400)), this.ex5RandomWS);
  }

  // tests GameData select
  void testGameDataSelect(Tester t) {
    this.initData();
    // Test missed click
    this.gameEx0.draw(new WorldScene(700, 400));
    this.gameEx0Same.draw(new WorldScene(700, 400));
    this.gameEx0.select(100, 100);
    t.checkExpect(this.gameEx0, this.gameEx0Same);

    this.initData();
    // Test click on unselected word
    this.gameEx0.draw(new WorldScene(700, 400));
    this.gameEx0Selected.draw(new WorldScene(700, 400));
    this.gameEx0.select(410, 205);
    this.gameEx0.draw(new WorldScene(700, 400));
    t.checkExpect(this.gameEx0, this.gameEx0Selected);

    this.initData();
    // Test click on an already selected word
    this.gameEx0Selected.draw(new WorldScene(700, 400));
    this.gameEx0.draw(new WorldScene(700, 400));
    this.gameEx0Selected.select(410, 205);
    this.gameEx0Selected.draw(new WorldScene(700, 400));
    t.checkExpect(this.gameEx0Selected, this.gameEx0);

    this.initData();
    // Test click on an unselected word on a board with 4 selected words
    this.gameEx6.draw(new WorldScene(700, 400));
    this.gameEx6Same.draw(new WorldScene(700, 400));
    this.gameEx6.select(410, 205);
    t.checkExpect(this.gameEx6, this.gameEx6Same);

    this.initData();
    // Test click on an already selected word on a board with 4 selected words
    this.gameEx6.draw(new WorldScene(700, 400));
    this.gameEx6Deselect.draw(new WorldScene(700, 400));
    this.gameEx6.select(565, 70);
    this.gameEx6.draw(new WorldScene(700, 400));
    t.checkExpect(this.gameEx6, this.gameEx6Deselect);

    this.initData();
    // Test click on a scored category
    this.gameEx1.draw(new WorldScene(700, 400));
    this.gameEx1Same.draw(new WorldScene(700, 400));
    this.gameEx1.select(265, 70);
    t.checkExpect(this.gameEx1, this.gameEx1Same);
  }

  // tests GameData score
  void testGameDataScore(Tester t) {
    this.initData();
    // Test life loss
    this.initData();
    this.gameEx0.score();
    this.gameEx0Same.lives = 3;
    t.checkExpect(this.gameEx0, this.gameEx0Same);

    // Test valid score
    this.gameEx6.fixedScore(new Random(1));
    t.checkExpect(this.gameEx6, this.gameEx6Scored);
  }

  // tests GameData shuffle
  void testShuffle(Tester t) {
    this.initData();
    // Tests shuffle
    this.gameEx0.fixedShuffle(new Random(1));
    t.checkExpect(this.gameEx0.positions.pos, new ArrayList<Integer>(
        Arrays.asList(15, 8, 10, 9, 12, 14, 6, 7, 13, 3, 11, 0, 4, 5, 1, 2)));

    this.initData();
    // Tests shuffle with different random
    this.gameEx0.fixedShuffle(new Random(2));
    t.checkExpect(this.gameEx0.positions.pos, new ArrayList<Integer>(
        Arrays.asList(1, 8, 7, 13, 15, 0, 12, 11, 10, 6, 5, 4, 14, 3, 9, 2)));

    // Tests shuffle on scored board
    this.gameEx1.fixedShuffle(new Random(1));
    t.checkExpect(this.gameEx1.positions.pos, new ArrayList<Integer>(
        Arrays.asList(15, 8, 10, 9, 12, 14, 6, 7, 13, 3, 11, 0, 4, 5, 1, 2)));

  }

  // tests GameData fourSelected
  void testFourSelected(Tester t) {
    this.initData();
    // Tests 4 selected in same category
    t.checkExpect(this.gameEx6.fourSelected(), true);
    // Tests 4 selected across multiple categories
    t.checkExpect(this.gameEx7.fourSelected(), true);
    // Tests false condition
    t.checkExpect(this.gameEx0.fourSelected(), false);

  }

  // tests GameData endGame
  void testEndGame(Tester t) {
    this.initData();
    // Tests false condition
    t.checkExpect(this.gameEx0.endGame(), false);
    // Tests true condition
    this.gameEx0.lives = 0;
    t.checkExpect(this.gameEx0.endGame(), true);

  }

  // tests GameData countScored
  void testCountScored(Tester t) {
    this.initData();
    // Tests with 0 scored
    t.checkExpect(this.gameEx0.countScored(), 0);
    // Tests with at least 1 scored
    t.checkExpect(this.gameEx7.countScored(), 1);
    // Tests with all scored
    t.checkExpect(this.gameEx4.countScored(), 4);

  }

  // TESTS FOR CONNECTIONS WORLD
  // tests the ConnectionsWorld makeScene method
  void testMakeScene(Tester t) {
    this.initData();
    // Test on a starting board
    t.checkExpect(this.world0.makeScene(), this.ex0RandomWS);
    // Test on a board that has some scored categories
    t.checkExpect(this.world1.makeScene(), this.ex1RandomWS);
    // Test on a board with lives lost
    this.initData();
    this.gameEx1.lives = 2;
    t.checkExpect(this.world1.makeScene(), this.ex2RandomWS);
    // Test on a board that has some scored categories and selected words
    t.checkExpect(this.world3.makeScene(), this.ex3RandomWS);

  }

  // tests the ConnectionsWorld lastScene method
  void testLastScene(Tester t) {
    this.initData();
    // Test on a loss for a starting board
    this.ex4RandomWS.placeImageXY(new TextImage("You lose!", 25, FontStyle.REGULAR, Color.red), 335,
        340);
    t.checkExpect(this.world0.lastScene(""), ex4RandomWS);

    this.initData();
    // Test on a loss for a board with scored categories
    this.ex5RandomWS.placeImageXY(new TextImage("You lose!", 25, FontStyle.REGULAR, Color.red), 335,
        340);
    t.checkExpect(this.world1.lastScene(""), ex5RandomWS);

    this.initData();
    // Test on a loss for a board with scored categories and selected words
    this.ex5RandomWS.placeImageXY(new TextImage("You lose!", 25, FontStyle.REGULAR, Color.red), 335,
        340);
    t.checkExpect(this.world3.lastScene(""), ex5RandomWS);

    this.initData();
    // Test on a win
    this.world3.win = true;
    this.ex5RandomWS.placeImageXY(
        new TextImage("You won in 1 tries!", 25, FontStyle.REGULAR, Color.green), 335, 340);
    t.checkExpect(this.world3.lastScene(""), ex5RandomWS);

    this.initData();
    // Test on a win with multiple lives lost
    this.world3.win = true;
    this.world3.gameData.lives = 3;
    this.ex5RandomWS.placeImageXY(
        new TextImage("You won in 2 tries!", 25, FontStyle.REGULAR, Color.green), 335, 340);
    t.checkExpect(this.world3.lastScene(""), ex5RandomWS);
  }

  // tests the ConnectionsWorld onMouseClicked method
  void testOnMouseClicked(Tester t) {
    this.initData();
    // Test missed click
    this.gameEx0.draw(new WorldScene(700, 400));
    this.gameEx0Same.draw(new WorldScene(700, 400));
    this.world0.onMouseClicked(new Posn(100, 100));
    t.checkExpect(this.world0.gameData, this.gameEx0Same);

    this.initData();
    // Test click on unselected word
    this.gameEx0.draw(new WorldScene(700, 400));
    this.gameEx0Selected.draw(new WorldScene(700, 400));
    this.world0.onMouseClicked(new Posn(410, 205));
    this.gameEx0.draw(new WorldScene(700, 400));
    t.checkExpect(this.world0.gameData, this.gameEx0Selected);

    this.initData();
    // Test click on an already selected word
    this.gameEx0Selected.draw(new WorldScene(700, 400));
    this.gameEx0.draw(new WorldScene(700, 400));
    this.world0Selected.onMouseClicked(new Posn(410, 205));
    this.gameEx0Selected.draw(new WorldScene(700, 400));
    t.checkExpect(this.world0Selected.gameData, this.gameEx0);

    this.initData();
    // Test click on an unselected word on a board with 4 selected words
    this.gameEx6.draw(new WorldScene(700, 400));
    this.gameEx6Same.draw(new WorldScene(700, 400));
    this.world6.onMouseClicked(new Posn(410, 205));
    t.checkExpect(this.world6.gameData, this.gameEx6Same);

    this.initData();
    // Test click on an already selected word on a board with 4 selected words
    this.gameEx6.draw(new WorldScene(700, 400));
    this.gameEx6Deselect.draw(new WorldScene(700, 400));
    this.world6.onMouseClicked(new Posn(565, 70));
    this.gameEx6.draw(new WorldScene(700, 400));
    t.checkExpect(this.world6.gameData, this.gameEx6Deselect);

    this.initData();
    // Test click on a scored category
    this.gameEx1.draw(new WorldScene(700, 400));
    this.gameEx1Same.draw(new WorldScene(700, 400));
    this.world1.onMouseClicked(new Posn(265, 70));
    t.checkExpect(this.world1.gameData, this.gameEx1Same);
  }

  // tests the ConnectionsWorld onKeyEvent method
  void testOnKeyEvent(Tester t) {
    // Test "r"
    this.initData();
    this.world0.fixedOnKeyEvent("r", db1, new Random(1));
    t.checkExpect(this.world0.gameData, db1.ex6);
    this.initData();
    this.world1.fixedOnKeyEvent("r", db1, new Random(2));
    t.checkExpect(this.world1.gameData, db1.ex9);

    // Test "s"
    this.initData();
    // Test on board starting board (none selected)
    this.world0.onKeyEvent("s");
    t.checkExpect(this.world0.gameData, this.gameEx0Same);

    this.initData();
    // Test on board with not enough selected
    this.world3.onKeyEvent("s");
    t.checkExpect(this.world3.gameData, this.gameEx3Same);

    this.initData();
    // Test on valid score
    this.world6.fixedOnKeyEvent("s", db1, new Random(1));
    t.checkExpect(this.world6.gameData, this.gameEx6Scored);

    this.initData();
    // Test on valid life loss
    this.world7.onKeyEvent("s");
    t.checkExpect(this.world7.gameData, this.gameEx7LifeLoss);

    // Test "n"
    this.initData();
    // Tests shuffle
    this.world0.fixedOnKeyEvent("n", db1, new Random(1));
    t.checkExpect(this.world0.gameData.positions.pos, new ArrayList<Integer>(
        Arrays.asList(15, 8, 10, 9, 12, 14, 6, 7, 13, 3, 11, 0, 4, 5, 1, 2)));

    // Test "d"
    this.initData();
    // Test on a board with no scored and no selected
    this.world0.onKeyEvent("d");
    t.checkExpect(this.world0.gameData.categories, Arrays.asList(this.yellowEx0Deselected,
        this.greenEx0Deselected, this.blueEx0Deselected, this.purpleEx0Deselected));
    // Test on a board with no selected words and scored categories
    this.world1.onKeyEvent("d");
    t.checkExpect(this.world1.gameData.categories, Arrays.asList(this.yellowEx1Deselected,
        this.greenEx1Deselected, this.blueEx1Deselected, this.purpleEx1Deselected));
    // Test on a board with at least 1 selected word and scored categories
    this.world3.onKeyEvent("d");
    t.checkExpect(this.world3.gameData.categories, Arrays.asList(this.yellowEx3Deselected,
        this.greenEx3Deselected, this.blueEx3Deselected, this.purpleEx3Deselected));

  }

  // testing the whole game
  void testBigBang(Tester t) {
    ConnectionsWorld world = new ConnectionsWorld(new Database().randomGame());
    int worldWidth = 700;
    int worldHeight = 400;
    world.bigBang(worldWidth, worldHeight);
  }
}