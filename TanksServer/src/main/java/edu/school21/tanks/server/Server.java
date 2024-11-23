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
        private static final int SHOT_PAUSE = 1000;

        boolean isContiniousShot = false;
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
                Timer timer = new Timer(SHOT_PAUSE);
                timer.start();
                while (true) {
                    if (Server.this.exitStatus == Server.this.EXIT_NONE) {
                        int action = gson.fromJson(
                            client.getInputStream().readLine(), int.class);
                        updateState(action, timer);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        private void updateState(int action, Timer timer) {
            if ((ACTION_MOVE_LEFT == action) || (ACTION_MOVE_RIGHT == action)) {
                manager.move(client.getId(), action);
                timer.setTime(SHOT_PAUSE);
            } else if (ACTION_SHOT == action) {
                if (timer.getTime() > SHOT_PAUSE) {
                    manager.fire(client.getId());
                    timer.reset();
                }
            } else {
                timer.setTime(SHOT_PAUSE);
            }
        }

        private class Timer extends Thread {

            private static final int SLEEP_TIME = 500;

            private int time;
            private boolean closeStatus = false;

            public Timer(int start) { this.time = start; }

            public int getTime() { return this.time; }

            public void setTime(int time) { this.time = time; }

            public void reset() { this.time = 0; }

            public void close() { this.closeStatus = true; }

            @Override
            public void run() {
                while (true) {
                    ++this.time;
                    if (this.time < 0) {
                        this.time = 0;
                    }
                    try {
                        TimeUnit.MICROSECONDS.sleep(SLEEP_TIME);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    if (closeStatus) {
                        return;
                    }
                }
            }
        }
    }
}
