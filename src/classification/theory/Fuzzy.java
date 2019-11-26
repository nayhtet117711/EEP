package classification.theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classification.helper.Helper;
import classification.model.Data;
import classification.model.Item;
import classification.view.MainView;

public class Fuzzy {
	private int loopCount = 0; // just for test
	private int maxLoopCount = 1000; // just for test
	
	private Data data;
	private List<Item[]>[] sortedItems;
	private double[][] v;
	private int currentRowIndex = 0;
	
	private double[][] randomPair;
	private double[][] centroid;
	private int[] randomV;
	private double[][] clusters;
	private double[][] membershipMetrixs;
	private double[][] centroidNew;
	private double[][] centroidOld;
	
	private MainView mainView;
	
	public Fuzzy() {
		// TODO Auto-generated constructor stub
	}

	public Fuzzy(Data data, MainView mainView) {
		this.data = data;
		this.mainView = mainView;
		
		Helper.setupRandom(); // reset random
	}

//	public void sortByClass() {
//		sortedItems = new ArrayList[data.getClasses().size()];
//		for (int i = 0; i < sortedItems.length; i++) {
//			sortedItems[i] = new ArrayList<>();
//		}
//		for (int index = 0; index < data.getClasses().size(); index++) {
//			for (int i = 0; i < data.getItems().size(); i++) {
//				Item[] item = data.getItems().get(i);
//				if (item[data.getClassIndex()].getValue() == data.getClasses().get(index)) {
//
//					sortedItems[index].add(item);
//				}
//			}
//		}
////		System.out.println(Arrays.toString(sortedItems));
//	}

	public void doStep1() {
		v = new double[data.getItems().size()][data.getNoOfCell() - 1];
		centroid = new double[data.getClasses().size()][data.getNoOfCell() - 1];
		centroidOld = new double[data.getClasses().size()][data.getNoOfCell() - 1];

		// find random pairs
		discoverRandomPair();
//		System.out.println("\nRandomPair::");
		mainView.appendLog("\nRandomPair::");
		for (double[] rp : randomPair) {
			mainView.appendLog("    "+Arrays.toString(rp));
		}

		List<Item[]> itemss = data.getItems();
		
		
		// find v values
/*		for (int a = 0; a < itemss.size(); a++) {
			Item[] item = itemss.get(a);
			for (int b = 0; b < data.getNoOfCell() - 1; b++) {
				double[] tmpColData = new double[0];
				for (int i = 0; i < itemss.size(); i++) {
					Item[] its = itemss.get(i);
					if (its[data.getClassIndex()].getValue() == item[data.getClassIndex()].getValue()) {
						double[] tmpColData1 = new double[tmpColData.length + 1];
						System.arraycopy(tmpColData, 0, tmpColData1, 0, tmpColData.length);
						tmpColData1[tmpColData1.length - 1] = its[b].getValue();
						tmpColData = tmpColData1;
					}
				}
//				System.out.println("DDD:> "+ Arrays.toString(tmpColData));
				double minColValue = tmpColData[Helper.findMinIndex(tmpColData)];
				double maxColValue = tmpColData[Helper.findMaxIndex(tmpColData)];
				double colValue = item[b].getValue();
				v[a][b] = Helper.dec2((colValue - minColValue) / (maxColValue - minColValue));
//				System.out.println("value: "+ v[a][b]+", min: "+ minColValue+", max: "+ maxColValue);
			}
//			System.out.println("-------------");
		}
		*/
		
		normalize();
		
		mainView.appendLog("\nV values:");
		for (double[] v1 : v) {
			mainView.appendLog("     "+Arrays.toString(v1));
		}

		// picked up random V values as no of class
		randomV = new int[data.getClasses().size()];
		for (int j = 0; j < itemss.size(); j++) {
			Item[] it = itemss.get(j);
			for (int i = 0; i < randomV.length; i++) {
				if (it[data.getClassIndex()].getValue() == data.getClasses().get(i)) {
					randomV[i] = j;
				}
			}
		}
		System.out.println("\nRandom V value indexes:: ");
		for (int it : randomV) {
			System.out.println(it + " __ ");
		}

	}

