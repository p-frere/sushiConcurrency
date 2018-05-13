package server;

import java.io.Serializable;

public enum DroneStatus implements Serializable {
    IDLE,
    DELIVERING,
    RECOVERING
}
