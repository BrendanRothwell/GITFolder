/**
 * 
 * This file is part of the CarParkSimulator Project, written as 
 * part of the assessment for INB370, semester 1, 2014. 
 *
 * CarParkSimulator
 * asgn2CarParks 
 * 21/04/2014
 * 
 */
package asgn2CarParks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import asgn2Exceptions.SimulationException;
import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;
import asgn2Simulators.Simulator;
import asgn2Vehicles.Car;
import asgn2Vehicles.MotorCycle;
import asgn2Vehicles.Vehicle;

/**
 * The CarPark class provides a range of facilities for working with a car park
 * in support of the simulator. In particular, it maintains a collection of
 * currently parked vehicles, a queue of vehicles wishing to enter the car park,
 * and an historical list of vehicles which have left or were never able to gain
 * entry.
 * 
 * The class maintains a wide variety of constraints on small cars, normal cars
 * and motorcycles and their access to the car park. See the method javadoc for
 * details.
 * 
 * The class relies heavily on the asgn2.Vehicle hierarchy, and provides a
 * series of reports used by the logger.
 * 
 * @author hogan
 * 
 */
public class CarPark {

	/**
	 * CarPark constructor sets the basic size parameters. Uses default
	 * parameters
	 */
	private Integer maxCarSpaces;
	private Integer maxSmallCarSpaces;
	private Integer maxMotorCycleSpaces;
	private Integer maxQueueSize;
	private Integer numSmallCars = 0;
	private Integer numCars = 0;
	private Integer numMotorCycles = 0;
	private Integer numDissatisfied = 0;
	private Integer count = 1;

	private Queue<Vehicle> queue;
	private ArrayList<Vehicle> spaces;
	private ArrayList<Vehicle> past;

	String state = "N";
	private Vehicle vehicle;

	public CarPark() {
		this(Constants.DEFAULT_MAX_CAR_SPACES,
				Constants.DEFAULT_MAX_SMALL_CAR_SPACES,
				Constants.DEFAULT_MAX_MOTORCYCLE_SPACES,
				Constants.DEFAULT_MAX_QUEUE_SIZE);
	}

	/**
	 * CarPark constructor sets the basic size parameters.
	 * 
	 * @param maxCarSpaces
	 *            maximum number of spaces allocated to cars in the car park
	 * @param maxSmallCarSpaces
	 *            maximum number of spaces (a component of maxCarSpaces)
	 *            restricted to small cars
	 * @param maxMotorCycleSpaces
	 *            maximum number of spaces allocated to MotorCycles
	 * @param maxQueueSize
	 *            maximum number of vehicles allowed to queue
	 */
	public CarPark(int maxCarSpaces, int maxSmallCarSpaces,
			int maxMotorCycleSpaces, int maxQueueSize) {
		this.maxCarSpaces = maxCarSpaces;
		this.maxSmallCarSpaces = maxSmallCarSpaces;
		this.maxMotorCycleSpaces = maxMotorCycleSpaces;
		this.maxQueueSize = maxQueueSize;

		spaces = new ArrayList<Vehicle>(maxCarSpaces + maxSmallCarSpaces
				+ maxMotorCycleSpaces);
		queue = new LinkedList<Vehicle>();
		past = new ArrayList<Vehicle>();
	}

	/**
	 * Archives vehicles exiting the car park after a successful stay. Includes
	 * transition via Vehicle.exitParkedState().
	 * 
	 * @param time
	 *            int holding time at which vehicle leaves
	 * @param force
	 *            boolean forcing departure to clear car park
	 * @throws VehicleException
	 *             if vehicle to be archived is not in the correct state
	 * @throws SimulationException
	 *             if one or more departing vehicles are not in the car park
	 *             when operation applied
	 */
	public void archiveDepartingVehicles(int time, boolean force)
			throws VehicleException, SimulationException {
		Iterator<Vehicle> cars = spaces.iterator();
		while (cars.hasNext()) {
			if (force) {
				vehicle.exitQueuedState(time);
				past.add((Vehicle) cars);
				spaces.remove(cars);
				this.state = "A";
			}
		}
		throw new SimulationException(
				"One or more departing vehicles are not in the car park when operation applied");
	}

