package structures;

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
    /*
     * Is user i connected to campaign j?
     */
    public boolean isConnected(int i, int j){
    	return this.connections[i][j];
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
