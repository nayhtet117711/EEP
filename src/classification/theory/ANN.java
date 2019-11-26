package classification.theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classification.helper.Helper;
import classification.model.Data;
import classification.model.Item;
import classification.view.MainView;

public class ANN {
	private Data data;
	private double[][] v;
	private double[][] hiddenWeights; 	// [weight count][input count]
	private double[][] classWeights;	// [weight count][class count]
	private double[] hiddenIvalues;
	private double[] hiddenOvalues;
	private double[] classIvalues;
	private double[] classOvalues;
	private double[] hidden0values;
	private double[] class0values;
	
	private double[] classEvalues;
	private double[] hiddenEvalues;
	
	private List<Double> predictResultClass = new ArrayList<>();
	private int[][] accuracyStore;
	private double accuracyResulted = 0;
	
	private MainView mainView;

	
	public ANN() { }
	
	public ANN(Data data, MainView mainView) {
		super();
		this.data = data;
		this.mainView = mainView;
		
		Helper.setupRandom(); // reset random
	}
	
	public void doTrainJob() {
		List<Item[]> items = data.getItems();
		List<Item[]> testItems = data.getTestItems();
		List<Double> classes = data.getClasses();
		int noOfCell = data.getNoOfCell();
		
		doStep1();
		
;		for(int itemIndex=0; itemIndex<items.size(); itemIndex++) {
			
			mainView.appendLog("\n("+(itemIndex+1)+") For Train Item "+Item.printInArray(items.get(itemIndex))); 
			
			System.out.println("\n("+(itemIndex+1)+") For Train Item "+Item.printInArray(items.get(itemIndex)));
			System.out.println("\nI and O values:");
			
			// find I values and O values of hidden layer
			for(int r=0; r<noOfCell-1; r++) {
				for(int c=0; c<noOfCell-1; c++) {
					hiddenIvalues[r] += v[itemIndex][c] * hiddenWeights[r][c];
				}
				hiddenIvalues[r] += hidden0values[r];			
				hiddenIvalues[r] = Helper.dec2(hiddenIvalues[r]);
				
				hiddenOvalues[r] = 1.0 / ( 1 + Math.pow( Math.E, hiddenIvalues[r]*-1 ) );
				hiddenOvalues[r] = Helper.dec2(hiddenOvalues[r]);
			}
			System.out.println("hidden I: "+ Arrays.toString(hiddenIvalues));
			System.out.println("hidden O: "+ Arrays.toString(hiddenOvalues));
			
			// find I values and O values
			for(int cl=0; cl<classes.size(); cl++) {
				for(int r=0; r<noOfCell-1; r++) {
					classIvalues[cl] += hiddenOvalues[r] * classWeights[r][cl];
				}
				classIvalues[cl] += class0values[cl];
				classIvalues[cl] = Helper.dec2(classIvalues[cl]);
							
				classOvalues[cl] = 1.0 / ( 1 + Math.pow( Math.E, classIvalues[cl]*-1 ) );
				classOvalues[cl] = Helper.dec2(classOvalues[cl]);
			}
		
			System.out.println("class I: "+ Arrays.toString(classIvalues));
			System.out.println("class O: "+ Arrays.toString(classOvalues));
			
			System.out.println("\nError for class and hidden layer:");
			
			// Find errors for class Layer
			classEvalues = new double[classOvalues.length];
			hiddenEvalues = new double[hiddenOvalues.length];
			
			for(int i=0; i<classEvalues.length; i++) {
				classEvalues[i] = classOvalues[i] * ( 1 - classOvalues[i] ) * ( classes.get(i) - classOvalues[i] ); // Err = Oj*( 1-Oj)(Tj - Oj) // Tj=0
				classEvalues[i] = Helper.dec2(classEvalues[i]);
			}
			// Find errors for hidden Layer
			for(int i=0; i<hiddenEvalues.length; i++) {
				double summation = 0;
				for(int w=0; w<classEvalues.length; w++) {
					summation += classEvalues[w] * classWeights[i][w];
				}
				hiddenEvalues[i] = hiddenOvalues[i] * ( 1 - hiddenOvalues[i] ) * summation; // Err = Oj*( 1-Oj) Summation
				hiddenEvalues[i] = Helper.dec2(hiddenEvalues[i]);
			}
			System.out.println("class Err: "+ Arrays.toString(classEvalues));
			System.out.println("hidden Err: "+ Arrays.toString(hiddenEvalues));
			
			System.out.println("\nNew Weight values:");
			// calculate weight values from Err and O
			mainView.appendLog("\nErrors and new Weight first Layer: (right-left)");
			for(int cl=0; cl<classes.size(); cl++) {
				for(int c=0; c<noOfCell-1; c++) {
					double l = 0.5;
					double Err = classEvalues[cl];
					double O = hiddenOvalues[c];
					double deltaWeight = l * Err * O;
					deltaWeight = Helper.dec2(deltaWeight);
					double newWeight = classWeights[c][cl] * deltaWeight;
					newWeight = Helper.dec2(newWeight);
					classWeights[c][cl] = newWeight;
					mainView.appendLog("     W["+c+","+cl+"] =\t l * Err["+cl+"] * O["+c+"] = "+l+" * "+ Err + " * " + O + "\t = "+ deltaWeight +"\t\t | "+ newWeight);	
				}
			}
			System.out.println();
			mainView.appendLog("\nErrors and new Weight Hidden Layer:");
			for(int cl=0; cl<noOfCell-1; cl++) {
				for(int c=0; c<noOfCell-1; c++) {
					double l = 0.5;
					double Err = hiddenEvalues[cl];
					double O = v[itemIndex][c]; //hiddenOvalues[c];
					double deltaWeight = l * Err * O;
					deltaWeight = Helper.dec2(deltaWeight);
					double newWeight = hiddenWeights[c][cl] * deltaWeight;
					newWeight = Helper.dec2(newWeight);
					hiddenWeights[c][cl] = newWeight;
					mainView.appendLog("     W["+c+","+cl+"] =\t l * Err["+cl+"] * O["+c+"] = "+l+" * "+ Err + " * " + O + "\t = "+ deltaWeight +"\t\t | "+ newWeight);	
				}
			}
		}
		
	}
	
