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
	private int tilesLeft;
	private final int bombs;
	static MinesweeperPanel[][] board;

	public GameBoard(int size) {
		
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
	public int getTilesLeft() {
		return tilesLeft;
	}

	/**
	 * Sets the number of tiles that have not been opened yet
	 * @param tilesLeft Numbers of tiles that have not been open yet
	 */
	public void setTilesLeft(int tilesLeft) {
		this.tilesLeft = tilesLeft;
	}
	
	/**
	 * 
	 * @return Returns a boolean value depending if all the normal tiles have been cleared
	 */
	public boolean boardCleared() {
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
					orig.revealedPanelText.setIcon(new ImageIcon("bomb1.png"));
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
																								// this
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
	 * Stops the game from running by opening all the tiles on the board.
	 */
	public static void gameOver() {
		for (MinesweeperPanel[] row : board) {
			for (MinesweeperPanel col : row) {
				if (!col.getRevealStatus()) {
					col.revealPanel();
				}
			}
		}
	}
	
}