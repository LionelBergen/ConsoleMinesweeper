package console.minesweeper.model;

public enum MinesweeperBlock{
	BOMB('@'), 
	MARKED_AS_BOMB('X'), 
	EMPTY('_'), 
	OPEN('W'),
	ONE_BOMB_AROUND('1'),
	TWO_BOMB_AROUND('2'),
	THREE_BOMB_AROUND('3'),
	FOUR_BOMB_AROUND('4'),
	FIVE_BOMB_AROUND('5'),
	SIX_BOMB_AROUND('6'),
	SEVEN_BOMB_AROUND('7'),
	EIGHT_BOMB_AROUND('8');

	char character;

	MinesweeperBlock(char character) {
		this.character = character;
	}

	public static MinesweeperBlock valueOf(char character) {
		for (MinesweeperBlock mark : values()) {
			if (character == mark.character) {
				return mark;
			}
		}
		return null;
	}

	@Override
	public String toString() {
	    return String.valueOf(character);
	}
}
