package console.minesweeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import console.minesweeper.model.CommandSelected;
import console.minesweeper.model.Coordinate;
import console.minesweeper.model.MinesweeperBlock;
import console.minesweeper.model.UserOption;

import static console.minesweeper.util.Utility.isNumeric;
import static console.minesweeper.util.Utility.convertSingleDigitIntegerToCharacter;
import static console.minesweeper.util.Utility.getRandomCoordinatesWithoutDuplicates;

public class Minesweeper {
    private static final String GREETING_MESSAGE = "Hi, lets play minesweeper!";
    private static final String PICK_X_AND_Y_AREA_MESSAGE = "Pick x.length and y.length of area(print \"x y\"): ";
    private static final String NUMBER_OF_BOMBS_PROMPT = "print number of bombs: ";
    private static final String PROMPT_USER_FOR_COMMAND = "Print \"open x y\", if u want open this area, print \"mark x y\", if u want mark this area as bomb or unmark this area";

    private static final String INCORRECT_COMMAND_NUMBER_OF_ARGUMENTS = "fill out the form correctly!";
    private static final String INVALID_COMMAND = "first work should be equal \"open\" or \"mark\"!";
    private static final String COMMAND_NOT_A_NUMBER = "x and y should be numbers!";
    private static final String INVALID_COORDINATE = "x and y should be in area! P.S.: area.lengthY=%s. area.lengthX=%s";

    private static final Scanner SCANNER = new Scanner(System.in);
    
    public static void main(String[] args) {
    	Minesweeper mineSweeper = new Minesweeper();
    	
    	mineSweeper.play();
    }

    public void play() {
        boolean isWin = false;

        System.out.println(GREETING_MESSAGE);
        MinesweeperBlock[][] grid = getLengthsOfAreaFromUser(SCANNER);

        final int numberOfBombs = getNumberOfBombsFromUser(grid, SCANNER);
        System.out.println("hi");
        fillArea(grid, numberOfBombs);

        while(true) {
            printGrid(grid, true);

            MinesweeperBlock mark = playerMakeAMove(grid, SCANNER);

            if(mark == MinesweeperBlock.BOMB) {
                isWin = false;
                break;
            }
            else if(playerHasWonGame(grid)) {
                isWin = true;
                break;
            }
        }

        if(isWin) {
            printGrid(grid, false);
            System.out.println("U won!");
        }
        else {
            printGrid(grid, false);
            System.out.println("Defieat!");
        }
    }

