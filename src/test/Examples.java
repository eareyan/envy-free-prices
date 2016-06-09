package test;

import algorithms.waterfall.Waterfall;
import algorithms.waterfall.WaterfallPrices;
import structures.Campaign;
import structures.Market;
import structures.User;
import util.Printer;

public class Examples {
	
	public static void main(String[] args){
		/* * Example where All Connected EVPApp performs better than plus 1 EVPApp in NON-Uniform, unit demand case */
		double [][] valuationMatrix = new double[3][3];
		
		valuationMatrix[0][0] = 29.60;		
		valuationMatrix[0][1] = 18.66;		
		valuationMatrix[0][2] = Double.NEGATIVE_INFINITY;		

		valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;		
		valuationMatrix[1][1] = Double.NEGATIVE_INFINITY;		
		valuationMatrix[1][2] = 27.10;		

		valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;		
		valuationMatrix[2][1] = 58.66;		
		valuationMatrix[2][2] = 97.06;		

		Printer.printMatrix(valuationMatrix);
		
		/* * Example where Plus 1 EVPApp performs better than All Connected EVPApp in NON-Uniform, unit demand case */
		valuationMatrix = new double[3][2];
		valuationMatrix[0][0] = 19.47;
		valuationMatrix[0][1] = Double.NEGATIVE_INFINITY;

		valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
		valuationMatrix[1][1] = 41.11;

		valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
		valuationMatrix[2][1] = 73.16;
		
		/* * Example where LP with all connected, simple reserve price Rj/Ij does better than regular EVP (All-connected)*/
		valuationMatrix = new double[3][2];
		valuationMatrix[0][0] = 39.92;
		valuationMatrix[0][1] = Double.NEGATIVE_INFINITY;

		valuationMatrix[1][0] = Double.NEGATIVE_INFINITY;
		valuationMatrix[1][1] = 43.51;

		valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
		valuationMatrix[2][1] = 43.51;
		
		/* Example to test LP in the uniform unit-demand case with and without walrasian conditions */
		valuationMatrix = new double[3][2];
		valuationMatrix[0][0] = Double.NEGATIVE_INFINITY;
		valuationMatrix[0][1] = 62;

		valuationMatrix[1][0] = 96;
		valuationMatrix[1][1] = 62;

		valuationMatrix[2][0] = Double.NEGATIVE_INFINITY;
		valuationMatrix[2][1] = 62;
		
		/* Example market where GeneralMaxWEQ produces non-envy-free prices
		Campaign c1 = new Campaign(1, 79);
		Campaign c2 = new Campaign(1, 89);
		Campaign c3 = new Campaign(1, 53);
		Campaign[] campaigns = new Campaign[3];
		campaigns[0] = c1;
		campaigns[1] = c2;
		campaigns[2] = c3;
		
		User u1 = new User(1);
		User u2 = new User(1);
		User u3 = new User(1);
		User[] users = new User[3];
		users[0] = u1;
		users[1] = u2;
		users[2] = u3;
		
		boolean[][] connections = new boolean[3][3];
		connections[0][0] = true;
		connections[0][2] = true;
		
		connections[1][1] = true;
		
		connections[2][1] = true;
		
		Market market = new Market(users,campaigns,connections);
		
		/* Example where real envy-free prices do not exists */
		Campaign c1 = new Campaign(2, 25);
		Campaign c2 = new Campaign(3, 45);
		Campaign c3 = new Campaign(2, 25);
		Campaign[] campaigns = new Campaign[3];
		campaigns[0] = c1;
		campaigns[1] = c2;
		campaigns[2] = c3;
		
		User u1 = new User(2);
		User u2 = new User(2);
		User[] users = new User[2];
		users[0] = u1;
		users[1] = u2;
		
		boolean[][] connections = new boolean[2][3];
		connections[0][0] = true;
		connections[0][1] = true;
		
		connections[1][1] = true;
		
		connections[1][2] = true;
		
		//Market market = new Market(users,campaigns,connections);
		
		
		/* Example where WF fails*/
		int numCamp = 30;
		int numUser = 5;
		
		numUser = numCamp;
		Campaign[] campaigns1 = new Campaign[numCamp];
		for(int j=0;j<numCamp;j++){
			campaigns1[j] = new Campaign(2  /* demand */ ,2*(numCamp - j)/* reward */);
		}
		User[] users1 = new User[numUser];
		for(int i=0;i<numUser;i++){
			users1[i] = new User(1);
		}
		boolean[][] connections1 = new boolean[numUser][numCamp];
		int counter = 0;
		//connections[0][0] = true;
		//connections[0][numUser-1] = true;
		connections1[numUser-1][numUser-1] = true;
		for(int i=0;i<numUser-1;i++){
			connections1[i][counter] = true;
			counter++;
			connections1[i][counter] = true;
		}
		/*connections[0][0] = true;
		connections[1][0] = true;
		connections[1][1] = true;
		connections[2][1] = true;
		connections[2][2] = true;
		//connections[3][2] = true;
		connections[3][3] = true;*/
		
		Market weirdMarket = new Market(users1,campaigns1,connections1);
		System.out.println(weirdMarket);
		Waterfall WF = new Waterfall(weirdMarket);
		WaterfallPrices WFSol = WF.Solve();
		System.out.println("WF Alloc");
		//Printer.printMatrix(WFSol.getMarketAllocation().getAllocation());	
		System.out.println(WFSol.getMarketAllocation().value());
		
		//MarketAllocation y = new MarketAllocation(weirdMarket,new EfficientAllocationILP(weirdMarket).Solve(new IloCplex()).get(0));
		System.out.println("Efficient Alloc");
		//Printer.printMatrix(y.getAllocation());
		//System.out.println(y.value());
		
		//System.out.println(WFSol.getMarketAllocation().value() / y.value());	
		
	}
	
