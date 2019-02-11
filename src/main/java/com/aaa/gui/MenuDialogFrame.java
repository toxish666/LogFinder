package com.aaa.gui;

import com.aaa.finder.FinderCriteria;
import com.aaa.finder.FinderStrategy;
import com.aaa.ftp.FtpClient;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class MenuDialogFrame extends JPanel {

    private javax.swing.JTextArea TextSearchTextArea;
    private javax.swing.JButton acceptButton;
    private javax.swing.JPanel criteriaSearchPanel;
    private javax.swing.JFormattedTextField formatTextField;
    private javax.swing.JRadioButton ftpRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JRadioButton localRadioButton;
    private javax.swing.JFormattedTextField pathTextField;
    private javax.swing.JPanel textSearchPanel;
    private javax.swing.JScrollPane textSearchScrollPane;

    private JDialog dialog;
    private boolean ok;

    String textToSearch;
    Pattern format;
    String folderpath;

    MenuDialogFrame(){
        initComponents();
        initAdditionalComponents();
    }


    public String getTextToSearch(){
        return textToSearch;
    }

    public Pattern getFormat(){
        return format;
    }

    public String getFolderpath(){
        return folderpath;
    }

    public FinderCriteria getFinderCriteria(){
        if(localRadioButton.isSelected()){
            return FinderCriteria.LOCALL;
        } else if(ftpRadioButton.isSelected()){
            return FinderCriteria.FTP;
        }
        return FinderCriteria.LOCALL;
    }

    private void initComponents() {

        textSearchPanel = new javax.swing.JPanel();
        textSearchScrollPane = new javax.swing.JScrollPane();
        TextSearchTextArea = new javax.swing.JTextArea();
        criteriaSearchPanel = new javax.swing.JPanel();
        localRadioButton = new javax.swing.JRadioButton();
        ftpRadioButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        acceptButton = new javax.swing.JButton();
        pathTextField = new javax.swing.JFormattedTextField();
        formatTextField = new javax.swing.JFormattedTextField();
        formatTextField.setText("log");

        textSearchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Search text"));

        TextSearchTextArea.setColumns(20);
        TextSearchTextArea.setRows(5);
        textSearchScrollPane.setViewportView(TextSearchTextArea);

        javax.swing.GroupLayout textSearchPanelLayout = new javax.swing.GroupLayout(textSearchPanel);
        textSearchPanel.setLayout(textSearchPanelLayout);
        textSearchPanelLayout.setHorizontalGroup(
                textSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(textSearchScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
        );
        textSearchPanelLayout.setVerticalGroup(
                textSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(textSearchScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
        );

        criteriaSearchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Search Сriteria"));

        localRadioButton.setText("local");

        ftpRadioButton.setText("ftp");

        jLabel1.setText("Folder path");

        jLabel2.setText("File format (log, txt...)");

        acceptButton.setText("Accept");

        javax.swing.GroupLayout criteriaSearchPanelLayout = new javax.swing.GroupLayout(criteriaSearchPanel);
        criteriaSearchPanel.setLayout(criteriaSearchPanelLayout);
        criteriaSearchPanelLayout.setHorizontalGroup(
                criteriaSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(criteriaSearchPanelLayout.createSequentialGroup()
                                .addGroup(criteriaSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(criteriaSearchPanelLayout.createSequentialGroup()
                                                .addComponent(localRadioButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(ftpRadioButton)
                                                .addGap(18, 18, 18)
                                                .addComponent(acceptButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(criteriaSearchPanelLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(criteriaSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(pathTextField)
                                                        .addComponent(formatTextField))))
                                .addContainerGap())
        );
        criteriaSearchPanelLayout.setVerticalGroup(
                criteriaSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, criteriaSearchPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(formatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(criteriaSearchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(localRadioButton)
                                        .addComponent(ftpRadioButton)
                                        .addComponent(acceptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(criteriaSearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(textSearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(textSearchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(criteriaSearchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        ButtonGroup G = new ButtonGroup();
        G.add(localRadioButton);
        G.add(ftpRadioButton);
        localRadioButton.setSelected(true);
    }// </editor-fold>

    private void initAdditionalComponents() {
        Pattern regexForPathLocal = Pattern.compile("(^(/)?([^/\\\0]+(/)?)+$)");
        //ftp-url
        Pattern regexForPathRemoteFtp = Pattern.compile("^ftp://([a-z0-9]+:[a-z0-9]+@)?([\\\\.a-z0-9]+)/([\\\\./a-z0-9]+)$");

        acceptButton.addActionListener(e -> {

            try {
                format = Pattern.compile(".*?\\."+formatTextField.getText());

                if(localRadioButton.isSelected()) {
                    if (!regexForPathLocal.matcher(pathTextField.getText()).matches())
                        throw new InvalidPathException(pathTextField.getText(), "Wrong path being given for local");
                }
                else if(ftpRadioButton.isSelected()) {
                        if (!regexForPathRemoteFtp.matcher(pathTextField.getText()).matches())
                            throw new InvalidPathException(pathTextField.getText(), "Wrong path being given for local;\n " +
                                    "example: ftp://user:password@host/<url-path>");
                        testForFtpConnectionPossibility(pathTextField.getText().trim());
                }

                //ftp://aaa:212412@localhost/asdasd
                folderpath = pathTextField.getText().trim();

                textToSearch = TextSearchTextArea.getText();

                ok = true;
                dialog.setVisible(false);
            } catch (PatternSyntaxException patternexception) {
                JOptionPane.showMessageDialog(MenuDialogFrame.this, patternexception.getMessage());
            } catch (InvalidPathException pathexception){
                JOptionPane.showMessageDialog(MenuDialogFrame.this, pathexception.getReason());
            } catch (IOException ex){
                JOptionPane.showMessageDialog(MenuDialogFrame.this, "Can't connect to remote server");
            }
        });
    }

    public boolean showDialog(Component parent, String title){
        ok = false;
        Frame owner = null;
        if (parent instanceof Frame)
            owner = (Frame)parent;
        else
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        if (dialog == null || dialog.getOwner() != owner){
            dialog = new JDialog(owner, true);
            dialog.add(this);
            dialog.getRootPane().setDefaultButton(acceptButton);
            dialog.pack();
        }

        dialog.setTitle(title);
        dialog.setVisible(true);
        return ok;

    }




    private void testForFtpConnectionPossibility(String ftpPath) throws IOException{
        URL url = new URL(ftpPath);
        String server = url.getHost();
        String user = url.getUserInfo().split(":")[0];
        String passwd = url.getUserInfo().split(":")[1];
        FtpClient ftpClient = new FtpClient(server, 21, user, passwd);
        ftpClient.open();
    }


}
