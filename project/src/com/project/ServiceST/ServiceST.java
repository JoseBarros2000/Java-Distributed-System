package com.project.ServiceST;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ServiceST extends UnicastRemoteObject implements Serializable {
    private Hashtable<String, ServiceInfo> presentServices = new Hashtable<>();

    public ServiceST(Hashtable<String, ServiceInfo> presentServices) throws RemoteException {
        super();
        this.presentServices=presentServices;
    }

    public ServiceST() throws RemoteException {
        super();
        this.presentServices = new Hashtable<String, ServiceInfo>();
    }

    public Hashtable<String, ServiceInfo> getServices(String comunicationType) {


        Hashtable<String, ServiceInfo> presentServices = new Hashtable<>();
        synchronized(this) {
            for (Enumeration<ServiceInfo> e = this.presentServices.elements(); e.hasMoreElements(); ) {
                ServiceInfo element = e.nextElement();
                if (element.getType().equals(comunicationType)) {
                    if (comunicationType.equals("rmi"))
                        presentServices.put(element.getIp() + element.getPort() + element.getName(), element);
                    else
                        presentServices.put(element.getIp() + element.getPort(), element);
                }
            }
        }
        return presentServices;
    }


    public  Hashtable<String, ServiceInfo> getPresentServices() {
        return presentServices;
    }

    public void setPresentServices(Hashtable<String, ServiceInfo> presentServices) {
        this.presentServices = presentServices;
    }

    public void postService(String key,String ip,int port,String name,String description,String type) {

        long actualTime = new Date().getTime();
        long validTime = actualTime + ( 180*1000);

        synchronized(this) {
            if (presentServices.containsKey(key)) {
                ServiceInfo service = presentServices.get(key);
                service.setTimestamp(validTime);
            }
            else {
                ServiceInfo service = new ServiceInfo(ip,name,type,description,port,validTime);
                presentServices.put(key,service);
            }
        }
    }
}


class ServiceInfo implements Serializable{

    private String ip;
    private String name;
    private String type;
    private String description;
    private int port;
    private long timestamp;

    public ServiceInfo(String ip, String name, String type, String description, int port, long timestamp) {
        this.ip = ip;
        this.name = name;
        this.type = type;
        this.description = description;
        this.port = port;
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean timeOutPassed(int timeout){
        boolean result = false;
        long timePassedSinceLastSeen = new Date().getTime() - this.timestamp;
        if (timePassedSinceLastSeen >= timeout)
            result = true;
        return result;
    }
}
