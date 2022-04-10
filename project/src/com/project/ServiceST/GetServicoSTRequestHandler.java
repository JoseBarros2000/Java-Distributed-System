package com.project.ServiceST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class GetServicoSTRequestHandler extends Thread{

    Socket ligacao;
    BufferedReader in;
    PrintWriter out;
    Serializacao serializacao;
    ServiceST servico;

    public GetServicoSTRequestHandler(Socket ligacao, Serializacao serializacao, ServiceST servico) throws NoSuchAlgorithmException {
        this.ligacao = ligacao;
        this.serializacao=serializacao;
        this.servico = servico;
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
                String response ="";
                System.out.println("Request=" + msg);

                StringTokenizer tokens = new StringTokenizer(msg);
                String method = tokens.nextToken();

                    if (method.equals("get")) {
                        response += "200 OK \nNAME IP PORT DESCRIPTION TIMESTAMP KEY \n";
                        String id = tokens.nextToken();
                        String idHash = tokens.nextToken();
                        String comunicationType = tokens.nextToken();

                        MessageDigest m = MessageDigest.getInstance("MD5");
                        m.reset();
                        m.update(id.getBytes("UTF-8"));
                        byte[] digest = m.digest();
                        BigInteger bigInt = new BigInteger(1, digest);
                        String hashtext = bigInt.toString(16);
                        // Now we need to zero pad it if you actually want the full 32 chars.

                        while (hashtext.length() < 32) {
                            hashtext = "0" + hashtext;
                        }
                        if (!hashtext.equals(idHash)) {
                            out.println("403 Forbidden  ");
                            out.flush();
                            in.close();
                            out.close();
                            ligacao.close();
                        }
                        if (!comunicationType.equals("rmi") && !comunicationType.equals("sockets")) {
                            out.println("400 Bad Request ");
                            out.flush();
                            in.close();
                            out.close();
                            ligacao.close();
                        }
                        ZonedDateTime instant;
                        for (Enumeration<ServiceInfo> e = servico.getServices(comunicationType).elements(); e.hasMoreElements(); ) {
                            ServiceInfo element = e.nextElement();
                            instant = Instant.ofEpochMilli(element.getTimestamp()).atZone(ZoneId.of("Portugal"));
                            if(comunicationType.equals("rmi"))
                                response += element.getName() + " " + element.getIp() + " " + element.getPort() + " " + element.getDescription() + " " + instant + " " +element.getIp().concat(String.valueOf(element.getPort())).concat(element.getName())+" \n";
                            else
                                response += element.getName() + " " + element.getIp() + " " + element.getPort() + " " + element.getDescription() + " " + instant + " " +element.getIp().concat(String.valueOf(element.getPort()))+" \n";
                        }
                        out.println(response);
                    } else {
                        if (method.equals("post")) {
                            try {
                                String ip = tokens.nextToken();
                                int port = Integer.parseInt(tokens.nextToken());
                                String name = tokens.nextToken();
                                String description = tokens.nextToken();
                                String type = tokens.nextToken();
                                if ( !type.equals("rmi") && (!type.equals("sockets")))
                                    out.println("400 Bad Request ");
                                else {
                                    String key = "";
                                    if (type.equals("rmi")) {
                                        key = ip+ port+name;
                                    }
                                    else
                                        key = ip+port;
                                    servico.postService(key, ip, port, name, description, type);
                                    out.println("200 OK ");
                                    serializacao.guardar(servico);
                                }
                            } catch (Exception e) {
                                out.println("400 Bad Request ");
                            }
                        } else {
                            out.println("404 method not allowed ");
                        }
                    }
                out.flush();
                in.close();
                out.close();
                ligacao.close();

            } catch (IOException | NoSuchAlgorithmException e) {
                System.out.println("Erro na execucao do servidor: " + e);
                System.exit(1);
            }
        }
    }

