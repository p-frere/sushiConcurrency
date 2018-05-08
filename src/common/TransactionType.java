package common;

public enum TransactionType {
    //client sends - server receives
    requestLogin,
    requestCancel,
    requestOrder,
    requestRegister,

    //sever sends - client receives
    replyLogin,
    replyOrder,
    updateInfo,
    deliverOrder,
    updateStatus
}
