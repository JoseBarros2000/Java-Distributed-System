package com.project.ServiceSI;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class ServerSI {

    static int DEFAULT_PORT=2000;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        ServerSocket servidor = null;

    // Create a server socket, bound to the specified port: API java.net.ServerSocket

        try {
            servidor = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Erro na criação do socket: "+e);
            System.exit(1);
        }

        System.out.println("Servidor a' espera de ligacoes no porto " + port);

        while(true) {
            try {

    // Listen for a connection to be made to the socket and accepts it: API java.net.ServerSocket

                Socket ligacao = servidor.accept();

    // Start a GetServicoSIRequestHandler thread

                GetServicoSIRequestHandler socketThread = null;
                try {
                    socketThread = new GetServicoSIRequestHandler(ligacao);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                socketThread.start();

            } catch (IOException e) {
                System.out.println("Erro na execucao do servidor: "+e);
                System.exit(1);
            }
        }
    }
}
