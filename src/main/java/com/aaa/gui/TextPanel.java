package com.aaa.gui;

import org.apache.commons.lang3.tuple.Pair;

import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TextPanel extends javax.swing.JPanel {
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JTextArea textArea;

    private final Pair<Path, List<Integer>> fileinfo;
    private final int numberOfMatchedString;
    private Integer cursorPosition=0;

    public TextPanel(Pair<Path, List<Integer>> fileinfo, int numberOfMatchedString){
        this.numberOfMatchedString = numberOfMatchedString;
        this.fileinfo = fileinfo;

        initComponents();

        try {
            BufferedReader in = new BufferedReader(new FileReader(fileinfo.getKey().toFile()));
            String line = in.readLine();
            while(line != null){
                textArea.append(line + "\n");
                line = in.readLine();
            }
        } catch (IOException ioexcept){
            ioexcept.printStackTrace();
        }

        nextButton.addActionListener(a ->{
            try {
                if(++cursorPosition >= fileinfo.getValue().size() - 1)
                    cursorPosition=0;
                textArea.requestFocusInWindow();
                textArea.requestFocus();
                int endLine = fileinfo.getValue().get(cursorPosition);
                int startLine = endLine - (numberOfMatchedString - 1);

                textArea.select(textArea.getLineStartOffset(startLine), textArea.getLineEndOffset(endLine));

            }catch (BadLocationException blex){
                blex.printStackTrace();
            }
        });

        previousButton.addActionListener(a ->{
            try {
                if(--cursorPosition < 0)
                    cursorPosition=fileinfo.getValue().size();
                textArea.requestFocusInWindow();
                textArea.requestFocus();
                int endLine = fileinfo.getValue().get(cursorPosition);
                int startLine = endLine - (numberOfMatchedString - 1);

                textArea.select(textArea.getLineStartOffset(startLine), textArea.getLineEndOffset(endLine));


            }catch (BadLocationException blex){
                blex.printStackTrace();
            }
        });


        selectAllButton.addActionListener(a ->{
            textArea.selectAll();
        });
    }


    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();

        textArea.setColumns(20);
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        previousButton.setText("Previous");

        nextButton.setText("Next");

        selectAllButton.setText("Select All");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(previousButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(nextButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 275, Short.MAX_VALUE)
                                                .addComponent(selectAllButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jScrollPane1)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(previousButton)
                                        .addComponent(nextButton)
                                        .addComponent(selectAllButton))
                                .addContainerGap())
        );
    }// </editor-fold>

}
