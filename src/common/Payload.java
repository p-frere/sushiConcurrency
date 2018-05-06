package common;

import java.io.Serializable;

public class Payload implements Serializable{
    private TransactionType transactionType;
    private Model object;

    public Payload(Model object, TransactionType type){
        this.object = object;
        this.transactionType = type;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Model getObject() {
        return object;
    }
}
