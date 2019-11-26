package classification;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import classification.controller.ViewController;
import classification.view.MainView;

public class EEPMain {
	private MainView mainView;
	private ViewController viewController;
	
	public EEPMain() {
		mainView = new MainView();
		viewController = new ViewController(mainView);
		mainView.setViewController(viewController);
		
		viewController.startApp();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
//					for(LookAndFeelInfo l : UIManager.getInstalledLookAndFeels()) System.out.println(l.getClassName());
//					UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//					UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				EEPMain eepMain = new EEPMain();
				eepMain.viewController.startApp();
			}
		});
	}

}
