package classification.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import classification.controller.ViewController;

public class MainView extends JFrame implements ComponentListener{
	private ViewController viewController;
	
	//Views
	private JPanel mainPanel;
	private JPanel controlPanel;
	//Components controlPanel
	private JButton loadDataSetButton;
	private JButton naiveBayesButton;
	private JButton annButton;
	private JButton fuzzyButton;
	private JButton fuzzyNaiveButton;
	private JButton fuzzyAnnButton;
	//Components mainPanel
	private JTextArea logArea;
	
	public MainView() {
		super();
	}
	
	public void initUI() {
		this.setTitle("Classification");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(new BorderLayout(2,2));
		
		setupViews();
		
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.pack();
		this.setResizable(true);
		this.setVisible(true);
	}
	
	private void setupViews() {
		mainPanel = new JPanel();
		controlPanel = new JPanel();
		
//		mainPanel.setBackground(Color.WHITE);
//		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
						
		controlPanel.setBackground(Color.LIGHT_GRAY);
		controlPanel.setPreferredSize(new Dimension(350, 400));
		controlPanel.setBorder(new EmptyBorder(40, 10, 10, 10));
		
		this.getContentPane().setBackground(Color.LIGHT_GRAY);
//		this.getContentPane().setLayout(new BorderLayout(2, 2));
		this.add(controlPanel, BorderLayout.WEST);
		this.add(mainPanel);
		this.addComponentListener(this);
		
		setupComponentMainPanel();
		setupComponentControlPanel();
	}
	
	private void setupComponentControlPanel() {
		loadDataSetButton = new JButton("Load Data Set");
		naiveBayesButton = new JButton("Naive Bayes");
		annButton = new JButton("ANN");
		fuzzyButton = new JButton("Fuzzy");
		fuzzyNaiveButton = new JButton("Naive Bayes");
		fuzzyAnnButton = new JButton("ANN");
		
		loadDataSetButton.setPreferredSize(new Dimension(300, 40));
		naiveBayesButton.setPreferredSize(new Dimension(300, 40));
		annButton.setPreferredSize(new Dimension(300, 40));
		fuzzyButton.setPreferredSize(new Dimension(300, 40));
		fuzzyNaiveButton.setPreferredSize(new Dimension(300, 40));
		fuzzyAnnButton.setPreferredSize(new Dimension(300, 40));
		
		loadDataSetButton.addActionListener(viewController.actionLoadDataset());
		naiveBayesButton.addActionListener(viewController.actionNaiveBayes());
		annButton.addActionListener(viewController.actionANN());
		fuzzyButton.addActionListener(viewController.actionFuzzy());
		fuzzyNaiveButton.addActionListener(viewController.actionNaiveBayes());
		fuzzyAnnButton.addActionListener(viewController.actionNaiveBayes());
		
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 20));
				
		JLabel controlPanelTitle = new JLabel("FUNCTIONS");
		controlPanelTitle.setFont(new Font("", Font.BOLD, 20));
		controlPanelTitle.setForeground(Color.DARK_GRAY);
		
		JPanel gapPanel1 = new JPanel();
		gapPanel1.setPreferredSize(new Dimension(300, 10));
		gapPanel1.setBackground(Color.LIGHT_GRAY);
		
		JPanel gapPanel2 = new JPanel();
		gapPanel2.setPreferredSize(new Dimension(300, 10));
		gapPanel2.setBackground(Color.LIGHT_GRAY);
		
		controlPanel.add(controlPanelTitle);
		
		controlPanel.add(gapPanel1);
		controlPanel.add(loadDataSetButton);
		controlPanel.add(naiveBayesButton);
		controlPanel.add(annButton);
		
		controlPanel.add(gapPanel2);
		controlPanel.add(fuzzyButton);
		controlPanel.add(fuzzyNaiveButton);
		controlPanel.add(fuzzyAnnButton);
		controlPanel.revalidate();
		
	}
	
	public void setupComponentMainPanel() {
		logArea = new JTextArea("");
		logArea.setBackground(Color.WHITE);
		logArea.setDisabledTextColor(Color.BLACK);
//		logArea.setEnabled(false);
		logArea.setEditable(false);
		
		JScrollPane logScroll = new JScrollPane(logArea);
		logArea.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		JButton clearButton = new JButton(" Clear Logs ");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearLog();
			}
		});
		JPanel mainTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		mainTopPanel.setBorder(new EmptyBorder(10, 30, 10, 10));
		mainTopPanel.add(clearButton);
		
		mainPanel.setLayout(new BorderLayout(5, 5));
		mainPanel.add(logScroll, BorderLayout.CENTER);
		mainPanel.add(mainTopPanel, BorderLayout.NORTH);
		mainPanel.validate();
	}
	
	public void setViewController(ViewController viewController) {
		this.viewController = viewController;
	}
	
	public void appendLog(String text) {
		if(logArea!=null)
			logArea.append("\n"+text);
	}
	
	public void clearLog() {
		logArea.setText("");
	}

	public void showInfoAlert(String text) {
		JOptionPane.showMessageDialog(null, text, null, JOptionPane.INFORMATION_MESSAGE);
	}
	
	//////////////////////////////////////////////////
	@Override
	public void componentHidden(ComponentEvent e) {
		
	}
	@Override
	public void componentMoved(ComponentEvent e) {
	
	}
	@Override
	public void componentResized(ComponentEvent e) {

	}
	@Override
	public void componentShown(ComponentEvent e) {
	
	}

}
