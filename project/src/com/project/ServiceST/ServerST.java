package com.project.ServiceST;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

public class ServerST {
    static int DEFAULT_PORT=2001;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;


        ServiceST servico = null;
        try {
            servico=new ServiceST();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        String ficheiroDados = String.format("%s\\utilizador.data", System.getProperty("user.dir"));
        Serializacao serializacao = new Serializacao(ficheiroDados);

        //if the file does not exist
        if (!serializacao.getFicheiro().exists()) {

        }else{
            servico = serializacao.carregar();
        }

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

                GetServicoSTRequestHandler socketThread = null;
                try {
                    socketThread = new GetServicoSTRequestHandler(ligacao,serializacao,servico);
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
