import tester.Tester;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// represents all of the different possible games
class Database {
  // Game 1
  ACategory yellow1 = new UnscoredCategory("MOVIE GENRES", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HORROR"), new UnselectedWord("MUSICAL"),
              new UnselectedWord("ROMANCE"), new UnselectedWord("WESTERN"))));
  ACategory green1 = new UnscoredCategory("PATTERNS", Color.green,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("HOUNDSTOOTH"), new UnselectedWord("PAISLEY"),
              new UnselectedWord("PLAID"), new UnselectedWord("STRIPES"))));
  ACategory blue1 = new UnscoredCategory("SYNONYMS FOR FALSEHOOD", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIB"), new UnselectedWord("FICTION"),
          new UnselectedWord("LIE"), new UnselectedWord("TALE"))));
  ACategory purple1 = new UnscoredCategory("CANDY PIECES", Color.magenta, // no purple
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOT"), new UnselectedWord("GOOBER"),
          new UnselectedWord("KISS"), new UnselectedWord("WHOPPER"))));

  ArrayList<ACategory> categories1 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow1, this.green1, this.blue1, this.purple1));

  GameData ex1 = new GameData(categories1);

  // Game 2
  ACategory yellow2 = new UnscoredCategory("COMPUTER EQUIPMENT", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("KEYBOARD"), new UnselectedWord("MONITOR"),
              new UnselectedWord("MOUSE"), new UnselectedWord("SPEAKER"))));
  ACategory green2 = new UnscoredCategory("RODENTS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("GERBIL"),
          new UnselectedWord("HAMSTER"), new UnselectedWord("RAT"), new UnselectedWord("VOLE"))));
  ACategory blue2 = new UnscoredCategory("MUSICAL INSTRUMENTS", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("HARP"), new UnselectedWord("HORN"),
          new UnselectedWord("ORGAN"), new UnselectedWord("TRIANGLE"))));
  ACategory purple2 = new UnscoredCategory("SYNONYMS FOR COMPLAIN", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("CARP"), new UnselectedWord("GRIPE"),
          new UnselectedWord("GROUSE"), new UnselectedWord("MOAN"))));

  ArrayList<ACategory> categories2 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow2, this.green2, this.blue2, this.purple2));

  GameData ex2 = new GameData(categories2);

  // Game 3
  ACategory yellow3 = new UnscoredCategory("FABRICS", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DENIM"), new UnselectedWord("LINEN"),
          new UnselectedWord("CORDUROY"), new UnselectedWord("TWEED"))));
  ACategory green3 = new UnscoredCategory("APPARITIONS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("GHOST"), new UnselectedWord("PHANTOM"),
          new UnselectedWord("SPIRIT"), new UnselectedWord("SPECTER"))));
  ACategory blue3 = new UnscoredCategory("SYNOMYMS FOR BOTHER", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("NEEDLE"), new UnselectedWord("POKE"),
          new UnselectedWord("RIB"), new UnselectedWord("TEASE"))));
  ACategory purple3 = new UnscoredCategory("DISNEY CHARACTERS", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("BEAST"), new UnselectedWord("GENIE"),
          new UnselectedWord("SCAR"), new UnselectedWord("STITCH"))));

  ArrayList<ACategory> categories3 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow3, this.green3, this.blue3, this.purple3));

  GameData ex3 = new GameData(categories3);

  // Game 4
  ACategory yellow4 = new UnscoredCategory("SCUBA GEAR", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FINS"), new UnselectedWord("MASK"),
          new UnselectedWord("SNORKEL"), new UnselectedWord("TANK"))));
  ACategory green4 = new UnscoredCategory("PUNCTUATION MARKS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("COLON"), new UnselectedWord("COMMA"),
          new UnselectedWord("HYPHEN"), new UnselectedWord("PERIOD"))));
  ACategory blue4 = new UnscoredCategory("RUN QUICKLY", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("BOLT"), new UnselectedWord("DASH"),
          new UnselectedWord("RACE"), new UnselectedWord("SPRINT"))));
  ACategory purple4 = new UnscoredCategory("___ CAT", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DOJA"), new UnselectedWord("FAT"),
          new UnselectedWord("HOUSE"), new UnselectedWord("JUNGLE"))));

  ArrayList<ACategory> categories4 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow4, this.green4, this.blue4, this.purple4));

  GameData ex4 = new GameData(categories4);

  // Game 5
  ACategory yellow5 = new UnscoredCategory("BOTTLED WATER BRANDS", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DASANI"), new UnselectedWord("EVIAN"),
          new UnselectedWord("FIJI"), new UnselectedWord("VOSS"))));
  ACategory green5 = new UnscoredCategory("SLANG FOR COFFEE", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("BREW"), new UnselectedWord("JAVA"),
          new UnselectedWord("JOE"), new UnselectedWord("MUD"))));
  ACategory blue5 = new UnscoredCategory("ISLAND COUNTRIES", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("CUBA"), new UnselectedWord("JAPAN"),
          new UnselectedWord("MALTA"), new UnselectedWord("PALAU"))));
  ACategory purple5 = new UnscoredCategory("CEREAL MASCOTS", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("LUCKY"), new UnselectedWord("POP"),
          new UnselectedWord("SAM"), new UnselectedWord("TONY"))));

  ArrayList<ACategory> categories5 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow5, this.green5, this.blue5, this.purple5));

  GameData ex5 = new GameData(categories5);

  // Game 6
  ACategory yellow6 = new UnscoredCategory("AMAZON ANIMALS", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("ANACONDA"), new UnselectedWord("JAGUAR"),
              new UnselectedWord("CAPYBARA"), new UnselectedWord("TOUCAN"))));
  ACategory green6 = new UnscoredCategory("LOWEST POINT", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("BASE"), new UnselectedWord("BOTTOM"),
          new UnselectedWord("FOOT"), new UnselectedWord("FOUNDATION"))));
  ACategory blue6 = new UnscoredCategory("MUSICALS", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("COMPANY"), new UnselectedWord("RENT"),
          new UnselectedWord("HAIR"), new UnselectedWord("GREASE"))));
  ACategory purple6 = new UnscoredCategory("SEA ___", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("LEGS"), new UnselectedWord("LION"),
          new UnselectedWord("CHANGE"), new UnselectedWord("CUCUMBER"))));

  ArrayList<ACategory> categories6 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow6, this.green6, this.blue6, this.purple6));

  GameData ex6 = new GameData(categories6);

  // Game 7
  ACategory yellow7 = new UnscoredCategory("PAINTING ACCESSORIES", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("BRUSH"), new UnselectedWord("CANVAS"),
          new UnselectedWord("EASEL"), new UnselectedWord("PALETTE"))));
  ACategory green7 = new UnscoredCategory("AUTOMATIC TRANSMISSION SETTINGS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("DRIVE"), new UnselectedWord("PARK"),
          new UnselectedWord("NEUTRAL"), new UnselectedWord("REVERSE"))));
  ACategory blue7 = new UnscoredCategory("THINGS WITH TEETH", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("COMB"), new UnselectedWord("GEAR"),
          new UnselectedWord("SAW"), new UnselectedWord("ZIPPER"))));
  ACategory purple7 = new UnscoredCategory("___ CHAIR", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FIRST"), new UnselectedWord("HIGH"),
          new UnselectedWord("FOLDING"), new UnselectedWord("LAWN"))));

  ArrayList<ACategory> categories7 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow7, this.green7, this.blue7, this.purple7));

  GameData ex7 = new GameData(categories7);

  // Game 8
  ACategory yellow8 = new UnscoredCategory("DRINK VESSELS", Color.yellow,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("GOBLET"), new UnselectedWord("SNIFTER"),
              new UnselectedWord("TUMBLER"), new UnselectedWord("STEIN"))));
  ACategory green8 = new UnscoredCategory("WOODWINDS", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FLUTE"), new UnselectedWord("OBOE"),
          new UnselectedWord("CLARINET"), new UnselectedWord("SAXOPHONE"))));
  ACategory blue8 = new UnscoredCategory("AMERICAN POETS", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("BISHOP"), new UnselectedWord("FROST"),
          new UnselectedWord("OLDS"), new UnselectedWord("POUND"))));
  ACategory purple8 = new UnscoredCategory("CONSECUTIVE DOUBLE LETTERS", Color.magenta,
      new ArrayList<AWord>(
          Arrays.asList(new UnselectedWord("BALLOON"), new UnselectedWord("BASSOON"),
              new UnselectedWord("COFFEE"), new UnselectedWord("FRICASSEE"))));

  ArrayList<ACategory> categories8 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow8, this.green8, this.blue8, this.purple8));

  GameData ex8 = new GameData(categories8);

  // Game 9
  ACategory yellow9 = new UnscoredCategory("APPETIZER UNIT", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FRY"), new UnselectedWord("NACHO"),
          new UnselectedWord("POPPER"), new UnselectedWord("WING"))));
  ACategory green9 = new UnscoredCategory("RESPONSE TO A CORRECT ANSWER", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("YES"), new UnselectedWord("BINGO"),
          new UnselectedWord("CORRECT"), new UnselectedWord("RIGHT"))));
  ACategory blue9 = new UnscoredCategory("MAR", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("SCRATCH"), new UnselectedWord("NICK"),
          new UnselectedWord("DING"), new UnselectedWord("CHIP"))));
  ACategory purple9 = new UnscoredCategory("___JACK", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("APPLE"), new UnselectedWord("CRACKER"),
          new UnselectedWord("FLAP"), new UnselectedWord("LUMBER"))));

  ArrayList<ACategory> categories9 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow9, this.green9, this.blue9, this.purple9));

  GameData ex9 = new GameData(categories9);

  // Game 10
  ACategory yellow10 = new UnscoredCategory("DEPART QUICKLY", Color.yellow,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("BOOK"), new UnselectedWord("BOUNCE"),
          new UnselectedWord("RUN"), new UnselectedWord("SPLIT"))));
  ACategory green10 = new UnscoredCategory("ANIMALS THAT END WITH X", Color.green,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("FOX"), new UnselectedWord("LYNX"),
          new UnselectedWord("IBEX"), new UnselectedWord("ORYX"))));
  ACategory blue10 = new UnscoredCategory("SHADES OF BLACK", Color.cyan,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("EBONY"), new UnselectedWord("JET"),
          new UnselectedWord("RAVEN"), new UnselectedWord("ONYX"))));
  ACategory purple10 = new UnscoredCategory("WORDS BEFORE DAYS OF THE WEEK", Color.magenta,
      new ArrayList<AWord>(Arrays.asList(new UnselectedWord("ASH"), new UnselectedWord("BLACK"),
          new UnselectedWord("CYBER"), new UnselectedWord("FAT"))));

  ArrayList<ACategory> categories10 = new ArrayList<ACategory>(
      Arrays.asList(this.yellow10, this.green10, this.blue10, this.purple10));

  GameData ex10 = new GameData(categories10);

  ArrayList<GameData> games = new ArrayList<GameData>(Arrays.asList(this.ex1, this.ex2, this.ex3,
      this.ex4, this.ex5, this.ex6, this.ex7, this.ex8, this.ex9, this.ex10));

  // chooses a random game from the database to be played
  GameData randomGame() {
    return this.games.get(new Random().nextInt(this.games.size()));
  }

  // chooses a random game from the database to be played (used for testing)
  GameData fixedRandomGame(Random rand) {
    return this.games.get(rand.nextInt(this.games.size()));
  }
}

class DatabaseExamples {
  Database db1 = new Database();

  // tests the Database randomGame method
  void testRandomGame(Tester t) {
    t.checkExpect(this.db1.fixedRandomGame(new Random(1)), this.db1.ex6);
    t.checkExpect(this.db1.fixedRandomGame(new Random(2)), this.db1.ex9);
  }
}