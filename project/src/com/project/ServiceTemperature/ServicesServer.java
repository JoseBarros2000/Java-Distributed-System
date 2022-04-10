package com.project.ServiceTemperature;

import java.lang.SecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ServicesServer {
	
	String SERVICE_NAME="/TemperatureService";

	private void bindRMI(Sources sources) throws RemoteException {

		System.getProperties().put( "java.security.policy", "src/com/project/ServiceTemperature/server.policy");

		if( System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try { 
			LocateRegistry.createRegistry(1099);
		} catch( RemoteException e) {
			
		}
		try {
		  LocateRegistry.getRegistry("192.168.0.186",1099).rebind(SERVICE_NAME, sources); // host from our internal network
		  } catch( RemoteException e) {
		  	System.out.println("Registry not found");
		  }
	}

	public ServicesServer() {
		super();
	}
	
	public void createServices() {
		
		Sources sources = null;
		try {
			sources = new Sources();
		} catch (RemoteException e1) {
			System.err.println("unexpected error...");
			e1.printStackTrace();
		}
		
		try {
			bindRMI(sources);
		} catch (RemoteException e1) {
			System.err.println("erro ao registar o stub...");
			e1.printStackTrace();
		}
		
	}
}
