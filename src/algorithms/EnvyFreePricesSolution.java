package algorithms;

import java.text.DecimalFormat;
/*
 * This class stores the resulting envy-free prices from LP.
 * 
 * @author Enrique Areyan Viqueira
 */
public class EnvyFreePricesSolution {
	protected double[] PricesVector;
	protected double[][] PricesMatrix;
	String Status;
	
	public EnvyFreePricesSolution(){
		this.Status = "Empty";
	}
	public EnvyFreePricesSolution(String Status){
		this.Status = Status;
	}
	
	public EnvyFreePricesSolution(double[] PricesVector, String Status){
		this.Status = Status;
		this.PricesVector = PricesVector;
	}
	
	public EnvyFreePricesSolution(double[][] PricesMatrix, String Status){
		this.Status = Status;
		this.PricesMatrix = PricesMatrix;
	}
	
	public String getStatus(){
		return this.Status;
	}
	
	public double[] getPrices(){
		return this.PricesVector;
	}
	
	public void printPricesMatrix(){
		DecimalFormat df = new DecimalFormat("#.00"); 
		System.out.println("Prices Matrix: ");
    	for(int i=0; i< this.PricesMatrix.length; i++){
    		for(int j=0; j<this.PricesMatrix[0].length; j++){
    			System.out.print(df.format(this.PricesMatrix[i][j]) + "\t\t");
    		}
    		System.out.println("");
    	}
	}
	public void printPricesVector(){
		DecimalFormat df = new DecimalFormat("#.00"); 
		System.out.println("Prices Vector: ");
    	for(int i=0; i< this.PricesVector.length; i++){
    		System.out.println("P("+i+") = " + df.format(this.PricesVector[i]));
    	}	
	}
	
	public double valuePriceVector(int[][] allocationMatrix){
		double value = 0;
		for(int i=0;i<allocationMatrix.length;i++){
			for(int j=0;j<allocationMatrix[0].length;j++){
				value += allocationMatrix[i][j] * this.PricesVector[i];
			}
		}
		return value;
	}
	public double valuePriceMatrix(int[][] allocationMatrix){
		double value = 0;
		for(int i=0;i<allocationMatrix.length;i++){
			for(int j=0;j<allocationMatrix[0].length;j++){
				value += allocationMatrix[i][j] * this.PricesMatrix[i][j];
			}
		}
		return value;
	}
}
