package classification.helper;

import java.util.List;
import java.util.Random;

public class Helper {
	public static Random random;

	public static double dec2(double value) {
		return (int)(value*10000.0)/10000.0;
	}
	
	public static int findDuplicateDouble(double item, List<Double> itemArray) {
		int index = -1;
		for(int i=0; i<itemArray.size(); i++) {
			if(item==itemArray.get(i)) index = i;
		}
		return index;
	}
	
	public static int findMaxIndex(double[] array) {
		int maxIndex = 0;
		double maxValue = array[maxIndex];
		for(int i=0; i<array.length; i++) {
			if(maxValue<array[i]) {
				maxIndex = i;
				maxValue = array[i];
			}
		}
		return maxIndex;
	}
	
	public static int findMinIndex(double[] array) {
		int maxIndex = 0;
		double minValue = array[maxIndex];
		for(int i=0; i<array.length; i++) {
			if(minValue>array[i]) {
				maxIndex = i;
				minValue = array[i];
			}
		}
		return maxIndex;
	}
	
	public static void setupRandom() {
		random = new Random();
		random.setSeed(12353434);
	}
	
	public static double getRandom01() {
//		int value = (int) (Math.random()*1000.0);
		int value = (int) (random.nextDouble()*1000.0);
		return (double)value/1000.0;
	}
	
	public static double getRandom05() {
		double max = 0.5;
		double min = -0.5;
		int value = (int) (((random.nextDouble() * (max - min)) + min ) * 1000.0);
		return (double)value/1000.0;
//		return Helper.dec2((Math.random() * ((max - min))) + min);
	}
	
	public static int findDivider(double maxValue) {
		int value = 10;
		while((int)maxValue/value>0) {
			value *= 10;
		}
		return value;
	}
	
	public static boolean is0Exist(double[][] data) {
		boolean exists = false;
		for(double[] a : data) {
			for(double b : a) {
				if(b==0) {
					exists = true;
					break;
				}
			}
		}
		return exists;
	}

}
