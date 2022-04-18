package de.MarkusTieger.Tigxa.gui.components;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.List;

public class ModifiedTabbedPane extends JTabbedPane {

    private boolean dragging = false;
    private Image tabImage = null;
    private Point currentMouseLocation = null;
    private int draggedTabIndex = 0;

    private static final Logger LOGGER = Logger.getLogger(ModifiedTabbedPane.class);

    private BiConsumer<Integer, Component> closeHandler;
    private Runnable addHandler;

    public ModifiedTabbedPane() {
        super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {

                if (!dragging) {
                    // Gets the tab index based on the mouse position
                    int tabNumber = getUI().tabForCoordinate(ModifiedTabbedPane.this, e.getX(), e.getY());

                    if(tabNumber == indexOfComponent(plus)){

                        dragging = true;

                    } else
                    if (tabNumber >= 0) {
                        draggedTabIndex = tabNumber;
                        Rectangle bounds = getUI().getTabBounds(ModifiedTabbedPane.this, tabNumber);


                        // Paint the tabbed pane to a buffer
                        Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics totalGraphics = totalImage.getGraphics();
                        totalGraphics.setClip(bounds);
                        // Don't be double buffered when painting to a static image.
                        setDoubleBuffered(false);
                        paint(totalGraphics);

                        // Paint just the dragged tab to the buffer
                        tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
                        Graphics graphics = tabImage.getGraphics();
                        graphics.drawImage(totalImage, 0, 0, bounds.width, bounds.height, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, ModifiedTabbedPane.this);

                        dragging = true;
                        repaint();
                    }
                } else {
                    currentMouseLocation = e.getPoint();

                    // Need to repaint
                    repaint();
                }

                super.mouseDragged(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {

                if (dragging) {
                    int tabNumber = getUI().tabForCoordinate(ModifiedTabbedPane.this, e.getX(), 10);

                    if (tabNumber >= 0 && !(tabNumber == draggedTabIndex) && tabNumber != indexOfComponent(plus)) {
                        Component comp = getComponentAt(draggedTabIndex);
                        String title = getTitleAt(draggedTabIndex);
                        Icon icon = getIconAt(draggedTabIndex);
                        String tip = getToolTipTextAt(draggedTabIndex);

                        removeTabAt(draggedTabIndex);
                        insertTab(title, icon, comp, tip, tabNumber);
                    }
                }

                dragging = false;
                tabImage = null;
            }

            @Override
            public void mousePressed(MouseEvent e) {

                if(e.getButton() == MouseEvent.BUTTON2) {

                    if(closeHandler != null){
                        int tabNumber = getUI().tabForCoordinate(ModifiedTabbedPane.this, e.getX(), 10);
                        if(tabNumber >= 0){
                            closeHandler.accept(tabNumber, getComponentAt(tabNumber));
                        }
                    }

                }

            }
        });

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(getSelectedComponent() == plus && addHandler != null){
                    addHandler.run();
                }
            }
        });
    }

    public void removeAll() {
        remove();

        int tabCount = getTabCount();
        // We invoke removeTabAt for each tab, otherwise we may end up
        // removing Components added by the UI.
        while (tabCount-- > 0) {
            super.removeTabAt(tabCount);
        }
        add();
    }

    public void setHandler(BiConsumer<Integer, Component> closeHandler, Runnable addHandler){
        this.closeHandler = closeHandler;
        this.addHandler = addHandler;
    }

    @Override
    public Dimension getPreferredSize() {
        int tabsWidth = 0;

        for (int i = 0; i < getTabCount(); i++) {
            tabsWidth += getBoundsAt(i).width;
        }

        Dimension preferred = super.getPreferredSize();

        preferred.width = Math.max(preferred.width, tabsWidth);

        return preferred;
    }

    public void paint(Graphics g) {
        super.paint(g);

        if (dragging && currentMouseLocation != null && tabImage != null) {
            g.drawImage(tabImage, currentMouseLocation.x, currentMouseLocation.y, this);
            repaint();
        }
    }

    private final JLabel plus = new JLabel("Why do you see that?");

    public void remove(){
        if(addHandler == null) return;
        int index = indexOfComponent(plus);
        if(index < 0) return;
        super.removeTabAt(index);
    }

    public void add(){
        if(addHandler == null) return;
        super.insertTab(" + ", null, plus, null, getTabCount());
    }

    @Override
    public void addTab(String title, Component component) {
        remove();
        super.addTab(title, component);
    }

    @Override
    public void addTab(String title, Icon icon, Component component) {
        remove();
        super.addTab(title, icon, component);
    }

    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        remove();
        super.addTab(title, icon, component, tip);
    }

    @Override
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        remove();
        super.insertTab(title, icon, component, tip, index);
        add();
    }

    @Override
    public void removeTabAt(int index) {
        remove();
        super.removeTabAt(index);
        add();
    }

}
