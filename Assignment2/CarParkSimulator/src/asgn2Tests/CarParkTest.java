package asgn2Tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import asgn2Exceptions.VehicleException;
import asgn2Exceptions.SimulationException;
import asgn2VehiclesCarparks.CarPark;
import asgn2Vehicles.Vehicle;
import asgn2Vehicles.Simulator;

public class CarParkTest {
	// Initializations
	private static CarPark cp;
	private static Vehicle ve;

	// Constants
	static final Vehicle VEHIC;
	static final Simulator SIMULATOR;
	static final int CAR_SPACES = 20;
	static final int SMALL_CAR_SPACES = 20;
	static final int MOTO_SPACES = 10;
	static final int MAX_QUEUE_SIZE = 10;
	static final int SIM_DURATION = 18 * 60;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cp = new CarPark(CAR_SPACES, SMALL_CAR_SPACES, MOTO_SPACES, MAX_QUEUE_SIZE);
	}
	
// Testing for exceptions
	
	// Exception: archiveDepartingVehicles - If vehicle is in the incorrect state to exit the carpark
    @Test (expected = VehicleException.class)
    public void incorrectExitState() throws VehicleException {
    	new ve.enterParkedState(10, 15);
    	new cp.archiveDepartingVehicles(25, true);	
    }
    
	// Exception: If one or more departing vehicles aren't in the carpark when archiveDepartingVehicles is applied
    @Test (expected = SimulationException.class)
    public void noCarsInCarParkToArchive() throws SimulationException {
    	new cp.carParkEmpty() = true;
    	new cp.archiveDepartingVehicles(25, true);
    }
    
    // Exception: archiveNewVehicle - If vehicle is currently queued or parked when archiveNewVehicle is applied
    @Test (expected = SimulationException.class)
    public void queuedWhenNew() throws SimulationException {
    	new cp.enterQueue(VEHIC);
    	new cp.archiveNewVehicle(VEHIC);
    }
    
    @Test (expected = SimulationException.class)
    public void parkedWhenNew() throws SimulationException {
    	new cp.spacesAvailable(VEHIC) = true;
    	new cp.parkVehicle(VEHIC, 50, 10);
    	new cp.archiveNewVehicle(VEHIC);
    }
    
 // Exception: If one or more vehicles are not in correct state when archiveQueueFailures is applied
    @Test (expected = VehicleException.class)
    public void incorrectQueueFailState() throws VehicleException {
    	new cp.carParkEmpty() = true;
    	new cp.enterQueue(VEHIC);
    	new cp.exitQueue(VEHIC, 1);
    	new ve.enterParkedState(5, 10);
    	new cp.archiveQueueFailures(2);
    }
    
    // Exception: enterQueue - If queue is full
    @Test (expected = SimulationException.class)
    public void queueIsFull() throws SimulationException {
    	new cp.queueFull() = true;
    	new cp.enterQueue(VEHIC);
    }
    
    // Exception: vehicle not in correct state when enterQueue is applied
    @Test (expected = VehicleException.class)
    public void incorrectEnterQueueState() throws VehicleException {
    	new ve.enterQueuedState();
    	new as.enterQueue(VEHIC);
    }
    
    // Exception: exitQueue - If vehicle is not in car park or not in parked state
    @Test (expected = SimulationException.class)
    public void notInCarpark() throws SimulationException {
    	new cp.unparkVehicle(VEHIC, 10);
    	new cp.exitQueue(VEHIC, 10);
    }
    
    // Exception: exitQueue - if the vehicle is in a parked state or not in a queued state, or if exitTime
    // is not later than arrivalTime for this vehicle
    @Test (expected = VehicleException.class)
    public void notInQueuedState() throws VehicleException {
    	new ve.exitParkedState(10);
    	new cp.exitQueue(VEHIC, 10);
    }
    
    @Test (expected = VehicleException.class)
    public void exitTimeEarlierThanArrival() throws VehicleException {
    	new ve.Vehicle("ABC123", 15);
    	new cp.exitQueue(VEHIC, 10);
    }
    
    // Exception: parkVehicle - If no suitable spaces are available for parking
    @Test (expected = SimulationException.class)
    public void noSpacesParkVehicle() throws SimulationException {
    	new cp.spacesAvailable(VEHIC) = false;
    	new cp.parkVehicle(VEHIC, 10, 15);
    }
   
    // Exception: vehicle not in correct state when parkVehicle is applied
    @Test (expected = VehicleException.class)
    public void incorrectParkVehicleState() throws VehicleException {
    	new ve.enterParkedState(5, 20);
    	new as.parkVehicle(VEHIC, 10, 15);
    }
    
    // Exception: processQueue - If no suitable spaces are available for parking
    @Test (expected = SimulationException.class)
    public void noSpacesProcessQueue() throws SimulationException {
    	new cp.spacesAvailable(VEHIC) = false;
    	new cp.processQueue(5, SIMULATOR);
    }
    
    // Exception: vehicle not in correct state when processQueue is applied
    @Test (expected = VehicleException.class)
    public void incorrectProcessQueueState() throws VehicleException {
    	new ve.exitQueuedState(5);
    	new cp.processQueue(5, SIMULATOR);
    }
    
    // Exception: processQueue - time violates constraints
    @Test (expected = VehicleException.class)
    public void timeConstraintViolation() throws VehicleException {
    	new cp.processQueue(SIM_DURATION + 10, SIMULATOR);
    }
    
    // Exception: tryProcessNewVehicles - If no suitable spaces are available when operation attempted
    @Test (expected = SimulationException.class)
    public void noSpacesTryProcessNewVehicle() throws SimulationException {
    	new cp.queueFull() = true;
    	new cp.spacesAvailable(VEHIC) = false;
    	new cp.tryProcessNewVehicle(5, SIMULATOR);
    }
    
    // Exception: tryProcessNewVehicles - if vehicles creation violates constraints
    //Duration
    @Test (expected = VehicleException.class)
    public void vehicleTimeConstraintViolation() throws VehicleException {
    	new cp.tryProcessNewVehicle(SIM_DURATION + 10, SIMULATOR);
    }
    
    //Frequency
    @Test (expected = VehicleException.class)
    public void vehicleFreqConstraintViolation() throws VehicleException {
    	new cp.tryProcessNewVehicle(5, SIMULATOR);
    	new cp.tryProcessNewVehicle(5, SIMULATOR);
    }
    
    // Exception: unparkVehicle - vehicle not parked
    @Test (expected = VehicleException.class)
    public void unParkNotParked() throws VehicleException {
    	new ve.exitParkedState(5);
    	new cp.unparkVehicle(VEHIC, 6);
    }
    
    // Exception: unparkVehicle - vehicle in queue
    @Test (expected = VehicleException.class)
    public void unParkNotParked() throws VehicleException {
    	new ve.enterQueuedState();
    	new cp.carParkFull() = true;
    	new cp.unparkVehicle(VEHIC, 2);
    }
    
    // Exception: unparkVehicle - violates timing (duration) constraints
    @Test (expected = VehicleException.class)
    public void unParkNotParked() throws VehicleException {
    	new cp.parkVehicle(VEHIC, 5, 10);
    	new cp.unparkVehicle(VEHIC, SIM_DURATION + 10);
    }
    
    // Exception: unparkVehicle - violates timing (intended stay) constraints 2
    @Test (expected = VehicleException.class)
    public void unParkNotParked() throws VehicleException {
    	new cp.parkVehicle(VEHIC, 5, 10);
    	new cp.unparkVehicle(VEHIC, 20);
    }
    
    // Exception: unparkVehicle - If vehicle is not in carpark
    @Test (expected = SimulationException.class)
    public void noSpacesTryProcessNewVehicle() throws SimulationException {
    	new cp.unparkVehicle(VEHIC, 5);
    	new cp.unparkVehicle(VEHIC, 6);
    }
    
    // Exception: unparkVehicle - If vehicle is not in correct state
    @Test (expected = SimulationException.class)
    public void noSpacesTryProcessNewVehicle() throws SimulationException {
    	new cp.enterQueue(VEHIC);
    	new cp.unparkVehicle(VEHIC, 2);
    }
}
