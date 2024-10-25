package org.account.mgmtsystem.data;

public enum StatusType {

    REQUESTED("Requested"),
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String value;

    StatusType(String value) { this.value = value; }

    public String getValue() {
        return value;
    }
}
