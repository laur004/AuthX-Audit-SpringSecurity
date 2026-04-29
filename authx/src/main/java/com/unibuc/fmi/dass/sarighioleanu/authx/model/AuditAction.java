package com.unibuc.fmi.dass.sarighioleanu.authx.model;

public enum AuditAction {
    LOGIN,
    LOGOUT,
    REGISTER,
    GENERATE_RESET_PASSWORD_TOKEN,
    CHANGE_PASSWORD,
    ACCOUNT_BLOCKED,

    CREATE_TICKET,
    EDIT_TICKET,
    DELETE_TICKET,

    VIEW_LOGS
}
