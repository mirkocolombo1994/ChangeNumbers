import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;

public class UserInterface {

    private JFrame frame;

    private JTextArea hideCharText;
    private JRadioButton hideTrue;
    private JRadioButton hideFalse;
    private ButtonGroup hideButtons;
    private JComboBox<Integer> finalDigitsBox;
    private JButton pathFinder;
    private JButton goButton;

    private JLabel lFinalDigits;
    private JLabel lHideChar;
    private JLabel lHIde;
    private JLabel lSelectPath;
    private JLabel lPath;

    private Controller controller = Controller.getInstance();

    private final static Integer[] finalDigits = {2,3,4,5,6,7};

    public UserInterface(){
        initializeElements();
        createWindow();
        visualizeWindow();
    }

    private void initializeElements(){

        lFinalDigits = new JLabel("Digits to change: ");
        lHIde = new JLabel("Hide");
        lHideChar = new JLabel("Hiding character");
        lPath = new JLabel("Path: ");
        lSelectPath = new JLabel();


        //lSelectPath.setText("C:\\Users\\QWMQ5885\\Desktop\\New folder (2)\\CDR_20181124.tsv");
        lSelectPath.setText("C:\\Users\\QWMQ5885\\Desktop\\New folder (6)");


        hideTrue = new JRadioButton("True");
        hideTrue.setMnemonic(KeyEvent.VK_T);
        hideTrue.setActionCommand("1");
        hideFalse = new JRadioButton("False");
        hideFalse.setMnemonic(KeyEvent.VK_F);
        hideFalse.setActionCommand("0");
        hideButtons = new ButtonGroup();
        hideButtons.add(hideFalse);
        //hideButtons.add(hideTrue);
        hideFalse.setSelected(true);
        hideTrue.addActionListener(e -> hideCharText.setEnabled(true));
        hideFalse.addActionListener(e -> hideCharText.setEnabled(false));

        hideCharText = new JTextArea();

        finalDigitsBox = new JComboBox<>(finalDigits);

        pathFinder = new JButton("Find");
        pathFinder.addActionListener((e) -> chosePath());
        //TODO search for the folder finder

        goButton = new JButton("Go");
        goButton.addActionListener(e -> changeNumbers());
    }

    private void chosePath() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TSV File","tsv");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal==JFileChooser.APPROVE_OPTION){
            controller.setPath(chooser.getSelectedFiles());
        }
    }


    private void createWindow(){
        JPanel panel = new JPanel();
        JPanel digitsPanel = new JPanel();
        JPanel hidePanel = new JPanel();
        JPanel radioPanel = new JPanel();
        JPanel hideCharPanel = new JPanel();

        panel.setLayout(new GridLayout(2,3));
        digitsPanel.setLayout(new GridLayout(1,2));
        hidePanel.setLayout(new GridLayout(1,2));
        radioPanel.setLayout(new GridLayout(2,1));
        hideCharPanel.setLayout(new GridLayout(1,2));

        panel.add(lPath);
        panel.add(lSelectPath);
        panel.add(pathFinder);

        digitsPanel.add(lFinalDigits);
        digitsPanel.add(finalDigitsBox);

        radioPanel.add(hideTrue);
        radioPanel.add(hideFalse);

        hidePanel.add(lHIde);
        hidePanel.add(radioPanel);

        hideCharPanel.add(lHideChar);
        hideCharPanel.add(hideCharText);

        panel.add(digitsPanel);
        panel.add(hidePanel);
        panel.add(hideCharPanel);

        frame = new JFrame("Change Numbers");

        frame.setSize(475,175);
        frame.setResizable(false);

        frame.add(panel,BorderLayout.NORTH);
        frame.add(goButton,BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void visualizeWindow() {
        frame.setVisible(true);
    }

    private void changeNumbers() {
        JOptionPane.showMessageDialog(frame,finalDigitsBox.getSelectedItem().toString());
        JOptionPane.showMessageDialog(frame,hideButtons.getSelection().getActionCommand());
        JOptionPane.showMessageDialog(frame,hideCharText.getText());
        JOptionPane.showMessageDialog(frame,lSelectPath.getText());

        //JOptionPane.showMessageDialog(frame,controller.test(lSelectPath.getText()));

        try {
            controller.readAllFiles(lSelectPath.getText());
            //controller.readFile(lSelectPath.getText());
        } catch (NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,"No Files Selected");
        } catch (TooFewDigitsException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,"Too few digits");
        }


        //TODO call controller method after controlling all fields
    }

    public static void main (String[] args){
        new UserInterface();
    }

}
