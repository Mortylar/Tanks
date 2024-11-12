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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("Server")
public class Server {

    @Autowired private UsersService usersService;

    @Autowired private StatisticsService statisticsService;

    private ServerSocket server;
    private Client first;
    private Client second;
    private StateManager gameManager;

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
        // TODO
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
            if (message.equals("SignUp")) {
                user = usersService.signUp(inStream.readLine());
            }
            if ("SignIn".equals(message)) {
                user = usersService.signIn(inStream.readLine());
            }
            if (!user.isPresent()) {
                outStream.println("0");
                return null;
            }
            outStream.println(user.get().getId());
            return user.get();
        }
    }

    private class SenderThread extends Thread {

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
                    sendState();
                }
            }, 0, 500);
        }

        private void sendState() {
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
                while (true) {
                    updateState(gson.fromJson(
                        client.getInputStream().readLine(), int.class));
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
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