	public static Market market1(){
		/* Example where the first implementation of CK outputs envy prices*/
		
		Campaign c1 = new Campaign(5, 8.79);
		Campaign c2 = new Campaign(5, 6.45);
		Campaign c3 = new Campaign(4, 3.47);
		Campaign[] campaigns = new Campaign[3];
		campaigns[0] = c1;
		campaigns[1] = c2;
		campaigns[2] = c3;
		
		User u1 = new User(12);
		User u2 = new User(2);
		User[] users = new User[2];
		users[0] = u1;
		users[1] = u2;
		
		boolean[][] connections = new boolean[2][3];
		connections[0][0] = true;
		
		connections[0][1] = true;
		
		connections[1][0] = true;		
		connections[1][1] = true;		
		Market market = new Market(users,campaigns,connections);
		return market;
	}
	
	public static Market market2(){
		Campaign c1 = new Campaign(10, 100);
		Campaign c2 = new Campaign(9, 10);
		Campaign[] campaigns = new Campaign[2];
		campaigns[0] = c1;
		campaigns[1] = c2;
		
		User u1 = new User(10);
		User u2 = new User(5);
		User[] users = new User[2];
		users[0] = u1;
		users[1] = u2;

		boolean[][] connections = new boolean[2][2];
		connections[0][0] = false;
		connections[0][1] = true;
		connections[1][0] = true;
		connections[1][1] = true;
		
		return new Market(users,campaigns,connections);
	}
	
	public static Market market3(){
		Campaign c1 = new Campaign(10, 1234);
		Campaign c2 = new Campaign(454, 2856);
		Campaign[] campaigns = new Campaign[2];
		campaigns[0] = c1;
		campaigns[1] = c2;
		
		User u1 = new User(25);
		User u2 = new User(25);
		User[] users = new User[2];
		users[0] = u1;
		users[1] = u2;

		boolean[][] connections = new boolean[2][2];
		connections[0][0] = true;
		connections[0][1] = true;
		connections[1][0] = true;
		connections[1][1] = true;
		
		return new Market(users,campaigns,connections);
	}
	
	public static Market market4(){
		Campaign c1 = new Campaign(50, 500);
		Campaign[] campaigns = new Campaign[1];
		campaigns[0] = c1;
		
		User u1 = new User(25);
		User u2 = new User(25);
		User[] users = new User[2];
		users[0] = u1;
		users[1] = u2;

		boolean[][] connections = new boolean[2][1];
		connections[0][0] = true;
		connections[1][0] = true;
		
		return new Market(users,campaigns,connections);
	}
	
	
	public static Market market5(){
		/* Example market with priorities*/
		
		Campaign c1 = new Campaign(5, 8,0,1);
		Campaign c2 = new Campaign(5, 6,0,1);
		Campaign c3 = new Campaign(5, 3,0,2);
		Campaign c4 = new Campaign(5, 1,0,2);
		Campaign[] campaigns = new Campaign[4];
		campaigns[0] = c1;
		campaigns[1] = c2;
		campaigns[2] = c3;
		campaigns[3] = c4;
		
		User u1 = new User(4);
		User u2 = new User(6);
		User[] users = new User[2];
		users[0] = u1;
		users[1] = u2;
		
		boolean[][] connections = new boolean[2][4];
		connections[0][0] = true;
		connections[0][1] = true;
		connections[0][2] = true;
		connections[0][3] = true;
		
		connections[1][0] = true;		
		connections[1][1] = true;		
		connections[1][2] = true;		
		connections[1][3] = true;		

		Market market = new Market(users,campaigns,connections);
		return market;
	}	
	
	public static Market typicalTACMarket(){
		Campaign c0 = new Campaign(4142,4622.0,0,2);
		Campaign c1 = new Campaign(1920,671.0,1,2);
		Campaign c2 = new Campaign(481,900.0,2,1);
		Campaign c3 = new Campaign(2478,6195.0,3,1);
		Campaign c4 = new Campaign(259,387.0,4,1);
		Campaign c5 = new Campaign(1921,4802.0,5,1);
		Campaign[] campaigns = new Campaign[6];
		campaigns[0] = c0;
		campaigns[1] = c1;
		campaigns[2] = c2;
		campaigns[3] = c3;
		campaigns[4] = c4;
		campaigns[5] = c5;
		
		User u0 = new User(2812);
		User u1 = new User(3410);
		User u2 = new User(735);
		User u3 = new User(1148);
		User u4 = new User(2549);
		User u5 = new User(578);
		User u6 = new User(2608);
		User u7 = new User(364);
		User[] users = new User[8];
		users[0] = u0;
		users[1] = u1;
		users[2] = u2;
		users[3] = u3;
		users[4] = u4;
		users[5] = u5;
		users[6] = u6;
		users[7] = u7;
		
		boolean[][] connections = new boolean[8][6];
		connections[0][1] = true;
		
		connections[1][2] = true;
		connections[1][5] = true;
		
		connections[2][0] = true;
		connections[2][3] = true;
		connections[2][4] = true;
		
		connections[3][0] = true;
		connections[3][3] = true;
		
		connections[4][3] = true;

		connections[5][0] = true;
		
		connections[6][3] = true;
		
		connections[7][0] = true;

		return new Market(users,campaigns,connections);
	}
}
