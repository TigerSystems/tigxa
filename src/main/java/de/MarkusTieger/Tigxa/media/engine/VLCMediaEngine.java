package de.MarkusTieger.Tigxa.media.engine;

import de.MarkusTieger.Tigxa.api.media.IMediaEngine;
import uk.co.caprica.vlcj.media.InfoApi;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VLCMediaEngine implements IMediaEngine {

    private final EmbeddedMediaPlayer player;

    public VLCMediaEngine(EmbeddedMediaPlayer player){
        this.player = player;
    }


    @Override
    public int getMaxFrame() {
        return 0;
    }

    @Override
    public int getFrame() {
        return 0;
    }

    @Override
    public int getVolume() {
        return player.audio().volume();
    }

    @Override
    public void setVolume(int i) {
        player.audio().setVolume(i);
    }

    @Override
    public void pause() {
        player.controls().pause();
    }

    @Override
    public void play() {
        player.controls().play();
    }

    @Override
    public void setFrame(int i) {

    }

    @Override
    public String getLocation() {
        InfoApi info = player.media().info();
        return info == null ? "about:blank" : (info.mrl() == null ? "about:blank" : info.mrl());
    }

    public void load(String mrl){
        if(mrl == null || mrl.equalsIgnoreCase("about:blank")){
            player.controls().stop();
            return;
        }
        player.media().start(mrl);
    }
}
