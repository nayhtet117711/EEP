package classification.theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classification.helper.Helper;
import classification.helper.Probability;
import classification.model.Data;
import classification.model.Item;
import classification.view.MainView;

public class NaiveBayes {
	private Data data;
	private double[] probabilityClass;
	private Map<Double, Integer>[] inputItemCountEach; //key=unique input value, value=occurence
	private Map<Double, Integer>[][] inputItemCountByClass; // key=noOfColumn, value=noOfClass
	private Map<Double, Double>[][] inputItemCountByClassResult; // key=noOfColumn, value=noOfClass
	
	private List<Double> predictResultClass = new ArrayList<>();
	private int[][] accuracyStore;
	private double accuracyResulted = 0;
	
	private MainView mainView;
	
	public NaiveBayes() {
		
	}

	public NaiveBayes(Data data, MainView mainView) {
		super();
		this.data = data;
		probabilityClass = new double[data.getClasses().size()];
		inputItemCountEach = new HashMap[data.getNoOfCell()];
		inputItemCountByClass = new HashMap[data.getNoOfCell()-1][data.getClasses().size()];
		inputItemCountByClassResult = new HashMap[data.getNoOfCell()-1][data.getClasses().size()];
		
		this.mainView = mainView;
		
		Helper.setupRandom(); // reset random
	}
	
	public void doTrainJob() {
		System.out.println("\ncalculateInputItemCountEach()=====================");
		calculateInputItemCountEach();
		System.out.println("\ncalculateInputCountByClass()=====================");
		calculateInputCountByClass();
		
		for(int c=0; c<data.getNoOfCell()-1; c++) {
			boolean noOccurence = foundNoOccurenceForClass(c);
			executeLaplaceOrWithout(c, noOccurence);
		}
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		mainView.appendLog("Probability--");
		for(Map<Double, Double>[] maps : inputItemCountByClassResult) {
			mainView.appendLog("    ----");
			for(int i=0; i<maps.length; i++) {
				Map<Double, Double> map = maps[i];
				System.out.println(map);
				mainView.appendLog("   "+map.toString());
			}
		}
	}
	
