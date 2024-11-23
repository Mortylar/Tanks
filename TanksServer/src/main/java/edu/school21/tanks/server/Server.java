package edu.school21.tanks.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.school21.state.StateManager;
import edu.school21.tanks.models.Statistic;
import edu.school21.tanks.models.User;
import edu.school21.tanks.services.StatisticsService;
import edu.school21.tanks.services.UsersService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("Server")
public class Server {

    public static final int EXIT_NONE = 0;
    public static final int EXIT_KILL = 1;
    public static final int EXIT_ERROR = 2;
    public static final int EXIT_FROM_CLIENT = 3;

    @Autowired private UsersService usersService;

    @Autowired private StatisticsService statisticsService;

    private ServerSocket server;
    private Client first;
    private Client second;
    private StateManager gameManager;
    private int exitStatus = EXIT_NONE;

    public Server(UsersService uService, StatisticsService sService) {
        this.usersService = uService;
        this.statisticsService = sService;
        this.first = new Client();
        this.second = new Client();
    }

    public void run(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            server = socket;
            System.out.printf("\nServer running in %d port\n", port);

            AuthenticateThread firstThread =
                new AuthenticateThread(server, first);
            AuthenticateThread secondThread =
                new AuthenticateThread(server, second);
            firstThread.start();
            secondThread.start();
            firstThread.join();
            secondThread.join();
            gameLoop();

        } catch (Exception e) {
            System.err.printf("\n%s\nExiting..", e.getMessage());
            return;
        }
    }

    public boolean isAlreadyLogin(String name) {
        if (this.first.getUser() != null) {
            if (this.first.getUser().getName().equals(name)) {
                return true;
            }
        }
        if (this.second.getUser() != null) {
            if (this.second.getUser().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void gameLoop() throws IOException {
        this.first.createStreams();
        this.second.createStreams();
        this.gameManager =
            new StateManager(this.first.getId(), this.second.getId());
        SenderThread sender =
            new SenderThread(this.first, this.second, this.gameManager);
        ReaderThread firstReader =
            new ReaderThread(this.first, this.gameManager);
        ReaderThread secondReader =
            new ReaderThread(this.second, this.gameManager);
        sender.start();
        firstReader.start();
        secondReader.start();
        return;
    }

    public void checkKilling() {
        if (this.gameManager.isKilled()) {
            exitStatus = EXIT_KILL;
        }
    }

    private class Client {

        private User user;
        private Socket socket;
        private BufferedReader inStream;
        private PrintWriter outStream;

        public Client() {}

        public void setUser(User user) { this.user = user; }

        public void setSocket(Socket socket) { this.socket = socket; }

        public User getUser() { return this.user; }

        public Socket getSocket() { return this.socket; }

        public Long getId() { return this.user.getId(); }

        public void createStreams() throws IOException {
            this.inStream = new BufferedReader(
                new InputStreamReader(this.socket.getInputStream()));
            this.outStream = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(this.socket.getOutputStream())));
        }

        public BufferedReader getInputStream() { return this.inStream; }

        public PrintWriter getOutputStream() { return this.outStream; }
    }

    private class AuthenticateThread extends Thread {

        private ServerSocket server;
        private Client client;

        public AuthenticateThread(ServerSocket server, Client client) {
            this.server = server;
            this.client = client;
        }

        @Override
        public void run() {
            catchUser();
        }

        private void catchUser() {
            while (null == client.getUser()) {
                try {
                    client.setSocket(server.accept());
                    System.out.println("Accepted client");
                    while (null == client.getUser()) {
                        client.setUser(authenticate(client.getSocket()));
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    client.setUser(null);
                }
            }
        }

        private User authenticate(Socket client) throws Exception {
            Optional<User> user = Optional.empty();
            BufferedReader inStream = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
            PrintWriter outStream =
                new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                                    client.getOutputStream())),
                                true);
            String message = inStream.readLine();
            if (null == message) {
                return null;
            }
            try {
                if (message.equals("SignUp")) {
                    String name = inStream.readLine();
                    checkName(name);
                    user = usersService.signUp(name);
                }
                if ("SignIn".equals(message)) {
                    String name = inStream.readLine();
                    checkName(name);
                    user = usersService.signIn(name);
                }
                if (!user.isPresent()) {
                    outStream.println("0");
                    return null;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                outStream.println("0");
                return null;
            }
            outStream.println(user.get().getId());
            return user.get();
        }

        private void checkName(String name) throws Exception {
            if (Server.this.isAlreadyLogin(name)) {
                throw new Exception(
                    String.format("Client %s is already login.", name));
            }
        }
    }

    private class SenderThread extends Thread {

        private static final int delta = 100;

        private Client first;
        private Client second;
        private StateManager manager;
        private Gson gson;

        public SenderThread(Client first, Client second, StateManager manager) {
            this.first = first;
            this.second = second;
            this.manager = manager;
            this.gson = new GsonBuilder().create();
        }

        @Override
        public void run() {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (Server.this.exitStatus == Server.this.EXIT_NONE) {
                        sendState();
                    } else {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                        sendState();
                        return;
                    }
                }
            }, 0, this.delta);
        }

        private void sendState() {
            manager.moveBullets();

            String state = this.gson.toJson(this.manager);
            this.first.getOutputStream().println(state);
            this.second.getOutputStream().println(state);
            this.first.getOutputStream().flush();
            this.second.getOutputStream().flush();
        }
    }

    private class ReaderThread extends Thread {

        private static final int ACTION_MOVE_LEFT = -1;
        private static final int ACTION_SHOT = 0;
        private static final int ACTION_MOVE_RIGHT = 1;
        private static final int ACTION_END = 2;

        private Client client;
        private StateManager manager;
        private Gson gson;

        public ReaderThread(Client client, StateManager manager) {
            this.client = client;
            this.manager = manager;
            this.gson = new GsonBuilder().create();
        }

        @Override
        public void run() {
            try {
                while (Server.this.exitStatus == Server.this.EXIT_NONE) {
                    String answer = client.getInputStream().readLine();
                    if (answer == null) {
                        Server.this.exitStatus = Server.this.EXIT_ERROR;
                        break;
                    }
                    int action = gson.fromJson(answer, int.class);
                    if (action == ACTION_END) {
                        Server.this.exitStatus = Server.this.EXIT_FROM_CLIENT;
                        break;
                    }
                    updateState(action);
                }
            } catch (IOException e) {
                Server.this.exitStatus = Server.this.EXIT_ERROR;
                return;
            }
        }

        private void updateState(int action) {
            if ((ACTION_MOVE_LEFT == action) || (ACTION_MOVE_RIGHT == action)) {
                manager.move(client.getId(), action);
            } else if (ACTION_SHOT == action) {
                manager.fire(client.getId());
            }
        }
    }
}
