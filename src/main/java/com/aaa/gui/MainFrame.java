package com.aaa.gui;

import com.aaa.finder.FinderCriteria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class MainFrame extends JFrame {

    private javax.swing.JTabbedPane taskTabbedPane;
    private JMenuBar menuBar;
    private MenuDialogFrame dialog = null;


    public MainFrame(){
        initComponents();
        initMenu();
    }

    //generated
    private void initComponents() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (Exception e){
            e.printStackTrace();
        }

        taskTabbedPane = new JTabbedPaneCloseButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(taskTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(taskTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void initMenu(){
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu editMenu = new JMenu("Find");
        menuBar.add(editMenu);

        JMenuItem pasteItem = new JMenuItem("Set Properties");
        pasteItem.addActionListener(new SetPropertiesAction());
        editMenu.add(pasteItem);


    }


    //call dialog
    private class SetPropertiesAction implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if (dialog == null) dialog = new MenuDialogFrame();

            if (dialog.showDialog(MainFrame.this, "Connect")){


                new TreePanelBuilder(dialog.getFolderpath(),
                        dialog.getFormat(),
                        dialog.getTextToSearch(),
                        dialog.getFinderCriteria()).execute();
            }
        }

    }


    private class ProgressData {
        public String file; }
    private class TreePanelBuilder extends SwingWorker<JPanel, ProgressData>{
        String initpath;
        Pattern format;
        String textToSearch;
        FinderCriteria criteria;
        JPanel panelToShow;

        Component tabToRemove;

        public TreePanelBuilder(String initpath, Pattern format, String textToSearch, FinderCriteria criteria){
            this.initpath = initpath;
            this.format = format;
            this.textToSearch = textToSearch;
            this.criteria = criteria;
        }
        @Override
        public JPanel doInBackground(){
            tabToRemove = new JPanel();
            taskTabbedPane.addTab(dialog.getFolderpath(), tabToRemove);

            JPanel panel = new TreeAndTabbedTextPane(initpath, format, textToSearch, criteria);
            panelToShow = panel;
            return panel;
        }
        @Override
        public void done(){
            taskTabbedPane.remove(tabToRemove);
            taskTabbedPane.addTab(dialog.getFolderpath(), panelToShow);
        }
    }
}
