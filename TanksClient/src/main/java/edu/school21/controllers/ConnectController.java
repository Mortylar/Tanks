package edu.school21.controllers;

import edu.school21.observers.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ConnectController {

    private static final String LOCALHOST_ADDRESS = "localhost";

    @FXML private Button connect;
    @FXML private Button cancel;
    @FXML private TextField port;
    @FXML private TextField address;

    private Observable observer;

    @FXML
    public void initialize() {
        connect.addEventHandler(MouseEvent.MOUSE_CLICKED,
                                new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent ev) {
                                        if (!isPortValidate()) {
                                            showWarning("Invalid port value");
                                        } else if (!isAddressValidate()) {
                                            showWarning("Invalid ip address");
                                        } else {
                                            observer.notifyView();
                                        }
                                    }
                                });

        cancel.addEventHandler(MouseEvent.MOUSE_CLICKED,
                               new EventHandler<MouseEvent>() {
                                   @Override
                                   public void handle(MouseEvent ev) {
                                       reset();
                                   }
                               });
    }

    public void showWarning(String info) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setContentText(info);
        alert.showAndWait();
    }

    public void setObserver(Observable observer) { this.observer = observer; }

    public int getPort() { return Integer.parseInt(this.port.getText()); }

    public String getIpAddress() { return this.address.getText(); }

    public void reset() { port.clear(); }

    public boolean isPortValidate() {
        final int MIN_PORT = 1024;
        final int MAX_PORT = 65535;
        String portStr = port.getText();
        if (portStr == null) {
            return false;
        }

        int number;
        try {
            number = Integer.parseInt(portStr);
            if ((number < MIN_PORT) || (number > MAX_PORT)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isAddressValidate() {
        String addressStr = this.address.getText();
        if (null == addressStr) {
            return false;
        }
        if (addressStr.equals(LOCALHOST_ADDRESS)) {
            return true;
        }
        String[] octets = addressStr.split("\\.");
        final int OCTETS_COUNT = 4;
        if (octets.length != OCTETS_COUNT) {
            return false;
        }
        for (int i = 0; i < OCTETS_COUNT; ++i) {
            try {
                int octet = Integer.parseInt(octets[i]);
                if ((octet < 0) || (octet > 255)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
