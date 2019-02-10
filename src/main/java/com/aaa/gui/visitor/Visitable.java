package com.aaa.gui.visitor;

public interface Visitable<T> {

        public void accept(Visitor<T> visitor);
}
