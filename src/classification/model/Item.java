package classification.model;

import java.util.ArrayList;
import java.util.List;

//for cell in data 
public class Item {
	private String name;
	private double value;
	private boolean className;

	public Item() {
		super();
	}

	public Item(String name, double value, boolean className) {
		super();
		this.name = name;
		this.value = value;
		this.className = className;
	}

	public Item(String name, double value) {
		super();
		this.name = name;
		this.value = value;
		this.className = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isClassName() {
		return className;
	}

	public void setClassName(boolean className) {
		this.className = className;
	}
	
	public Item clone() {
		Item it = new Item();
		it.setClassName(className);
		it.setName(name);
		it.setValue(value);
		return it;
	}
	
	public static List<Item[]> cloneList(List<Item[]> itemList) {
		List<Item[]> itemListResulted = new ArrayList<>();
		for(Item[] items : itemList) {
			Item[] its = new Item[items.length];
			for(int i=0; i<items.length; i++) {
				its[i] = items[i].clone();
			}
			itemListResulted.add(its);
		}
		return itemListResulted;
	}

	@Override
	public String toString() {
//		return "{ "+ name + " : " + value + " }";
		return String.format("{ %s: %.2f } ", name ,value);
	}
	
//	@Override
//	public String toString() {
//		return (className==true ? new Double(value).intValue()+"" :  value+"");
//	}
	
	public static String printInArray(Item[] items) {
		String resultString = "";
		for(Item it : items) {
//			resultString += it.getValue()+", ";
			resultString += it.toString();
		}
		return resultString;
	}
	
} // end class Item
