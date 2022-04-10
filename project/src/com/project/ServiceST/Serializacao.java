/**
 *
 * @author Bruno Ferreira (bruno@dsi.uminho.pt)
 */

package com.project.ServiceST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializacao {    
    private final File ficheiro;

    public Serializacao(String ficheiro) {
        this.ficheiro = new File(ficheiro);
    }

    public File getFicheiro() {
        return ficheiro;
    }          

    public ServiceST carregar() {
        try (FileInputStream fileIn = new FileInputStream(ficheiro); 
        ObjectInputStream in = new ObjectInputStream(fileIn)) {
            ServiceST servico = (ServiceST) in.readObject();
            return servico;
        }catch(IOException | ClassNotFoundException ex) {
            throw new RuntimeException(String.format(
                  "Ocorreu um erro ao ler o ficheiro de dados: %s", 
                  ex.getLocalizedMessage()), ex);        
        }              
    }

    public void guardar(ServiceST servico) {
        try (FileOutputStream fileOut = new FileOutputStream(ficheiro); 
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            try {
                out.writeObject(servico);
            } catch (IOException ex) {
                throw new RuntimeException(String.format(
                        "Ocorreu um erro ao guardar o ficheiro de dados: %s", 
                        ex.getLocalizedMessage()), ex);
            }
        } catch (IOException ex) {
             throw new RuntimeException(String.format(
                        "Ocorreu um erro ao guardar o ficheiro de dados: %s", 
                        ex.getLocalizedMessage()), ex);
        }
    }

}