package cdr.userInterface;

import cdr.Controller;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class UserInterface implements Runnable{



    private JFrame frame;

    private JTextArea hideCharText;
    private JRadioButton hideTrue;
    private JRadioButton hideFalse;
    private JComboBox<Integer> finalDigitsBox;
    private JButton pathFinder;
    private JButton dataBaseFinder;
    private JButton goButton;

    private JLabel lFinalDigits;
    private JLabel lHideChar;
    private JLabel lHIde;
    private JLabel lPath;

    private Controller controller = Controller.getInstance();

    private final static Integer[] finalDigits = {3,4};

    public UserInterface(){
        initializeElements();
        createWindow();
    }

    private void initializeElements(){

        lFinalDigits = new JLabel("Digits to change: ");
        lHIde = new JLabel("Hide");
        lHideChar = new JLabel("Hiding character");
        lPath = new JLabel("Path of the files: ");

        hideTrue = new JRadioButton("True");
        hideTrue.setMnemonic(KeyEvent.VK_T);
        hideTrue.addActionListener(e -> controller.setHideFinaldigits(true));
        //hideTrue.setActionCommand("1");
        hideFalse = new JRadioButton("False");
        hideFalse.setMnemonic(KeyEvent.VK_F);
        hideFalse.addActionListener(e -> controller.setHideFinaldigits(false));
        //hideFalse.setActionCommand("0");
        ButtonGroup hideButtons = new ButtonGroup();
        hideButtons.add(hideFalse);
        hideButtons.add(hideTrue);
        hideFalse.setSelected(true);
        hideTrue.addActionListener(e -> hideCharText.setEnabled(true));
        hideFalse.addActionListener(e -> hideCharText.setEnabled(false));

        hideCharText = new JTextArea();

        finalDigitsBox = new JComboBox<>(finalDigits);
        finalDigitsBox.addActionListener(e -> controller.setFinalDigitsToChange(finalDigitsBox.getItemAt(finalDigitsBox.getSelectedIndex())));

        pathFinder = new JButton("Find");
        pathFinder.addActionListener((e) -> choseFiles());

        dataBaseFinder = new JButton("Find Database");
        dataBaseFinder.addActionListener(e -> databasePath());

        goButton = new JButton("Go");
        goButton.addActionListener(e -> changeNumbers());
    }

    private void databasePath() {
    }

    private void choseFiles() {
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
        //panel.add(dataBaseFinder);
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
        //panel.add(hideCharPanel);

        frame = new JFrame("Change Numbers");

        frame.setSize(475,175);
        frame.setResizable(false);

        frame.add(panel,BorderLayout.NORTH);
        frame.add(goButton,BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void visualizeWindow() {
        frame.setVisible(true);
    }

    private void changeNumbers() {
        JOptionPane.showMessageDialog(frame,Objects.requireNonNull(finalDigitsBox.getSelectedItem()).toString());
        //JOptionPane.showMessageDialog(frame,hideButtons.getSelection().getActionCommand());
        //JOptionPane.showMessageDialog(frame,hideCharText.getText());
        //JOptionPane.showMessageDialog(frame,lSelectPath.getText());

        //JOptionPane.showMessageDialog(frame,controller.test(lSelectPath.getText()));

        Thread control = new Thread(controller);
        control.setPriority(Thread.MAX_PRIORITY);
        try {
            control.run();
            //controller.readAllFiles();
        } catch (NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,"No Files Selected");
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        visualizeWindow();
    }
}
