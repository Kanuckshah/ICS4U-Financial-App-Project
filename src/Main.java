import javax.swing.SwingUtilities;

/**
 * Entry point for the Student Finance Tracker application.
 * Initializes the GUI on the Event Dispatch Thread.
 */
public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        FinanceGUI gui = new FinanceGUI();
        gui.display();
      }
    });
  }
}
