package net.novucs.colordash;

/**
 * A service for the game that can be safely initialized and terminated.
 */
public interface GameService {

    /**
     * Initializes the service.
     */
    void initialize();

    /**
     * Terminates the service.
     */
    void terminate();

}
