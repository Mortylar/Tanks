package edu.school21.client;

import com.google.gson.Gson;
import edu.school21.observers.Observable;
import edu.school21.state.StateManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private static final String LOGIN_COMMAND = "SignIn";
    private static final String REGISTRATION_COMMAND = "SignUp";
    private static final Long INVALID_ID = 0L;

    private static final String LEFT_KEY = "a";
    private static final String RIGHT_KEY = "d";
    private static final String FIRE_KEY = " ";

    private static final int LEFT_DIRECTION = -1;
    private static final int FIRE_ACTION = 0;
    private static final int RIGHT_DIRECTION = 1;
    private static final int NO_ACTION = 2;

    private Socket socket;
    private Long id = INVALID_ID;
    private String name;
    private BufferedReader inStream;
    private PrintWriter outStream;
    private StateManager manager;
    private Gson gson;
    private Observable observer;
    private boolean gameStatus = true;

    public Client() { this.gson = new Gson(); }

    public void setIpAndPort(String ip, int port) throws Exception {
        this.socket = new Socket(ip, port);
        this.inStream =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outStream =
            new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                                socket.getOutputStream())),
                            true);
    }

    public boolean isConnected() {
        if (this.socket == null) {
            return false;
        }
        return socket.isConnected();
    }

    public void setId(Long id) { this.id = id; }

    public Long getId() { return this.id; }

    public void setObserver(Observable observer) { this.observer = observer; }

    public void sendSignal() { this.observer.notifyView(); }

    public void endGame() {
        this.gameStatus = false;
        try {
            this.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean isEndGame() { return !this.gameStatus; }

    public void updateStatistic() { this.manager.updateStatistic(this.id); }

    public String getStatisticInfo() {
        String format = "Shots = %d\nHits = %d\nMisses = %d\n";
        String total =
            String.format(format, this.manager.getTotalShots(this.id),
                          this.manager.getTotalHits(this.id),
                          this.manager.getTotalMisses(this.id));
        String current = String.format(format, this.manager.getShots(this.id),
                                       this.manager.getHits(this.id),
                                       this.manager.getMisses(this.id));

        return String.format(
            "Current game statistic:\n%s\nTotal statistic:\n%s\n", current,
            total);
    }
    public boolean login(String name) {
        try {
            outStream.println(LOGIN_COMMAND);
            outStream.println(name);
            outStream.flush();
            this.id = Long.parseLong(inStream.readLine());
            if (id == INVALID_ID) {
                return false;
            }
            this.name = name;
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean registration(String name) {
        try {
            outStream.println(REGISTRATION_COMMAND);
            outStream.println(name);
            outStream.flush();
            this.id = Long.parseLong(inStream.readLine());
            if (id == INVALID_ID) {
                return false;
            }
            this.name = name;
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public String getName() { return this.name; }

    public void close() throws Exception {
        inStream.close();
        outStream.close();
        socket.close();
    }

    public void sendAction(String action) {
        outStream.println(action);
        outStream.flush();
    }

    public String readState() throws IOException { return inStream.readLine(); }

    public void setAction(String action) {
        if (this.gameStatus) {
            if (LEFT_KEY.equals(action)) {
                sendAction(this.gson.toJson(LEFT_DIRECTION));
            } else if (RIGHT_KEY.equals(action)) {
                sendAction(this.gson.toJson(RIGHT_DIRECTION));
            } else if (FIRE_KEY.equals(action)) {
                sendAction(this.gson.toJson(FIRE_ACTION));
            }
        } else {
            sendAction(this.gson.toJson(NO_ACTION));
        }
    }

    public void playGame() {
        this.manager = new StateManager();
        new Listener(this.manager).start();
    }

    public StateManager getStateManager() { return this.manager; }

    public class Listener extends Thread {

        private StateManager manager;
        private Gson gson;

        public Listener(StateManager manager) {
            this.manager = manager;
            this.gson = new Gson();
        }

        @Override
        public void run() {
            try {
                while (Client.this.gameStatus) {
                    Client.this.manager = gson.fromJson(Client.this.readState(),
                                                        StateManager.class);
                    Client.this.sendSignal();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
