package com.aaa.gui.visitor;

public interface Visitor<T> {

    Visitor<T> visitTree(Tree<T> tree);

    void visitData(Tree<T> parent, T data);
}