	public void doTestJob() {
		List<Item[]> items = data.getItems();
		List<Item[]> testitems = data.getTestItems();
		List<Double> classes = data.getClasses();
		int noOfCell = data.getNoOfCell();
		
		double[][] vv = normalizeTestItems();
		
		for(int itemIndex=0; itemIndex<vv.length; itemIndex++) {
			mainView.appendLog("\n("+(itemIndex+1)+") For Test Item "+Item.printInArray(items.get(itemIndex))); 
			
			System.out.println("\nI and O values:");
			
			// find I values and O values of hidden layer
			for(int r=0; r<noOfCell-1; r++) {
				for(int c=0; c<noOfCell-1; c++) {
					hiddenIvalues[r] += vv[itemIndex][c] * hiddenWeights[r][c];
				}
				hiddenIvalues[r] += hidden0values[r];			
				hiddenIvalues[r] = Helper.dec2(hiddenIvalues[r]);
				
				hiddenOvalues[r] = 1.0 / ( 1 + Math.pow( Math.E, hiddenIvalues[r]*-1 ) );
				hiddenOvalues[r] = Helper.dec2(hiddenOvalues[r]);
			}
			System.out.println("hidden I: "+ Arrays.toString(hiddenIvalues));
			mainView.appendLog("      hidden O: "+ Arrays.toString(hiddenOvalues));
			
			// find I values and O values
			for(int cl=0; cl<classes.size(); cl++) {
				for(int r=0; r<noOfCell-1; r++) {
					classIvalues[cl] += hiddenOvalues[r] * classWeights[r][cl];
				}
				classIvalues[cl] += class0values[cl];
				classIvalues[cl] = Helper.dec2(classIvalues[cl]);
							
				classOvalues[cl] = 1.0 / ( 1 + Math.pow( Math.E, classIvalues[cl]*-1 ) );
				classOvalues[cl] = Helper.dec2(classOvalues[cl]);
			}
		
			System.out.println("     class I: "+ Arrays.toString(classIvalues));
			mainView.appendLog("     class O: "+ Arrays.toString(classOvalues));
			
			int predictClassIndex = -1;
			for(int i=0; i<classOvalues.length; i++) {
				if(predictClassIndex<0) predictClassIndex = i;
				else if(classOvalues[i]>classOvalues[predictClassIndex]) predictClassIndex = i;
			}
			predictResultClass.add(data.getClasses().get(predictClassIndex));
			mainView.appendLog("Predict Class: "+ data.getClasses().get(predictClassIndex));
		}

	}
	
