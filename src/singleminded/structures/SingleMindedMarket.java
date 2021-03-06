package singleminded.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import structures.Bidder;
import structures.Goods;
import structures.Market;
import structures.exceptions.MarketCreationException;

/**
 * A specialized type of market for single-minded valuations. Provides a representation of the market (A,r), where A is a boolean matrix denoting the demand set
 * of each bidder and r is the rewards (budget) vector.
 * 
 * @author Enrique Areyan Viqueira
 *
 * @param <G>
 * @param <B>
 */
public class SingleMindedMarket<G extends Goods, B extends Bidder<G>> extends Market<G, B> {

  /**
   * A is the boolean matrix encoding the single-minded preferences over items.
   */
  private final boolean[][] A;

  /**
   * r is the reward vector of bidders.
   */
  private final double[] r;

  /**
   * Map between a good and an index.
   */
  private final HashMap<G, Integer> goodsToIndex;

  /**
   * Map between a bidder and an index.
   */
  private final HashMap<B, Integer> biddersToIndex;

  /**
   * Set of dummy goods
   */
  private Set<G> dummyGoods;

  /**
   * Constructor. Takes in a list of goods, a list of bidders, creates the structures for the single-minded market.
   * 
   * @param goods
   * @param bidders
   * @throws MarketCreationException
   */
  public SingleMindedMarket(ArrayList<G> goods, ArrayList<B> bidders) throws MarketCreationException {
    super(goods, bidders);
    // First, check if the given goods are in unit supply. O/W the input market is not a valid single-minded market.
    for (G good : goods) {
      if (good.getSupply() != 1) {
        throw new MarketCreationException("In a single-minded market, all goods must be in unit supply.");
      }
    }
    // Second, check that the reported demand size is actually equal to the number of items this bidder is connected to.
    for (B bidder : bidders) {
      if (bidder.getDemandSet().size() != bidder.getDemand()) {
        throw new MarketCreationException("The bidder: " + bidder + " reports a demand set size of " + bidder.getDemand() + " but actually demands "
            + bidder.getDemandSet().size());
      }
    }
    // Create maps that point from bidders to indices, and from goods to indices.
    this.goodsToIndex = new HashMap<G, Integer>();
    for (int i = 0; i < goods.size(); i++) {
      this.goodsToIndex.put(goods.get(i), i);
    }
    this.biddersToIndex = new HashMap<B, Integer>();
    for (int j = 0; j < bidders.size(); j++) {
      this.biddersToIndex.put(bidders.get(j), j);
    }
    // Produce a matrix to encode the single-minded preferences.
    this.A = new boolean[goods.size()][bidders.size()];
    this.r = new double[bidders.size()];
    for (B bidder : bidders) {
      this.r[this.biddersToIndex.get(bidder)] = bidder.getReward();
      for (G good : goods) {
        this.A[this.goodsToIndex.get(good)][this.biddersToIndex.get(bidder)] = bidder.demandsGood(good);
      }
    }
  }

  /**
   * Setter for dummy goods. Can only be set at most once.
   * 
   * @param dummyGoods
   * @throws MarketCreationException
   */
  public void setDummyGoods(Set<G> dummyGoods) throws MarketCreationException {
    if (this.dummyGoods != null) {
      throw new MarketCreationException("Dummy goods can be set at most once");
    }
    this.dummyGoods = dummyGoods;
  }

  /**
   * Checks if a given good is a dummy good.
   * 
   * @param good
   * @return
   */
  public boolean isDummyGood(G good) {
    return this.dummyGoods != null && this.dummyGoods.contains(good);
  }

  /**
   * Returns a copy of the matrix of demand of this market.
   * 
   * @return a good x bidder matrix of booleans denoting the connections matrix.
   */
  public boolean[][] getCopyOfA() {
    boolean[][] copyOfA = new boolean[this.A.length][this.A[0].length];
    for (int i = 0; i < this.A.length; i++) {
      copyOfA[i] = this.A[i].clone();
    }
    return copyOfA;
  }

  /**
   * Given a bidder, returns its index.
   * 
   * @param bidder
   * @return the bidder index.
   */
  public int getBidderIndex(B bidder) {
    return this.biddersToIndex.get(bidder);
  }

  /**
   * Given a good, returns its index.
   * 
   * @param good
   * @return the good index.
   */
  public int getGoodIndex(G good) {
    return this.goodsToIndex.get(good);
  }

  /**
   * Produces a flat string representation of the bidders' preferences over items, i.e., of the edges in the bipartite graph defining the market.
   * 
   * @return the edges string representation
   */
  public String getEdgesStringRepresentation() {
    String edgesRepresentation = "";
    for (int i = 0; i < this.A.length; i++) {
      for (int j = 0; j < this.A[i].length; j++) {
        edgesRepresentation += (this.A[i][j]) ? "1" : "0";
      }
    }
    return edgesRepresentation;
  }

  /**
   * Produces a flat string representation of the bidders' rewards as a list of comma separated values.
   * 
   * @return the bidders' rewards string representation
   */
  public String getRewardStringRepresentation() {
    String rewardsRepresentation = "";
    for (B b : this.bidders) {
      rewardsRepresentation += b.getReward() + ",";
    }
    return rewardsRepresentation.substring(0, rewardsRepresentation.length() - 1);
  }

  /**
   * Returns the vector of rewards of this market. The length of the vector is equal to the number of bidders.
   * 
   * @return the vector of rewards of this market.
   */
  public double[] getR() {
    return this.r;
  }

  /**
   * Printer. Representation of objects as strings.
   * 
   * @return a string representation of the information of goods in the market
   */
  @Override
  protected String stringGoodsInfo() {
    String ret = "Goods Supply";
    for (G good : this.goods) {
      ret += "\nN = " + good.getSupply() + ((this.dummyGoods != null && this.dummyGoods.contains(good)) ? "\t Dummy" : "");
    }
    return ret;
  }
}
