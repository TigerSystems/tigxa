package de.markustieger.tigxa.gui.window;

import de.markustieger.tigxa.Browser;
import de.markustieger.tigxa.gui.image.ImageLoader;
import de.markustieger.tigxa.gui.node.DevElementNode;
import de.markustieger.tigxa.web.MainContent;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.List;

public class DevWindow {

    private final MainContent.MainContentData data;

    private DevWindow(MainContent.MainContentData data) {
        this.data = data;
    }

    public static void create(MainContent.MainContentData data) {
        DevWindow window = new DevWindow(data);
        window.init();
    }

    public void init() {

        Document doc = data.webEngine().getDocument();

        if (doc == null) return;

        BufferedImage image = ImageLoader.loadInternalImage("/res/gui/logo.png");

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        int width = 600;

        JFrame frame = new JFrame();
        frame.setSize(width, 800);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLocation(toolkit.getScreenSize().width - width, 0);
        if (image != null) frame.setIconImage(image);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                List<JFrame> frames = Browser.getFrames();
                synchronized (frames) {
                    frames.add(frame);
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                List<JFrame> frames = Browser.getFrames();
                synchronized (frames) {
                    frames.remove(frame);
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        JTabbedPane tabs = new JTabbedPane() {

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getPreferredSize() {
                return frame.getContentPane().getSize();
            }
        };
        frame.add(tabs);

        JTree tree = new JTree();
        DefaultTreeModel model = new DefaultTreeModel(new DevElementNode(null, doc.getDocumentElement()));
        tree.setModel(model);

        tabs.addTab("Elements", tree);

        frame.setVisible(true);
    }

}
