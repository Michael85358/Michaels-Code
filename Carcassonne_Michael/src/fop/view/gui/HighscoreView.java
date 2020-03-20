package fop.view.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

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
	private JButton btnCertificate;
	private JTable scoreTable;
	private JLabel lblTitle;
	private JScrollPane scrollPane;

	private Font shadowsIntoLightTwo;

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

//		btnBack.setLocation((getWidth() / 3) - (BUTTON_SIZE.width / 2), getHeight() - BUTTON_SIZE.height - 25);
//		btnClear.setLocation((2 * (getWidth() / 3) - (BUTTON_SIZE.width / 2)), getHeight() - BUTTON_SIZE.height - 25);
//		btnCertificate.setLocation(50, 50);
		btnBack.setLocation((getWidth() / 4) - (BUTTON_SIZE.width / 2), getHeight() - BUTTON_SIZE.height - 25);
		btnCertificate.setLocation((2 * (getWidth() / 4) - (BUTTON_SIZE.width / 2)),
				getHeight() - BUTTON_SIZE.height - 25);
		btnClear.setLocation((3 * (getWidth() / 4) - (BUTTON_SIZE.width / 2)), getHeight() - BUTTON_SIZE.height - 25);
	}

	@Override
	protected void onInit() {
		btnBack = createButton("Back");
		btnClear = createButton("Delete");
		btnCertificate = createButton("Certify");
		btnCertificate.setToolTipText("Creates a certificate for the selected player");
		lblTitle = createLabel("Highscores", 45, true);
		// Register a handwriting Font
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT,
					new File("res" + System.getProperty("file.separator") + "ShadowsIntoLightTwo-Regular.ttf"));
			shadowsIntoLightTwo = f.deriveFont(Font.BOLD, 32f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			shadowsIntoLightTwo = new Font("SansSerif", Font.BOLD, 32);
		}

		Resources resources = Resources.getInstance();
		List<ScoreEntry> scoreEntries = resources.getScoreEntries();
		String[] columnNames = { "Name", "Score", "Date" };
		Object[][] data = new Object[scoreEntries.size()][3];
		// Formatting of Date
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		// Store all ScoreEntries in the Array
		for (int i = 0; i < scoreEntries.size(); i++) {
			ScoreEntry scoreEntry = scoreEntries.get(i);
			data[i][0] = scoreEntry.getName();
			data[i][1] = scoreEntry.getScore();
			data[i][2] = formatter.format(scoreEntry.getDate());
		}
		// Add the table
		scoreTable = new JTable(data, columnNames);
		scrollPane = new JScrollPane(scoreTable);
		// Better for small Tables
		scoreTable.setFillsViewportHeight(true);
		// Rows can be sorted
		scoreTable.setAutoCreateRowSorter(true);
		// Columns cannot be rearanged
		scoreTable.getTableHeader().setReorderingAllowed(false);
		// Only a single row can be selected at a time
		scoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// preselect the first row
		scoreTable.setRowSelectionInterval(0, 0);

		add(scrollPane);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(btnBack)) {
			GameMethods.GoToMainMenu();

		} else if (actionEvent.getSource().equals(btnCertificate)) {
			int row = scoreTable.getSelectedRow();
			if (row >= 0) {
				// Get the information
				String name = scoreTable.getValueAt(row, 0).toString();
				String score = scoreTable.getValueAt(row, 1).toString();
				String date = scoreTable.getValueAt(row, 2).toString();
				// File path as string
				String path = "Certificates" + System.getProperty("file.separator") + "Certificate_" + name + ".png";
				createCertificate(name, score, date, path);
				// A popup to inform the user about the location
				JOptionPane.showMessageDialog(null,
						"Your certificate was saved at\n" + System.getProperty("user.dir")
								+ System.getProperty("file.separator") + path,
						"Confirmation", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			int message = MessagesConstants.deleteHighScore();
			GameMethods.deleteHighScoreEntries(message);
		}
	}

	/**
	 * Saves a certificate
	 * 
	 * @author <u>team 129</u>
	 * @param name  The name of the player
	 * @param score The score of the player
	 * @param date  The date this score was achieved
	 * @param path  Where the certificate shall be saved (relativ from base)
	 */
	private void createCertificate(String name, String score, String date, String path) {
		try {
			// The template
			BufferedImage img = ImageIO
					.read(new File("res" + System.getProperty("file.separator") + "Certificate.png"));
			BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
			// Copy the template
			Graphics2D graphics = result.createGraphics();
			graphics.drawImage(img, 0, 0, null);
			// Get Font informations
			graphics.setColor(new Color(0, 0, 64));
			graphics.setFont(shadowsIntoLightTwo);
			FontMetrics fontMetrics = graphics.getFontMetrics();
			// Draw the new certificate
			graphics.drawString(name, 317 - (fontMetrics.stringWidth(name) / 2), 510);
			graphics.drawString(date, 317 - (fontMetrics.stringWidth(date) / 2), 684);
			graphics.drawString(score, 317 - (fontMetrics.stringWidth(score) / 2), 808);
			graphics.dispose();
			// Save it
			ImageIO.write(result, "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
