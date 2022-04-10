package com.project.ServiceSI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.util.StringTokenizer;

public class GetServicoSIRequestHandler extends Thread{

    Socket ligacao;
    BufferedReader in;
    PrintWriter out;
    static int DEFAULT_PORT_ST=2001;
    static String DEFAULT_HOST_ST="127.0.0.1";

    public GetServicoSIRequestHandler(Socket ligacao) throws NoSuchAlgorithmException {
        this.ligacao = ligacao;
        try {
            this.in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
            this.out = new PrintWriter(ligacao.getOutputStream());
        } catch (IOException e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }


        public void run() {
            try {
                System.out.println("Aceitou ligacao de cliente no endereco " + ligacao.getInetAddress() + " na porta " + ligacao.getPort());
                String msg = in.readLine();
                System.out.println("Request=" + msg);

                StringTokenizer tokens = new StringTokenizer(msg);
                String method = tokens.nextToken();
                if(tokens.countTokens()==1) {
                    String identificador = tokens.nextToken();

                    MessageDigest m = MessageDigest.getInstance("MD5");
                    m.reset();
                    m.update(identificador.getBytes("UTF-8"));
                    byte[] digest = m.digest();
                    BigInteger bigInt = new BigInteger(1, digest);
                    String hashtext = bigInt.toString(16);
                    // Now we need to zero pad it if you actually want the full 32 chars.

                    while (hashtext.length() < 32) {
                        hashtext = "0" + hashtext;
                    }
                    hashtext += "\n" + DEFAULT_HOST_ST + " " + DEFAULT_PORT_ST;
                    out.println(hashtext);

                    out.flush();
                    in.close();
                    out.close();
                    ligacao.close();
                }
                else {
                    out.println("400 BAD REQUEST");

                    out.flush();
                    in.close();
                    out.close();
                    ligacao.close();
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                System.out.println("Erro na execucao do servidor: " + e);
                System.exit(1);
            }
        }
    }

