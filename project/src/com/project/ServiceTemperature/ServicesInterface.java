package com.project.ServiceTemperature;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.Instant;


public interface ServicesInterface extends Remote {

	public Float getTemp(Instant time) throws RemoteException;

}