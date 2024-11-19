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

    private Socket socket;
    private String ip;
    private Long id = INVALID_ID;
    private BufferedReader inStream;
    private PrintWriter outStream;
    private StateManager manager;
    private Gson gson;
    private Observable observer;
    private boolean gameStatus = true;

    public Client(String ip) {
        this.ip = ip;
        this.gson = new Gson();
    }

    public void setPort(int port) throws Exception {
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

    public void endGame() { this.gameStatus = false; }

    public String getStatisticInfo() {
        return String.format("Shots = %d\nHits = %d\nMisses = %d\n",
                             this.manager.getShots(this.id),
                             this.manager.getHits(this.id),
                             this.manager.getMisses(this.id));
    }
    public boolean login(String name) {
        try {
            outStream.println(LOGIN_COMMAND);
            outStream.println(name);
            outStream.flush();
            this.id = Long.parseLong(inStream.readLine());
            System.out.printf("\nClient %s id = %d\n", name, id);
            if (id == INVALID_ID) {
                return false;
            }
            return true;
        } catch (Exception e) {
            System.out.printf("AAAAA");
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
            System.out.printf("\nClient %s id = %d\n", name, id);
            if (id == INVALID_ID) {
                return false;
            }
            return true;
        } catch (Exception e) {
            System.out.printf("AAAAA");
            System.err.println(e.getMessage());
            return false;
        }
    }

    public void close() throws Exception {
        inStream.close();
        outStream.close();
        socket.close(); // TODO check closing
    }

    public void sendAction(String action) {
        outStream.println(action);
        outStream.flush();
    }

    public String readState() throws IOException { return inStream.readLine(); }

    public void setAction(String action) {
        if (LEFT_KEY.equals(action)) {
            sendAction(this.gson.toJson(LEFT_DIRECTION));
        } else if (RIGHT_KEY.equals(action)) {
            sendAction(this.gson.toJson(RIGHT_DIRECTION));
        } else if (FIRE_KEY.equals(action)) {
            sendAction(this.gson.toJson(FIRE_ACTION));
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
                while (true) {
                    Client.this.manager = gson.fromJson(Client.this.readState(),
                                                        StateManager.class);
                    Client.this.sendSignal();
                    if (Client.this.gameStatus == false) {
                        return;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
