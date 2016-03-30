package algorithms.ascendingauction;

import java.util.ArrayList;
import java.util.Collections;

import structures.Market;
import unitdemand.ascendingauction.Bid;
import util.Printer;

/*
 * Implements an ascending auction in a market object.
 * Outputs allocation and prices.
 * 
 * @author Enrique Areyan Viqueira
 */
public class AscendingAuction {
	protected Market market;
	
	protected static double epsilon = 0.5;
	
	double[][] prices;
	int[][] allocation;
	
	public AscendingAuction(Market market){
		this.market = market;
		this.prices = new double[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		this.allocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
	}
	
	public void Solve(){
		
		System.out.println("Initial Allocation:");
		Printer.printMatrix(this.allocation);

		System.out.println("Initial Prices:");
		Printer.printMatrix(this.prices);
		
		/* Initial structures */
		ArrayList<Integer> unallocatedCampaigns = new ArrayList<Integer>();
		ArrayList<ArrayList<UserPriceAlloc>> listOfUsers = new ArrayList<ArrayList<UserPriceAlloc>>(this.market.getNumberCampaigns()); 
		for(int j=0;j<this.market.getNumberCampaigns();j++){ /* Initially all campaigns are unallocated*/
			unallocatedCampaigns.add(j);
			/* Populate the list of users that campaigns have access to*/
			ArrayList<UserPriceAlloc> listOfUsersForCampaign = new ArrayList<UserPriceAlloc>();
			for(int i=0;i<this.market.getNumberUsers();i++){
				if(this.market.isConnected(i, j)){
					listOfUsersForCampaign.add(new UserPriceAlloc(i,0.0,0));
				}
			}
			listOfUsers.add(listOfUsersForCampaign);
		}
		
		/* Main Loop*/
		while(true){
			ArrayList<Bundle> setOfBids = new ArrayList<Bundle>();
			System.out.println(listOfUsers);
			System.out.println("unallocatedCampaigns = " + unallocatedCampaigns);
			for(Integer j: unallocatedCampaigns){
				ArrayList<UserPriceAlloc> bundle = this.findGreedyMatch(listOfUsers.get(j),j);
				System.out.println("Greedy match for campaign " + j + " => " + bundle);
				if(bundle.size()>0){
					setOfBids.add(new Bundle(j,bundle));
				}
			}
			System.out.println("All bids: " + setOfBids);
			
			if(setOfBids.size()>0){
				Bundle b = setOfBids.get(0);
				System.out.println("Current bid = " + b);
				int campaignIndex = b.getCampaignIndex();
				ArrayList<UserPriceAlloc> currentAlloc = listOfUsers.get(campaignIndex);
				System.out.println("currentAlloc = " + currentAlloc);
				System.out.println("Allocate the following bundle: " + b.getBundle());
				unallocatedCampaigns.remove(new Integer(campaignIndex));
				for(UserPriceAlloc u:b.getBundle()){
					System.out.println("User = " + u.getI());
					updateAllocAndPrices(currentAlloc,u.getI(),u.getAlloc());
					if(userOverSupplied(listOfUsers,u.getI())){
						System.out.println("User is OVERSUPPLIED!");
						/* Unallocate enough campaigns to have this user not beign oversupplied*/
						unallocateCampaigns(listOfUsers, unallocatedCampaigns, u.getI(),campaignIndex);
					}
				}
				
			}else{ //No more bids, halt with current allocation and prices
				break;
			}
			System.out.println("unallocatedCampaigns = " + unallocatedCampaigns);
		}
			
			System.out.println(listOfUsers);
			convertBidsIntoMatrices(listOfUsers);
			System.out.println("Final Allocation:");
			Printer.printMatrix(this.allocation);

			System.out.println("Final Prices:");
			Printer.printMatrix(this.prices);
	}
	
	public void updateAllocAndPrices(ArrayList<UserPriceAlloc> currentAlloc, int i, int alloc){
		System.out.println("Current alloc update user " + i + " to have " + alloc);
		for(UserPriceAlloc x: currentAlloc){
			if(x.getI() == i){
				x.updateAlloc(alloc);
				x.updatePrice(x.getPrice() + AscendingAuction.epsilon);
			}
		}
	}
	
	public void unallocateCampaigns(ArrayList<ArrayList<UserPriceAlloc>> listOfUsers,ArrayList<Integer> unallocatedCampaigns, int i,int j){
		int currentCampaignIndex = 0;
		main_loop:
		for(ArrayList<UserPriceAlloc> allocFromCampaign : listOfUsers){
			if(currentCampaignIndex!= j){
				for(UserPriceAlloc y: allocFromCampaign){
					if(y.getI() == i){
						completelyUnallocateCampaign(allocFromCampaign);
						unallocatedCampaigns.add(currentCampaignIndex);
						if(!userOverSupplied(listOfUsers,i)){
							break main_loop;
						}
					}
				}
			}
			currentCampaignIndex++;
		}
	}
	
	public void completelyUnallocateCampaign(ArrayList<UserPriceAlloc> allocFromCampaign){
		for(UserPriceAlloc y: allocFromCampaign){
			y.updateAlloc(0);
			y.updatePrice(y.getPrice() + AscendingAuction.epsilon);
		}
	}
	
	public boolean userOverSupplied(ArrayList<ArrayList<UserPriceAlloc>> listOfUsers, int i){
		System.out.println(">>Check if user " + i + " is over supplied");
		int totalSupply = 0;
		for(ArrayList<UserPriceAlloc> allocFromCampaign : listOfUsers){
			for(UserPriceAlloc y: allocFromCampaign){
				if(y.getI() == i){
					totalSupply += y.getAlloc();
				}
			}
		}
		System.out.println(">>>> totalSupply for user: " + i + " is = " + totalSupply);
		if(totalSupply > this.market.getUser(i).getSupply()){
			return true;
		}else{
			return false;
		}
	}
	
	public ArrayList<UserPriceAlloc> findGreedyMatch(ArrayList<UserPriceAlloc> listOfUsers,int j){
		//System.out.println("findGreedyMatch for campaign " + j);
		Collections.sort(listOfUsers,new UserPriceAllocComparatorByPrice());
		double priceOfBundle = 0.0;
		int sizeOfBundle = 0 , x = 0;
		ArrayList<UserPriceAlloc> bundle = new ArrayList<UserPriceAlloc>();
		for(UserPriceAlloc u:listOfUsers){
			//System.out.println(u);
			x = this.market.getCampaign(j).getDemand() - sizeOfBundle;
			if(x < this.market.getUser(u.getI()).getSupply()){
				//System.out.println("Take: " + x + " from user " + u.getI());
				priceOfBundle += x * (u.getPrice() + AscendingAuction.epsilon);
				sizeOfBundle  += x;
				bundle.add(new UserPriceAlloc(u.getI(),-1.0,x));
			}else{
				//System.out.println("Take: " + this.market.getUser(u.getI()).getSupply() + " from user " + u.getI());
				priceOfBundle += this.market.getUser(u.getI()).getSupply() * (u.getPrice() + AscendingAuction.epsilon);
				sizeOfBundle  += this.market.getUser(u.getI()).getSupply();
				bundle.add(new UserPriceAlloc(u.getI(),-1.0,this.market.getUser(u.getI()).getSupply()));
			}
			if(sizeOfBundle == this.market.getCampaign(j).getDemand() && priceOfBundle<= this.market.getCampaign(j).getReward()){
				//System.out.println("We can satisfy campaign " + j);
				//System.out.println("Price of bundle: " + priceOfBundle);
				//System.out.println("final bundle = " + bundle);
				return bundle;
			}
		}
		return new ArrayList<UserPriceAlloc>();
	}
	
	public void convertBidsIntoMatrices(ArrayList<ArrayList<UserPriceAlloc>> listOfUsers){
		int campaignIndex = 0;
		for(ArrayList<UserPriceAlloc> allocFromCampaign : listOfUsers){
			for(UserPriceAlloc y: allocFromCampaign){
				this.allocation[y.getI()][campaignIndex] = y.getAlloc();
				this.prices[y.getI()][campaignIndex] = y.getPrice();
			}
			campaignIndex++;
		}
	}
}
