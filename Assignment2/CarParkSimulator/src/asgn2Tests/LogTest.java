package asgn2Tests;

import java.io.*;

import static org.junit.Assert.*;
import asgn2Exceptions.VehicleException;
import asgn2Simulators.Log;
import asgn2Simulators.Simulator;


import org.junit.BeforeClass;
import org.junit.Test;

public class LogTest {
	// Initializations
	private Log lo;
	private CarPark cp;
	// Constants
	static final CarPark CARPARK;
	static final Simulator SIMULATOR;
	
	@BeforeClass
	public static void setUpBeforeClass() throws java.io.IOException {
		lo = new Log();
	}

// Testing for exceptions
	// Constructor exception: if log file or BufferWriter cannot be created
	@Test (expected = IOException.class)
	public void noLogOrBufferWriter() throws IOException {
		lo.LogFinalise(CARPARK);
		lo.Log();
	}
	
	// finalise: on write or closure failures
	@Test (expected = IOException.class)
	public void finaliseFailure() throws IOException {
		cp.finalState();
		lo.finalise(CARPARK);
	}
	
	// intialEntry: on write failures
	@Test (expected = IOException.class)
	public void initialEntryFailure() throws IOException {
		cp.initialState();
		lo.initialEntry(CARPARK, SIMULATOR);
	}
	
	// logEntry: on write failures
	@Test (expected = IOException.class)
	public void logEntryFailure() throws IOException {
		cp.getStatus(2);
		lo.logEntry(2, SIMULATOR);
	}
}
