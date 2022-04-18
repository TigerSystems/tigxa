package de.MarkusTieger.Tigxa.gui.window;

import com.yubico.client.v2.ResponseStatus;
import com.yubico.client.v2.VerificationResponse;
import com.yubico.client.v2.YubicoClient;
import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.gui.image.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PasswordWindow {

    public static <T> T requestPWD(String s, Function<char[], T> verify) {

        BufferedImage image = ImageLoader.loadInternalImage("/res/gui/logo.png");

        JFrame frame = new JFrame(s);
        frame.setSize(300, 175);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

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
                List<JFrame> frames = Browser.getFrames();
                synchronized (frames) {
                    frames.remove(frame);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

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

        JPasswordField pwd = new JPasswordField();
        pwd.putClientProperty("FlatLaf.style", "showRevealButton: true");
        List<JButton> btns = new ArrayList<>();

        T[] result = (T[]) new Object[]{null};

        boolean[] b = new boolean[]{false};
        ActionListener action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                btns.forEach((btn) -> btn.setEnabled(false));

                result[0] = verify.apply(pwd.getPassword());

                if (result[0] == null) {
                    btns.forEach((btn) -> btn.setEnabled(true));
                    if(pwd.getClientProperty("JComponent.outline") == null) pwd.putClientProperty("JComponent.outline", "error");
                } else {
                    b[0] = true;
                }

            }
        };

        pwd.addActionListener(action);
        pwd.setBounds(25, 25, 250, 25);
        frame.add(pwd);

        JButton done = new JButton("Done");
        done.setBounds(25, 75, 125, 25);
        btns.add(done);
        done.addActionListener(action);
        frame.add(done);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(150, 75, 125, 25);
        btns.add(cancel);
        cancel.addActionListener((e) -> frame.setVisible(false));
        frame.add(cancel);

        frame.setVisible(true);

        while (frame.isVisible()) {

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (b[0]) {
                frame.setVisible(false);
                return result[0];
            }

        }

        return null;

    }

    public static <T> T requestYUBI(String s, Function<VerificationResponse, T> verify) {
        JFrame frame = new JFrame(s);
        frame.setSize(300, 175);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        T[] result = (T[]) new Object[]{null};

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
                List<JFrame> frames = Browser.getFrames();
                synchronized (frames) {
                    frames.remove(frame);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

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

        JTextField pwd = new JTextField();
        List<JButton> btns = new ArrayList<>();

        boolean[] b_tmp = new boolean[]{false},
                b = new boolean[]{false};
        ActionListener action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result[0] = auth(b_tmp, btns, pwd, verify);
                b[0] = b_tmp[0];
            }
        };

        pwd.addActionListener(action);
        pwd.setBounds(25, 25, 250, 25);
        frame.add(pwd);

        JButton done = new JButton("Done");
        done.setBounds(25, 75, 125, 25);
        btns.add(done);
        done.addActionListener(action);
        frame.add(done);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(150, 75, 125, 25);
        btns.add(cancel);
        cancel.addActionListener((e) -> frame.setVisible(false));
        frame.add(cancel);

        frame.setVisible(true);

        while (frame.isVisible()) {

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (b[0]) {
                frame.setVisible(false);
                return result[0];
            }

        }

        return null;
    }

    private static final int CLIENT_ID = 73782;
    private static final String SECRET = "z2X7fv+UguOtKIcXmW8K5US4AU4=";

    private static <T> T auth(boolean[] b, List<JButton> btns, JTextField pwd, Function<VerificationResponse, T> verify) {

        btns.forEach((btn) -> btn.setEnabled(false));

        String otp = pwd.getText();

        if (otp.isEmpty()) {
            T object = verify.apply(new VerificationResponse() {
                @Override
                public boolean isOk() {
                    return false;
                }

                @Override
                public String getH() {
                    return null;
                }

                @Override
                public String getT() {
                    return null;
                }

                @Override
                public ResponseStatus getStatus() {
                    return null;
                }

                @Override
                public String getTimestamp() {
                    return null;
                }

                @Override
                public String getSessioncounter() {
                    return null;
                }

                @Override
                public String getSessionuse() {
                    return null;
                }

                @Override
                public String getSl() {
                    return null;
                }

                @Override
                public String getOtp() {
                    return null;
                }

                @Override
                public String getNonce() {
                    return null;
                }

                @Override
                public Map<String, String> getKeyValueMap() {
                    return null;
                }

                @Override
                public String getPublicId() {
                    return null;
                }
            });
            if (object == null) {
                btns.forEach((btn) -> btn.setEnabled(true));
                if(pwd.getClientProperty("JComponent.outline") == null) pwd.putClientProperty("JComponent.outline", "error");
                return null;
            } else {
                b[0] = true;
                return object;
            }
        }

        YubicoClient client = YubicoClient.getClient(CLIENT_ID, SECRET);

        try {
            VerificationResponse response = client.verify(otp);

            if (response.isOk()) {

                T result = verify.apply(response);

                if (result == null) {
                    throw new IOException("Verifier failed!");
                } else {
                    b[0] = true;
                    return result;
                }

            }
        } catch (Throwable e) {
            btns.forEach((btn) -> btn.setEnabled(true));
            pwd.setBackground(Color.RED);
            return null;
        }
        return null;
    }
}
