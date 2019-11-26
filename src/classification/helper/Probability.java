package classification.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classification.model.Data;
import classification.model.Item;

public class Probability {
	private Data data;
	
	public Probability() {
		
	}

	public Probability(Data items) {
		super();
		this.data = items;
	}

	public Data getItems() {
		return data;
	}

	public void setItems(Data items) {
		this.data = items;
	}
	
	// P(class)
	public List<Object> PClass() {
		double[] p = new double[data.getClasses().size()];
		
		List<Item[]> items = data.getItems();
		int classIndex = data.getClassIndex();
		double totalCount = data.getItems().size();
		int[] countEachClass = new int[data.getClasses().size()];
		
		//find count of each class in data
		for(int i=0; i<countEachClass.length; i++) {
			countEachClass[i] = countOfEvent(classIndex, data.getClasses().get(i), items);
			int count =countEachClass[i];
			p[i] = (double)count/totalCount;
		}
		
		return Arrays.asList( countEachClass, p );
	}
	
	// P(X | class)
	public List<Object> PX(int rowIndex, double classValue, int classTotalCount, double classProb) {
		double[] p = new double[data.getNoOfCell()];
		
		List<Item[]> items = new ArrayList<>();
		int classIndex = data.getClassIndex();
		double totalCount = classTotalCount;
		int[] countEachClass = new int[data.getNoOfCell()-1];
		
		// filter items only of class value
		for(Item[] its : data.getItems()) {
			if(its[classIndex].getValue()==classValue) 
				items.add(its);
		}
		
		//find count of each class in data
		for(int i=0; i<countEachClass.length; i++) {
			Item[] itemI = data.getItems().get(rowIndex);
			countEachClass[i] = countOfEvent(i, itemI[i].getValue(), items);
			int count =countEachClass[i];
			p[i] = (double)count/totalCount;
//			// test
//			if(i==1 && rowIndex==1) {
//				System.out.println("\n "+ classValue);
//				System.out.println(i+", "+ itemI[i].getValue());
//				System.out.println("count: "+ Arrays.toString(countEachClass));
//				System.out.println("prob: "+Arrays.toString(p));
//			}
		}
		p[p.length-1] = classProb;
		
		System.out.println(">-----------| C"+ classValue+" | "+Arrays.toString(countEachClass)+" |-------| "+ Arrays.toString(p));

		return Arrays.asList( countEachClass, p );
	}
	
	private int countOfEvent(int index, double value, List<Item[]> items) {
		int count = 0;
		for(int i=0; i<items.size(); i++) {
			double valueI = items.get(i)[index].getValue();
			if(valueI==value) {
				count++;
			}
		}
		return count;
	}
	
}
