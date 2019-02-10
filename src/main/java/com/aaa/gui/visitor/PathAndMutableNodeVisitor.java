package com.aaa.gui.visitor;

import javax.swing.tree.DefaultMutableTreeNode;

public class PathAndMutableNodeVisitor implements Visitor<PathAndMutableNode> {

    private final PathAndMutableNode root;
    private DefaultMutableTreeNode next;

    String nameofroot;

    public PathAndMutableNodeVisitor(PathAndMutableNode root) {
        this.root = root;
    }

    public Visitor<PathAndMutableNode> visitTree(Tree<PathAndMutableNode> tree) {

        return new PathAndMutableNodeVisitor(
                new PathAndMutableNode(nameofroot, next));
    }

    public void visitData(Tree<PathAndMutableNode> parent, PathAndMutableNode data) {

       //nameofroot = (String) ((DefaultMutableTreeNode) root.getDefaultMutableTreeNode()).getUserObject();

        nameofroot=data.getDataString();
        root.setDataString(data.getDataString());

        next = new DefaultMutableTreeNode(nameofroot);
        root.getDefaultMutableTreeNode().add(next);

    }
}
