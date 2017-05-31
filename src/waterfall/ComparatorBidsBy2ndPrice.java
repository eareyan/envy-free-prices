package waterfall;

import java.util.Comparator;

import structures.Goods;

public class ComparatorBidsBy2ndPrice<G extends Goods> implements Comparator<Bids<G>> {

  @Override
  public int compare(Bids<G> o1, Bids<G> o2) {
    if (o1.get2ndHighest() > o2.get2ndHighest()) {
      return 1;
    } else if (o1.get2ndHighest() < o2.get2ndHighest()) {
      return -1;
    } else {
      return 0;
    }
  }
}
