package com.aaa.gui;

import com.aaa.finder.FilesFoundLocally;
import com.aaa.finder.FilesFoundRemotelyFtp;
import com.aaa.finder.FinderCriteria;
import com.aaa.finder.FinderStrategy;
import com.aaa.ftp.FtpClient;
import com.aaa.gui.visitor.*;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class TreeAndTabbedTextPane extends javax.swing.JPanel {

    private javax.swing.JScrollPane fileSystemScrollPane;
    private javax.swing.JTree fileSystemTree;
    private javax.swing.JPanel fileSystemTreePanel;
    private javax.swing.JTabbedPane textTabbedPane;

    private final Pattern format;
    private final String textToFind;
    private final FinderCriteria finderCriteria;
    private String initpath;
    private Map<Path, List<Integer>> infoAboutFileSystem;
    private Map<String, DefaultMutableTreeNode> treeMap;

    private Optional<FtpClient> ftpClient = Optional.empty();

    public TreeAndTabbedTextPane(String initpath, Pattern format, String textToSearch, FinderCriteria criteria){
        this.finderCriteria = criteria;
        this.initpath = initpath;
        this.format = format;
        this.textToFind = textToSearch;
        treeMap = new HashMap<>();

        initComponents();
        buildFileSystemTree();
    }

    public void test(){}


    private void initComponents() {

        fileSystemTreePanel = new javax.swing.JPanel();
        fileSystemScrollPane = new javax.swing.JScrollPane();
        fileSystemTree = new javax.swing.JTree();
        textTabbedPane = new JTabbedPaneCloseButton();

        setPreferredSize(new java.awt.Dimension(830, 508));

        fileSystemTreePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File System Tree"));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("\\");
                fileSystemTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        fileSystemScrollPane.setViewportView(fileSystemTree);

        javax.swing.GroupLayout fileSystemTreePanelLayout = new javax.swing.GroupLayout(fileSystemTreePanel);
        fileSystemTreePanel.setLayout(fileSystemTreePanelLayout);
        fileSystemTreePanelLayout.setHorizontalGroup(
                fileSystemTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(fileSystemScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        );
        fileSystemTreePanelLayout.setVerticalGroup(
                fileSystemTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(fileSystemScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(fileSystemTreePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 648, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(fileSystemTreePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(textTabbedPane))
        );
    }// </editor-fold>

    private void buildFileSystemTree(){
        extractInfoAboutFileSystem();

        DefaultTreeModel model = (DefaultTreeModel)fileSystemTree.getModel();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(initpath);

        model.setRoot(root);


        Tree<PathAndMutableNode> forest = new Tree<>(new PathAndMutableNode("", root));
        Tree<PathAndMutableNode> current = forest;

        for (Path path : infoAboutFileSystem.keySet()) {
            Paths.get(initpath).relativize(path);
            String tree = Paths.get(initpath).relativize(path).toString();
            Tree<PathAndMutableNode> rooot = current;

            for (String data : tree.split("/")) {
                current = current.child(new PathAndMutableNode(data, new DefaultMutableTreeNode(data)));
            }
            current = rooot;
        }

        forest.accept(
                new PathAndMutableNodeVisitor(
                        new PathAndMutableNode(initpath, root)));

        //this peaace of code just for the cleanup
        int removechildrencount = root.getChildAt(0).getChildCount();
        for(int i=0; i<removechildrencount; i++) {
            root.add((DefaultMutableTreeNode) root.getChildAt(0).getChildAt(0));
        }
        root.remove(0);



        //
        model.reload(root);

        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = fileSystemTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = fileSystemTree.getPathForLocation(e.getX(), e.getY());
                if(selRow != -1) {
                    if(e.getClickCount() == 2 ) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                fileSystemTree.getLastSelectedPathComponent();
                        String nameofnode = (String) ((DefaultMutableTreeNode) node).getUserObject();
                        if(format.matcher(nameofnode).matches()){
                            String given = Arrays.stream(node.getPath())
                                    .map(tn -> (String)((DefaultMutableTreeNode) tn).getUserObject())
                                    .reduce(String.valueOf(' '), (a, el) -> a+"/"+el)
                                    .trim()
                                    .substring(1);
                            List<Integer> listOfMatches = infoAboutFileSystem.get(Paths.get(given));

                            textTabbedPane.addTab(nameofnode, new TextPanel(Pair.of(Paths.get(given), listOfMatches),
                                    textToFind.split("\\r?\\n").length,
                                    finderCriteria,
                                    ftpClient));
                        }
                    }
                }
            }
        };
        fileSystemTree.addMouseListener(ml);

    }

    //and update infoAboutFileSystem
    private void extractInfoAboutFileSystem(){
        FinderStrategy finderStrategy = null;

        switch(finderCriteria){
            case LOCALL:
                finderStrategy = new FilesFoundLocally();
                break;
            case FTP:
                FtpClient ftpClient = extractFtpClientInfo(initpath);
                this.ftpClient = Optional.of(ftpClient);
                finderStrategy = new FilesFoundRemotelyFtp(ftpClient);
                break;
        }
        infoAboutFileSystem = finderStrategy.listAllMatchedFiles(Paths.get(initpath),format,textToFind);
    }

    private FtpClient extractFtpClientInfo(String ftpPath){
        FtpClient ftpClient=null;
        String server=null;
        String pathToFind;
        String user=null;
        String passwd=null;
        try {
            URL url = new URL(ftpPath);
            server = url.getHost();
            pathToFind = url.getPath();
            user = url.getUserInfo().split(":")[0];
            passwd = url.getUserInfo().split(":")[1];
            initpath = pathToFind;
            ftpClient = new FtpClient(server, 21, user, passwd);
            ftpClient.open();
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return ftpClient;
    }

}
