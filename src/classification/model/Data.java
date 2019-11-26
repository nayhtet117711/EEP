package classification.model;

import java.util.ArrayList;
import java.util.List;

import classification.helper.Helper;

public class Data {
	private int noOfCell;
	private int classIndex;
	private List<Item[]> items;
	private List<Item[]> testItems;
	private List<Item[]> itemsOriginal;
	private List<Double> classes = new ArrayList<>();
	
	public Data() {
		// TODO Auto-generated constructor stub
	}
	
	public Data(int noOfCell, int classIndex) {
		super();
		this.noOfCell = noOfCell;
		this.classIndex = classIndex;
	}

	public Data(int noOfCell, int classIndex, List<Item[]> items) {
		super();
		this.noOfCell = noOfCell;
		this.classIndex = classIndex;
		this.items = items;
	}

	public int getNoOfCell() {
		return noOfCell;
	}

	public void setNoOfCell(int noOfCell) {
		this.noOfCell = noOfCell;
	}

	public List<Item[]> getItems() {
		return items;
	}

	public void setItems(List<Item[]> items) {
		this.items = items;
	}
	
	public List<Double> getClasses() {
		return classes;
	}

	public void setClasses(List<Double> classes) {
		this.classes = classes;
	}

	public int getClassIndex() {
		return classIndex;
	}

	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

	public List<Item[]> getItemsOriginal() {
		return itemsOriginal;
	}
	
	

	public List<Item[]> getTestItems() {
		return testItems;
	}

	public void setTestItems(List<Item[]> testItems) {
		this.testItems = testItems;
	}

	public void setItemsOriginal(List<Item[]> itemsOriginal) {
		this.itemsOriginal = new ArrayList<>();
		for(Item[] its : itemsOriginal) {
			Item[] itsNew = new Item[its.length];
			for(int i=0; i<itsNew.length; i++) 
				itsNew[i] = its[i].clone();
			this.itemsOriginal.add(itsNew);
		}
	}

	@Override
	public String toString() {
		String stringFormated = "Data {\n";
		for(Item[] its : items) {
			stringFormated += String.format("%2s", "");
			for(int i=0; i<its.length; i++) {
				Item it = its[i];
				stringFormated += String.format("%-10s", it + (i<its.length-1 ? ", " : ""));
			}
			stringFormated += "\n";
		}
		stringFormated += "}\n";
		return stringFormated;
	}	
	
	public String toTestString() {
		String stringFormated = "Data {\n";
		for(Item[] its : testItems) {
			stringFormated += String.format("%2s", "");
			for(int i=0; i<its.length; i++) {
				Item it = its[i];
				stringFormated += String.format("%-10s", it + (i<its.length-1 ? ", " : ""));
			}
			stringFormated += "\n";
		}
		stringFormated += "}\n";
		return stringFormated;
	}	
	
	public String toStringTest() {
		String stringFormated = "DataTest {\n";
		for(Item[] its : itemsOriginal) {
			stringFormated += String.format("%2s", "");
			for(int i=0; i<its.length; i++) {
				Item it = its[i];
				stringFormated += String.format("%-10s", it + (i<its.length-1 ? ", " : ""));
			}
			stringFormated += "\n";
		}
		stringFormated += "}\n";
		return stringFormated;
	}
	
	public String toStringTest2() {
		return "Data {noOfCell: " + noOfCell + ", classIndex: " + classIndex + ", items: " + items + ", itemsOriginal: "
				+ itemsOriginal + ", classes: " + classes + "}";
	}

	public Data clone() {
		Data data = new Data();
//		data.setClasses(classes);
		data.setClassIndex(classIndex);
		data.setItems(items);
		data.setNoOfCell(noOfCell);
		data.setClasses(classes);
		return data;
	}
	
	public void discoverClasses() {
		for(Item[] its : items) {
			double cit = its[noOfCell-1].getValue();
			if(Helper.findDuplicateDouble(cit, classes)==-1) 
				classes.add(cit);
		}
	}
	
//	==================================================
	
	public static List<List<Item[]>> divide70And30(List<Item[]> items) {
		List<List<Item[]>> result = new ArrayList<>();
		result.add(new ArrayList<>());
		result.add(new ArrayList<>());
		
		int index70 = new Double((0.7 * items.size())+0.5).intValue();
		
		for(int i=0; i<items.size(); i++) {
			if(i<index70) {
				result.get(0).add(items.get(i));
			} else {
				result.get(1).add(items.get(i));
			}
		}
		return result;
	}
	
}