	public void doAccuracy() {
		List<Item[]> trainItems = data.getItems();
		List<Item[]> testItems = data.getTestItems();
		List<Double> classes = data.getClasses();
		
		// initialize and add data to accuracy store
		accuracyStore = new int[classes.size()][classes.size()];
		for(int i=0; i<testItems.size(); i++) {
			Item[] testItem = testItems.get(i);
			int testClassIndex = NaiveBayes.findIndexOfClass(testItem[data.getClassIndex()].getValue(), data.getClasses());
			int predictClassIndex = NaiveBayes.findIndexOfClass(predictResultClass.get(i), data.getClasses());
			accuracyStore[testClassIndex][predictClassIndex]++;
		}
			
		for(int r=0; r<classes.size(); r++) {
			System.out.println(Arrays.toString(accuracyStore[r]));
			mainView.appendLog("   " +Arrays.toString(accuracyStore[r]));
			for(int c=0; c<classes.size(); c++) {}
		}
		
		double overallAcc = 0;
		
		for(int i=0; i<classes.size(); i++) {
			int TP = NaiveBayes.findTruePositive(classes.get(i), classes, accuracyStore);
			int TN = NaiveBayes.findTrueNegative(classes.get(i), classes, accuracyStore);
			int FP = NaiveBayes.findFalsePositive(classes.get(i), classes, accuracyStore);
			int FN = NaiveBayes.findFalseNegative(classes.get(i), classes, accuracyStore);
			accuracyResulted = (double)(TP+TN)/(TP+TN+FP+FN);
			if((TP+TN+FP+FN)==0) 
				accuracyResulted = 0;
			System.out.println("   Accuracy for class "+ classes.get(i) + " = "+ accuracyResulted + "  ( TP="+ TP + ", TN=" + TN + ", FP=" + FP + ", FN=" + FN + " )");
			mainView.appendLog("   Accuracy for class "+ classes.get(i) + " = "+ accuracyResulted + "  ( TP="+ TP + ", TN=" + TN + ", FP=" + FP + ", FN=" + FN + " )");
		
			overallAcc += accuracyResulted;
		}
		// find overall acc
		overallAcc = overallAcc/data.getClasses().size();
		mainView.appendLog("\n   Overall Accuracy: "+ (int)(overallAcc*100) +" %"); 
	}
	
	private void doStep1() {
		List<Item[]> items = data.getItems();
		List<Double> classes = data.getClasses();
		int noOfCell = data.getNoOfCell();
		
		// initialize data
		v = new double[items.size()][noOfCell-1];
		hiddenWeights = new double[noOfCell-1][noOfCell-1];
		classWeights = new double[noOfCell-1][classes.size()];
		hiddenIvalues = new double[noOfCell-1];
		hiddenOvalues = new double[noOfCell-1];
		classIvalues = new double[classes.size()];
		classOvalues = new double[classes.size()];
		hidden0values = new double[noOfCell-1];
		class0values = new double[classes.size()];
		
		// add random values -0.5 to 0.5
		for(int r=0; r<noOfCell-1; r++) 
			for(int c=0; c<noOfCell-1; c++) 
				hiddenWeights[r][c] = Helper.getRandom05();
		for(int r=0; r<noOfCell-1; r++) 
			for(int c=0; c<classes.size(); c++) 
				classWeights[r][c] = Helper.getRandom05();
		for(int i=0; i<hidden0values.length; i++) 
			hidden0values[i] = Helper.getRandom05();
		for(int i=0; i<class0values.length; i++) 
			class0values[i] = Helper.getRandom05();
		
		normalize();
		mainView.appendLog("\nNormalize values:");
		for(double[] vi : v) 
			mainView.appendLog("     "+Arrays.toString(vi));
	
		mainView.appendLog("\nRandom Weight and Theda Values: ");
		mainView.appendLog("    Hidden Weight: \t"+Arrays.deepToString(hiddenWeights));
		mainView.appendLog("    Hidden Theta:  \t"+Arrays.toString(hidden0values));
		mainView.appendLog("    Class Weight:  \t"+Arrays.deepToString(classWeights));
		mainView.appendLog("    Class Theta:   \t"+Arrays.toString(class0values));
		
	}
	
	//========================================================================================
	
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
	
	// normalize data set or find V' values
	public double[][] normalizeTestItems() {
		List<Item[]> items = data.getTestItems();
		int noOfCell = data.getNoOfCell();
		
		double[][] vv = new double[items.size()][data.getNoOfCell()-1];
		
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
				vv[r][c] = (item[c].getValue()-minValue) / ( maxValue - minValue );
				vv[r][c] = Helper.dec2(vv[r][c]);
			}			
		}
		return vv;
	}
	
}
