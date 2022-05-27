package MinesweeperGame;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MinesweeperPanel extends JPanel {

	private static final long serialVersionUID = 7591460034405223955L;
	
	private boolean hasBomb;
	private MinesweeperPanel[][] attachedBoard;
	private int bombsAround = 0; // Base value (changes during initialization of board)
	
	private JPanel revealedPanel = new JPanel();
	private JLabel revealedPanelText = new JLabel("");
	private JPanel defaultPanel = new JPanel();
	private JPanel flaggedPanel = new JPanel();
	private JLabel flaggedPanelIcon = new JLabel("");
	
	CardLayout cards = new CardLayout();
	
	private boolean revealedStatus = false;
	private boolean flaggedStatus = false;

	/**
	 * Initializes an Individual Minesweeper Panel for use in MinesweeperPanel (The
	 * game board)
	 * 
	 * @param hasBomb Indicates whether or not this panel contains a bomb
	 */
	public MinesweeperPanel(boolean hasBomb) {
		this.hasBomb = hasBomb;
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setLayout(cards);
		
		defaultPanel.setBackground(Color.lightGray);
		
		flaggedPanel.setBackground(Color.lightGray);
		flaggedPanel.add(flaggedPanelIcon);
		flaggedPanelIcon.setAlignmentX(CENTER_ALIGNMENT);
		flaggedPanelIcon.setIcon(new ImageIcon("flag.png"));

		revealedPanelText.setAlignmentX(CENTER_ALIGNMENT);
		revealedPanel.add(revealedPanelText);
		
		this.add(defaultPanel, "Default");
		this.add(revealedPanel, "Revealed");
		this.add(flaggedPanel, "Flagged");
		
		this.addMouseListener(new ClickListener());
	}

	// Accessor and getter methods

	public boolean containsBomb() {
		return hasBomb;
	}

	public int getBombsAround() {
		return bombsAround;
	}
	
	public boolean getRevealStatus() {
		return revealedStatus;
	}

	public void setBombStatus(boolean b) {
		hasBomb = b;
	}

	public void setBombsAround(int b) {
		bombsAround = b;
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
				board[r][c].attachedBoard = board;
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

	private class ClickListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if(revealedStatus) {
				return;
			}
			MinesweeperPanel source = (MinesweeperPanel) e.getSource();
			if(SwingUtilities.isLeftMouseButton(e)) {
				source.revealPanel();
				if(source.containsBomb()) {
					source.gameOver();
				}
			} else if(SwingUtilities.isRightMouseButton(e)) {
				if(flaggedStatus) {
					cards.show(source, "Default");
					flaggedStatus = false;
				} else {
					cards.show(source, "Flagged");
					flaggedStatus = true;
				}
			}
		}
		
		public void mouseEntered(MouseEvent e) {
			if(!revealedStatus) {
				setBackground(Color.gray);
				setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			}
		}
		
		public void mouseExited(MouseEvent e) {
			if(!revealedStatus) {
				setBackground(Color.lightGray);
				setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			}
		}
	}
	
	void revealPanel() {
		cards.show(this, "Revealed");
		setBackground(Color.lightGray);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		revealedStatus = true;
	}

	public void gameOver() {
		for (MinesweeperPanel[] row : attachedBoard) {
			for (MinesweeperPanel col : row) {
				if (!col.getRevealStatus()) {
					col.revealPanel();
				}
			}
		}
	}
}
