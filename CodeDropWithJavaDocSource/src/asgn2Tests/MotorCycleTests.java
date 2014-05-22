package asgn2Tests;

import static org.junit.Assert.*;
import static asgn2Vehicles.MotorCycle;

import org.junit.BeforeClass;
import org.junit.Test;

import asgn2Exceptions.VehicleException;

public class MotorCycleTest {
	// Initializations
	private MotorCycle mc;
	//Constants
	static final String VEHICLE_ID = "ABC123";
	int ARRIVAL_TIME = 5;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mc = new MotorCycle(VEHICLE_ID, ARRIVAL_TIME);
	}

// Testing for exceptions
	
	// Exception: Constructor - If arrivalTime is < 0
	@Test (expected = VehicleException.class)
	public void negArrivalTime() throws VehicleException {
	    new mc.MotorCycle(VEHICLE_ID, -3);
	}
	
	// Exception: Constructor - If arrivalTime is = 0
	@Test (expected = VehicleException.class)
	public void zeroArrivalTime() throws VehicleException {
	    new mc.MotorCycle(VEHICLE_ID, 0);
	}
}
