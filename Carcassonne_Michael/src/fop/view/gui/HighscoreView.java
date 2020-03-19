package fop.view.gui;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

import fop.controller.GameController;
import fop.model.interfaces.GameConstants;
import fop.model.interfaces.GameMethods;
import fop.model.interfaces.MessagesConstants;
import fop.model.player.ScoreEntry;
import fop.view.components.View;
import fop.view.components.gui.Resources;

/**
 * HighScore Area
 *
 */
public class HighscoreView extends View {

	private JButton btnBack;
	private JButton btnClear;
	private JTable scoreTable;
	private JLabel lblTitle;
	private JScrollPane scrollPane;

	public HighscoreView(GameWindow gameWindow) {
		super(gameWindow);
	}

	@Override
	public void onResize() {
		int offsetY = 25;
		lblTitle.setLocation((getWidth() - lblTitle.getWidth()) / 2, offsetY);
		offsetY += lblTitle.getSize().height + 25;
		scrollPane.setLocation(25, offsetY);
		scrollPane.setSize(getWidth() - 50, getHeight() - 50 - BUTTON_SIZE.height - offsetY);

		btnBack.setLocation((getWidth() / 3) - (BUTTON_SIZE.width / 2), getHeight() - BUTTON_SIZE.height - 25);
		btnClear.setLocation((2 * (getWidth() / 3) - (BUTTON_SIZE.width / 2)), getHeight() - BUTTON_SIZE.height - 25);
	}

	@Override
	protected void onInit() {
		btnBack = createButton("Back");
		btnClear = createButton("Delete");
		lblTitle = createLabel("Highscores", 45, true);

		Resources resources = Resources.getInstance();

		// TODO
		String ABOUT_TEXT = "~ Carcassone ~\nFOP-Projekt WiSe 19/20";
		 JTextPane txtInfo = new JTextPane();
		scrollPane = new JScrollPane();
		
		txtInfo.setText(ABOUT_TEXT);
		scrollPane.add(txtInfo);

		List<ScoreEntry> scoreEntries = resources.getScoreEntries();
		String[] columnNames = { "Name", "Score", "Date" };
		Object[][] data = new Object[scoreEntries.size()][3];
		for (int i = 0; i < scoreEntries.size(); i++) {
			ScoreEntry scoreEntry = scoreEntries.get(i);
			data[i][0] = scoreEntry.getName();
			data[i][1] = scoreEntry.getScore();
			data[i][2] = scoreEntry.getDate();
		}
		scoreTable = new JTable(data, columnNames);
		scrollPane = new JScrollPane(scoreTable);
		scoreTable.setFillsViewportHeight(true);
		scoreTable.setAutoCreateRowSorter(true);
		add(scrollPane);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(btnBack)) {
			GameMethods.GoToMainMenu();

		} else {
				int message = MessagesConstants.deleteHighScore();
				GameMethods.deleteHighScoreEntries(message);
		}
	}
}
