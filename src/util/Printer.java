package util;

import java.text.DecimalFormat;

/*
 * Library with common methods for printing information to the standard output.
 * 
 * @author Enrique Areyan Viqueira
 */
public class Printer {
	
    public static void printMatrix(double[][] matrix){
    	DecimalFormat df = new DecimalFormat("#.00"); 
    	for(int i=0;i<matrix.length;i++){
    		for(int j=0;j<matrix[0].length;j++){
    			if(matrix[i][j] == Double.MAX_VALUE){
    				System.out.print("Inf \t");
    			}else{
    				System.out.print(df.format(matrix[i][j]) + "\t");
    			}
    		}
    		System.out.print("\n");
    	}
    }

    public static void printMatrix(int[][] matrix){
    	for(int i=0;i<matrix.length;i++){
    		for(int j=0;j<matrix[0].length;j++){
    			System.out.print(matrix[i][j] + "\t");
    		}
    		System.out.print("\n");
    	}
    }
	public static void printMatrix(boolean[][] matrix){
		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix[0].length;j++){
				if(matrix[i][j]){
					System.out.print("\t yes");
				}else{
					System.out.print("\t no");
				}
			}
			System.out.print("\n");
		}
   }
	public static void printVector(double[] vector){
		DecimalFormat df = new DecimalFormat("#.000000"); 
		for(int i=0;i<vector.length;i++){
			if(vector[i] == -1.0*Double.MAX_VALUE){
				System.out.print("-Inf \t");
			}else{
				System.out.print(df.format(vector[i]) + "\t");
			}
		}
		System.out.print("\n");
	}
	public static void printVector(Double[] vector){
		DecimalFormat df = new DecimalFormat("#.00"); 
		for(int i=0;i<vector.length;i++){
			if(vector[i] == -1.0*Double.MAX_VALUE){
				System.out.print("-Inf \t");
			}else{
				System.out.print(df.format(vector[i]) + "\t");
			}
		}
		System.out.print("\n");
	}	
	public static void printVector(int[] vector){
		for(int i=0;i<vector.length;i++){
			System.out.print(vector[i] + "\t");
		}
		System.out.print("\n");
	}	
}
