package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class displays a selection dialog to select a n input format and a scenario
 * @author Hanselmann, Rietzler, Clauss, Herzog
 * @version 29.11.17
 */
public final class SelectionDialog extends JDialog implements Runnable{
    private static SelectionDialog selectionDialog = new SelectionDialog();
    private final String XMLPath = "xml/";
    private final String JSONPath = "json/";
    private ButtonGroup szenarioSelection;
    private JPanel szenarioSelectionPanel;
    ButtonGroup formatSelection;
    private String selectedSzenario;
    private String selectedFormat;
    private Lock lock = new ReentrantLock();

    /**
     * return the only selectiondialog object
     * @return the only SelectionDialog object
     */
    public static SelectionDialog create() {
        return selectionDialog;
    }

    /**
     * private construtor for a scenario object
     */
    private SelectionDialog(){
        super();
        setUpDialog();
    }

    /**
     * set up for the dialog
     */
    private void setUpDialog() {
        Container contentPane = this.getContentPane();
        JPanel main = new JPanel();
        contentPane.add(main);

        main.setLayout(new GridLayout(3,2));

        JLabel eingabeFormat = new JLabel("Eingabeformat:");
        JLabel szenario = new JLabel("Szenario:");

        formatSelection = new ButtonGroup();
        szenarioSelection = new ButtonGroup();

        JPanel formatSelectionPanel = new JPanel();
        szenarioSelectionPanel = new JPanel();

        JRadioButton JSONButton = new JRadioButton("JSON");
        JSONButton.setActionCommand(JSONPath);
        JSONButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSzenarioSelection(JSONPath);
            }
        });
        formatSelection.add(JSONButton);
        formatSelectionPanel.add(JSONButton);

        JRadioButton XMLButton = new JRadioButton("XML");
        XMLButton.setActionCommand(XMLPath);
        XMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSzenarioSelection(XMLPath);
            }
        });
        formatSelection.add(XMLButton);
        formatSelectionPanel.add(XMLButton);

        createRadioButtons(XMLPath);

        JPanel buttonPanel = new JPanel();

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okClicked();
            }
        });
        buttonPanel.add(okButton);

        main.add(eingabeFormat);
        main.add(szenario);
        main.add(formatSelectionPanel);
        main.add(szenarioSelectionPanel);
        main.add(buttonPanel);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.setModal(true);
        this.pack();
        this.setVisible(true);

    }

    /**
     * method is executed when the OK-Button in the dialog is clicked
     * notifies the Lock object
     */
    public void okClicked() {
        selectedFormat = formatSelection.getSelection().getActionCommand();
        selectedSzenario = szenarioSelection.getSelection().getActionCommand();
        synchronized (lock){
            lock.notify();
        }
        this.dispose();
    }

    /**
     * updates the szenarioSelection RadioButtons to the corresponding Szenario directories of the selected format directory
     * @param path the path of the selected format directory
     */
    public void updateSzenarioSelection(String path) {
        this.setVisible(false);
        szenarioSelection.clearSelection();
        szenarioSelectionPanel.removeAll();
        createRadioButtons(path);

        this.pack();
        this.setVisible(true);
    }

    /**
     * creates a set of scenario radioButtons according to the directories in the specified format folder
     * @param path the format folder
     */
    private void createRadioButtons(String path){
        File szenarioFolders = new File(path);
        File[] allSzenarios = szenarioFolders.listFiles();

        for(File file : allSzenarios){
            String name = file.getName();
            JRadioButton szenarioButton = new JRadioButton(name);
            szenarioButton.setActionCommand(file.getName() + "/");
            szenarioSelection.add(szenarioButton);
            szenarioSelectionPanel.add(szenarioButton);
        }
    }

    /**
     * returns the selected input format(xml or json) and the selected scenario folder
     * the controll flow is blocked until the OK-button is clicked
     * after that the values are returned
     * @return an String array that contains the selected format at position 0 and the selected scenario at position 1
     */
    public String[] getSelected(){
        Thread thread = new Thread(this,"thread1");
        thread.start();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (Exception e) {
            }
        }
        String[] selected = {selectedFormat,selectedSzenario};
        return selected;
    }

    /**
     * a dummy for the run method
     */
    public void run(){}


}
