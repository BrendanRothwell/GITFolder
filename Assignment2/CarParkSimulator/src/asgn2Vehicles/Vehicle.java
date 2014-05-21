package asgn2Vehicles;

import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;
import asgn2Simulators.Simulator;
import asgn2Vehicles.Car;

/**
 * Vehicle is an abstract class specifying the basic state of a vehicle and the methods used 
 * to set and access that state. A vehicle is created upon arrival, at which point it must either 
 * enter the car park to take a vacant space or become part of the queue. If the queue is full, then 
 * the vehicle must leave and never enters the car park. The vehicle cannot be both parked and queued 
 * at once and both the constructor and the parking and queuing state transition methods must respect 
 * this constraint. Vehicles are created in a neutral state. If the vehicle is unable to park or queue, 
 * then no changes are needed if the vehicle leaves the carpark immediately. Vehicles that remain and 
 * can't park enter a queued state via enterQueuedState and leave the queued state via exitQueuedState. 
 * Note that an exception is thrown if an attempt is made to join a queue when the vehicle is already 
 * in the queued state, or to leave a queue when it is not. Vehicles are parked using the enterParkedState 
 * method and depart using exitParkedState Note again that exceptions are thrown if the state is 
 * inappropriate: vehicles cannot be parked or exit the car park from a queued state.
 * @author Brendan Rothwell n8540683
 * @version 1.0
 */
public abstract class Vehicle extends java.lang.Object {
	
	String vehID;
	int arrivalTime;
	int exitTime;
	int parkingTime;
	int departureTime;
	private String state = "";
	private String satisfaction;
	
	/**
	 * Vehicle Constructor
	 * @param vehID - String identification number or plate of the vehicle
	 * @param arrivalTime - int time (minutes) at which the vehicle arrives and is either queued, given entry to 
	 * the car park or forced to leave
	 * @throws VehicleException - if arrivalTime is <= 0
	 */
	public Vehicle(String vehID, int arrivalTime) throws VehicleException {
		if (arrivalTime <= 0) {
			throw new VehicleException ("arrivalTime must be a positive integer");
		}
		this.vehID = vehID;
		this.arrivalTime = arrivalTime;
		if (Simulator.newCarTrial() == true);
	}
	
	/**
	 * Transition vehicle to parked state (mutator) Parking starts on arrival or on exit from the queue, but time is set here
	 * @param parkingTime - int time (minutes) at which the vehicle was able to park
	 * @param intendedDuration - int time (minutes) for which the vehicle is intended to remain in the car park. Note that 
	 * the parkingTime + intendedDuration yields the departureTime
	 * @throws VehicleException - if the vehicle is already in a parked or queued state, if parkingTime < 0, or if 
	 * intendedDuration is less than the minimum prescribed in asgnSimulators.Constants
	 */
	public void enterParkedState(int parkingTime, int intendedDuration) throws VehicleException {
		if (state != "") {
			throw new VehicleException ("Vehicle already in parked or queued state");
		}
		if (parkingTime < 0) {
			throw new VehicleException ("Parking time cannot be a negative integer");
		}
		if (intendedDuration < Constants.MINIMUM_STAY) {
			throw new VehicleException ("intendedDuration cannot be less than the Minimum Stay");
		}
		departureTime = parkingTime + intendedDuration;
		state = "P";
		isParked();
	}
	
	/**
	 * Transition vehicle to queued state (mutator) Queuing formally starts on arrival and ceases with a call to exitQueuedState
	 * @throws VehicleException - if the vehicle is already in a queued or parked state
	 */
	public void enterQueuedState() throws VehicleException {
		if (state == "Q") {
			throw new VehicleException ("Vehicle is already in queued state");
		}
		if (state == "P") {
			throw new VehicleException ("Vehicle is currently in a parked state and cannot be queued");
		}
		state = "Q";
		isQueued();
	}
	
	/**
	 * Transition vehicle from parked state (mutator)
	 * @param departureTime - int holding the actual departure time
	 * @throws VehicleException - if the vehicle is not in a parked state, is in a queued state or if the 
	 * revised departureTime < parkingTime
	 */
	public void exitParkedState(int departureTime) throws VehicleException {
		if (state != "P") {
			throw new VehicleException("Vehicle is not in a parked state");
		}
		if (departureTime < parkingTime) {
			throw new VehicleException("departureTime cannot be less/earlier than parkingTime");
		}
		wasParked();
		state = "";
	}
	
