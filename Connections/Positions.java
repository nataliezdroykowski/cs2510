import tester.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// Represents a set of remaining positions on the connections board
class Positions {
  ArrayList<Integer> pos = new ArrayList<Integer>();
  int currRowIndex = 0;
  int colOffset = 0;
  final int ROW_LENGTH = 4;
  final int COL_LENGTH = 4;

  // Constructor that creates a list based on the amount of rows remaining
  Positions(int colOffset) {
    this.colOffset = colOffset;

    for (int i = colOffset * ROW_LENGTH; i < ROW_LENGTH * COL_LENGTH; i++) {
      this.pos.add(i);
    }
  }

  // Gets the next row of indices
  ArrayList<Integer> getNext() {
    ArrayList<Integer> subPos = new ArrayList<Integer>();
    for (int i = 0; i < ROW_LENGTH; i++) {
      subPos.add(this.pos.get(COL_LENGTH * currRowIndex + i));
    }

    currRowIndex++;
    if (currRowIndex >= COL_LENGTH - colOffset) {
      currRowIndex = 0;
    }

    return subPos;
  }

  // Shuffles the positions
  void shuffle() {
    for (int i = pos.size() - 1; i > 0; i--) {
      int j = new Random().nextInt(i + 1);
      int temp = pos.get(i);
      pos.set(i, pos.get(j));
      pos.set(j, temp);
    }
  }

  // Shuffles the positions (used for testing)
  void fixedShuffle(Random rand) {
    for (int i = pos.size() - 1; i > 0; i--) {
      int j = rand.nextInt(i + 1);
      int temp = pos.get(i);
      pos.set(i, pos.get(j));
      pos.set(j, temp);
    }
  }
}

class PositionsExamples {
  Positions pos0 = new Positions(0);
  Positions pos1 = new Positions(1);
  Positions pos2 = new Positions(2);
  Positions pos3 = new Positions(3);
  Positions pos4 = new Positions(4);

  Random rand1 = new Random(1);
  Random rand2 = new Random(2);

  void initData() {
    this.pos0 = new Positions(0);
    this.pos1 = new Positions(1);
    this.pos2 = new Positions(2);
    this.pos3 = new Positions(3);
    this.pos4 = new Positions(4);

    this.rand1 = new Random(1);
    this.rand2 = new Random(2);
  }

  // tests the Positions constructor
  void testPositions(Tester t) {
    this.initData();
    // Test with no offset
    t.checkExpect(this.pos0.pos, new ArrayList<Integer>(
        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
    // Test with 1 offset
    t.checkExpect(this.pos1.pos,
        new ArrayList<Integer>(Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
    // Test with 2 offset
    t.checkExpect(this.pos2.pos,
        new ArrayList<Integer>(Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15)));
    // Test with 3 offset
    t.checkExpect(this.pos3.pos, new ArrayList<Integer>(Arrays.asList(12, 13, 14, 15)));
    // Test with 4 offset
    t.checkExpect(this.pos4.pos, new ArrayList<Integer>());
  }

  // tests the Positions getNext method
  void testGetNext(Tester t) {
    this.initData();

    // Initial Check
    t.checkExpect(this.pos0.pos, new ArrayList<Integer>(
        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
    // Test with no offset
    t.checkExpect(this.pos0.getNext(), new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3)));
    t.checkExpect(this.pos0.getNext(), new ArrayList<Integer>(Arrays.asList(4, 5, 6, 7)));
    t.checkExpect(this.pos0.getNext(), new ArrayList<Integer>(Arrays.asList(8, 9, 10, 11)));
    t.checkExpect(this.pos0.getNext(), new ArrayList<Integer>(Arrays.asList(12, 13, 14, 15)));
    t.checkExpect(this.pos0.getNext(), new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3)));

    // Initial Check
    t.checkExpect(this.pos1.pos,
        new ArrayList<Integer>(Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
    // Test with offset
    t.checkExpect(this.pos1.getNext(), new ArrayList<Integer>(Arrays.asList(4, 5, 6, 7)));
    t.checkExpect(this.pos1.getNext(), new ArrayList<Integer>(Arrays.asList(8, 9, 10, 11)));
    t.checkExpect(this.pos1.getNext(), new ArrayList<Integer>(Arrays.asList(12, 13, 14, 15)));
    t.checkExpect(this.pos1.getNext(), new ArrayList<Integer>(Arrays.asList(4, 5, 6, 7)));
  }

  // tests the Positions shuffle method
  void testShuffle(Tester t) {
    this.initData();
    // Initial Check
    t.checkExpect(this.pos0.pos, new ArrayList<Integer>(
        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
    // Test with no offset
    this.pos0.fixedShuffle(this.rand1);
    t.checkExpect(this.pos0.pos, new ArrayList<Integer>(
        Arrays.asList(10, 5, 0, 12, 3, 8, 6, 7, 14, 4, 15, 2, 9, 1, 13, 11)));

    // Initial Check
    t.checkExpect(this.pos1.pos,
        new ArrayList<Integer>(Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
    // Test with offset
    this.pos1.fixedShuffle(this.rand1);
    t.checkExpect(this.pos1.pos,
        new ArrayList<Integer>(Arrays.asList(5, 11, 9, 8, 15, 12, 13, 7, 10, 6, 4, 14)));

    this.initData();
    // Initial Check
    t.checkExpect(this.pos0.pos, new ArrayList<Integer>(
        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
    // Test with no offset on different random
    this.pos0.fixedShuffle(this.rand2);
    t.checkExpect(this.pos0.pos, new ArrayList<Integer>(
        Arrays.asList(13, 5, 7, 14, 10, 2, 3, 15, 0, 6, 1, 9, 8, 4, 12, 11)));

    // Initial Check
    t.checkExpect(this.pos1.pos,
        new ArrayList<Integer>(Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
    // Test with offset on different random
    this.pos1.fixedShuffle(this.rand2);
    t.checkExpect(this.pos1.pos,
        new ArrayList<Integer>(Arrays.asList(15, 14, 5, 9, 11, 7, 4, 8, 6, 13, 10, 12)));
  }

}