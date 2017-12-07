package view;

import io.FactoryJSON;
import io.FactoryXML;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * This class displays a selection dialog to select a n input format and a scenario
 * @author Hanselmann, Rietzler, Clauss, Herzog
 * @version 2017-11-29
 */
public final class SelectionDialog extends JDialog implements FormatAndScenarioSelection{
    private static SelectionDialog selectionDialog = new SelectionDialog();
    private final String XMLPATH = FactoryXML.FORMAT_DIRECTORY;
    private final String JSONPATH = FactoryJSON.FORMAT_DIRECTORY;
    private ButtonGroup szenarioSelection;
    private JPanel szenarioSelectionPanel;
    private ButtonGroup formatSelection;
    private String selectedSzenario;
    private String selectedFormat;
    private JLabel errorLabel;


    /**
     * private construtor for a scenario object
     */
    private SelectionDialog() {
        super();
        setUpDialog();
    }

    /**
     * return the only SelectionDialog object
     *
     * @return the only SelectionDialog object
     */
    public static SelectionDialog create() {
        return selectionDialog;
    }

    /**
     * set up for the dialog
     */
    private void setUpDialog() {
        Container contentPane = this.getContentPane();
        JPanel main = new JPanel();
        contentPane.add(main);

        main.setLayout(new GridLayout(3, 2));

        JLabel eingabeFormat = new JLabel("Eingabeformat:");
        JLabel szenario = new JLabel("Szenario:");

        formatSelection = new ButtonGroup();
        szenarioSelection = new ButtonGroup();

        JPanel formatSelectionPanel = new JPanel();
        szenarioSelectionPanel = new JPanel();

        JRadioButton jsonButton = new JRadioButton("JSON");
        jsonButton.setActionCommand(JSONPATH);
        jsonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSzenarioSelection(JSONPATH);
            }
        });
        formatSelection.add(jsonButton);
        formatSelectionPanel.add(jsonButton);

        JRadioButton xmlButton = new JRadioButton("XML");
        xmlButton.setActionCommand(XMLPATH);
        xmlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSzenarioSelection(XMLPATH);
            }
        });
        formatSelection.add(xmlButton);
        formatSelectionPanel.add(xmlButton);

        createRadioButtons(XMLPATH);

        JPanel buttonPanel = new JPanel();

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okClicked();
            }
        });
        buttonPanel.add(okButton);

        errorLabel = new JLabel();

        main.add(eingabeFormat);
        main.add(szenario);
        main.add(formatSelectionPanel);
        main.add(szenarioSelectionPanel);
        main.add(buttonPanel);
        main.add(errorLabel);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //dialog has to be selected before executing
        this.setModal(true);
        this.pack();
        this.setAlwaysOnTop(true);
        this.setVisible(true);

    }

    /**
     * method is executed when the OK-Button in the dialog is clicked
     */
    public void okClicked() {
        try {
            selectedFormat = formatSelection.getSelection().getActionCommand();
            selectedSzenario = szenarioSelection.getSelection().getActionCommand();
            synchronized (this) {
                this.notify();
            }
            this.dispose();
        }
        catch(Exception e){
            errorLabel.setText("Kein Eingabeformat oder Szenario ausgewählt!");
        }
    }

    /**
     * updates the szenarioSelection RadioButtons to the corresponding Szenario directories of the selected format directory
     *
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
     *
     * @param path the format folder
     */
    private void createRadioButtons(String path) {
        File szenarioFolders = new File(path);
        File[] allSzenarios = szenarioFolders.listFiles();
        //check if there are files in the directory
        if (allSzenarios != null) {
            for (File file : allSzenarios) {
                String name = file.getName();
                JRadioButton szenarioButton = new JRadioButton(name);
                szenarioButton.setActionCommand(file.getName() + "/");
                szenarioSelection.add(szenarioButton);
                szenarioSelectionPanel.add(szenarioButton);
            }
        }
    }

    /**
     * returns the selected input format(xml or json) and the selected scenario folder
     * the control flow is blocked until the OK-button is clicked
     * after that the values are returned
     *
     * @return a String array that contains the selected format at position 0 and the selected scenario at position 1
     */
    @Override
    public String[] getSelectedFormatAndScenario() {
        synchronized (this) {
            try {
                this.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String[] selected = {selectedFormat, selectedSzenario};
        return selected;
    }
}
