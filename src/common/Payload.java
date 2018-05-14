package common;

import java.io.Serializable;
/**
 * The object that is serialized and sent between server and client
 * It contains an Model Object and a transactionType that signals
 * what the object being delivered it to be used for.
 */
public class Payload implements Serializable{
    private TransactionType transactionType;
    private Model object;

    //Constructor
    public Payload(Model object, TransactionType type){
        this.object = object;
        this.transactionType = type;
    }

    //Getters
    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Model getObject() {
        return object;
    }
}
