package asgn2Vehicles;

import asgn2Exceptions.VehicleException;
import asgn2Simulators.Simulator;

/**
 * The Car class is a specialisation of the Vehicle class to cater for production cars 
 * This version of the class does not cater for model types, but records whether or not 
 * the vehicle can use a small parking space.
 * @author Brendan Rothwell
 * @version 1.0
 */
public class Car extends Vehicle {
	String vehID;
	int arrivalTime;
	boolean small;
	private int count = 1;
	
	/**
	 * The Car Constructor - small set at creation, not mutable.
	 * @param vehID
	 * @param arrivalTime
	 * @param small
	 * @throws VehicleException
	 */
	public Car(String vehID, int arrivalTime, boolean small) throws VehicleException {
		if (arrivalTime <= 0) {
			throw new VehicleException ("arrivalTime must be a positive integer");
		}
		this.small = false;
		this.arrivalTime = arrivalTime;
		this.vehID = "C"+this.count;
		count++;
	}
	
	/**
	 * Boolean status indicating whether car is small enough for small car parking spaces
	 * @return true if small parking space, false otherwise
	 */
	public boolean isSmall() {
		if (Simulator.newCarTrial() == true); 
	}
	    
}