	public void doTestJob() {
		List<Item[]> trainItems = data.getItems();
		List<Item[]> testItems = data.getTestItems();
		
		predictResultClass.clear();
		
		for(int testI=0; testI<testItems.size(); testI++) {
			double predictResult = 0;
			double predictClass = -1;
			Item[] testItem = testItems.get(testI);
			for(int cl=0; cl<data.getClasses().size(); cl++) {
				double predictResultTmp = 1;
				double classValue = data.getClasses().get(cl);
				double[] testItemProb = new double[data.getNoOfCell()];
				for(int c=0; c<testItemProb.length-1; c++) {
					// find probability of each test input by current class
					Map<Double, Double> itemMap = inputItemCountByClassResult[c][cl];
					Map<Double, Integer> itemTotalMap = inputItemCountEach[data.getClassIndex()];
					Item item = testItem[c];
					
					System.out.println("IItem: "+ item + ", c: "+c);
					System.out.println(">>: "+itemMap.get(item.getValue()));
					
					double p = itemMap.get(item.getValue())!=null ? itemMap.get(item.getValue()) : 0;
					testItemProb[c] = p;
//					System.out.println(" = "+p);
				}System.out.println();
				// find probability of current class
				Map<Double, Integer> itemTotalMap = inputItemCountEach[data.getClassIndex()];
				double classP = (double)itemTotalMap.get(classValue) / trainItems.size();
				classP = Helper.dec2(classP);
				testItemProb[testItemProb.length-1] = classP;
				
				String outputText = "   ";
				System.out.print("For class "+ classValue +", item: ");
				outputText += "For class "+ classValue +", item: ";
				for(double p : testItemProb) {
					predictResultTmp *= p;
					System.out.print(p+", ");
					outputText += p+", ";
				}
				if(predictResultTmp>predictResult) {
					predictResult = predictResultTmp;
					predictClass = classValue;
				}
				System.out.print("Predict :\t"+ predictResultTmp +", Predict Class: "+ predictClass);
				outputText += "\tPredict :"+ predictResultTmp +", Predict Class: "+ predictClass;
				mainView.appendLog(outputText);
			}
			predictResultClass.add(predictClass);
			System.out.println("\n\ntest complete>>>>>>>>>>>>> Class: "+ testItem[data.getClassIndex()]+ ", Predict Class => "+ predictClass);
			mainView.appendLog("("+(testI+1)+") Calculation for test data "+ Arrays.toString(testItem) +" is completed. -> Class: "+ testItem[data.getClassIndex()]+ ", Predict Class => "+ predictClass+"\n");
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
	
	public static int findTruePositive(double classValue, List<Double> classes, int[][] accStore) {
		int result = 0;
		int classIndex = findIndexOfClass(classValue, classes);
		result = accStore[classIndex][classIndex];
		return result;
	}
	
	public static int findTrueNegative(double classValue, List<Double> classes, int[][] accStore) {
		int result = 0;
		for(int r=0; r<classes.size(); r++) {
			for(int c=0; c<classes.size(); c++) {
				int classIndex = findIndexOfClass(classValue, classes);
				if(r!=classIndex && c!=classIndex) {
					result += accStore[r][c];
				}
			}
		}
		return result;
	}
	
	public static int findFalsePositive(double classValue, List<Double> classes, int[][] accStore) {
		int result = 0;
		for(int r=0; r<classes.size(); r++) {
			int classIndex = findIndexOfClass(classValue, classes);
			if(r!=classIndex) {
				result += accStore[r][classIndex];
			}
		}
		return result;
	}
	
	public static int findFalseNegative(double classValue, List<Double> classes, int[][] accStore) {
		int result = 0;
		for(int c=0; c<classes.size(); c++) {
			int classIndex = findIndexOfClass(classValue, classes);
			if(c!=classIndex) {
				result += accStore[classIndex][c];
			}
		}
		return result;
	}
	
//===================================================
	
	public static int findIndexOfClass(double classValue, List<Double> classes) {
		int index = 0;
		for(int i=0; i<classes.size(); i++) {
			if(classes.get(i)==classValue) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public void executeLaplaceOrWithout(int columnIndex, boolean noOccurence) {
		List<Item[]> items = data.getItems();
		List<Double> classes = data.getClasses();
		
		for(int cl=0; cl<classes.size(); cl++) {
//			mainView.appendLog("\nCLASS: "+ classes.get(cl));
			for(int r=0; r<items.size(); r++) {
				double rowValue = items.get(r)[columnIndex].getValue();
				Map<Double, Integer> itemMap = inputItemCountByClass[columnIndex][cl];
				Map<Double, Integer> itemTotalMap = inputItemCountEach[data.getClassIndex()];
				
				double valueCount = itemMap.get(rowValue);
				double classCount = itemTotalMap.get(classes.get(cl));
				double totalUniqueValueCount = inputItemCountEach[columnIndex].size();
				
				double result = 0;
				if(noOccurence) 
					result = (valueCount+1)/(classCount+totalUniqueValueCount);
				else 
					result = valueCount/classCount;
				result = Helper.dec2(result);
				Map<Double, Double> resultMap = inputItemCountByClassResult[columnIndex][cl];
				if(resultMap==null) resultMap = new HashMap<>();
				resultMap.put(rowValue, result);
				inputItemCountByClassResult[columnIndex][cl] = resultMap;
								
//				mainView.appendLog("   "+ rowValue+" x "+ classes.get(cl) +" = "+ result);
				
			}
		}		
	}
	
	public boolean foundNoOccurenceForClass(int columnIndex) {
		boolean result = false;
		List<Item[]> items = data.getItems();
		
		for(int cl=0; cl<data.getClasses().size(); cl++) {
			for(int r=0; r<items.size(); r++) {
				double rowValue = items.get(r)[columnIndex].getValue();
				Map<Double, Integer> itemMap = inputItemCountByClass[columnIndex][cl];
				if(itemMap.get(rowValue)==null) itemMap.put(rowValue, 0);
				if(itemMap.get(rowValue)==0) {
					result = true;
				}
			}
		}		
		return result;
	}
	
	public void calculateInputItemCountEach() {
		List<Item[]> items = data.getItems();
		for(int c=0; c<inputItemCountEach.length; c++) {
			inputItemCountEach[c] = new HashMap<Double, Integer>(); 
			for(int i=0; i<items.size(); i++) {
				Item item = items.get(i)[c];
				Map<Double, Integer> map = inputItemCountEach[c];
				Integer count = map.get(item.getValue());
				if(count==null) map.put(item.getValue(), 1);
				else map.put(item.getValue(), map.get(item.getValue())+1);
			}
		}
		// inputItemCountEach
		mainView.appendLog("\nInput Class Count:");
		for(int cl=0; cl<inputItemCountEach.length; cl++) {
			Map<Double, Integer> map = inputItemCountEach[cl];
			System.out.println(map+" -> size: "+ map.size());
			if(cl==data.getClassIndex())
				mainView.appendLog("   "+map.toString());
		}
	}
	
	public void calculateInputCountByClass() {
		List<Item[]> items = data.getItems();
		for(int c=0; c<inputItemCountByClass.length; c++) {
			for(int cl=0; cl<data.getClasses().size(); cl++) {
				inputItemCountByClass[c][cl] = new HashMap<Double, Integer>(); 
				
				for(int i=0; i<items.size(); i++) {
					if(data.getClasses().get(cl)==items.get(i)[data.getClassIndex()].getValue()) {
						Item item = items.get(i)[c];
						Map<Double, Integer> map = inputItemCountByClass[c][cl];
						if(map.get(item.getValue())==null) {
							map.put(item.getValue(), 1);
						} else {
							map.put(item.getValue(), map.get(item.getValue())+1);
						}
					}
				}
			}
		}
		// inputItemCountByClass
//		mainView.appendLog("\nInput count by class.");
		for(int i=0; i<inputItemCountByClass.length; i++) {
			Map<Double, Integer>[] mapList = inputItemCountByClass[i];
			for(int c=0; c<mapList.length; c++) {
				Map<Double, Integer> map = mapList[c];
				System.out.print("("+new Double(data.getClasses().get(c)).intValue()+") ");
//				mainView.appendLog("   Class: "+ (data.getClasses().get(c)).intValue() +" "+ map );
				System.out.println(map+" -> size: "+ map.size());
			}
			System.out.println("---");
		}
	}
	
// GETTER & SETTER=================================================================================
	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public double[] getProbabilityClass() {
		return probabilityClass;
	}

	public void setProbabilityClass(double[] probabilityClass) {
		this.probabilityClass = probabilityClass;
	}
	
	
}
