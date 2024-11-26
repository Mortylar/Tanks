package edu.school21.tanks.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.school21.state.StateManager;
import edu.school21.tanks.models.Statistic;
import edu.school21.tanks.models.User;
import edu.school21.tanks.pair.Pair;
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
import java.util.ArrayList;
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
    private static final int MAX_PAIR_COUNT = 100;

    @Autowired private UsersService usersService;

    @Autowired private StatisticsService statisticsService;

    private ServerSocket server;
    private ArrayList<Pair<Client>> clientList;
    private ArrayList<BabyServer> babyServerList;
    private boolean isCompletedPair;

    public Server(UsersService uService, StatisticsService sService) {
        this.usersService = uService;
        this.statisticsService = sService;
        this.clientList = new ArrayList<Pair<Client>>();
        this.babyServerList = new ArrayList<BabyServer>();
    }

    public void run(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            this.server = socket;
            System.out.printf("\nServer running in %d port\n", port);

            this.isCompletedPair = true;
            int num = 0;
            for (int i = 0; (i < MAX_PAIR_COUNT) && (!this.server.isClosed());
                 ++i) {
                if (this.isCompletedPair) {
                    this.babyServerList.add(new BabyServer(socket, i));
                    this.isCompletedPair = false;
                    babyServerList.get(i).start();
                } else {
                    i -= 1;
                }
            }

        } catch (Exception e) {
            System.err.printf("\n%s\nExiting..", e.getMessage());
            return;
        }
    }

    public BabyServer getBabyServer(int id) {
        return this.babyServerList.get(id);
    }

    public int getExitStatus(int id) {
        return this.getBabyServer(id).getExitStatus();
    }

    public void setExitStatus(int id, int status) {
        this.getBabyServer(id).setExitStatus(status);
    }

    public boolean isAlreadyLogin(String name) {
        for (Pair<Client> pair : this.clientList) {
            if (pair.getFirst().getUser() != null) {

                if (pair.getFirst().getUser().getName().equals(name)) {
                    return true;
                }
            }
            if (pair.getSecond().getUser() != null) {

                if (pair.getSecond().getUser().getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private class BabyServer extends Thread {

        private ServerSocket server;
        private int id;
        private int pairIndex = 0;
        private int exitStatus = Server.this.EXIT_NONE;
        private Client first;
        private Client second;

        public BabyServer(ServerSocket server, int id) throws Exception {
            this.server = server;
            this.id = id;
            this.first = new Client();
            this.second = new Client();
        }

        public int getExitStatus() { return this.exitStatus; }

        public void setExitStatus(int status) { this.exitStatus = status; }

        @Override
        public void run() {
            pairIndex = Server.this.clientList.size();
            Server.this.clientList.add(new Pair<Client>(first, second));
            AuthenticateThread firstThread =
                new AuthenticateThread(this.server, first);
            AuthenticateThread secondThread =
                new AuthenticateThread(this.server, second);
            firstThread.start();
            secondThread.start();
            try {
                firstThread.join();
                secondThread.join();
                Server.this.isCompletedPair = true;
                gameLoop(this.id, first, second);
                while (this.exitStatus == Server.this.EXIT_NONE) {
                }
                close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        private int gameLoop(int id, Client first, Client second)
            throws Exception {
            first.createStreams();
            second.createStreams();
            StateManager gameManager =
                new StateManager(first.getId(), second.getId());
            setOldStatistic(gameManager, first.getId());
            setOldStatistic(gameManager, second.getId());
            SenderThread sender =
                new SenderThread(id, first, second, gameManager);
            ReaderThread firstReader = new ReaderThread(id, first, gameManager);
            ReaderThread secondReader =
                new ReaderThread(id, second, gameManager);
            sender.start();
            firstReader.start();
            secondReader.start();
            return Server.this.EXIT_KILL;
        }

        private void setOldStatistic(StateManager manager, Long id) {
            Optional<Statistic> stat =
                Server.this.statisticsService.findByUserId(id);
            if (stat.isPresent()) {
                manager.setOldStatistic(id, stat.get().getShots(),
                                        stat.get().getHits(),
                                        stat.get().getMisses());
            }
        }

        private void close() throws Exception {
            first.close();
            second.close();
            Server.this.clientList.remove(pairIndex);
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

        public void close() throws Exception {
            inStream.close();
            outStream.close();
            socket.close();
        }
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
                    client.setSocket(this.server.accept());
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
        private int id;

        public SenderThread(int id, Client first, Client second,
                            StateManager manager) {
            this.id = id;
            this.first = first;
            this.second = second;
            this.manager = manager;
            this.gson = new GsonBuilder().create();
        }

        @Override
        public void run() {
            Timer timer = new Timer();
            final int SLEEP_TIME = 5;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (Server.this.getExitStatus(id) ==
                        Server.this.EXIT_NONE) {
                        sendState();
                    } else {
                        try {
                            sendState();
                            saveStats(first.getUser());
                            saveStats(second.getUser());
                            timer.cancel();
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                        return;
                    }
                }
            }, 0, this.delta);
        }

        private void saveStats(User user) {
            int shots = this.manager.getShots(user.getId());
            int hits = this.manager.getHits(user.getId());
            int misses = this.manager.getMisses(user.getId());
            Statistic local = new Statistic(user, shots, hits, misses);

            Optional<Statistic> stat =
                Server.this.statisticsService.findByUserId(user.getId());
            if (!stat.isPresent()) {
                Server.this.statisticsService.save(local);
            } else {
                Server.this.statisticsService.update(local.add(stat.get()));
            }
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
        private int id;

        public ReaderThread(int id, Client client, StateManager manager) {
            this.id = id;
            this.client = client;
            this.manager = manager;
            this.gson = new GsonBuilder().create();
        }

        @Override
        public void run() {
            try {
                while (Server.this.getExitStatus(this.id) ==
                       Server.this.EXIT_NONE) {
                    String answer = client.getInputStream().readLine();
                    if (answer == null) {
                        Server.this.setExitStatus(this.id,
                                                  Server.this.EXIT_ERROR);
                        break;
                    }
                    int action = gson.fromJson(answer, int.class);
                    if (action == ACTION_END) {
                        Server.this.setExitStatus(this.id,
                                                  Server.this.EXIT_FROM_CLIENT);
                        break;
                    }
                    updateState(action);
                    if (manager.isKilled()) {
                        Server.this.setExitStatus(this.id,
                                                  Server.this.EXIT_KILL);
                    }
                }
            } catch (IOException e) {
                Server.this.setExitStatus(this.id, Server.this.EXIT_ERROR);
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
