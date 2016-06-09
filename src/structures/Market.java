package structures;

import java.util.ArrayList;

/*
 * A market is a bipartite graph with Users connected to Campaigns.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Market {
    /*
     * Array of Users
     */    
    protected User[] users;	
	/*
	 * Array of Campaigns
	 */	
    protected Campaign[] campaigns;
    /*
     * Boolean matrix indicating which campaign is connected to which user.
     */
    protected boolean[][] connections;
    /*
     * Highest reward among all campaigns. Implemented as a singleton.
     */
    protected double highestReward = -1.0;    
    /*
     * Constructors. The first constructor is the most fundamental one.
     * It receives campaigns, users and connections.
     */
    public Market(User[] users,Campaign[] campaigns,boolean[][] connections){
    	this.users = users;
    	this.campaigns = campaigns;
    	this.connections = connections;
    }
    /*
     * Getters
     */
    public Campaign[] getCampaigns(){
    	return this.campaigns;
    }
    
    public Campaign getCampaign(int j){
    	return this.campaigns[j];
    }
    
    public User[] getUsers(){
    	return this.users;
    }
    
    public User getUser(int i){
    	return this.users[i];
    }
    
    public int getNumberUsers(){
    	return this.users.length;
    }
    
    public int getNumberCampaigns(){
    	return this.campaigns.length;
    }
    
    public boolean[][] getConnections(){
    	return this.connections;
    }
    /*
     * add a single campaign
     */
    public void addCampaign(Campaign c, ArrayList<Integer> users){
    	/* Add the new campaign to the array of existing campaigns */
    	Campaign[] newCampaigns = new Campaign[this.getNumberCampaigns() + 1];
    	System.arraycopy(this.campaigns, 0, newCampaigns, 0, this.getNumberCampaigns());
    	newCampaigns[this.getNumberCampaigns()] = c;
    	this.campaigns = newCampaigns;
    	/* add this new campaign's connections */
		boolean[][] newConnections = new boolean[this.getNumberUsers()][this.getNumberCampaigns()];
		for(int i=0;i<this.getNumberUsers();i++){
			for(int j=0;j<this.getNumberCampaigns()-1;j++){
				newConnections[i][j] = this.connections[i][j];
			}
		}
		for(Integer userid: users){
			newConnections[userid][this.getNumberCampaigns()-1] = true;
		}
		this.connections = newConnections;
    }
    /*
     * Get highest reward among all campaigns. Implements a singleton
     */
    public double getHighestReward(){
    	if(this.highestReward == -1.0){
    		double temp = -1.0;
    		for(int j=0;j<this.getNumberCampaigns();j++){
    			if(this.getCampaign(j).getReward() > temp){
    				temp = this.getCampaign(j).getReward();
    			}
    		}
    		this.highestReward = temp;
    	}
    	return this.highestReward;
    }
    /*
     * Does user i has any connections at all?
     */
    public boolean hasConnectionsUser(int i){
    	for(int j=0;j<this.getNumberCampaigns();j++){
    		if(this.isConnected(i, j)){
    			return true;
    		}
    	}
    	return false;
    }
    /*
     * Does campaign j has any connections at all?
     */
    public boolean hasConnectionsCampaign(int j){
    	for(int i=0;i<this.getNumberUsers();i++){
    		if(this.isConnected(i, j)){
    			return true;
    		}
    	}
    	return false;
    }
    /*
     * Get a list of users connected to campaign j
     */
    public ArrayList<Integer> getListConnectedUsers(int j){
    	ArrayList<Integer> listOfUsers = new ArrayList<Integer>();
    	for(int i=0;i<this.getNumberUsers();i++){
    		if(this.isConnected(i, j)){
    			listOfUsers.add(i);
    		}
    	}
    	return listOfUsers;
    }
    /*
     * Is user i connected to campaign j?
     */
    public boolean isConnected(int i, int j){
    	return this.connections[i][j];
    }
    /*
     * Total supply is defined as \sum_{i} N_i
     */
    public int getTotalSupply(){
    	int totalSupply = 0;
    	for(int i=0;i<this.getNumberUsers();i++){
    		totalSupply += this.getUser(i).getSupply();
    	}
    	return totalSupply;
    }    
    /*
     * Total demand is defined as \sum_{j} I_j
     */
    public int getTotalDemand(){
    	int totalDemand = 0;
    	for(int j=0;j<this.getNumberCampaigns();j++){
    		totalDemand += this.getCampaign(j).getDemand();
    	}
    	return totalDemand;
    }
    /*
     * Get supply to demand ratio. 
     */
    public double getSupplyToDemandRatio(){
		return (double) this.getTotalSupply() / this.getTotalDemand();
    }
    /*
     * Printers. Representation of objects as strings.
     */
    protected String stringUsersInfo(){
    	String ret = "";
    	for(int i=0;i<this.getNumberUsers();i++){
    		ret += "\nN("+i+") = " + this.users[i].supply;
    	}
    	return ret;
    }    
	protected String stringCampaignsInfo(){
		String ret = "";
		for(int j=0;j<this.getNumberCampaigns();j++){
			ret += "\nR("+j+") = "+ this.campaigns[j].reward + ";\t I("+j+") = " + this.campaigns[j].demand;
			if(this.campaigns[j].backpointer != -1){
				ret += ";\tBackpoints to: " + this.campaigns[j].backpointer;
			}
			if(this.campaigns[j].priority != -1){
				ret += ";\tPriority: " + this.campaigns[j].priority;
			}
		}
		return ret;
	}    
	protected String stringConnectionsMatrix(){
		if(this.connections != null){	   
			String ret = "";
			for(int i=0;i<this.getNumberUsers();i++){
				ret += "\n";
				for(int j=0;j<this.getNumberCampaigns();j++){
					if(this.isConnected(i, j)){
						ret += "\t yes";
					}else{
						ret += "\t no";
					}
				}
			}
			return ret+"\n";
		}else{
			return "The connection matrix is not initialized";
		}
   }
    @Override
    public String toString(){
    	return  "NbrCampaigns:\t"+this.getNumberCampaigns() + "\n" +
    			"NbrUsers:\t"+this.getNumberUsers() + "\n" +
    			"Campaigns Rewards \t\t Campaigns Demand" + this.stringCampaignsInfo() + "\n" +
    			"Users Supply" + this.stringUsersInfo() + "\n" +
    			"Connections Matrix:\t"+this.stringConnectionsMatrix();
    }
}
