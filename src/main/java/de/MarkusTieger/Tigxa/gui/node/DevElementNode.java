package de.MarkusTieger.Tigxa.gui.node;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class DevElementNode implements TreeNode {

    private final List<DevElementNode> nodes = new ArrayList<>();

    private final Element element;
    private final DevElementNode parent;
    private final String str;

    public DevElementNode(DevElementNode parent, Element element) {
        this.element = element;
        this.parent = parent;
        NodeList childs = element.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node n = childs.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) n;
                nodes.add(new DevElementNode(this, elem));
            }
        }

        this.str = elementToString();
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
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) == node) return i;
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
        Iterator<DevElementNode> it = nodes.iterator();
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
        return str;
    }

    private String elementToString() {

        final Node n = element;

        String name = n.getNodeName();

        short type = n.getNodeType();

        if (Node.CDATA_SECTION_NODE == type) {
            return "<![CDATA[" + n.getNodeValue() + "]]&gt;";
        }

        if (name.startsWith("#")) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        sb.append('<').append(name);

        NamedNodeMap attrs = n.getAttributes();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                sb.append(' ').append(attr.getNodeName()).append("=\"").append(attr.getNodeValue()).append(
                        "\"");
            }
        }

        String textContent = null;
        NodeList children = n.getChildNodes();

        if ((textContent = n.getTextContent()) != null && !"".equals(textContent)) {
            sb.append('>').append(textContent).append("</").append(name).append('>');
        } else {
            sb.append("/>").append('\n');
        }

        return sb.toString();
    }

}
