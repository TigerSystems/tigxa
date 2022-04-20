package de.MarkusTieger.Tigxa.gui.screen.settings.tree;

import lombok.Getter;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class TreeFolder implements TreeNode {

    @Getter
    private final List<TreeNode> nodes = new ArrayList<>();
    private final TreeNode parent;
    private final String name;

    public TreeFolder(String name, TreeNode parent){
        this.parent = parent;
        this.name = name;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return nodes.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return nodes.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        for(int i = 0; i < nodes.size(); i++){
            if(nodes.get(i).equals(node)) return i;
        }
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        Iterator<TreeNode> it = nodes.iterator();;
        return new Enumeration<TreeNode>() {
            @Override
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            @Override
            public TreeNode nextElement() {
                return it.next();
            }
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
