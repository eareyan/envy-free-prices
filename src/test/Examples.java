package test;

import algorithms.waterfall.Waterfall;
import algorithms.waterfall.WaterfallPrices;
import structures.Bidder;
import structures.Market;
import structures.Goods;
import structures.exceptions.BidderCreationException;
import structures.exceptions.MarketAllocationException;
import util.Printer;

public class Examples {
	
	public static void main(String[] args) throws BidderCreationException, MarketAllocationException{
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
		Bidder c1 = new Bidder(2, 25);
		Bidder c2 = new Bidder(3, 45);
		Bidder c3 = new Bidder(2, 25);
		Bidder[] campaigns = new Bidder[3];
		campaigns[0] = c1;
		campaigns[1] = c2;
		campaigns[2] = c3;
		
		Goods u1 = new Goods(2);
		Goods u2 = new Goods(2);
		Goods[] users = new Goods[2];
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
		Bidder[] campaigns1 = new Bidder[numCamp];
		for(int j=0;j<numCamp;j++){
			campaigns1[j] = new Bidder(2  /* demand */ ,2*(numCamp - j)/* reward */);
		}
		Goods[] users1 = new Goods[numUser];
		for(int i=0;i<numUser;i++){
			users1[i] = new Goods(1);
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
	
	public static Market market1() throws BidderCreationException{
		/* Example where the first implementation of CK outputs envy prices*/
		
		Bidder c1 = new Bidder(5, 8.79);
		Bidder c2 = new Bidder(5, 6.45);
		Bidder c3 = new Bidder(4, 3.47);
		Bidder[] campaigns = new Bidder[3];
		campaigns[0] = c1;
		campaigns[1] = c2;
		campaigns[2] = c3;
		
		Goods u1 = new Goods(5);
		Goods u2 = new Goods(5);
		Goods[] users = new Goods[2];
		users[0] = u1;
		users[1] = u2;
		
		boolean[][] connections = new boolean[2][3];
		connections[0][0] = false;
		
		connections[0][1] = false;
		
		connections[1][0] = false;		
		connections[1][1] = true;		
		Market market = new Market(users,campaigns,connections);
		return market;
	}
	
	public static Market market2() throws BidderCreationException{
		Bidder c1 = new Bidder(10, 100, 0.5, 1.0, -1, -1, 10);
		Bidder c2 = new Bidder(9, 10, 1.0, 0.0, -1, -1, 8);
		Bidder[] campaigns = new Bidder[2];
		campaigns[0] = c1;
		campaigns[1] = c2;
		
		Goods u1 = new Goods(10);
		Goods u2 = new Goods(5);
		Goods[] users = new Goods[2];
		users[0] = u1;
		users[1] = u2;

		boolean[][] connections = new boolean[2][2];
		connections[0][0] = true;
		connections[0][1] = true;
		connections[1][0] = true;
		connections[1][1] = true;
		
		return new Market(users,campaigns,connections);
	}
	
	public static Market market3() throws BidderCreationException{
		Bidder c1 = new Bidder(10, 1234);
		Bidder c2 = new Bidder(454, 2856);
		Bidder[] campaigns = new Bidder[2];
		campaigns[0] = c1;
		campaigns[1] = c2;
		
		Goods u1 = new Goods(25);
		Goods u2 = new Goods(25);
		Goods[] users = new Goods[2];
		users[0] = u1;
		users[1] = u2;

		boolean[][] connections = new boolean[2][2];
		connections[0][0] = true;
		connections[0][1] = true;
		connections[1][0] = true;
		connections[1][1] = true;
		
		return new Market(users,campaigns,connections);
	}
	
	public static Market market4() throws BidderCreationException{
		Bidder c1 = new Bidder(50, 500);
		Bidder[] campaigns = new Bidder[1];
		campaigns[0] = c1;
		
		Goods u1 = new Goods(25);
		Goods u2 = new Goods(25);
		Goods[] users = new Goods[2];
		users[0] = u1;
		users[1] = u2;

		boolean[][] connections = new boolean[2][1];
		connections[0][0] = true;
		connections[1][0] = true;
		
		return new Market(users,campaigns,connections);
	}
	
	
	public static Market market5() throws BidderCreationException{
		/* Example market with priorities*/
		
		Bidder c1 = new Bidder(5, 8, 0.4, 0.0, 0, 1);
		Bidder c2 = new Bidder(5, 6, 0.49,0.0, 0, 1);
		Bidder c3 = new Bidder(5, 3, 1.0, 0.0, 0, 2);
		Bidder c4 = new Bidder(5, 1, 1.0, 0.0, 0, 2);
		Bidder[] campaigns = new Bidder[4];
		campaigns[0] = c1;
		campaigns[1] = c2;
		campaigns[2] = c3;
		campaigns[3] = c4;
		
		Goods u1 = new Goods(4);
		Goods u2 = new Goods(6);
		Goods[] users = new Goods[2];
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
	
	public static Market market6() throws BidderCreationException{
		/* Example market with priorities*/
		
		Bidder c1 = new Bidder(3, 8,0,1);
		Bidder[] campaigns = new Bidder[1];
		campaigns[0] = c1;
		
		Goods u1 = new Goods(4);
		Goods[] users = new Goods[1];
		users[0] = u1;
		
		boolean[][] connections = new boolean[1][1];
		connections[0][0] = true;

		Market market = new Market(users,campaigns,connections);
		return market;
	}
	
	public static Market market7() throws BidderCreationException{
		int numberC = 10;
		Bidder[] campaigns = new Bidder[numberC];
		for(int j=0;j<numberC;j++){
			campaigns[j] = new Bidder(j*800 + 50, j*900 + 100 , j, (j<5) ? 2 : 1 ); 
		}
		int numberU = 12;
		Goods[] users = new Goods[numberU];
		for(int i=0;i<numberU;i++){
			users[i] = new Goods(100*i);
		}
		boolean[][] connections = new boolean[numberU][numberC];
		for(int i=0;i<numberU;i++){
			for(int j=0;j<numberC;j++){
				connections[i][j] = true;
			}
		}
		return new Market(users,campaigns,connections);
	}
	
	public static Market market8() throws BidderCreationException{
		Bidder[] campaigns = new Bidder[3];
		campaigns[0] = new Bidder(10,100.0);
		campaigns[1] = new Bidder(10,200.0);
		campaigns[2] = new Bidder(10,300.0);
		
		Goods[] users = new Goods[4];
		users[0] = new Goods(10);
		users[1] = new Goods(10);
		users[2] = new Goods(10);
		users[3] = new Goods(10);
		
		boolean[][] connections = new boolean[4][3];
		connections[0][0] = true;
		//connections[0][1] = true;
		
		connections[1][1] = true;

		connections[2][0] = true;
		//connections[2][1] = true;

		connections[3][2] = true;
		
		return new Market(users,campaigns,connections);
}
	
	public static Market typicalTACMarket() throws BidderCreationException{
		//Campaign c0 = new Campaign(4142,4622.0,Math.pow(0.9,10),0.5,0,2);
		Bidder c0 = new Bidder(4142, 4622.0, 1.0, 0.0, 0, 2, 4000);
		Bidder c1 = new Bidder(1920, 671.0, 1, 0.08, 1, 2, 0);
		Bidder c2 = new Bidder(481, 900.0, 1.0, 0.0, 2, 1, 479);
		Bidder c3 = new Bidder(2478, 6195.0, 1.0, 0.0,  3, 1, 100);
		Bidder c4 = new Bidder(259, 387.0, 4, 2);
		Bidder c5 = new Bidder(1921, 4802.0, 1.0, 0.0, 5, 2, 0);
		Bidder[] campaigns = new Bidder[6];
		campaigns[0] = c0;
		campaigns[1] = c1;
		campaigns[2] = c2;
		campaigns[3] = c3;
		campaigns[4] = c4;
		campaigns[5] = c5;
		
		Goods u0 = new Goods(2812);
		Goods u1 = new Goods(3410);
		Goods u2 = new Goods(735);
		Goods u3 = new Goods(1148);
		Goods u4 = new Goods(2549);
		Goods u5 = new Goods(578);
		Goods u6 = new Goods(2608);
		Goods u7 = new Goods(364);
		Goods[] users = new Goods[8];
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
	
	public static Market singleMinded() throws BidderCreationException{
		Bidder[] campaigns = new Bidder[3];
		campaigns[0] = new Bidder(3,5.35);
		campaigns[1] = new Bidder(1,3.42);
		campaigns[2] = new Bidder(1,8.99);
		
		Goods[] users = new Goods[3];
		users[0] = new Goods(1);
		users[1] = new Goods(1);
		users[2] = new Goods(1);
		
		boolean[][] connections = new boolean[3][3];
		connections[0][0] = true;
		connections[0][2] = true;
		
		connections[1][0] = true;
		connections[1][1] = true;

		connections[2][0] = true;
		
		return new Market(users,campaigns,connections);
	}
	
	public static Market singleMinded2() throws BidderCreationException{
		Bidder[] campaigns = new Bidder[3];
		campaigns[0] = new Bidder(1, 4.39);
		campaigns[1] = new Bidder(3, 2.48);
		campaigns[2] = new Bidder(1, 9.79);
		
		Goods[] users = new Goods[4];
		users[0] = new Goods(1);
		users[1] = new Goods(1);
		users[2] = new Goods(1);
		users[3] = new Goods(1);
		
		boolean[][] connections = new boolean[4][3];
		
		connections[0][1] = true;
		
		connections[2][0] = true;
		connections[2][1] = true;
		
		connections[3][1] = true;
		connections[3][2] = true;
		
		return new Market(users,campaigns,connections);
	}
}
