package com.project.Client;

import com.project.ServiceTemperature.ServicesInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.time.Instant;

public class Cliente {

    public static void main(String[] args) {
        try{
            String servidor = args[0];
            int porto = Integer.parseInt(args[1]);

            if ((args.length == 3 && args[2].length() == 8) || (args.length == 5) || (args.length == 7) || (args.length == 3 && args[2].length() == 24) || (args.length == 4 && args[3].length() == 24)) {
                if (args.length == 4 ) { // get temperature
                    try {
                        ServicesInterface services = (ServicesInterface) LocateRegistry.getRegistry(servidor).lookup(args[2]);
                        float temperature = services.getTemp(Instant.parse(args[3]));
                        System.out.println("200 OK\n" + temperature);
                    }catch(Exception e) {
                        System.out.println("400 BAD REQUEST\n" + 9999.9999f);
                    }
                }else {

                    InetAddress serverAddress = null;
                    try {
                        serverAddress = InetAddress.getByName(servidor);
                    } catch (UnknownHostException e) {
                        System.out.println("Host inválido: " + e);
                        System.exit(1);
                    }

                    Socket ligacao = null;

                    // Create a client sockets (also called just "sockets"). A socket is an endpoint for communication between two machines: API java.net.Socket

                    try {
                        ligacao = new Socket(serverAddress, porto);
                    } catch (IOException e) {
                        System.out.println("Socket inválido: " + e);
                        System.exit(1);
                    }

                    // Create a java.io.BufferedReader for the Socket; Use java.io.Socket.getInputStream() to obtain the Socket input stream

                    BufferedReader input = null;
                    try {
                        input = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
                    } catch (IOException e) {
                        System.out.println("input inválido: " + e);
                        System.exit(1);
                    }

                    // Create a java.io. PrintWriter for the Socket; Use java.io.Socket.etOutputStream() to obtain the Socket output stream

                    PrintWriter output = null;
                    try {
                        output = new PrintWriter(ligacao.getOutputStream(), true);
                    } catch (IOException e) {
                        System.out.println("output inválido: " + e);
                        System.exit(1);
                    }

                    String request = "";
                    if (args.length == 3 && args[2].length() == 8) { //get hash
                        request = " get " + args[2];
                    }
                    if (args.length == 5) { // get service list
                        request = " get " + args[2] + " " + args[3] + " " + args[4];
                    }
                    if (args.length == 7) { // post service
                        request = " post " + args[2] + " " + args[3] + " " + args[4] + " " + args[5] + " " + args[6];
                    }
                    if (args.length == 3 && args[2].length() != 8) { // get humidity
                        request = " getHumidity " + args[2];
                    }

                    // write the request into the Socket

                    output.println(request);

                    String response = null;
                    String finalMsg = "";

                    // Read the server response - read the data until null

                    try {
                        while ((response = input.readLine()) != null) {
                            finalMsg += response + "\n";
                        }
                    } catch (IOException e) {
                        System.out.println("Resposta inválida: " + e);
                        System.exit(1);
                    }

                    // Close the Socket

                    try {
                        ligacao.close();
                    } catch (IOException e) {
                        System.out.println("Erro ao fechar socket:" + e);
                        System.exit(1);
                    }
                    System.out.println("Terminou a ligacao com a seguinte mensagem:\n" + finalMsg);
                }
            } else{
                    System.out.println("400 BAD REQUEST");
                    System.exit(-1);

                }
        }
        catch (Exception e){
            System.out.println("400 BAD REQUEST");
            System.exit(-1);
        }
    }
}
