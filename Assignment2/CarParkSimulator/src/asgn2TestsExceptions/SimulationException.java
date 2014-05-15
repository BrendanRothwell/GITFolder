package asgn2Exceptions;

import java.lang.*;
/**
 * This class represents exceptions generated during the simulation, from classes which utilise the CarPark and Vehicle hierarchy.
 * @author Brendan Rothwell
 * @version 1.0
 */
public interface SimulationException extends java.lang.Exception {
	/**
	 * Constructor - Creates a new instance of SimulationException.
	 * @param message - String holding an informative message about the problem encountered
	 */
}
