package console.minesweeper.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import console.minesweeper.model.Coordinate;

public class Utility {
    public static boolean isNumeric(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static int[][] getRandomCoordinatesWithoutDuplicates(int numberOfCoordinates, int widthOfGrid, int heightOfGrid) {
        List<Coordinate> cordinates = new ArrayList<Coordinate>();
        int[][] randomCoordinates = new int[numberOfCoordinates][2];

        // Get a list of all possible coordinates
        for (int x = 0; x < widthOfGrid; x++) {
            for (int y = 0; y < heightOfGrid; y++) {
                cordinates.add(new Coordinate(x, y));
            }
        }

        // shuffle aka randomize the list
        Collections.shuffle(cordinates);

        // Take top X
        for (int i = 0; i < numberOfCoordinates; i++) {
            randomCoordinates[i] = cordinates.get(i).toArray();
        }

        return randomCoordinates;
    }

    public static char convertSingleDigitIntegerToCharacter(int number) {
        return String.valueOf(number).charAt(0);
    }
}
