package de.MarkusTieger.Tigxa.web.engine.djnatives;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.reflect.InvocationTargetException;

public class DJNativesWebEngine implements IWebEngine {

    private final JWebBrowser browser;
    private final DJNativesWebHistory history;

    public DJNativesWebEngine(JWebBrowser browser){
        this.browser = browser;
        this.history = new DJNativesWebHistory(browser);
    }

    private void syncExec(final Runnable r) {
        try {
            if (EventQueue.isDispatchThread()) r.run();
            else EventQueue.invokeAndWait(r);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setZoom(double v) {

    }

    @Override
    public void print() {
        syncExec(new Runnable() {
            public void run() {
                PrinterJob print = PrinterJob.getPrinterJob();
                print.setPrintable(new Printable() {

                    @Override
                    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

                        if(pageIndex > 0) {
                            return NO_SUCH_PAGE;
                        }

                        BufferedImage image = new BufferedImage(browser.getWidth(), browser.getHeight(), BufferedImage.TYPE_INT_ARGB);

                        browser.paint(image.getGraphics());

                        g.drawImage(image, (int)pageFormat.getImageableX(), (int)pageFormat.getImageableY(), (int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight(), null);

                        return PAGE_EXISTS;
                    }
                });


                if(print.printDialog()) {
                    try {
                        print.print();
                    } catch (PrinterException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void load(String s) {
        syncExec(() -> {

            browser.navigate(s);

        });
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public Object executeScript(String s) {
        Object[] array = new Object[1];
        syncExec(() -> {

            array[0] = browser.executeJavascriptWithResult(s);

        });
        return array[0];
    }

    @Override
    public IWebHistory getHistory() {
        return history;
    }

    @Override
    public void loadContent(String s, String s1) {
        syncExec(() -> {
            browser.setHTMLContent(s);
        });
    }

    @Override
    public void reload() {
        syncExec(() -> {
            browser.reloadPage();
        });
    }

    @Override
    public String getLocation() {
        String[] array = new String[1];
        syncExec(() -> {
            array[0] = browser.getResourceLocation();
        });
        return array[0];
    }
}
