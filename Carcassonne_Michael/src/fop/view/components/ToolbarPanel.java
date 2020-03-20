package fop.view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import fop.model.interfaces.Observer;
import fop.model.player.Player;

/**
 * creates a ToolbarPanel which displays Playername und updates the score.
 *
 */
public class ToolbarPanel extends JPanel implements Observer<List<Player>> {
	JButton mission1Button;
	JButton mission2Button;
	
	JButton menuButton;
	JButton skipButton;
	JLabel[] playerLabels;

	/**
	 * creates boxes for the players which get the MeepleColor as the border color.
	 * Displays the name and the current score
	 * 
	 * @param players
	 */
	public ToolbarPanel(List<Player> players) {
		setLayout(new FlowLayout(FlowLayout.LEFT));

		playerLabels = new JLabel[players.size()];
		for (int i = 0; i < players.size(); i++) {
			playerLabels[i] = new JLabel();
			playerLabels[i].setBorder(
					BorderFactory.createTitledBorder(null, players.get(i).getName(), TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, null, players.get(i).getColor().getMeepleColor()));
			playerLabels[i].setPreferredSize(new Dimension(80, 54));
			add(playerLabels[i]);
		}
		
		mission1Button = new JButton("3 Burgen Vorsprung gewinnt");
		mission1Button.setForeground(Color.RED);
		
		mission1Button.setBackground(Color.RED);
		add(mission1Button);
		
		
		mission2Button = new JButton("Strasse der Länge 12 gewinnt");
		mission2Button.setForeground(Color.RED);
		
		mission2Button.setBackground(Color.RED);
		add(mission2Button);
		
		
		menuButton = new JButton("Main menu");
		add(menuButton);

		skipButton = new JButton("Skip");
		add(skipButton);
	}

	/**
	 * Adds an action listener to the menu and skip buttons.
	 * 
	 * @param l The action listener
	 */
	public void addToolbarActionListener(ActionListener l) {
		menuButton.addActionListener(l);
		skipButton.addActionListener(l);
		mission1Button.addActionListener(l);
		mission2Button.addActionListener(l);
	}

	/**
	 * if true => display will show up if false => display will hide
	 * 
	 * @param visible
	 */
	public void showSkipButton(boolean visible) {
		skipButton.setVisible(visible);
	}
	
	/**
	 * if true => display will show up if false => display will hide
	 * 
	 * @param visible
	 */
	public void showMission1Button(boolean visible) {
		mission1Button.setVisible(visible);
	}
	
	
	public void showMission2Button(boolean visible) {
		mission2Button.setVisible(visible);
	}
	
	
	public void activateMission1Button() {
		if(mission1Button.getBackground()!=Color.RED) {
			mission1Button.setBackground(Color.RED);
			mission1Button.setForeground(Color.RED);
		}
		else {
			mission1Button.setBackground(Color.GRAY);
			mission1Button.setForeground(Color.BLACK);
		}
	}
	
	public void activateMission2Button() {
		if(mission2Button.getBackground()!=Color.RED) {
			mission2Button.setBackground(Color.RED);
			mission2Button.setForeground(Color.RED);
		}
		else {
			mission2Button.setBackground(Color.GRAY);
			mission2Button.setForeground(Color.BLACK);
		}
	}

	/**
	 * Updates the current score, Castles and meeple count for each player.
	 * 
	 * @param players (an array of the players)
	 */
	private void updatePlayers(List<Player> players) {
		for (int i = 0; i < players.size(); i++) {
			if(mission1Button.getBackground()!=Color.RED) {
			
				playerLabels[i].setText("<html>Score:  " + players.get(i).getScore() + "<br>Meeples: "
						+ players.get(i).getMeepleAmount() );
			}
			
			else {
			playerLabels[i].setText("<html>Score:  " + players.get(i).getScore() + "<br>Meeples: "
					+ players.get(i).getMeepleAmount() +
					
					"<br>Castles:  " + players.get(i).getCastle() +"</html>" +"<br> ");
			}
		}
	}

	/**
	 * updates the current score on the Board
	 */
	@Override
	public void update(List<Player> players) {
		updatePlayers(players);
	}

}