    private boolean playerHasWonGame(MinesweeperBlock[][] area) {
        for (MinesweeperBlock[] markArray : area) {
            for (MinesweeperBlock mark : markArray) {
                if (mark == MinesweeperBlock.EMPTY) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Prompt the user to open a location or mark a location & return the updated {@link MinesweeperBlock} at the location 
     */
    private MinesweeperBlock playerMakeAMove(MinesweeperBlock[][] area, Scanner scanner) {
        CommandSelected selectedCommand = getValidCommandFromUser(scanner, area);
        int x = selectedCommand.getCoordinate().getX();
        int y = selectedCommand.getCoordinate().getY();

        MinesweeperBlock markOnArea = area[y][x];
        if(selectedCommand.getUserOption() == UserOption.OPEN) {
            if(markOnArea == MinesweeperBlock.BOMB) {
                return MinesweeperBlock.BOMB;
            }
            else {
                int numberOfBombsSurrounding = countBombsAtLocations(area, getSurroundingCoordinates(area, false, selectedCommand.getCoordinate()));

                if (numberOfBombsSurrounding == 0) {
                    area[y][x] = MinesweeperBlock.OPEN;
                    openAllAround(x, y, area);
                }
                else {
                    // E.G convert from 1 to "Mark.ONE_BOMB".
                    char character = convertSingleDigitIntegerToCharacter(numberOfBombsSurrounding);
                    area[y][x] = MinesweeperBlock.valueOf(character);
                }

                return area[y][x];
            }
        }
        else {
            area[y][x] = MinesweeperBlock.MARKED_AS_BOMB;
            return MinesweeperBlock.MARKED_AS_BOMB;
        }
    }

    private CommandSelected getValidCommandFromUser(Scanner scanner, MinesweeperBlock[][] area) {
        while (true) {
            System.out.println(PROMPT_USER_FOR_COMMAND);
            String[] commandAndXAndY = scanner.nextLine().split(" ");

            if(commandAndXAndY.length != 3) {
                System.out.println(INCORRECT_COMMAND_NUMBER_OF_ARGUMENTS);
            }
            else {
                String commandSelected = commandAndXAndY[0];
                String xSelected = commandAndXAndY[1];
                String ySelected = commandAndXAndY[2];

                if(!commandSelected.equalsIgnoreCase("open") && commandSelected.equalsIgnoreCase("mark")) {
                    System.out.println(INVALID_COMMAND);
                }
                else if(!isNumeric(xSelected) || !isNumeric(ySelected)) {
                    System.out.println(COMMAND_NOT_A_NUMBER);
                }
                else if(!isValidCoordinate(Integer.parseInt(xSelected), Integer.parseInt(ySelected), area)) {
                    System.out.print(String.format(INVALID_COORDINATE, area.length, area[0].length));
                }
                else {
                    return new CommandSelected(UserOption.fromValue(commandSelected), new Coordinate(Integer.parseInt(xSelected), Integer.parseInt(ySelected)));
                }
            }
        }
    }

    private void openAllAround(int x, int y, MinesweeperBlock[][] area) {
        List<Coordinate> coordinatesInArea = getAllSurroundingCoordinates(area, false, x, y);

        for(Coordinate coordinate : coordinatesInArea) {
            MinesweeperBlock markAtLocation = area[coordinate.getY()][coordinate.getX()];

            if (!hasLocationBeenMarked(markAtLocation) && MinesweeperBlock.BOMB != markAtLocation) {
                int numberOfBombsSurrounding = countBombsAtLocations(area, getSurroundingCoordinates(area, false, coordinate));

                if (numberOfBombsSurrounding == 0) {
                    area[coordinate.getY()][coordinate.getX()] = MinesweeperBlock.OPEN;
                    openAllAround(coordinate.getX(), coordinate.getY(), area);
                }
                else {
                    // E.G convert from 1 to "Mark.ONE_BOMB".
                    char character = convertSingleDigitIntegerToCharacter(numberOfBombsSurrounding);
                    area[coordinate.getY()][coordinate.getX()] = MinesweeperBlock.valueOf(character);
                }
            }
        }
    }

    private boolean hasLocationBeenMarked(MinesweeperBlock location) {
        final List<MinesweeperBlock> LOCATIONS_THAT_SHOULD_NOT_BE_MARKED = Arrays.asList(
                MinesweeperBlock.MARKED_AS_BOMB,
                MinesweeperBlock.ONE_BOMB_AROUND,
                MinesweeperBlock.TWO_BOMB_AROUND,
                MinesweeperBlock.THREE_BOMB_AROUND,
                MinesweeperBlock.FOUR_BOMB_AROUND,
                MinesweeperBlock.FIVE_BOMB_AROUND,
                MinesweeperBlock.SIX_BOMB_AROUND,
                MinesweeperBlock.SEVEN_BOMB_AROUND,
                MinesweeperBlock.EIGHT_BOMB_AROUND,
                MinesweeperBlock.OPEN
        );

        return LOCATIONS_THAT_SHOULD_NOT_BE_MARKED.contains(location);
    }

    /**
     * Returns all valid unmarked coordinates surrounding the area of the coordinate passed
     */
    private List<Coordinate> getAllSurroundingCoordinates(MinesweeperBlock[][] area, boolean includeCoordinatePassed, int xCoord, int yCoord) {
        List<Coordinate> validCoordinatesInArea = new ArrayList<Coordinate>();

        // We want the coord itself, the coord -1 and the coord + 1 to get the full area
        for (int x = xCoord - 1; x <= xCoord + 1; x++) {
            for (int y = yCoord - 1; y <= yCoord + 1; y++) {
                // Skip this coordinate if appropriate
                if (includeCoordinatePassed || xCoord != x || yCoord != y) {
                    if (isValidCoordinate(x, y, area)) {
                        validCoordinatesInArea.add(new Coordinate(x, y));
                    }
                }
            }
        }

        return validCoordinatesInArea;
    }

    private List<Coordinate> getSurroundingCoordinates(MinesweeperBlock[][] area, boolean includeCoordinatePassed, Coordinate coordinate) {
        return getAllSurroundingCoordinates(area, includeCoordinatePassed, coordinate.getX(), coordinate.getY());
    }

    private int countBombsAtLocations(MinesweeperBlock[][] grid, Coordinate... coordinatesToCountBombsIn) {
        int numberOfBombs = 0;

        for (Coordinate coordinate : coordinatesToCountBombsIn) {
            if (grid[coordinate.getY()][coordinate.getX()] == MinesweeperBlock.BOMB) {
                numberOfBombs++;
            }
        }

        return numberOfBombs;
    }

    private int countBombsAtLocations(MinesweeperBlock[][] grid, List<Coordinate> coordinatesToCountBombsIn) {
        Coordinate[] coordinateList = new Coordinate[coordinatesToCountBombsIn.size()];
        return countBombsAtLocations(grid, coordinatesToCountBombsIn.toArray(coordinateList));
    }

    private static void printGrid(MinesweeperBlock[][] grid, boolean hideBombs) {
        System.out.println();
        for(int y = 0; y < grid.length; y++) {
            for(int x=0; x < grid[0].length; x++) {
                if (hideBombs && grid[y][x] == MinesweeperBlock.BOMB) {
                    System.out.print(MinesweeperBlock.EMPTY);
                }
                else {
                    System.out.print(grid[y][x]);
                }

            }
            System.out.println();
        }
    }

    private static void fillArea(MinesweeperBlock[][] area, int numberOfBombs) {
        fillArea(area, MinesweeperBlock.EMPTY);

        // fill area with bombs
        fillAreaWithRandomMarks(area, numberOfBombs, MinesweeperBlock.BOMB);
    }

    private static void fillArea(MinesweeperBlock[][] area, MinesweeperBlock mark) {
        for (int y = 0; y < area.length; y++) {
            for (int x = 0; x < area[y].length; x++) {
                area[x][y] = mark;
            }
        }
    }

    private static void fillAreaWithRandomMarks(MinesweeperBlock[][] area, int numberOfBombs, MinesweeperBlock mark) {
        int[][] bombLocations = getRandomCoordinatesWithoutDuplicates(numberOfBombs, area.length, area[0].length);

        for (int i = 0; i < bombLocations.length; i ++) {
            int x = bombLocations[i][0];
            int y = bombLocations[i][1];

            area[x][y] = mark;
        }
    }

    private int getNumberOfBombsFromUser(Object[][] area, Scanner scanner) {
        int numberOfBombs = -1;
        boolean isValidNumberOfBombs = false;

        do
        {
            numberOfBombs = getValidNumberFromUser(scanner, NUMBER_OF_BOMBS_PROMPT);
            isValidNumberOfBombs = isValidNumerOfBombs(numberOfBombs, area);

            if (!isValidNumberOfBombs) {
                System.out.println("it should be positive and it should not exceed the field capacity!");
            }
        } while (!isValidNumberOfBombs);

        return numberOfBombs;
    }

    private int getValidNumberFromUser(Scanner scanner, String prompt) {
        System.out.println(prompt);
        String inputFromUser = scanner.nextLine();

        while (inputFromUser == null || !isNumeric(inputFromUser)) {
            System.out.println("it should be number!");
            inputFromUser = scanner.nextLine();
        }

        return Integer.parseInt(inputFromUser);
    }

    private boolean isValidNumerOfBombs(int numberOfBombs, Object[][] area) {
        // Between 1 and the max number of spaces
        return numberOfBombs > 0 && (area.length * area[0].length) >= numberOfBombs;
    }

    private static MinesweeperBlock[][] getLengthsOfAreaFromUser(Scanner scanner) {
        MinesweeperBlock[][] gridCreated = null;
        while(gridCreated == null) {
            System.out.println(PICK_X_AND_Y_AREA_MESSAGE);
            String[] turnXandY = scanner.nextLine().split(" "); 

            if(turnXandY.length != 2) {
                System.out.println("print: \"x y\"!");
            }
            else if(!isNumeric(turnXandY[0]) || !isNumeric(turnXandY[1])) {
                System.out.println("x and y should be numbers!");
            }
            else if(Integer.parseInt(turnXandY[0]) <= 0 || Integer.parseInt(turnXandY[1]) <= 0) {
                System.out.println("x and y should be greater than 0!");
            }
            else {
                gridCreated = new MinesweeperBlock[Integer.parseInt(turnXandY[0])][Integer.parseInt(turnXandY[1])];
            }
        }

        return gridCreated;
    }

    private static boolean isValidCoordinate(int x, int y, MinesweeperBlock[][] area) {
        if(x < 0 || area[0].length <= x) {
            return false;
        }
        if(y < 0 || area.length <= y) {
            return false;
        }
        return true;
    }
}
