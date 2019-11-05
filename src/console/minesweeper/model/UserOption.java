package console.minesweeper.model;

public enum UserOption {
    MARK("MARK"),
    OPEN("OPEN");

    private String option;

    UserOption(String option) {
        this.option = option;
    }

    public static UserOption fromValue(String option) {
        for (UserOption userOption : values()) {
            if (userOption.option.equalsIgnoreCase(option)) {
                return userOption;
            }
        }

        return null;
    }
}
