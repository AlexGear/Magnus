package ru.eltex.magnus.streamer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

import static javax.swing.JOptionPane.*;

public class GUI extends JFrame {

    private static final int WINDOW_H = 300;
    private static final int WINDOW_W = 270;

    private static final Logger LOG = LogManager.getLogger(GUI.class);

    private static GUI instance;

    private TrayIcon trayIcon;
    private BufferedImage normalIcon;
    private BufferedImage warningIcon;
    private BufferedImage errorIcon;

    public enum TrayIconState { NORMAL, WARNING, ERROR }

    private JLabel status;
    private JTextField hostField;
    private JTextField portField;
    private JTextField loginField;
    private JTextField passwordField;

    public static void init() {
        if(instance == null) {
            instance = new GUI();
        }
    }

    public static GUI getInstance() {
        return instance;
    }

    private GUI() {
        super("Magnus");

        LOG.info("Creating GUI instance");

        loadIcons();
        fillWindowContent();
        populateControlsUsingProperties();
        setWindowSettings();

        try {
            createTrayIcon();
        } catch (AWTException | UnsupportedOperationException e) {
            String message = "Failed to create tray icon: " + e.toString();
            showMessageDialog(this, message,"Tray icon creation error", ERROR_MESSAGE);
            LOG.error(message);
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
            final String message = "Failed to load icon(s)";
            LOG.error(message);
            throw new RuntimeException(message, e);
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

        add(new JLabel("IP"));
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
        JButton saveButton = new JButton("Save and Hide");
        saveButton.setPreferredSize(new Dimension(WINDOW_W - 30, 30));
        saveButton.addActionListener(actionEvent-> {
            if(updateProperties()) {
                setVisible(false);
            }
        });
        add(saveButton);
    }

    private void populateControlsUsingProperties() {
        hostField.setText(App.PROPERTIES.getServerAddress());
        portField.setText(String.valueOf(App.PROPERTIES.getServerPort()));
        loginField.setText(App.PROPERTIES.getLogin());
        passwordField.setText(App.PROPERTIES.getPassword());
    }

    private boolean updateProperties() {
        String host = hostField.getText().trim();
        String portStr = portField.getText().trim();
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (host.isEmpty() || portStr.isEmpty() || login.isEmpty() || password.isEmpty()) {
            setStatusMessage("Please, fill all the fields", MessageType.ERROR);
            return false;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            setStatusMessage("Port must be a number", MessageType.ERROR);
            return false;
        }
        if(port < 0 || port > 65535) {
            setStatusMessage("Port must be in range [0; 65535]", MessageType.ERROR);
            return false;
        }

        App.PROPERTIES.setServerAddress(host);
        App.PROPERTIES.setServerPort(port);
        App.PROPERTIES.setLogin(login);
        App.PROPERTIES.setPassword(password);
        if(!App.PROPERTIES.save()) {
            final String message = "Failed to save properties";
            final String title = "Error saving properties";
            JOptionPane.showMessageDialog(this, message, title, WARNING_MESSAGE);
        }

        App.STREAMER.onPropertiesUpdated();

        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            revalidate();
            repaint();
        }
    }

    public void setTrayIconState(TrayIconState state) {
        BufferedImage icon = normalIcon;
        switch (state) {
            case WARNING: icon = warningIcon;
            case ERROR: icon = errorIcon;
        }
        trayIcon.setImage(icon);
        icon.flush();
    }

    public void setStatusMessage(String msg, MessageType type) {
        LOG.info("Displaying " + type.name() + " status message: " + msg);
        status.setText(msg);

        Color color = Color.GREEN;
        switch (type) {
            case WARNING:
            case ERROR:
                color = Color.RED; break;
        }
        status.setForeground(color);
    }

    public void displayTrayMessage(String msg, MessageType type) {
        LOG.info("Displaying " + type.name() + " tray message: " + msg);
        trayIcon.displayMessage("Magnus", msg, type);
    }
}