package bgu.spl.mics.application.messages; // The package

// Imports:
import bgu.spl.mics.Event;

/**
 * Class that represents Bomb Destroyer event
 * Received only by Lando after R2D2 finishes deactivating the shield generator.
 */
public class BombDestroyerEvent implements Event<Boolean> { }
