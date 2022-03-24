package pl.edu.pwr.gui;

import pl.edu.pwr.filechecker.FileChecker;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class GUI {
    private JList<Object> fileList;
    private JButton selectButton;
    private JButton runCheckButton;
    private JPanel panelMain;
    private JLabel selectedPathLabel;
    private JList<Object> editedFilesList;
    private JLabel editLabel;
    private JLabel filesLabel;
    private String currentPath = "";

    public GUI() {
        selectButton.addActionListener(e -> {
            chooseDirectory();
            addFileNames();
        });

        runCheckButton.addActionListener(e -> {
            if (!currentPath.equals("")) {
                processChecksums();
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "You need to choose directory!");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("File checker");
        frame.setContentPane(new GUI().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void chooseDirectory() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.showSaveDialog(null);
            currentPath = fileChooser.getSelectedFile().toString();
            selectedPathLabel.setText("Selected path: " + currentPath);
        } catch (NullPointerException exp) {
            JOptionPane.showMessageDialog(new JFrame(), "You need to choose directory!");
        }
    }

    private void addFileNames() {
        File folder = new File(currentPath);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> listOfFileNames = new ArrayList<>();
        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (!listOfFile.isDirectory()) listOfFileNames.add(listOfFile.getName());
        }
        fileList.setListData(listOfFileNames.toArray());
    }

    private void processChecksums() {
        FileChecker fileChecker = new FileChecker(currentPath);
        ArrayList<String> editedFiles = fileChecker.compareFilesChecksum();
        editedFilesList.setListData(editedFiles.toArray());
    }
}