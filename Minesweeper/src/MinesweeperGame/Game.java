package MinesweeperGame;

/* TO-DO LIST
* 
* Make a "clump system" where user can reveal multiple blocks with one click.
*  - For this system, make it so that if a bomb isn't in the radius of ex. 2 blocks, the clump system will occur
*  
*  Make the frame actually look good
*/

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

import java.awt.event.*;

public class Game extends JFrame {

	private static final long serialVersionUID = 3135936523749221092L;
	private static CardLayout cards = new CardLayout();
	static JPanel frameSwitcher = new JPanel(cards);
	//Menu bar stuff
	static JMenuBar menuBar = new JMenuBar();
	static JMenu game = new JMenu("Game");
	static JMenuItem mainMenu = new JMenuItem("Main Menu"); 
	
	static JMenu newGame = new JMenu("New Game");
	static JMenuItem easyMenu = new JMenuItem("Easy");
	static JMenuItem mediumMenu = new JMenuItem("Medium");
	static JMenuItem hardMenu = new JMenuItem("Hard");
	static JMenuItem customMenu = new JMenuItem("Custom");
	
	static GameBoard currBoard;
	
	static boolean inPlay = false;

	public Game() {
		currBoard = new GameBoard();		
		
		setTitle("Minesweeper");
		setSize(400, 400);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		setJMenuBar(menuBar);
		menuBar.setBounds(0, 0, 400, 20);
		
		newGame.add(easyMenu);
		newGame.add(mediumMenu);
		newGame.add(hardMenu);
		newGame.add(customMenu);

		game.add(newGame);
		game.add(mainMenu);
		menuBar.add(game);
		

		ImageIcon bomb = new ImageIcon("bomb1.png");
		setIconImage(bomb.getImage());

		frameSwitcher.add(new GameStartup(), "GameStartup");
		cards.show(frameSwitcher, "GameStartup");

		getContentPane().add(frameSwitcher);
		setVisible(true);
		
		easyMenu.addActionListener(new GameLoader());
		mediumMenu.addActionListener(new GameLoader());
		hardMenu.addActionListener(new GameLoader());
		customMenu.addActionListener(new GameLoader());
		mainMenu.addActionListener(new GameLoader());
	}

	

	class GameStartup extends JPanel{

		private static final long serialVersionUID = -639427492631083189L;
		static JButton easy = new JButton("Easy");
		static JButton medium = new JButton("Medium");
		static JButton hard = new JButton("Hard");
		static JButton custom = new JButton("Custom");

		final JLabel title = new JLabel("Minesweeper", SwingConstants.CENTER);

		public GameStartup() {

			// Layout manager for GUI
			this.setLayout(new GridLayout(2, 1));

			// Top part of Main Panel
			JPanel topPanel = new JPanel(new BorderLayout());
			topPanel.add(title, BorderLayout.CENTER);
			title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 50));
			this.add(topPanel);

			// Bottom part of Main Panel
			JPanel bottomPanel = new JPanel(new GridBagLayout());
			bottomPanel.setBorder(new TitledBorder("Difficulty Selection"));

			JPanel bottomHelperPanel = new JPanel(new FlowLayout());

			easy.setSize(100, 50);
			easy.setFocusable(false);
			bottomHelperPanel.add(easy);

			medium.setSize(100, 50);
			medium.setFocusable(false);
			bottomHelperPanel.add(medium);

			hard.setSize(100, 50);
			hard.setFocusable(false);
			bottomHelperPanel.add(hard);

			custom.setSize(100, 50);
			custom.setFocusable(false);
			bottomHelperPanel.add(custom);

			bottomPanel.add(bottomHelperPanel, new GridBagConstraints());
			this.add(bottomPanel);

			easy.addActionListener(new GameLoader());
			medium.addActionListener(new GameLoader());
			hard.addActionListener(new GameLoader());
			custom.addActionListener(new GameLoader());

		}
	}
	
	/**
	 * Initiates the board for the game
	 * @param size Size of the board
	 */
	public void initiateGame(int size) {
		currBoard.initGame(size);
		frameSwitcher.add(currBoard, "Board");
		setSize(40 * size, 40 * size + 20);
		switchFrames(1);
	}
	
	/**
	 * Switches the panel (Board or Main Menu) depending on it's current state
	 * @param panel Current panel to be switched
	 */
	private void switchFrames(int panel) {
		if (panel == 0) {
			inPlay = false;
			cards.show(frameSwitcher, "GameStartup");
		} else {
			inPlay = true;
			cards.show(frameSwitcher, "Board");
		}
	}

	/**
	 * Stops the game from running by opening all the tiles on the board.
	 */
	public static void gameOver() {
		for (MinesweeperPanel[] row : GameBoard.board) {
			for (MinesweeperPanel col : row) {
				if (!col.getRevealStatus()) {
					col.revealPanel();
				}
			}
		}
	}
	
	
	
	/**
	 * Implements a new Action Listener to create a new game based on Source of the Action
	 *
	 */
	private class GameLoader implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(inPlay) {
				boolean ans = confirmationDialog();
				if(!ans) return;
			}
			
			if (e.getSource() == GameStartup.easy || e.getSource() == easyMenu) {
				initiateGame(7);
			} else if (e.getSource() == GameStartup.medium || e.getSource() == mediumMenu) {
				initiateGame(12);
			} else if (e.getSource() == GameStartup.hard || e.getSource() == hardMenu) {
				initiateGame(20);
			} else if (e.getSource() == GameStartup.custom || e.getSource() == customMenu) {
				String temp = JOptionPane.showInputDialog(null, "Input an integer (4 - 30) for the size of the game",
						"5");
				if (temp == null) {
					return;
				}
				try {
					int size = Integer.parseInt(temp);
					if (size > 30) {
						size = 30;
					} else if (size < 2) {
						size = 2;
					}
					initiateGame(size);
				} catch (NumberFormatException n) {
					System.out.println("Invalid answer");
				}
			} else if (e.getSource() == mainMenu) {
				switchFrames(0);
				setSize(400, 400);
			}
		}
		
	}
	
	public boolean confirmationDialog() {
		int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "", JOptionPane.YES_NO_OPTION);
		
		if(ans == 0) {
			return true;
		} else {
			return false;
		}
	}
}
