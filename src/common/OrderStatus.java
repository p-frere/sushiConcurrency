package common;

/**
 * Signals where the order's status and position in the cooking process
 */
public enum OrderStatus {
    PENDING,
    COOKINGQUEUE,
    DELIVERYQUEUE,
    CANCELED,
    COMPLETE
}
