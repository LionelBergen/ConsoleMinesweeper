package console.minesweeper.model;

public class CommandSelected {
    private UserOption optionSelected;
    private Coordinate coordinateSelected;

    public UserOption getUserOption() {
        return this.optionSelected;
    }

    public Coordinate getCoordinate() {
        return this.coordinateSelected;
    }

    public CommandSelected(UserOption option, Coordinate coordinate) {
        this.optionSelected = option;
        this.coordinateSelected = coordinate;
    }
}
