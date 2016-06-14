import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Navigator {
    private JTextField textField1;
    private JTree tree1;

    private JEditorPane editorPane1;
    private JPanel MainPanel;
    private JToolBar Drives;

    public static void showAsMain() {
        JFrame frame = new JFrame("Navigator");
        frame.setContentPane(new Navigator().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        String[] drives = { "C:", "D:", "E:"};

        Drives = new JToolBar();
        for (String drv: drives) {
            JButton drvB = new JButton(drv);
            drvB.setMinimumSize(new Dimension(50, 20));
            drvB.addActionListener(e -> driveClicked(drv));
            Drives.add(drvB);
        }
    }

    private void driveClicked(String drv) {
        System.out.println(drv);
    }

}
