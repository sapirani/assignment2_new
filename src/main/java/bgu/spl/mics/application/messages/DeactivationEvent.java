package bgu.spl.mics.application.messages; // The package

// Imports:
import bgu.spl.mics.Event;

/**
 * Class that represents Deactivation event
 * Received only by R2D2 after all the attacks that Leia sends are resolved.
 */
public class DeactivationEvent implements Event<Boolean> { }
