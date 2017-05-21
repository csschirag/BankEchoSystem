package com.system.bank.bankechosystem.helper;

/**
 * Created by B0097489 on 5/21/17.
 */

public class SessionManager {

    private static SessionManager sessionManager;
    private boolean isUserReg;

    private SessionManager() {

    }

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }

    public void setUserReg(boolean userReg) {
        isUserReg = userReg;
    }

    public boolean isUserReg() {
        return isUserReg;
    }
}
