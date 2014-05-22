package asgn2Tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import asgn2Exceptions.VehicleException;
import asgn2Vehicles.Car;

public class CarTest {
	
	private static Car as;
	
	// Constants
	static final String VEHICLE_ID = "ABC123";
	static final int ARRIVAL_TIME = 5;
	static final boolean SMALL = false;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		as = new Car(VEHICLE_ID, ARRIVAL_TIME, SMALL);
	}
	
// Testing for exceptions
	
	// Exception: If arrival time is less than or equal to 0
	@Test (expected = VehicleException.class)
	public void NegArrivalTime() throws VehicleException {
		as.Car(VEHICLE_ID, -ARRIVAL_TIME, SMALL);
	}
	
	@Test (expected = VehicleException.class)
	public void ZeroArrivalTime() throws VehicleException {
		as.Car(VEHICLE_ID, 0, SMALL);
	}
	
// Testing method functionality
	
	//IsSmall method
	@Test
	public void isNotASmallCar() {
		assertFalse(as.isSmall());
	}
	
	@Test
	public void isASmallCar() {
		as.Car(VEHICLE_ID, ARRIVAL_TIME, true);
		assertTrue(as.isSmall());
	}
	
	//toString method
	@Test
	public void correctString() {
		assertTrue(as.toString() = VEHICLE_ID);
	}
}