	/**
	 * Transition vehicle from queued state (mutator) Queuing formally starts on arrival with a call to 
	 * enterQueuedState Here we exit and set the time at which the vehicle left the queue
	 * @param exitTime - int holding the time at which the vehicle left the queue
	 * @throws VehicleException - if the vehicle is in a parked state or not in a queued state, or if 
	 * exitTime is not later than arrivalTime for this vehicle
	 */
	public void exitQueuedState(int exitTime) throws VehicleException {
		if (state != "Q") {
			throw new VehicleException("Vehicle is not in a queued state");
		}
		if (exitTime < arrivalTime) {
			throw new VehicleException("exitTime cannot be less/earlier than arrivalTime");
		}
		wasQueued();
		state = "";
	}
	
	/**
	 * Simple getter for the arrival time
	 * @return the arrivalTime
	 */
	public int getArrivalTime() {
		return arrivalTime;
	}
	
	/**
	 * Simple getter for the departure time from the car park Note: result may be 0 before parking, 
	 * show intended departure time while parked; and actual when archived
	 * @return the departureTime
	 */
	public int getDepartureTime() {
		return departureTime;
	}
	
	/**
	 * Simple getter for the parking time Note: result may be 0 before parking
	 * @return the parkingTime
	 */
	public int getParkingTime() {
		return parkingTime;
	}
	
	/**
	 * Simple getter for the vehicle ID
	 * @return the vehID
	 */
	public String getVehID() {
		return vehID;
	}
	
	/**
	 * Boolean status indicating whether vehicle is currently parked
	 * @return true if the vehicle is in a parked state; false otherwise
	 */
	public boolean isParked() {
		return state == "P";
	}
	
	/**
	 * Boolean status indicating whether vehicle is currently queued
	 * @return true if vehicle is in a queued state, false otherwise
	 */
	public boolean isQueued() {
		return state == "Q";
	}
	
	/**
	 * Boolean status indicating whether customer is satisfied or not Satisfied if they park; 
	 * dissatisfied if turned away, or queuing for too long Note that calls to this method may 
	 * not reflect final status
	 * @return true if satisfied, false if never in parked state or if queuing time exceeds max allowable
	 */
	public boolean isSatisfied() {
		if (arrivalTime - exitTime > Constants.MAXIMUM_QUEUE_TIME) {
			return false;
		}
		else if (wasParked() == true) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @overrides toString in class java.lang.Object
	 */
	@Override
	public String toString() {
		if (isSatisfied() == true) {
			satisfaction = " was ";
		} else {
			satisfaction = " was not ";
		}
		String vehicleStr = "Vehicle vehID: " + vehID + 
				" Arrival Time: " + arrivalTime;
		if (wasQueued() == true) {
			vehicleStr += " Exit from Queue: " + exitTime + 
					" Queuing Time: " + (exitTime - arrivalTime);
		} else {
			vehicleStr += " Vehicle was not queued";
		}
		if (wasParked() == true) {
			vehicleStr += " Entry to Car Park: " + exitTime +
			" Exit from Car Park: " + departureTime
			+ " Parking Time: " + (departureTime - exitTime);
		} else {
			vehicleStr += " Vehicle was not parked";
		}
		vehicleStr += " Customer" + satisfaction + "satisfied";
		if (vehID = "C" + Car.count) { 
			if (Car.isSmall() == true) {
				vehicleStr += " Car can use small car parking space";
			} else {
				vehicleStr += " Car cannot use small parking space";
			}
		}
		return vehicleStr;
	}

	
	/**
	 * Boolean status indicating whether vehicle was ever parked Will return false for vehicles in queue or turned away
	 * @return true if vehicle was or is in a parked state, false otherwise
	 */
	public boolean wasParked() {
		return state == "P";
	}
	
	/**
	 * Boolean status indicating whether vehicle was ever queued
	 * @return true if vehicle was or is in a queued state, false otherwise
	 */
	public boolean wasQueued() {
		return state == "Q";
	}
}
