package util;

/*
 * Library with common methods for number manipulation.
 * 
 * @author Enrique Areyan Viqueira
 */
public class NumberMethods {

	public static double[] roundPrices(double[] prices){
		for(int i=0;i<prices.length;i++){
			prices[i] = Math.round(prices[i] * 100000.0) / 100000.0;
		}
		return prices;
	}
	public static double getRatio(double v1,double v2){
		if(v1 == 0.0 && v2 == 0.0){
			return 1.0;
		}else{
			return v1/v2;
		}
	}	
}
