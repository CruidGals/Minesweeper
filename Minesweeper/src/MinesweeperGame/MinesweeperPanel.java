package MinesweeperGame;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MinesweeperPanel extends JPanel {

	private static final long serialVersionUID = 7591460034405223955L;
	
	private boolean hasBomb;
	private int bombsAround = 0; // Base value (changes during initialization of board)
	
	JPanel revealedPanel = new JPanel();
	JLabel revealedPanelText = new JLabel("");
	JPanel defaultPanel = new JPanel();
	JPanel flaggedPanel = new JPanel();
	JLabel flaggedPanelIcon = new JLabel("");
	
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

	
	private class ClickListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if(revealedStatus) {
				return;
			}
			MinesweeperPanel source = (MinesweeperPanel) e.getSource();
			if(SwingUtilities.isLeftMouseButton(e)) {
				source.revealPanel();
				if(source.containsBomb()) {
					GameBoard.gameOver();
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
		
		Game.currBoard.setTilesLeft(Game.currBoard.getTilesLeft() - 1);
		if(Game.currBoard.boardCleared()) {
			GameBoard.gameOver();
		}
	}

	
	
}
