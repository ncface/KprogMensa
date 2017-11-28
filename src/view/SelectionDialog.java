package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public final class SelectionDialog extends JDialog {
    private static SelectionDialog selectionDialog = new SelectionDialog();
    private final String XMLPath = "xml/";
    private final String JSONPath = "json/";
    private ButtonGroup szenarioSelection;
    private JPanel szenarioSelectionPanel;

    public static void main(String[] args){
        create();
    }

    public static SelectionDialog create() {
        return selectionDialog;
    }

    private SelectionDialog(){
        super();
        setUpDialog();
    }

    private void setUpDialog() {
        Container contentPane = this.getContentPane();
        JPanel main = new JPanel();
        contentPane.add(main);

        main.setLayout(new GridLayout(2,2));

        JLabel eingabeFormat = new JLabel("Eingabeformat:");
        JLabel szenario = new JLabel("Szenario:");

        ButtonGroup formatSelection = new ButtonGroup();
        szenarioSelection = new ButtonGroup();

        JPanel formatSelectionPanel = new JPanel();
        szenarioSelectionPanel = new JPanel();

        JRadioButton JSONButton = new JRadioButton("JSON");
        JSONButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSzenarioSelection(JSONPath);
            }
        });
        formatSelection.add(JSONButton);
        formatSelectionPanel.add(JSONButton);

        JRadioButton XMLButton = new JRadioButton("XML");
        XMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSzenarioSelection(XMLPath);
            }
        });
        XMLButton.setSelected(true);
        formatSelection.add(XMLButton);
        formatSelectionPanel.add(XMLButton);

        File szenarioFolders = new File(XMLPath);
        File[] allSzenarios = szenarioFolders.listFiles();

        for(File file : allSzenarios){
            String name = file.getName();
            JRadioButton szenarioButton = new JRadioButton(name);
            szenarioSelection.add(szenarioButton);
            szenarioSelectionPanel.add(szenarioButton);
            szenarioButton.setSelected(true);
        }

        main.add(eingabeFormat);
        main.add(szenario);
        main.add(formatSelectionPanel);
        main.add(szenarioSelectionPanel);

        this.setModal(true);
        this.pack();
        this.setVisible(true);

    }

    private void updateSzenarioSelection(String Path) {
        this.setVisible(false);
        szenarioSelection.clearSelection();
        szenarioSelectionPanel.removeAll();
        File szenarioFolders = new File(Path);
        File[] allSzenarios = szenarioFolders.listFiles();

        for(File file : allSzenarios){
            String name = file.getName();
            JRadioButton szenarioButton = new JRadioButton(name);
            szenarioSelection.add(szenarioButton);
            szenarioSelectionPanel.add(szenarioButton);
            szenarioButton.setSelected(true);
        }

        this.pack();
        this.setVisible(true);
    }
}
