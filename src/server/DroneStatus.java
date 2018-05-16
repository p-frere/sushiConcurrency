package server;

import java.io.Serializable;

/**
 * States all the statues a Drones job can be
 */
public enum DroneStatus implements Serializable {
    IDLE,
    DELIVERING,
    RECOVERING
}