	public void doStep2() {
		// find centroid values
		for (int i = 0; i < data.getClasses().size(); i++) {
			for (int j = 0; j < data.getNoOfCell() - 1; j++) {
				double centroidValue = Helper.dec2(v[randomV[i]][j] * randomPair[j][i]);
				centroid[i][j] = centroidValue;
				centroidOld[i][j] = centroidValue; //TODO updated.
				mainView.appendLog("   randomV*membership = " + (v[randomV[i]][j]) + " * " + randomPair[j][i] + " = "+ centroidValue + ", ");
			}
		}

		mainView.appendLog("\nCentroid:");
		for (double[] c : centroid) {
			mainView.appendLog("     "+Arrays.toString(c));
		}
	}
	
	public void doStep3() {
		clusters = new double[v.length][data.getClasses().size()];
		
		//for each centroid
		for(int i=0; i<centroid.length; i++) {
//			System.out.println("\nCentroid "+i + "============>");
			// for each row ( mean each G )
			for(int r=0; r<v.length; r++) {
				double[] rValues = v[r];
				double[] iCetroid = centroid[i];
				double dValue = findEuclidianDistance(rValues, iCetroid, iCetroid.length);
//				System.out.println("D"+r +": "+ dValue);
				clusters[r][i] = Helper.dec2(dValue);
			}
		}
		
		mainView.appendLog("\nClusters::");
		for (double[] c : clusters) {
			mainView.appendLog("     "+Arrays.toString(c));
		}
	}
	
	public void doStep4() {
		membershipMetrixs = new double[v.length][data.getClasses().size()];
		//for each cluster
		for(int i=0; i<data.getClasses().size(); i++) {
//			System.out.println("\nCluster "+i + "============>");
			// for each row ( mean each G ) or //for each cluster
			for(int r=0; r<clusters.length; r++) {
				double[] rCluster = clusters[r];
				double upValue = clusters[r][i];
				double membershipMetrix = findMembershipMetrix(upValue, rCluster);
//				System.out.println("MM"+r +": "+ membershipMetrix);
				membershipMetrixs[r][i] = Helper.dec2(membershipMetrix);
			}
		}
		
		mainView.appendLog("\nMemebership Matrix::");
		for (double[] c : membershipMetrixs) {
			mainView.appendLog("    "+Arrays.toString(c));
		}
	}
	
	public void doStep5() {
		centroidNew = new double[data.getClasses().size()][data.getNoOfCell() - 1];
		
		//for each class or cluster
		for(int i=0; i<data.getClasses().size(); i++) {
			//for each column of input (Mg Na AI ...)
			for(int c=0; c<v[0].length; c++) {
				double sumInputAndMembershipSquareMultiply = 0;
				double sumMembershipSquare = 0;
				// for each row ( mean each G ) or //for each membership matrix
				for(int r=0; r<membershipMetrixs.length; r++) {
					double inputValue = v[r][c]; // input values 
					double membershipMatrix = membershipMetrixs[r][i]; // to multiply with squre value
					sumMembershipSquare += Math.pow(membershipMatrix, 2);
					sumInputAndMembershipSquareMultiply += inputValue * Math.pow(membershipMatrix, 2);
				}
				double resultedCentroid = Helper.dec2(sumInputAndMembershipSquareMultiply/sumMembershipSquare);
				System.out.println("Cluster"+ i+"-InputCol"+c+ " = " + resultedCentroid);
				centroidNew[i][c] = resultedCentroid;
			}
		}
		
		boolean same = true;
		mainView.appendLog("\nNew Centroid:");
		for (int i=0; i<centroidNew.length; i++) {
			// check equal ( is there not equal values ?)
			for(int x=0; x<centroidNew[i].length; x++) {
//				centroidNew[i][x] = centroidNew[i][x];
				if(centroidNew[i][x]!=centroidOld[i][x]) {
					same =false;
				}
			}
			
			mainView.appendLog("     "+Arrays.toString(centroidNew[i]));
		}
		mainView.appendLog("");
		mainView.appendLog("Same Centroid: "+ (same? " YES " : "NO"));
		
		if(!same && loopCount<maxLoopCount) {
			centroidOld = centroidNew.clone();
			centroid = centroidNew.clone();
			loopCount++;
			mainView.appendLog("\n\n("+loopCount+")=============Loop to Step 3 4 5 ================================>>");
			doStep3();	
			doStep4();	
			doStep5();	
		} else {
			for(int i=0; i<data.getItems().size(); i++) {
				int colIndex = -1;
				for(int cl=0; cl<data.getClasses().size(); cl++) {
					if(colIndex==-1) colIndex = cl;
					else if(membershipMetrixs[i][cl]>=membershipMetrixs[i][colIndex]) colIndex = cl;
				}
				data.getItems().get(i)[data.getClassIndex()].setValue(data.getClasses().get(colIndex));
			}
			mainView.appendLog("\n * Dataset producted by Fuzzy.");
			for(int r=0; r<data.getItems().size();r++) {
				Item[] items = data.getItems().get(r);
				mainView.appendLog("    ("+(r+1)+") "+Item.printInArray(items));
			}
		}
		
	}
	
