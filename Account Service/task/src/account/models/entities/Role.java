package account.models.entities;

public enum Role {
    ROLE_USER(true, "USER"),
    ROLE_ACCOUNTANT(true, "ACCOUNTANT"),
    ROLE_ADMINISTRATOR(false, "ADMINISTRATOR"),
    ROLE_AUDITOR(true, "AUDITOR");

    private final boolean isBusinessUser;
    private final String name;

    Role(boolean isBusinessUser, String name) {
        this.isBusinessUser = isBusinessUser;
        this.name = name;
    }

    public boolean isBusinessUser() {
        return isBusinessUser;
    }

    public String getName() {
        return name;
    }
}
