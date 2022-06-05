package MinesweeperGame;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * This class creates the "board" for the Minesweeper game. It also includes functionality for the board.
 * @author kchie
 *
 */
public class GameBoard extends JPanel {

	private static final long serialVersionUID = 5564628847039485367L;
	private static int tilesLeft;
	private static int bombs;
	static MinesweeperPanel[][] board;

	private static ImageIcon bomb;

	public void initGame(int size) {
		
		bomb = new ImageIcon(getClass().getResource("resources/bomb.png"));
		this.setLayout(new GridLayout(size, size));
		tilesLeft = size * size;
		bombs = (int) Math.pow(size, 2) / 6;
		
		board = initMinesweeperBoard(size);
		
		for(MinesweeperPanel[] row : board) {
			for(MinesweeperPanel col : row) {
				this.add(col);
			}
		}
		
	}

	/**
	 * @return Returns the number of tiles that have not been opened yet
	 */
	public static int getTilesLeft() {
		return tilesLeft;
	}

	/**
	 * Sets the number of tiles that have not been opened yet
	 * @param tilesLeft Numbers of tiles that have not been open yet
	 */
	public static void setTilesLeft(int t) {
		tilesLeft = t;
	}
	
	/**
	 * 
	 * @return Returns a boolean value depending if all the normal tiles have been cleared
	 */
	public static boolean boardCleared() {
		return tilesLeft <= bombs;
	}
	
	/**
	 * Creates a MinesweeperPanel board with the given size and with randomized bomb
	 * placement
	 * 
	 * @param size Size of the board
	 * @return A square board initialized with MinesweeperPanel panels
	 */
	public static MinesweeperPanel[][] initMinesweeperBoard(int size) {
		MinesweeperPanel[][] board = new MinesweeperPanel[size][size];

		int spacesUnassigned = size * size;
		int maxBombs = (int) Math.pow(size, 2) / 6;

		// Creating the minesweeper panels
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				board[r][c] = new MinesweeperPanel(false);
			}
		}
		// Setting random bombs and setting bombsAround values
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				int rand = (int) (Math.random() * spacesUnassigned);
				MinesweeperPanel orig = board[r][c];
				
				if (rand < maxBombs) {
					orig.setBombStatus(true);
					maxBombs--;
					orig.revealedPanelText.setIcon(bomb);
					updateAdjacentBombsAround(board, r, c);
				}
				
				spacesUnassigned--;
			}
		}

		// Setting the color for the bombs
		for (int r = 0; r < board.length; r++) {
			for (int c = 0; c < board[r].length; c++) {
				MinesweeperPanel orig = board[r][c];
				JPanel panel = orig.revealedPanel;

				if (orig.containsBomb()) {
					panel.setBackground(Color.red);
				} else {
					if (orig.getBombsAround() > 0)
						orig.revealedPanelText.setText("" + orig.getBombsAround());
					panel.setBackground(Color.green);
				}

			}
		}

		return board;
	}
	
	/**
	 * Updates the adjacent panels in a board if the panel itself is a bomb.
	 * 
	 * @param board Board that will be updated
	 * @param r     Row of the bomb
	 * @param c     Column of the bomb
	 */
	private static void updateAdjacentBombsAround(MinesweeperPanel[][] board, int r, int c) { // Gotta be a more
																								// efficient way to do
		int[] loopValues = iterationValuesAroundTile(board, r, c);
		int startI = loopValues[0];
		int startJ = loopValues[1];
		int maxI = loopValues[2];
		int maxJ = loopValues[3];

		for (int i = startI; i <= maxI; i++) {
			for (int j = startJ; j <= maxJ; j++) {
				if (i == 0 && j == 0)
					continue;
				MinesweeperPanel temp = board[r + i][c + j];

				temp.setBombsAround(temp.getBombsAround() + 1);
			}
		}
	}
	
	/**
	 * Finds a tile on the game board and returns the coordinates for it
	 * @param panel Tile that will be examined
	 * @return Coordinates of the tile on the board
	 */
	public static int[] findTile(MinesweeperPanel panel) {
		int[] coords = new int[2];
		
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				if(board[i][j].equals(panel)) {
					coords[0] = i;
					coords[1] = j;
					return coords;
				}
			}
		}
		return coords;
	}
	
	/**
	 * Checks if there are bombs around a tile within a one tile radius
	 * @param panel Tile that will be examined
	 * @return A boolean value depicting whether or not there is a bomb around this tile
	 */
	public static boolean hasBombsAroundTile(MinesweeperPanel panel) {
		int[] coords = findTile(panel);
		int r = coords[0], c = coords[1];
		
		int[] loopValues = iterationValuesAroundTile(board, r, c);
		
		int startI = loopValues[0];
		int startJ = loopValues[1];
		int maxI = loopValues[2];
		int maxJ = loopValues[3];
		
		for (int i = startI; i <= maxI; i++) {
			for (int j = startJ; j <= maxJ; j++) {
				if (i == 0 && j == 0)
					continue;
				if(board[r + i][c + j].containsBomb()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Return the values needed to iterate around the specified tile (specified by it's row and column)
	 * @param board The board the tile is in
	 * @param r The row the tile is in
	 * @param c The column the tile is in
	 * @return Values that will be used to iterate an area around the specified tile
	 */
	public static int[] iterationValuesAroundTile(MinesweeperPanel[][] board, int r, int c) {
		
		int[] values = new int[4];
		int size = board.length - 1;
		int startI = -1, startJ = -1, maxI = 1, maxJ = 1;

		// Setting direction of panel
		if (r == 0) { // Row is the north side
			startI++;
			if (c == 0) //If column is left side (northwest)
				startJ++;
			else if (c == size) //If column is right side (northeast)
				maxJ--;
		} else if (r == size) { // Row is the south side
			maxI--;
			if (c == 0) // Column is left side (southwest)
				startJ++;
			else if (c == size) //Column is right side (southeast)
				maxJ--;
		} else if (c == 0) { //Column is left side
			startJ++;
		} else if (c == size) { //Column is right side
			maxJ--;
		}
		
		values[0] = startI;
		values[1] = startJ;
		values[2] = maxI;
		values[3] = maxJ;
		
		return values;
	}
}