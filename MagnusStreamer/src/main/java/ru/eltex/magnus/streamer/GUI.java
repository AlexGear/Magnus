package ru.eltex.magnus.streamer;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import javax.imageio.*;
import javax.swing.*;

import static javax.swing.JOptionPane.*;

public class GUI extends JFrame {

    private static final int WINDOW_H = 300;
    private static final int WINDOW_W = 300;

    private static GUI instance;

    private TrayIcon trayIcon;
    private BufferedImage normalIcon;
    private BufferedImage warningIcon;
    private BufferedImage errorIcon;

    private JLabel status;
    private JTextField hostField;
    private JTextField portField;
    private JTextField loginField;
    private JTextField passwordField;
    private JButton okButton;
    private JButton disconnectButton;

    public static void init() {
        if(instance == null) {
            instance = new GUI();
        }
    }

    public static void sendUserErrorMsg(String msg) {
        instance.errorMsg(msg);
    }

    public static void sendUserWarningMsg(String msg){
        instance.warningMsg(msg);
    }

    public static void sendUserInformMsg(String msg){
        instance.informMsg(msg);
    }

    private GUI() {
        super("Magnus");

        loadIcons();
        fillWindowContent();
        populateControlsUsingProperties();
        setWindowSettings();

        try {
            createTrayIcon();
        } catch (AWTException | UnsupportedOperationException e) {
            String message = "Failed to create tray icon: " + e.toString();
            showMessageDialog(this, message,"Tray icon creation error", ERROR_MESSAGE);
            dispose();
            throw new RuntimeException(message, e);
        }
    }

    private void loadIcons() {
        try {
            normalIcon = ImageIO.read(new File("normalIcon.jpg"));
            warningIcon = ImageIO.read(new File("warningIcon.jpg"));
            errorIcon = ImageIO.read(new File("errorIcon.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTrayIcon() throws AWTException {
        trayIcon = new TrayIcon(normalIcon);
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(true);
                setState(JFrame.NORMAL);
            }
        });

        MouseMotionListener mouM = new MouseMotionListener() {
            public void mouseDragged(MouseEvent ev) {
            }

            public void mouseMoved(MouseEvent ev) {
                trayIcon.setToolTip(status.getText());
            }
        };
        trayIcon.addMouseMotionListener(mouM);
        SystemTray systemTray = SystemTray.getSystemTray();
        systemTray.add(trayIcon);
    }

    private void setWindowSettings() {
        Container container = getContentPane();
        container.setLayout(new FlowLayout(FlowLayout.CENTER, WINDOW_W, 0));

        setAlwaysOnTop(true);
        setSize(WINDOW_W, WINDOW_H);
        setLocationRelativeTo(null);
        setIconImage(normalIcon);
        setResizable(false);
    }

    private void fillWindowContent() {

        status = new JLabel("   ");
        status.setSize(new Dimension(WINDOW_W - 30, 30));
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setVerticalAlignment(JLabel.VERTICAL);
        add(status);

        add(new JLabel("IP  "));
        hostField = new JTextField();
        hostField.setPreferredSize(new Dimension(WINDOW_W - 30, 30));
        add(hostField);

        add(new JLabel("PORT"));
        portField = new JTextField();
        portField.setPreferredSize(new Dimension(WINDOW_W - 30, 30));
        add(portField);

        add(new JLabel("LOGIN"));
        loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(WINDOW_W - 30, 30));
        add(loginField);

        add(new JLabel("PASSWORD"));
        passwordField = new JTextField();
        passwordField.setPreferredSize(new Dimension(WINDOW_W - 30, 30));
        add(passwordField);

        add(new JLabel("    "));
        okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(WINDOW_W - 30, 30));
        okButton.addActionListener(actionEvent-> {
            setItemsEnabled(false);
            updateProperties();
        });
        add(okButton);

        disconnectButton = new JButton("Disconnect");
        disconnectButton.setPreferredSize(new Dimension(WINDOW_W - 30, 30));
        disconnectButton.setVisible(false);
        disconnectButton.addActionListener(actionEvent -> App.STREAMER.disconnect());
        add(disconnectButton);
    }

    private void populateControlsUsingProperties() {
        hostField.setText(App.PROPERTIES.getServerAddress());
        portField.setText(String.valueOf(App.PROPERTIES.getServerPort()));
        loginField.setText(App.PROPERTIES.getLogin());
        passwordField.setText(App.PROPERTIES.getPassword());
    }

    private void setItemsEnabled(boolean value) {
        hostField.setEnabled(value);
        portField.setEnabled(value);
        loginField.setEnabled(value);
        passwordField.setEnabled(value);

        okButton.setVisible(value);
        disconnectButton.setVisible(!value);
    }

    private void updateProperties() {
        String host = hostField.getText().trim();
        String portStr = portField.getText().trim();
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (host.isEmpty() || portStr.isEmpty() || login.isEmpty() || password.isEmpty()) {
            errorMsg("Please, fill all the fields");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            errorMsg("Port must be a number");
            return;
        }
        if(port < 0 || port > 65535) {
            errorMsg("Port must be in range from 0 to 65535");
            return;
        }

        App.PROPERTIES.setServerAddress(host);
        App.PROPERTIES.setServerPort(port);
        App.PROPERTIES.setLogin(login);
        App.PROPERTIES.setPassword(password);
        App.PROPERTIES.save();

        App.STREAMER.onPropertiesUpdated();
    }

    private void displayMsg(String msg, MessageType type) {
        status.setText(msg);

        BufferedImage icon = normalIcon;
        Color color = Color.GREEN;
        switch (type) {
            case WARNING: icon = warningIcon; color = Color.RED; break;
            case ERROR: icon = errorIcon; color = Color.RED; break;
        }

        trayIcon.setImage(icon);
        status.setForeground(color);
    }

    private void errorMsg(String msg) {
        trayIcon.displayMessage("", msg, MessageType.ERROR);
        displayMsg(msg, MessageType.ERROR);
        setItemsEnabled(true);
    }

    private void warningMsg(String msg) {
        instance.displayMsg(msg, MessageType.WARNING);
    }

    private void informMsg(String msg) {
        displayMsg(msg, MessageType.INFO);
    }
}