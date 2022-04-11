package de.MarkusTieger.Tigxa.extension.api.impl.main.gui.window;

import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.extension.api.IAPI;
import de.MarkusTieger.Tigxa.extension.api.window.IWindow;
import de.MarkusTieger.Tigxa.extension.api.window.IWindowManager;
import de.MarkusTieger.Tigxa.gui.window.BrowserWindow;

import java.io.File;
import java.util.*;

public class MainWindowManager implements IWindowManager {

    final Map<BrowserWindow, IWindow> map = Collections.synchronizedMap(new HashMap<>());

    private final IAPI api;
    private final File configRoot;

    public MainWindowManager(IAPI api, File configRoot) {
        this.api = api;
        this.configRoot = configRoot;
    }


    @Override
    public IWindow addWindow() {
        BrowserWindow window = new BrowserWindow();
        window.initWindow(api, configRoot);
        IWindow win = genWindow(window);
        synchronized (map) {
            map.put(window, win);
        }
        return win;
    }

    @Override
    public List<IWindow> listWindows() {
        List<IWindow> windows = new ArrayList<>();

        List<BrowserWindow> bws = Browser.getWindows();
        synchronized (bws) {
            for (BrowserWindow bw : bws) {
                synchronized (map) {
                    IWindow window = map.get(bw);
                    if (window == null) {
                        window = genWindow(bw);
                        map.put(bw, window);
                    }
                    windows.add(window);
                }
            }
        }
        return windows;
    }

    private IWindow genWindow(BrowserWindow bw) {
        return new MainWindow(this, bw);
    }

    public IWindow fromBW(BrowserWindow window) {
        synchronized (map) {
            IWindow w = map.get(window);
            if (w != null) return w;

            w = genWindow(window);
            map.put(window, w);
            return w;
        }
    }
}
