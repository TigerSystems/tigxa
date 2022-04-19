package de.MarkusTieger.Tigxa.media;

import de.MarkusTieger.Tigxa.api.engine.IEngine;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.media.IMediaEngine;
import de.MarkusTieger.Tigxa.gui.window.BrowserWindow;
import de.MarkusTieger.Tigxa.media.engine.VLCMediaEngine;
import lombok.Getter;
import uk.co.caprica.vlcj.factory.MediaPlayerApi;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.videosurface.ComponentVideoSurface;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static de.MarkusTieger.Tigxa.web.WebUtils.takeScreenshot;

public class MediaUtils {

    @Getter
    private static boolean ready = false;

    public static void initialize(){
        ready = new NativeDiscovery().discover();
    }



    // VLC Implementation for Youtube (Context-Menu "Open in VLC" )


    public static Component createPanel(BrowserWindow window, JPanel nav, String url, IMediaEngine[] enginearray, JPanel panel, List<Runnable> visibleHandler) {

        IMediaEngine engine = null;
        Component component = null;

        if (true) { // IF VLC-PLAYER

            String[] d = new String[]{null};
            final Consumer<String>[] changearray = new Consumer[]{
                    (Consumer<String>) (c) -> {
                        d[0] = c;
                    }
            };
            Consumer<String> change = (c) -> changearray[0].accept(c);


            Runnable[] screenshotarray = new Runnable[]{() -> {
            }};
            Runnable screenshot = () -> screenshotarray[0].run();

            Runnable[] devtoolsarray = new Runnable[]{() -> {
            }};

            FullScreenStrategy fullScreenStrategy = null;

            MediaPlayerFactory factory = new MediaPlayerFactory();
            MediaPlayerApi api = factory.mediaPlayers();

            EmbeddedMediaPlayer player = api.newEmbeddedMediaPlayer();

            player.fullScreen().strategy(new FullScreenStrategy() {

                @Override
                public void enterFullScreenMode() {
                    window.enterFullscreen();
                }

                @Override
                public void exitFullScreenMode() {
                    window.exitFullscreen();
                }

                @Override
                public boolean isFullScreenMode() {
                    return window.isFullscreen();
                }
            });

            player.menu().activate();

            Canvas canvas = new Canvas();





            component = canvas;
            engine = new VLCMediaEngine(player);

            final IMediaEngine fe = engine;

            final Component finalc = component;

            screenshotarray[0] = () -> takeScreenshot(window, finalc);

            changearray[0] = window.buildNav(nav, () -> {

            }, () -> {


            }, () -> {}, (loc) -> {

                try {
                    URI uri = new URI(loc);
                    IScreen sc = window.getMapi().getGUIManager().getScreenRegistry().getRegistredScreen(uri.getScheme(), uri.getHost());
                    if (sc == null) {
                        fe.load(loc);
                    } else {
                        window.newTab(sc, true);
                        try {
                            changearray[0].accept(player.media().info().mrl());
                        } catch (Throwable e) {
                        }
                    }
                } catch (Throwable e) {
                    fe.load(loc);
                }

            }, engine);
            if (d[0] != null) {
                changearray[0].accept(d[0]);
            }

            // devtoolsarray[0] = () -> DevWindow.create(data);

            visibleHandler.add(() -> {

                ComponentVideoSurface surface = factory.videoSurfaces().newVideoSurface(canvas);
                surface.attach(player);

                if (url != null) fe.load(url);

            });

            enginearray[0] = engine;

        }


        Map<Component, IEngine> tabLinks = window.getTabLinks();
        synchronized (tabLinks) {
            tabLinks.put(component, engine);
        }

        return component;

    }


}
