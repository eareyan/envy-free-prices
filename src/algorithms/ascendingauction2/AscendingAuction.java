package algorithms.ascendingauction2;

import java.util.ArrayList;
import java.util.Collections;

import structures.Market;
import util.Printer;

public class AscendingAuction {
	
	protected Market market;
	
	protected double[] prices;
	
	protected int[][] allocation;
	
	protected static double epsilon = 0.0001;
	
	public AscendingAuction(Market market){
		this.market = market;
		this.prices = new double[this.market.getNumberUsers()];
		this.allocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
	}
	
	public void Solve(){
		ArrayList<UserPrice> currentPrices = new ArrayList<UserPrice>(this.market.getNumberUsers());
		/* Initial structures */
		for(int i=0;i<this.market.getNumberUsers();i++){
			currentPrices.add(new UserPrice(i,0.0));
		}
		ArrayList<Integer> unallocatedCampaigns = new ArrayList<Integer>();
		ArrayList<ArrayList<BundleEntry>> listOfUsers = new ArrayList<ArrayList<BundleEntry>>(this.market.getNumberCampaigns()); 
		for(int j=0;j<this.market.getNumberCampaigns();j++){ /* Initially all campaigns are unallocated*/
			unallocatedCampaigns.add(j);
			/* Populate the list of users that campaigns have access to*/
			ArrayList<BundleEntry> listOfUsersForCampaign = new ArrayList<BundleEntry>();
			for(int i=0;i<this.market.getNumberUsers();i++){
				if(this.market.isConnected(i, j)){
					listOfUsersForCampaign.add(new BundleEntry(i,0));
				}
			}
			listOfUsers.add(listOfUsersForCampaign);
		}
		System.out.println(currentPrices);
		System.out.println(listOfUsers);
		while(true){
			ArrayList<Bundle> B = new ArrayList<Bundle>();
			for(Integer j:unallocatedCampaigns){
				//System.out.println("Campaign "+j+" is unallocated, try to find a match");
				ArrayList<BundleEntry> bundle = this.findUtilityMaximizerBundle(currentPrices, j);
				//System.out.println("greedyBundle = " + bundle);
				if(!bundle.isEmpty()){
					B.add(new Bundle(j,bundle));
				}
			}
			//System.out.println("SET B = " + B);
			if(B.size()>0){ //If some unallocated campaign placed a bid.
				Bundle b = B.get(0);
				int campaignIndex = b.getJ();
				ArrayList<BundleEntry> bundle = b.getBundle();
				//System.out.println("Alloc campaign " + campaignIndex + " with bundle " + bundle);
				unallocatedCampaigns.remove(new Integer(campaignIndex));
				for(BundleEntry entry:bundle){ //For each entry in the bundle of the bid
					int userId = entry.getI() , campaignId = b.getJ(),  allocToUser = 0;
					//System.out.println("\t\tx[i][j] = x["+userId + "][" +campaignId + "] = " + entry.getX());
					this.allocation[userId][campaignId] = entry.getX(); //Allocate this many
					for(int l=0;l<this.market.getNumberCampaigns();l++){
						allocToUser += this.allocation[userId][l];
					}
					if(allocToUser > this.market.getUser(userId).getSupply()){ //Check if we haven't exceed the total supply of this user
						//We need to unallocate campaigns now since we have exceed supply!
						//System.out.println("We need to unallocate campaigns from user " + userId+", current alloc = " + allocToUser + ", max = " + this.market.getUser(userId).getSupply());
						this.updatePrices(currentPrices, userId);
						for(int l=0;l<this.market.getNumberCampaigns();l++){
							if(l!=campaignId){
								allocToUser -= this.allocation[userId][l];
								for(int i=0;i<this.market.getNumberUsers();i++){
									this.allocation[i][l] = 0;
								}
								unallocatedCampaigns.add(new Integer(l));
								if(allocToUser <= this.market.getUser(userId).getSupply()){
									break;
								}
							}
						}
					}
				}
			}else{
				break;
			}
		}
		System.out.println("Final Allocation:");
		Printer.printMatrix(this.allocation);

		System.out.println("Final Prices:");
		this.storeFinalPrices(currentPrices);
		Printer.printVector(this.prices);
		for(int i=0;i<this.market.getNumberUsers();i++){
			System.out.println("P["+i+"] = "+this.prices[i]);
		}
			
	}
	/*
	 * Stores final price vecto
	 */
	public void storeFinalPrices(ArrayList<UserPrice> prices){
		for(UserPrice u: prices){
			this.prices[u.getI()] = u.getPrice();
		}
	}
	/*
	 * Given the list of prices and a user i, increment P_i by epsilon
	 */
	public void updatePrices(ArrayList<UserPrice> prices, int i){
		for(int k=0;k<this.market.getNumberUsers();k++){
			if(prices.get(k).getI() == i){
				prices.get(k).updatePrice(prices.get(k).getPrice() + AscendingAuction.epsilon);
			}
		}
	}
	/*
	 * Receives the current prices and a campaign index and returns the utility maximizer bundle for that campaign
	 */
	public ArrayList<BundleEntry> findUtilityMaximizerBundle(ArrayList<UserPrice> currentPrices, int j){
		Collections.sort(currentPrices,new UserPriceComparatorByPrice());
		double priceOfBundle = 0.0, priceOfUser = 0.0;
		int sizeOfBundle = 0 , x = 0 , i = -1;
		ArrayList<BundleEntry> bundle = new ArrayList<BundleEntry>();
		for(UserPrice u:currentPrices){
			i = u.getI();
			priceOfUser = u.getPrice();
			if(this.market.isConnected(i, j)){
				x = this.market.getCampaign(j).getDemand() - sizeOfBundle;
				if(x < this.market.getUser(i).getSupply()){
					priceOfBundle += x * (priceOfUser + AscendingAuction.epsilon);
					sizeOfBundle  += x;
					bundle.add(new BundleEntry(i,x));
				}else{
					priceOfBundle += this.market.getUser(i).getSupply() * (priceOfUser + AscendingAuction.epsilon);
					sizeOfBundle  += this.market.getUser(i).getSupply();
					bundle.add(new BundleEntry(i,this.market.getUser(i).getSupply()));
				}
				if(sizeOfBundle == this.market.getCampaign(j).getDemand() && priceOfBundle<= this.market.getCampaign(j).getReward()){
					return bundle;
				}
			}
		}
		return new ArrayList<BundleEntry>();
	}

}
