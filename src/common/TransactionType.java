package common;

import java.io.Serializable;

public enum TransactionType implements Serializable {
    //client sends - server receives
    requestLogin,
    requestCancel,
    requestOrder,
    requestRegister,
    initUser,

    //sever sends - client receives
    replyLogin,
    updateInfo,
    deliverOrder,
}
