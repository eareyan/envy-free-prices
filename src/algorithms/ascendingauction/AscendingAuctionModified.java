package algorithms.ascendingauction;

import structures.Campaign;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.User;

public class AscendingAuctionModified {
	
	protected Market market;
	
	public AscendingAuctionModified(Market market){
		this.market = market;
	}

	public MarketPrices Solve(){
		/* Given a market, construct a new market where each user class i is
		 * divided into N_i new user classes. Run Ascending Auction in this new market
		 * and then convert the price of the different user classes to a single price
		 * via a min operator.
		 * */
		Campaign[] campaigns = this.market.getCampaigns(); 	// Use same campaigns
		int totalUserSupply = this.market.getTotalSupply(); // Compute total supply
		User[] newUsers = new User[totalUserSupply];		//Have as many new users as total supply
		boolean[][] newConnections = new boolean[totalUserSupply][this.market.getNumberCampaigns()];
		int k=0;
		for(int i=0;i<this.market.getNumberUsers();i++){ //For each original user
			//System.out.println("Create "+ this.market.getUser(i).getSupply() + " many users from user " + i);
			for(int iPrime=0;iPrime<this.market.getUser(i).getSupply();iPrime++){
				newUsers[k] = new User(1); //Create N_i copies for user i
				for(int j=0;j<this.market.getNumberCampaigns();j++){ //Compute the new connections
					newConnections[k][j] = this.market.isConnected(i, j);
				}
				k++;
			}
		}
		/*
		 * Given the new market, let us compute the final matching and prices.
		 */
		Market newMarket = new Market(newUsers,campaigns,newConnections);
		AscendingAuction A = new AscendingAuction(newMarket);
		MarketPrices auctionSolOnNewMarket = A.Solve();
		int[][] matchinOnNewMarket = auctionSolOnNewMarket.getMarketAllocation().getAllocation();
		double[] pricesOnNewMarket = auctionSolOnNewMarket.getPriceVector();
		/*Printer.printMatrix(matchinOnNewMarket);
		Printer.printVector(auctionSolOnNewMarket.getPriceVector());
		System.out.println(newMarket);*/
		/* Deduce matching */
		int[][] finalMatching = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			k=0;
			for(int i=0;i<this.market.getNumberUsers();i++){
				int totalAllocFromUser_i = 0;
				for(int iPrime=0;iPrime<this.market.getUser(i).getSupply();iPrime++){
					totalAllocFromUser_i += matchinOnNewMarket[k][j];
					k++;
				}
				finalMatching[i][j] = totalAllocFromUser_i;
			}
		}
		/*System.out.println("Deduced matching: ");
		Printer.printMatrix(finalMatching);*/
		/* Deduced pricing */
		k = 0;
		double[] finalprices = new double[this.market.getNumberUsers()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			double min = Double.POSITIVE_INFINITY;
			double priceUser_i = -1.0;
			for(int iPrime=0;iPrime<this.market.getUser(i).getSupply();iPrime++){
				if(pricesOnNewMarket[k] < min){
					priceUser_i = pricesOnNewMarket[k];
				}
				k++;
			}
			finalprices[i] = priceUser_i;
		}
		return new MarketPrices(new MarketAllocation(this.market,finalMatching),finalprices);
	}
}