	//=================================================
	
	public double findMembershipMetrix(double c, double[] centroids) {
		double result = 0;
		int m = 2;
		double dividers = 0;
		for(double cc : centroids) {
			dividers += 1/cc;
		}
		result = (1/c)/dividers;
		return result;
	}
	
	public double findEuclidianDistance(double[] v1, double[] v2, int length) { //v1 and v2 must be same length
		double result = 0;
		for(int i=0; i<length; i++) {
			result += Math.pow(v1[i]-v2[i], 2);
		}
		return Math.sqrt(result);
	}

	public void discoverRandomPair() {
		randomPair = new double[data.getNoOfCell() - 1][data.getClasses().size()];

		for (int i = 0; i < data.getNoOfCell() - 1; i++) {
			double total = 1;
			for (int j = 0; j < data.getClasses().size(); j++) {
				if (j < data.getClasses().size() - 1) {
					double randomValue = Helper.getRandom01() * total + 0.1;
//					randomValue = randomValue <= 0.6 ? randomValue : 0.6 - randomValue;
//					randomValue = randomValue > 0.0 ? randomValue : 0.1;
					randomValue = Math.abs(randomValue);
					randomValue = Helper.dec2(randomValue);
					total -= randomValue;
					randomPair[i][j] = randomValue;
				} else {
					randomPair[i][j] = Helper.dec2(total);
				}
			}
		}

	}
	
	// normalize data set or find V' values
		public void normalize() {
			List<Item[]> items = data.getItems();
			int noOfCell = data.getNoOfCell();
			
			for(int c=0; c<noOfCell-1; c++) {
				// find the whole column of items (to find min value and max value)
				double[] columnDataTmp = new double[items.size()];
				for(int r=0; r<items.size(); r++) {
					Item[] item = items.get(r);
					columnDataTmp[r] = item[c].getValue();
				}
				
				//Find minValue and maxValue of the column
				double minValue = columnDataTmp[ Helper.findMinIndex(columnDataTmp) ];
				double maxValue = columnDataTmp[ Helper.findMaxIndex(columnDataTmp) ];
				
				for(int r=0; r<items.size(); r++) {
					Item[] item = items.get(r);
					// Apply formula
					v[r][c] = (item[c].getValue()-minValue) / ( maxValue - minValue );
					v[r][c] = Helper.dec2(v[r][c]);
				}			
			}
		}

	//// ==================================================

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public List<Item[]>[] getSortedItems() {
		return sortedItems;
	}

	public void setSortedItems(List<Item[]>[] sortedItems) {
		this.sortedItems = sortedItems;
	}

}
