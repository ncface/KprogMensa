package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        XMLButton.setSelected(true);
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

    public void okClicked() {
        selectedFormat = formatSelection.getSelection().getActionCommand();
        selectedSzenario = szenarioSelection.getSelection().getActionCommand();
        synchronized (lock){
            lock.notify();
        }
        this.dispose();
    }

    public void updateSzenarioSelection(String path) {
        this.setVisible(false);
        szenarioSelection.clearSelection();
        szenarioSelectionPanel.removeAll();
        createRadioButtons(path);

        this.pack();
        this.setVisible(true);
    }

    private void createRadioButtons(String path){
        File szenarioFolders = new File(path);
        File[] allSzenarios = szenarioFolders.listFiles();

        for(File file : allSzenarios){
            String name = file.getName();
            JRadioButton szenarioButton = new JRadioButton(name);
            szenarioButton.setActionCommand(file.getName() + "/");
            szenarioSelection.add(szenarioButton);
            szenarioSelectionPanel.add(szenarioButton);
            szenarioButton.setSelected(true);
        }
    }

    public String getSelected(){
        Thread thread = new Thread(this,"thread1");
        thread.start();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (Exception e) {
            }
        }

        return selectedFormat + selectedSzenario;
    }

    public void run(){}


}
