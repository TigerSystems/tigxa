package de.MarkusTieger.Tigxa.gui.screen.settings.tree;

import lombok.Getter;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class TreeEntry implements TreeNode {

    private final String name;
    private final TreeNode parent;

    @Getter
    private final JPanel panel;

    public TreeEntry(String name, TreeNode parent, JPanel panel){
        this.name = name;
        this.parent = parent;
        this.panel = panel;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return new Enumeration<TreeNode>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public TreeNode nextElement() {
                return null;
            }
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
