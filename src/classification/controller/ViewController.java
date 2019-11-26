package classification.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import classification.model.Data;
import classification.model.Item;
import classification.theory.ANN;
import classification.theory.Fuzzy;
import classification.theory.NaiveBayes;
import classification.view.MainView;

public class ViewController {
	private MainView mainView;
	private List<Item[]> items = new ArrayList<>();
	private String[] columnNames = null;
	
	public final String LAST_DIR = "";

	public ViewController(MainView mainView) {
		this.mainView = mainView;
	}

	public void startApp() {
		mainView.initUI();
	}

	// ===================================================

	public ActionListener actionLoadDataset() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				items.clear();

				JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
//				JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Dataset-CSV", "csv");
				fileChooser.setFileFilter(filter);
				
				int returnValue = fileChooser.showOpenDialog(mainView);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
//					System.out.println(selectedFile.getAbsolutePath());
					
					String[] columns = null;
					try {
						Reader in = new FileReader(selectedFile.getAbsoluteFile());
						Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

						for (CSVRecord record : records) {
							long rowNo = record.getRecordNumber();
							long noColumn = record.size();
							if (columns == null) {
								columns = new String[(int) noColumn];
								columnNames = new String[(int) noColumn];
							}
							Item[] itemArray = new Item[record.size()];
							for (int i = 0; i < noColumn; i++) {
								if (rowNo == 1) {
									columns[i] = record.get(i);
									columnNames[i] = record.get(i);
								} else
									itemArray[i] = new Item(columns[i], Double.parseDouble(record.get(i)));
							}
							if (rowNo > 1)
								items.add(itemArray);
						}

					} catch (Exception ee) {
						ee.printStackTrace();
					}
					for (Item[] item : items)
						System.out.println(Arrays.deepToString(item));

					mainView.appendLog("Loaded dataset." + items.size() + " Rows.");
					
				}// end file chooser jobs
				else {
					
				}			

			} 
		};
	}

	public ActionListener actionNaiveBayes() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(columnNames==null) {
					mainView.showInfoAlert("There is no dataset file loaded! Please load a file.");
					return;
				}
				List<List<Item[]>> itemsDivided = Data.divide70And30(items);
				mainView.appendLog("\nTrain: " + itemsDivided.get(0).size() + " rows, Test: "
						+ itemsDivided.get(1).size() + " rows.");

				Data data1 = new Data(columnNames.length, columnNames.length-1, itemsDivided.get(0));
				data1.setTestItems(itemsDivided.get(1));
				data1.discoverClasses();

				mainView.appendLog("Train " + data1.toString());
				mainView.appendLog("Test " + data1.toTestString());
				mainView.appendLog("Classes " + data1.getClasses());

				mainView.appendLog("\nNaive Bayes Classification");
				NaiveBayes naiveBayes = new NaiveBayes(data1, mainView);
				mainView.appendLog("=============================================\nFor Train Data,");
				naiveBayes.doTrainJob();
				mainView.appendLog("=============================================\nFor Test Data,");
				naiveBayes.doTestJob();
				mainView.appendLog("=============================================\nFor Accuracy,");
				naiveBayes.doAccuracy();
				mainView.appendLog("\n<---------------------- Done Naive Bayes -----------------------"
						+ "---------------------------------------------------------------------------"
						+ "--------------------------------------------------------------------------->\n\n\n");
			}
		};
	}

	public ActionListener actionANN() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(columnNames==null) {
					mainView.showInfoAlert("There is no dataset file loaded! Please load a file.");
					return;
				}
				List<List<Item[]>> itemsDivided = Data.divide70And30(items);
				mainView.appendLog("\nTrain: " + itemsDivided.get(0).size() + " rows, Test: "
						+ itemsDivided.get(1).size() + " rows.");

				Data data1 = new Data(columnNames.length, columnNames.length-1, itemsDivided.get(0));
				data1.setTestItems(itemsDivided.get(1));
				data1.discoverClasses();

				mainView.appendLog("Train " + data1.toString());
				mainView.appendLog("Test " + data1.toTestString());
				mainView.appendLog("Classes " + data1.getClasses());

				mainView.appendLog("\nANN Classification");
				ANN ann = new ANN(data1, mainView);
				mainView.appendLog("=============================================\nFor Train Data,");
				ann.doTrainJob();
				mainView.appendLog("=============================================\nFor Test Data,");
				ann.doTestJob();
				mainView.appendLog("=============================================\nFor Accuracy,");
				ann.doAccuracy();

				mainView.appendLog("\n<---------------------- Done ANN  ---------------------------------"
						+ "------------------------------------------------------------------------------"
						+ "------------------------------------------------------------------------------>\n\n\n");
			}
		};
	}

	public ActionListener actionFuzzy() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(columnNames==null) {
					mainView.showInfoAlert("There is no dataset file loaded! Please load a file.");
					return;
				}

				Data data1 = new Data(columnNames.length, columnNames.length-1, items);
				data1.discoverClasses();

				mainView.appendLog("Input " + data1.toString());
				mainView.appendLog("Classes " + data1.getClasses());

				mainView.appendLog("\nFuzzy Classification");

				Fuzzy fuzzy = new Fuzzy(data1, mainView);

				mainView.appendLog("\nSTEP-1 ======================");
				fuzzy.doStep1();
				mainView.appendLog("\nSTEP-2 ======================");
				fuzzy.doStep2();
				mainView.appendLog("\nSTEP-3 ======================");
				fuzzy.doStep3();
				mainView.appendLog("\nSTEP-4 ======================");
				fuzzy.doStep4();
				mainView.appendLog("\nSTEP-5 ======================");
				fuzzy.doStep5();

				mainView.appendLog("\n<---------------------- Done Fuzzy  ---------------------------------"
						+ "------------------------------------------------------------------------------"
						+ "------------------------------------------------------------------------------>\n\n\n");

			}
		};
	}

	public ActionListener actionFuzzyNaiveBayes() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(columnNames==null) {
					mainView.showInfoAlert("There is no dataset file loaded! Please load a file.");
					return;
				}
				mainView.appendLog("Done Fuzzy Naive Bayes.");
			}
		};
	}

	public ActionListener actionFuzzyANN() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(columnNames==null) {
					mainView.showInfoAlert("There is no dataset file loaded! Please load a file.");
					return;
				}
				mainView.appendLog("Done Fuzzy ANN.");
			}
		};
	}

	/*
	 * TESTED public static void main(String[] args) { List<Item[]> items = new
	 * ArrayList<>(); String[] columns = null; try { Reader in = new
	 * FileReader("/home/nayhtet/Downloads/Liver.csv"); Iterable<CSVRecord> records
	 * = CSVFormat.EXCEL.parse(in);
	 * 
	 * for (CSVRecord record : records) { long rowNo = record.getRecordNumber();
	 * long noColumn = record.size(); if(columns==null) columns = new
	 * String[(int)noColumn]; Item[] itemArray = new Item[record.size()]; for(int
	 * i=0; i<noColumn; i++) { if(rowNo==1) { columns[i] = record.get(i); } else {
	 * itemArray[i] = new Item(columns[i], Double.parseDouble(record.get(i))); } }
	 * if(rowNo>1) items.add(itemArray); }
	 * 
	 * }catch(Exception ee) { ee.printStackTrace(); } for(Item[] item : items)
	 * System.out.println(Arrays.deepToString(item)); }
	 */

}
