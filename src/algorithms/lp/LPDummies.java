package algorithms.lp;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Collections;

import algorithms.EnvyFreePricesSolutionLP;
import algorithms.EnvyFreePricesVectorLP;
import algorithms.Waterfall;
import structures.Campaign;
import structures.Market;
import structures.MarketAllocation;
import structures.MarketPrices;
import structures.MarketPricesComparatorBySellerRevenue;
import structures.factory.MarketFactory;
import util.Printer;

public class LPDummies {
	
	protected Market market;
	protected int[][] initialAllocation;
	protected double originalSellerRevenue;
	protected boolean flag = false;
	
	public LPDummies(Market market) throws IloException{
		this.market = market;
		MarketAllocation marketAlloc = new Waterfall(this.market).Solve().getMarketAllocation();
		EnvyFreePricesVectorLP EFPLP = new EnvyFreePricesVectorLP(marketAlloc,new IloCplex());
		EFPLP.createLP();
		EnvyFreePricesSolutionLP sol = EFPLP.Solve();
		
		//System.out.println("Final revenue = " + sol.sellerRevenuePriceVector());
		this.originalSellerRevenue = sol.sellerRevenuePriceVector();
		//Printer.printMatrix(marketAlloc.getAllocation());
		//Printer.printVector(sol.getPriceVector());
		this.initialAllocation = marketAlloc.getAllocation();
	}
	
