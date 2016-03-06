import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by Martin on 2/22/2016.
 */
public class Battleship {
    private char[][] userBoard; // This will be the board the users sees, ships are hidden. Hits are marked as S. Misses are M.
    private char[][] answerBoard; // This will be the board that stores the location of ships, hidden from the user.
    private int turn = 0;
    private boolean debug = false;
    private static Scanner scan = new Scanner(System.in);

    public Battleship(int width, int height) {
        if(width < 3 || height < 3) {
            System.out.println("Minimum board dimensions are 3x3. Board set to 3x3.");
            width = 3;
            height = 3;
        }
        userBoard = new char[height][width];
        answerBoard = new char[height][width];
        for (int i = 0; i < userBoard.length; i++) {
            for (int j = 0; j < userBoard[0].length; j++) {
                userBoard[i][j] = '~';
                answerBoard[i][j] = '~';
            }
        }
        initializeBoard();
    }
    public void initializeBoard() {
        int area = userBoard.length * userBoard[0].length; // Variable number of ships based on board area
        for(int i = 0; i < (area + 11) / 20; i++) {
            this.addShip();
        }
        for(int i = 0; i < (area + 11) / 20; i++) {
            this.addMine(); // Ships must be added first, otherwise would put this in previous loop
        }
        /*
        Area:
        9-28:   1 ship, 1 mine
        29-48:  2 ships, 2 mines
        49-68:  3 ships, 3 mines
        Every multiple of 20 above this increases ship and mine count by 1
        */
    }
    public void addShip() {
        int x = (int)(Math.random() * userBoard[0].length);
        int y = (int)(Math.random() * userBoard.length);
        if(((int)(Math.random() * 2)) == 0) { // 50/50 whether ship is placed vertically or horizontally
            try { // Places ships horizontally
                if(x + 1 < answerBoard[0].length && x - 1 >= 0 && answerBoard[y][x - 1] != 's' && answerBoard[y][x] != 's' && answerBoard[y][x + 1] != 's') {
                    for(int i = -1; i <= 1; i++) {
                        answerBoard[y][x + i] = 's';
                    }
                } else {
                    this.addShip();
                }
            } catch (Exception e) {
                this.addShip(); // If "try block" tried to place ship off of board, the "catch" will have it replace the ship
            }
        } else {
            try { // Places ships vertically
                if(y + 1 < answerBoard.length && y - 1 >= 0 && answerBoard[y - 1][x] != 's' && answerBoard[y][x] != 's' && answerBoard[y + 1][x] != 's') {
                    for(int i = -1; i <= 1; i++) {
                        answerBoard[y + i][x] = 's';
                    }
                } else {
                    this.addShip();
                }
            } catch (Exception e) {
                this.addShip();
            }
        }
    }
    public void addMine() {
        int h = (int)(Math.random() * answerBoard.length);
        int r = (int)(Math.random() * answerBoard[0].length);
        if (answerBoard[h][r] == 's') {
            addMine();
        } else {
            answerBoard[h][r] = 'm';
        }
    }
    public String print(char[][] board) { // Had char[][] as parameter so it can print both userBoard and answerBoard
        String display = "";

        for(int i = board.length - 1; i >= 0; i--) {
            display += (i + 1) + ""; // Y axis label
            for(int j = 0; j < board[0].length; j++) {
                display += " " + board[i][j];
            }
            display += '\n';
        }
        display += "  ";
        for(int i = 1; i <= board[0].length; i++) { // Print numbers 1-5 along bottom of the grid
            display += i + " "; // X axis label
        }
        return display;
    }
    public void takeTurn() {
        System.out.println(print(this.userBoard) + '\n');
        if(debug) {
            System.out.println(print(this.answerBoard) + "\n");
        }
        turn++;
        System.out.println("Turn: " + turn);
        System.out.println("Enter x coordinate: ");
        int x = scan.nextInt();
        System.out.println("Enter y coordinate: ") ;
        int y = scan.nextInt();
        int r = y - 1;
        int c = x - 1;
        try {
            switch (answerBoard[r][c]) {
                case 's':
                    System.out.println("Ship has been hit!");
                    answerBoard[r][c] = 'x';
                    userBoard[r][c] = 'x';
                    break;
                case '~':
                    System.out.println("Miss.");
                    answerBoard[r][c] = 'o';
                    userBoard[r][c] = 'o';
                    break;
                case 'm':
                    System.out.println("You hit a mine, extra turn!");
                    answerBoard[r][c] = 'M';
                    userBoard[r][c] = 'M';
                    turn++;
                    break;
                case 'x':
                    System.out.println("You already guessed this!");
                    break;
                case 'o':
                    System.out.println("You already guessed this!");
                    break;
                case 'M':
                    System.out.println("You already guessed this!");
                    break;
            }
            System.out.println();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Guess is out of bounds.");
        }
    }
    public boolean isDone() {
        for(int i = 0; i < answerBoard.length; i++) {
            for(int j = 0; j < answerBoard[0].length; j++) {
                if(answerBoard[i][j] == 's') {
                    return false;
                }
            }
        }
        return true;
    }
    public void checkBest() {
        try {
            Scanner s = new Scanner(new File("bestScore"));
            int old = 0;
            try {
                old = s.nextInt();
            } catch (Exception d) {}
            System.out.println("Best score is " + old);
            if (turn < old) {
                try {
                    PrintWriter p = new PrintWriter(new File("bestScore"));
                    p.print(turn);
                    p.close();
                    System.out.println("New best score set at " + turn);
                } catch  (Exception f) {}
            }
        } catch (Exception e) {
            try {
                PrintWriter p = new PrintWriter(new File("bestScore"));
                p.print(turn);
                p.close();
            } catch  (Exception f) {}
            System.out.println("Best score set at " + turn);

        }
    }
    public static void main(String[] args) {
        Battleship b1;
        System.out.print("Test/Debug Mode (displays answer board too)? (Y/N):  ");
        String input = scan.nextLine();
        System.out.print("Default board settings? (Y/N):  ");
        if(scan.nextLine().equals("Y")) {
            b1 = new Battleship(5, 5);
        } else {
            System.out.println("Minimum board dimensions are 3x3.");
            System.out.print("Please enter width: ");
            int w = scan.nextInt();
            System.out.print("Please enter height: ");
            int h = scan.nextInt();
            b1 = new Battleship(w, h);
        }

        System.out.println("");

        if ("Y".equals(input)) {
            b1.debug = true;
        }

        while(!b1.isDone()) {
            b1.takeTurn();
        }
        System.out.println("User took " + b1.turn + " turns.");
        b1.checkBest();
    }
}