	/**
	 * Method to archive new vehicles that don't get parked or queued and are
	 * turned away
	 * 
	 * @param v
	 *            Vehicle to be archived
	 * @throws SimulationException
	 *             if vehicle is currently queued or parked
	 */
	public void archiveNewVehicle(Vehicle v) throws SimulationException {
		if (!v.isParked() && !v.isQueued()) {
			past.add(v);
			this.state = "A";
		} else {
			throw new SimulationException(
					"Vehicle is currently queued or parked");
		}
	}

	/**
	 * Archive vehicles which have stayed in the queue too long
	 * 
	 * @param time
	 *            int holding current simulation time
	 * @throws VehicleException
	 *             if one or more vehicles not in the correct state or if timing
	 *             constraints are violated
	 */
	public void archiveQueueFailures(int time) throws VehicleException {
		if (time > Constants.MAXIMUM_QUEUE_TIME) {
			vehicle.exitQueuedState(time);
			this.state = "A";// past list
			numDissatisfied++;
		}
	}

	/**
	 * Simple status showing whether carPark is empty
	 * 
	 * @return true if car park empty, false otherwise
	 */
	public boolean carParkEmpty() {
		if (spaces.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Simple status showing whether carPark is full
	 * 
	 * @return true if car park full, false otherwise
	 */
	public boolean carParkFull() {
		if (spaces.size() == maxCarSpaces + maxMotorCycleSpaces
				+ maxSmallCarSpaces) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method to add vehicle successfully to the queue Precondition is a test
	 * that spaces are available Includes transition through
	 * Vehicle.enterQueuedState
	 * 
	 * @param v
	 *            Vehicle to be added
	 * @throws SimulationException
	 *             if queue is full
	 * @throws VehicleException
	 *             if vehicle not in the correct state
	 */
	public void enterQueue(Vehicle v) throws SimulationException,
			VehicleException {
		if (!queueFull() && spacesAvailable(v)) {
			queue.offer(v);
			v.enterQueuedState();
		} else {
			throw new SimulationException("Queue is full");
		}
	}

	/**
	 * Method to remove vehicle from the queue after which it will be parked or
	 * archived. Includes transition through Vehicle.exitQueuedState.
	 * 
	 * @param v
	 *            Vehicle to be removed from the queue
	 * @param exitTime
	 *            int time at which vehicle exits queue
	 * @throws SimulationException
	 *             if vehicle is not in queue
	 * @throws VehicleException
	 *             if the vehicle is in an incorrect state or timing constraints
	 *             are violated
	 */
	public void exitQueue(Vehicle v, int exitTime) throws SimulationException,
			VehicleException {
		if (v.isQueued() && exitTime > vehicle.getArrivalTime()) {
			vehicle.exitQueuedState(exitTime);
			queue.poll();
		} else {
			throw new SimulationException("Vehicle is not in queue");
		}
	}

	/**
	 * State dump intended for use in logging the final state of the carpark All
	 * spaces and queue positions should be empty and so we dump the archive
	 * 
	 * @return String containing dump of final carpark state
	 */
	public String finalState() {
		String str = "Vehicles Processed: count:" + this.count + ", logged: "
				+ this.past.size() + "\nVehicle Record: \n";
		for (Vehicle v : this.past) {
			str += v.toString() + "\n\n";
		}
		return str + "\n";
	}

	/**
	 * Simple getter for number of cars in the car park
	 * 
	 * @return number of cars in car park, including small cars
	 */
	public int getNumCars() {
		return numCars;
	}

	/**
	 * Simple getter for number of motorcycles in the car park
	 * 
	 * @return number of MotorCycles in car park, including those occupying a
	 *         small car space
	 */
	public int getNumMotorCycles() {
		return numMotorCycles;
	}

	/**
	 * Simple getter for number of small cars in the car park
	 * 
	 * @return number of small cars in car park, including those not occupying a
	 *         small car space.
	 */
	public int getNumSmallCars() {
		return numSmallCars;
	}

	/**
	 * Method used to provide the current status of the car park. Uses private
	 * status String set whenever a transition occurs. Example follows (using
	 * high probability for car creation). At time 262, we have 276 vehicles
	 * existing, 91 in car park (P), 84 cars in car park (C), of which 14 are
	 * small (S), 7 MotorCycles in car park (M), 48 dissatisfied (D), 176
	 * archived (A), queue of size 9 (CCCCCCCCC), and on this iteration we have
	 * seen: car C go from Parked (P) to Archived (A), C go from queued (Q) to
	 * Parked (P), and small car S arrive (new N) and go straight into the car
	 * park<br>
	 * 262::276::P:91::C:84::S:14::M:7::D:48::A:176::Q:9CCCCCCCCC|C:P>A||C:Q>P||
	 * S:N>P|
	 * 
	 * @return String containing current state
	 */
	public String getStatus(int time) {
		String str = time + "::" + this.count + "::" + "P:"
				+ this.spaces.size() + "::" + "C:" + this.numCars + "::S:"
				+ this.numSmallCars + "::M:" + this.numMotorCycles + "::D:"
				+ this.numDissatisfied + "::A:" + this.past.size() + "::Q:"
				+ this.queue.size();
		for (Vehicle v : this.queue) {
			if (v instanceof Car) {
				if (((Car) v).isSmall()) {
					str += "S";
				} else {
					str += "C";
				}
			} else {
				str += "M";
			}
		}
		str += this.state;
		this.state = "";
		return str + "\n";
	}

	/**
	 * State dump intended for use in logging the initial state of the carpark.
	 * Mainly concerned with parameters.
	 * 
	 * @return String containing dump of initial carpark state
	 */
	public String initialState() {
		return "CarPark [maxCarSpaces: " + this.maxCarSpaces
				+ " maxSmallCarSpaces: " + this.maxSmallCarSpaces
				+ " maxMotorCycleSpaces: " + this.maxMotorCycleSpaces
				+ " maxQueueSize: " + this.maxQueueSize + "]";
	}

	/**
	 * Simple status showing number of vehicles in the queue
	 * 
	 * @return number of vehicles in the queue
	 */
	public int numVehiclesInQueue() {
		return queue.size();
	}

	/**
	 * Method to add vehicle successfully to the car park store. Precondition is
	 * a test that spaces are available. Includes transition via
	 * Vehicle.enterParkedState.
	 * 
	 * @param v
	 *            Vehicle to be added
	 * @param time
	 *            int holding current simulation time
	 * @param intendedDuration
	 *            int holding intended duration of stay
	 * @throws SimulationException
	 *             if no suitable spaces are available for parking
	 * @throws VehicleException
	 *             if vehicle not in the correct state or timing constraints are
	 *             violated
	 */
	public void parkVehicle(Vehicle v, int time, int intendedDuration)
			throws SimulationException, VehicleException {
		if (spacesAvailable(v)) {
			v.enterParkedState(time, intendedDuration);
			spaces.add(0, v);
		} else {
			throw new SimulationException(
					"No suitable spaces are available for parking");
		}
	}

	/**
	 * Silently process elements in the queue, whether empty or not. If
	 * possible, add them to the car park. Includes transition via
	 * exitQueuedState where appropriate Block when we reach the first element
	 * that can't be parked.
	 * 
	 * @param time
	 *            int holding current simulation time
	 * @throws SimulationException
	 *             if no suitable spaces available when parking attempted
	 * @throws VehicleException
	 *             if state is incorrect, or timing constraints are violated
	 */
	public void processQueue(int time, Simulator sim) throws VehicleException,
			SimulationException {
		Iterator<Vehicle> queuedCar = queue.iterator();
		while (queuedCar.hasNext()) {
			queuedCar.next();
			if (spacesAvailable(queue.element())) {
				vehicle.exitQueuedState(time);
				spaces.add(0, queue.poll());
				this.state = "P";
			}
			throw new SimulationException("No suitable spaces available");
		}
	}

	/**
	 * Simple status showing whether queue is empty
	 * 
	 * @return true if queue empty, false otherwise
	 */
	public boolean queueEmpty() {
		if (queue.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Simple status showing whether queue is full
	 * 
	 * @return true if queue full, false otherwise
	 */
	public boolean queueFull() {
		if (queue.size() == maxQueueSize) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method determines, given a vehicle of a particular type, whether there
	 * are spaces available for that type in the car park under the parking
	 * policy in the class header.
	 * 
	 * @param v
	 *            Vehicle to be stored.
	 * @return true if space available for v, false otherwise
	 */
	public boolean spacesAvailable(Vehicle v) {
		if (v instanceof Car) {
			if (((Car) v).isSmall()) {
				return numSmallCars < maxSmallCarSpaces + maxCarSpaces;
			} else {
				return numCars < maxCarSpaces;
			}
		} else {
			return maxMotorCycleSpaces < maxMotorCycleSpaces
					+ maxSmallCarSpaces;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CarPark [count: " + count + " numCars: " + numCars
				+ " numSmallCars: " + numSmallCars + " numMotorCycles: "
				+ numMotorCycles + " queue: " + (queue.size())
				+ " numDissatisfied: " + numDissatisfied + " past: "
				+ past.size() + "]";
	}

	/**
	 * Method to try to create new vehicles (one trial per vehicle type per time
	 * point) and to then try to park or queue (or archive) any vehicles that
	 * are created
	 * 
	 * @param sim
	 *            Simulation object controlling vehicle creation
	 * @throws SimulationException
	 *             if no suitable spaces available when operation attempted
	 * @throws VehicleException
	 *             if vehicle creation violates constraints
	 */
	public void tryProcessNewVehicles(int time, Simulator sim)
			throws VehicleException, SimulationException {
		count++;
		if (sim.smallCarTrial()) {
			Car newSmallCar = new Car("C" + this.count, time, true);

			if (queueFull() && carParkFull()) {
				archiveNewVehicle(newSmallCar);
			} else if (carParkFull() && !queueFull()) {
				enterQueue(newSmallCar);
			} else if (queueEmpty() && spacesAvailable(newSmallCar)) {
				parkVehicle(newSmallCar, time, sim.setDuration());
				numSmallCars++;
			}
		}

		if (sim.newCarTrial()) {
			Car newCar = new Car("C" + this.count, time, false);
		
			if (queueFull() && carParkFull()) {
				archiveNewVehicle(newCar);
			} else if (carParkFull() && !queueFull()) {
				enterQueue(newCar);
			} else if (queueEmpty() && spacesAvailable(newCar)) {
				parkVehicle(newCar, time, sim.setDuration());
				numCars++;
			}
		}

		if (sim.motorCycleTrial()) {
			Car newMotorCycle = new Car("MC" + this.count, time, false);
		
			if (queueFull() && carParkFull()) {
				archiveNewVehicle(newMotorCycle);
			} else if (carParkFull() && !queueFull()) {
				enterQueue(newMotorCycle);
			} else if (queueEmpty() && spacesAvailable(newMotorCycle)) {
				parkVehicle(newMotorCycle, time, sim.setDuration());
				numMotorCycles++;
			}
		}

	}

	/**
	 * Method to remove vehicle from the carpark. For symmetry with parkVehicle,
	 * include transition via Vehicle.exitParkedState. So vehicle should be in
	 * parked state prior to entry to this method.
	 * 
	 * @param v
	 *            Vehicle to be removed from the car park
	 * @throws VehicleException
	 *             if Vehicle is not parked, is in a queue, or violates timing
	 *             constraints
	 * @throws SimulationException
	 *             if vehicle is not in car park
	 */
	public void unparkVehicle(Vehicle v, int departureTime)
			throws VehicleException, SimulationException {
		if (v.isParked()) {
			vehicle.exitParkedState(departureTime);
			spaces.remove(v);
		} else {
			throw new SimulationException("Vehicle is not in car park");
		}
	}

	/**
	 * Helper to set vehicle message for transitions
	 * 
	 * @param v
	 *            Vehicle making a transition (uses S,C,M)
	 * @param source
	 *            String holding starting state of vehicle (N,Q,P)
	 * @param target
	 *            String holding finishing state of vehicle (Q,P,A)
	 * @return String containing transition in the form:
	 *         |(S|C|M):(N|Q|P)>(Q|P|A)|
	 */
	private String setVehicleMsg(Vehicle v, String source, String target) {
		String str = "";
		if (v instanceof Car) {
			if (((Car) v).isSmall()) {
				str += "S";
			} else {
				str += "C";
			}
		} else {
			str += "M";
		}
		return "|" + str + ":" + source + ">" + target + "|";
	}
}