	public double Solve() throws IloException{
		ArrayList<MarketPrices> setOfSolutions = new ArrayList<MarketPrices>();
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				if(this.initialAllocation[i][j]>0){
					/*
					 * Create new market with dummy campaigns.
					 */
					//System.out.println("******************************************Alloc["+i+"]["+j+"]");
					Market augmentedMarket = this.marketWithDummies(i, j);
					System.out.println(augmentedMarket);
					EnvyFreePricesVectorLP EFPLP = new EnvyFreePricesVectorLP(new Waterfall(augmentedMarket).Solve().getMarketAllocation(),new IloCplex());
					EFPLP.createLP();
					EnvyFreePricesSolutionLP sol = EFPLP.Solve();
					setOfSolutions.add(this.deduceMatching(sol));
					if(this.flag) System.exit(-1);
				}
			}
		}
		Collections.sort(setOfSolutions,new MarketPricesComparatorBySellerRevenue());
		//System.out.println(this.originalSellerRevenue);
		//System.out.println(setOfSolutions);
		if(setOfSolutions.size()>0){
			//System.out.println(setOfSolutions.get(0).sellerRevenuePriceVector() / this.originalSellerRevenue);
			return setOfSolutions.get(0).sellerRevenuePriceVector() / this.originalSellerRevenue;
		}else{
			return -1.0;
		}
	}
	protected Market marketWithDummies(int userIndex,int campaignIndex){
		Market augmentedMarket = MarketFactory.cloneMarket(this.market);
		for(int i=0;i<this.market.getNumberUsers();i++){
			ArrayList<Integer> users = new ArrayList<Integer>();
			if(this.market.isConnected(i, campaignIndex) && i!=userIndex && this.userAllocatesToOtherCampaigns(i, campaignIndex)){
				//System.out.println("Create dummy for user "+ i + ", backpointing to campaign " + campaignIndex + " with reward = " + (this.market.getCampaign(campaignIndex).getReward() / this.initialAllocation[userIndex][campaignIndex]) + " and demand = " + this.initialAllocation[userIndex][campaignIndex]);
				users = this.market.getListConnectedUsers(campaignIndex);
				//augmentedMarket.addCampaign(new Campaign(this.initialAllocation[userIndex][campaignIndex],this.market.getCampaign(campaignIndex).getReward() / this.initialAllocation[userIndex][campaignIndex],campaignIndex), users);
				//augmentedMarket.addCampaign(new Campaign(this.initialAllocation[userIndex][campaignIndex],this.market.getCampaign(campaignIndex).getReward() ,campaignIndex), users);
				//augmentedMarket.addCampaign(new Campaign(this.market.getCampaign(campaignIndex).getDemand(),this.market.getCampaign(campaignIndex).getReward() ,campaignIndex), users);
				augmentedMarket.addCampaign(new Campaign(this.initialAllocation[userIndex][campaignIndex],(this.market.getCampaign(campaignIndex).getReward()*this.initialAllocation[userIndex][campaignIndex]) / this.market.getCampaign(campaignIndex).getDemand() ,campaignIndex), users);
			}
		}
		return augmentedMarket;
	}
	protected boolean userAllocatesToOtherCampaigns(int userIndex,int campaignIndex){
		for(int j=0;j<this.market.getNumberCampaigns();j++){
			if(j!=campaignIndex && this.initialAllocation[userIndex][j]>0){
				return true;
			}
		}
		return false;
	}
	
	protected MarketPrices deduceMatching(EnvyFreePricesSolutionLP sol){
		MarketAllocation currentAllocation = sol.getMarketAllocation();
		
		int[][] allocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				allocation[i][j] = currentAllocation.getAllocation()[i][j];
			}
		}		
		
		Printer.printMatrix(currentAllocation.getAllocation());
		//Printer.printVector(sol.getPriceVector());
		for(int j=0;j<currentAllocation.getMarket().getNumberCampaigns();j++){
			if(currentAllocation.getMarket().getCampaign(j).getBackpointer() != -1){
				//System.out.println("Campaign "+j+" is a dummy campaign!!!");
				if(currentAllocation.getBundleNumber(j)>0){
					//System.out.println("--- This dummy WAS allocated something, we have to give it back!!!");
					int t = 0;
					for(int i=0;i<this.market.getNumberUsers();i++){
						t += allocation[i][currentAllocation.getMarket().getCampaign(j).getBackpointer()];
					}
					
					//if(currentAllocation.getBundleNumber(currentAllocation.getMarket().getCampaign(j).getBackpointer())==0){
					if(t==0){
						System.out.println("!!!!!!!!!!!!!!!MAKES SENSE TO GIVE IT BACK!!!!!!!!!!!!!!!");
						
						for(int i=0;i<this.market.getNumberUsers();i++){
							allocation[i][currentAllocation.getMarket().getCampaign(j).getBackpointer()] = currentAllocation.getAllocation()[i][j];
						}
						this.flag = true;
						this.mergeDummies(currentAllocation, currentAllocation.getMarket().getCampaign(j).getBackpointer());
					}
				}
			}
		}

		System.out.println("Final allocaiton: ");
		Printer.printMatrix(allocation);
		MarketPrices finalMarketPrices = new MarketPrices(new MarketAllocation(this.market,allocation),sol.getPriceVector());
		System.out.println("Final revenue = " + finalMarketPrices.sellerRevenuePriceVector());
		Printer.printVector(finalMarketPrices.getPriceVector());
		return finalMarketPrices;
	}
	
	
	
	protected ArrayList<Integer> mergeDummies(MarketAllocation marketAllocation, int campaignIndex){
		for(int j=0;j<marketAllocation.getMarket().getNumberCampaigns();j++){
			if(marketAllocation.getMarket().getCampaign(j).getBackpointer() == campaignIndex){
				System.out.print("\tj = "+j);
			}
		}
		return null;
	}
	
	protected MarketPrices deduceMatchingOLD(EnvyFreePricesSolutionLP sol){
		MarketAllocation currentAllocation = sol.getMarketAllocation();
		
		int[][] allocation = new int[this.market.getNumberUsers()][this.market.getNumberCampaigns()];
		for(int i=0;i<this.market.getNumberUsers();i++){
			for(int j=0;j<this.market.getNumberCampaigns();j++){
				allocation[i][j] = currentAllocation.getAllocation()[i][j];
			}
		}		
		
		//Printer.printMatrix(currentAllocation.getAllocation());
		//Printer.printVector(sol.getPriceVector());
		for(int j=0;j<currentAllocation.getMarket().getNumberCampaigns();j++){
			if(currentAllocation.getMarket().getCampaign(j).getBackpointer() != -1){
				//System.out.println("Campaign "+j+" is a dummy campaign!!!");
				if(currentAllocation.getBundleNumber(j)>0){
					//System.out.println("--- This dummy WAS allocated something, we have to give it back!!!");
					int t = 0;
					for(int i=0;i<this.market.getNumberUsers();i++){
						t += allocation[i][currentAllocation.getMarket().getCampaign(j).getBackpointer()];
					}
					
					//if(currentAllocation.getBundleNumber(currentAllocation.getMarket().getCampaign(j).getBackpointer())==0){
					if(t==0){
						System.out.println("!!!!!!!!!!!!!!!MAKES SENSE TO GIVE IT BACK!!!!!!!!!!!!!!!");
						for(int i=0;i<this.market.getNumberUsers();i++){
							allocation[i][currentAllocation.getMarket().getCampaign(j).getBackpointer()] = currentAllocation.getAllocation()[i][j];
						}
						//this.flag = true;
					}
				}
			}
		}

		//System.out.println("Final allocaiton: ");
		//Printer.printMatrix(allocation);
		MarketPrices finalMarketPrices = new MarketPrices(new MarketAllocation(this.market,allocation),sol.getPriceVector());
		//System.out.println("Final revenue = " + finalMarketPrices.sellerRevenuePriceVector());
		return finalMarketPrices;
	}	
}
