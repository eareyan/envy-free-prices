package util;

public class Printer {
	
    public static void printMatrix(double[][] matrix){
    	for(int i=0;i<matrix.length;i++){
    		for(int j=0;j<matrix[0].length;j++){
    			System.out.print(matrix[i][j] + "\t");
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
}
