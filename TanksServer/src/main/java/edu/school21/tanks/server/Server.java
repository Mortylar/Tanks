package edu.school21.tanks.server;

import com.google.gson.Gson;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("Server")
public class Server {

    @Autowired private UsersService usersService;

    @Autowired private StatisticsService statisticsService;

    private ServerSocket server;
    private Client first;
    private Client second;
    // private User first;
    // private User second;
    // private Socket firstClient;
    // private Socket secondClient;

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

        } catch (Exception e) {
            System.err.printf("\n%s\nExiting..", e.getMessage());
            return;
        }
    }

    private class Client {
        User user;
        Socket socket;

        public Client() {}

        public void setUser(User user) { this.user = user; }

        public void setSocket(Socket socket) { this.socket = socket; }

        public User getUser() { return this.user; }

        public Socket getSocket() { return this.socket; }

        public Long getId() { return this.user.getId(); }
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
            // System.out.printf("\nAAAAAAuth\n");
            Optional<User> user = Optional.empty();
            BufferedReader inStream = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
            PrintWriter outStream =
                new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                                    client.getOutputStream())),
                                true);
            String message = inStream.readLine();
            System.out.printf("\nmessage = %s\n", message);
            System.out.printf("\nmessage = %s\n", message.equals("SignUp"));
            if (null == message) {
                System.out.printf("\nNNNNNNNN\n");
                return null;
            }
            if (message.equals("SignUp")) {
                System.err.printf("\nReg  %s\n", message);
                user = usersService.signUp(inStream.readLine());
                System.out.printf("\nReg\n");
            }
            if ("SignIn".equals(message)) {
                System.out.printf("\nINNN\n");
                user = usersService.signIn(inStream.readLine());
            }
            if (!user.isPresent()) {
                System.out.printf("\nNULLLLLL\n");
                outStream.println("0");
                return null;
            }
            System.out.printf("\nNOT NULL  %s\n", user.get().getId());
            outStream.println(user.get().getId());
            return user.get();
        }
    }
}
