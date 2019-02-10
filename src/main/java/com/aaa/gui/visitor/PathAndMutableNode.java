package com.aaa.gui.visitor;

import org.apache.commons.lang3.tuple.Pair;

import javax.swing.tree.DefaultMutableTreeNode;

public class PathAndMutableNode {
    private Pair<String, DefaultMutableTreeNode> data;

    public PathAndMutableNode(String name, DefaultMutableTreeNode node ){
        data = Pair.of(name,node);
    }

    public String getDataString(){
        return data.getKey();
    }

    public void setDataString(String arg) {
        this.data = Pair.of(arg, data.getRight());
    }

    public DefaultMutableTreeNode getDefaultMutableTreeNode(){
        return data.getValue();
    }

    @Override
    public boolean equals(Object obj) {
        return ((PathAndMutableNode)obj).getDataString().equals(data.getKey());
    }
}
