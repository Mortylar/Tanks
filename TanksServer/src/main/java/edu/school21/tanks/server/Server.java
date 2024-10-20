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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("Server")
public class Server {

    @Autowired private UsersService usersService;

    @Autowired private StatisticsService statisticsService;

    public Server(UsersService uService, StatisticsService sService) {
        this.usersService = uService;
        this.statisticsService = sService;
    }

    public void run(int port) {}
}
