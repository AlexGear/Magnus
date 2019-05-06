package ru.eltex.magnus.streamer;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;
import javax.swing.*;

public class GUI extends JFrame {

    private static final int WINDOW_H = 250;
    private static final int WINDOW_W = 200;

    private static TrayIcon trayIcon;
    private static SystemTray systemTray;
    private static BufferedImage normalIcon;
    private static BufferedImage warningIcon;
    private static BufferedImage errorIcon;

    private static JLabel status;
    private static JTextField hostField;
    private static JTextField portField;
    private static JTextField loginField;
    private static JTextField passwordField;
    private static JButton connectButton;
    private static JButton disconnectButton;

    private static ArrayList<String> streamerDataArray;
    private static Streamer streamer;

    public GUI() {
        super("Magnus");

        loadIcons();
        fillWindowContent();
        setWindowSettings();
        setTraySettings();

        try {
            systemTray = SystemTray.getSystemTray();
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    private void loadIcons(){
        try {
            normalIcon = ImageIO.read(new File("normalIcon.jpg"));
            warningIcon = ImageIO.read(new File("warningIcon.jpg"));
            errorIcon = ImageIO.read(new File("errorIcon.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTraySettings() {
        trayIcon = new TrayIcon(normalIcon);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(actionEvent -> {
            setVisible(true);
            setState(JFrame.NORMAL);
        });

        MouseMotionListener mouM = new MouseMotionListener() {
            public void mouseDragged(MouseEvent ev) {
            }

            public void mouseMoved(MouseEvent ev) {
                trayIcon.setToolTip(status.getText());
            }
        };
        trayIcon.addMouseMotionListener(mouM);
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
        connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(WINDOW_W - 30, 30));
        connectButton.addActionListener(actionEvent-> {
            setItemsEnabled(false);
            createStreamer();
        });
        add(connectButton);

        disconnectButton = new JButton("Disconnect");
        disconnectButton.setPreferredSize(new Dimension(WINDOW_W - 30, 30));
        disconnectButton.setVisible(false);
        disconnectButton.addActionListener(actionEvent -> Streamer.disconnect());
        add(disconnectButton);
    }

    private static void setItemsEnabled(boolean value) {
        hostField.setEnabled(value);
        portField.setEnabled(value);
        loginField.setEnabled(value);
        passwordField.setEnabled(value);

        connectButton.setVisible(value);
        disconnectButton.setVisible(!value);
    }

    private void createStreamer() {
        streamerDataArray = new ArrayList<>();

        streamerDataArray.add(loginField.getText());
        streamerDataArray.add(passwordField.getText());
        streamerDataArray.add(hostField.getText());
        streamerDataArray.add(portField.getText());

        for(String data : streamerDataArray){
            if(data.isEmpty()){
                sendUserErrorMsg("Failed, fill all fields");
                return;
            }
        }
            new Thread(()-> new Streamer(streamerDataArray.get(0), streamerDataArray.get(1),
                    streamerDataArray.get(2), Integer.valueOf(streamerDataArray.get(3)))).start();
    }

    private static void displayMsg(String msg, MessageType type) {
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

    public static void sendUserErrorMsg(String msg){
        trayIcon.displayMessage("", msg, MessageType.ERROR);
        displayMsg(msg, MessageType.ERROR);
        setItemsEnabled(true);
    }

    public static void sendUserWarningMsg(String msg){
        displayMsg(msg, MessageType.WARNING);
    }

    public static void sendUserInformMsg(String msg){
        displayMsg(msg, MessageType.INFO);
    }

    public void setStreamerDataArray(ArrayList<String> data) {
        streamerDataArray = data;

        loginField.setText(streamerDataArray.get(0));
        passwordField.setText(streamerDataArray.get(1));
        hostField.setText(streamerDataArray.get(2));
        portField.setText(streamerDataArray.get(3));

        setItemsEnabled(false);
    }
}