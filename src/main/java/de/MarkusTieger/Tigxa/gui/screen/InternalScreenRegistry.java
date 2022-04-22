package de.MarkusTieger.Tigxa.gui.screen;

import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.impl.main.gui.screen.MainScreenRegistry;
import de.MarkusTieger.Tigxa.gui.screen.settings.SettingsScreen;
import de.MarkusTieger.Tigxa.lang.Translator;
import lombok.Getter;
import org.chromium.userinterface.GameScreen;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

public class InternalScreenRegistry {

    private final HashMap<String, IScreen> map = new HashMap<>();
    private final IAPI api;

    private IScreen about;
    private IScreen update;
    private IScreen chromeDino;

    public InternalScreenRegistry(IAPI api){
        this.api = api;
    }

    public void init(){

        initAbout();
        initUpdate();
        initChromeDino();
        SettingsScreen.initialize(api);

    }

    public void apply(){
        api.getGUIManager().getScreenRegistry().registerScreen(about, "about");
        api.getGUIManager().getScreenRegistry().registerScreen(update, "update");
        SettingsScreen.register(api);
        ((MainScreenRegistry)api.getGUIManager().getScreenRegistry()).registerScreen(chromeDino, "chrome", "dino");
    }

    private void initChromeDino(){
        chromeDino = api.getGUIManager().createScreen(Translator.translate(26), "chrome://dino");

        GameScreen gameScreen = new GameScreen(chromeDino.getContentPane());
        chromeDino.getContentPane().addKeyListener(gameScreen);
        chromeDino.getContentPane().addMouseListener(gameScreen);
        chromeDino.getContentPane().add(gameScreen);

        gameScreen.startGame();

    }

    private void initUpdate() {
        update = api.getGUIManager().createScreen(Translator.translate(27), api.getNamespace() + "://update");

        update.getContentPane().setLayout(null);

        JLabel label = new JLabel();
        label.setBounds(25, 25, 500, 50);
        update.getContentPane().add(label);

        JProgressBar bar = new JProgressBar();
        bar.setMinimum(0);
        bar.setMaximum(10000);
        bar.setBounds(25, 75, 500, 10);
        update.getContentPane().add(bar);

        JButton btn = new JButton(Translator.translate(27));
        btn.setBounds(25, 125, 100, 25);
        btn.setEnabled(false);
        update.getContentPane().add(btn);

        Browser.getUpdateListener().add((latest) -> {

            btn.setEnabled(true);
            String txt = Translator.translate(28, Browser.FULL_NAME, latest.version(), latest.build(), latest.commit());
            System.out.println(txt);
            label.setText(txt);
            btn.addActionListener((e) -> {

                btn.setEnabled(false);

                new Thread(() -> {
                    Browser.getUpdater().update(latest, (percend) -> {

                        if(percend == -1D){
                            bar.setValue(10000);
                            bar.setForeground(Color.GREEN);
                        } else {
                            bar.setValue((int) (percend * 100D));
                        }

                    });
                }, Translator.translate(29)).start();

            });

        });
    }

    private void initAbout(){

        about = api.getGUIManager().createScreen(Translator.translate(30), api.getNamespace() + "://about");

        about.getContentPane().setLayout(null);

        JLabel label = new JLabel() {

            private boolean rendered = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if(rendered) return;
                rendered = true;
                new Thread(this::start, "Starter").start();
            }

            private void start(){
                try
                {

                    InputStream in = Browser.class.getResourceAsStream("/res/gui/about/text");
                    String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                    in.close();

                    System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");
                    Central.registerEngineCentral("com.sun.speech.freetts" + ".jsapi.FreeTTSEngineCentral");
                    Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
                    synthesizer.allocate();
                    synthesizer.resume();

                    while (true){
                        synthesizer.speakPlainText(text, null);
                        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
                    }

                    // synthesizer.deallocate();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        label.setBounds(0, 0, 20, 20);
        about.getContentPane().add(label);
    }

}
