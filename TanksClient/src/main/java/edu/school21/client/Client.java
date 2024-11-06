package edu.school21.client;

import com.google.gson.Gson;
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

    private Socket socket;
    private String ip;
    private Long id = INVALID_ID;
    private BufferedReader inStream;
    private PrintWriter outStream;

    public Client(String ip) { this.ip = ip; }

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
}
