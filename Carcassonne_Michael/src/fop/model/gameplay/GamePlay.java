package fop.model.gameplay;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fop.controller.GameController;
import fop.model.interfaces.GameConstants;
import fop.model.interfaces.GameMethods;
import fop.model.interfaces.GamePlayMethods;
import fop.model.interfaces.MessagesConstants;
import fop.model.player.Player;
import fop.model.player.Players;
import fop.model.tile.Position;
import fop.model.tile.Tile;
import fop.model.tile.TileStack;
import fop.view.components.gui.GameView;

public class GamePlay extends Observable<List<Player>> implements GamePlayMethods, GameConstants {

	private GameController gc;

	//Marker, ob Mission 1 und 2 aktiv sind oder nicht
	public boolean mission1=true;
	public boolean mission2=true;
	
	
	public GamePlay(GameController gc) {

		this.gc = gc;
	}
	
	public GameController getGameController() {
		return this.gc;
	}

	@Override
	public Tile pickUpTile() {
		return gc.getTileStack().pickUpTile();
	}

	@Override
	public void newTile(Tile t, int x, int y) {
		gc.getGameBoard().newTile(t, x, y);
	}

	@Override
	public void placeMeeple(Position position) {
		gc.getGameBoard().placeMeeple(position, currentPlayer());
		nextRound();
	}

	@Override
	public boolean isTopTileAllowed(int x, int y) {
		return gc.getGameBoard().isTileAllowed(gc.getTileStack().peekTile(), x, y);
	}

	@Override
	public void rotateTopTile() {
		gc.getTileStack().rotateTopTile();
	}

	
	public Player mission1() {
		
		ArrayList<Player> pl = new ArrayList<Player>();
		pl.addAll(gc.getPlayers());
		int max=0;
		boolean t=false;
		Player Burgherr=null;
		for(Player p: pl) {
			
			
			if( max<p.getCastle())
				max = p.getCastle();
		}
		if(max>=3) t = true;
		
		Player master = null;
		
		ArrayList<Player> pl2 = new ArrayList<Player>();
		
		for(Player b: pl) {
			if((b.getCastle()==max)) 
				master = b;
			else
			    pl2.add(b);
			
		}
		
		for(Player p: pl2) 
			if(max-p.getCastle() <3)
					t = false;
		
		if(t) 
		
		
			return master;
		else
			return null;
	}
	
	
	
	
	public ArrayList<Player> mission2() {
		
		ArrayList<Player> pl = new ArrayList<Player>();
		pl.addAll(gc.getPlayers());
		ArrayList<Player> pl2 = new ArrayList<Player>();
		Player Strasse=null;
		for(Player p: pl) 
			
			
			if( p.getStreet()>11)
				pl2.add(p);
		
		
		
		if(pl2.size()>0)
			return pl2;
		else
			return null;
	}
	
	
	
	
	@Override
	public void nextRound() {
		
		if(mission1==false)
				gc.getGameView().getToolbarPanel().showMission1Button(false); 
		if(mission2==false)
			gc.getGameView().getToolbarPanel().showMission2Button(false); 
		
		ArrayList<Player> pl = new ArrayList<Player>();
		pl.addAll(gc.getPlayers());
		
		if(!currentPlayer().getName().equals("AI"))
			gc.getGameBoardPanel().removeTempMeepleOverlay();
		
		//Hinzugefügt: Siegesbedingungen für Missionen werden geprüft, wenn diese angeschaltet sind
		gc.getGameBoard().calculatePoints(gc.getState());
		Player m1 =mission1();
		if (	(gc.getTileStack().remainingTiles() == 0)||
				((mission1()!=null)&&mission1==true)||
				((mission2()!=null))&&(mission2==true))
			gc.setState(State.GAME_OVER);
		
		
		else {
			
			
			gc.getGameBoard().push(gc.getGameBoard());
			
			
			gc.incrementRound();
			gc.setState(State.PLACING_TILE);
			
			
					
		
			
			
		}
	}

	@Override
	public void initGameboard() {
		gc.getGameBoard().initGameboard(gc.getTileStack().pickUpTile());
	}

	@Override
	public void setupObservers() {
		addObserver(gc.getGameView().getToolbarPanel());
		gc.getTileStack().addObserver(gc.getTileStackPanel());
		gc.getTileStack().addObserver(gc.getGameBoardPanel().getTileOverlay());
		gc.getGameBoard().addObserver(gc.getGameBoardPanel());
	}

	@Override
	public void setupListeners() {
		gc.getGameView().getToolbarPanel().addToolbarActionListener((event) -> {
			switch (event.getActionCommand()) {
			case "Main menu":
				GameMethods.GoToMainMenu();
				break;
			case "Skip":
				nextRound();
				break;
				
			//Button zum zu- und abschalten von Mission2
			case "3 Burgen Vorsprung gewinnt":
				if(gc.getGameBoard().getTiles().size()==1)
					gc.getGameView().getToolbarPanel().activateMission1Button();
				if(mission1==true)
					mission1=false;
				else
					mission1=true;
					break;
					
			//Button zum zu- und abschalten von Mission2
			case "Strasse der Länge 12 gewinnt":
				if(gc.getGameBoard().getTiles().size()==1)
					gc.getGameView().getToolbarPanel().activateMission2Button();
				if(mission2==true)
					mission2=false;
				else
					mission2=true;
					break;
				
			}
		});
	}

	@Override

	public Player currentPlayer() {
		return gc.getPlayers().get(gc.getCurrentRound() % gc.getPlayers().size());
	}

	@Override
	public void initGame() {
		gc.setPlayers(Players.getPLayers());
		gc.setBoard(new Gameboard());
		gc.setTileStack(new TileStack());
		GameView view = new GameView(gc);
		gc.setGameView(view);
		gc.setView(view);
		gc.setGameBoardPanel(view.getGameBoardPanel());
		gc.setTileStackPanel(view.getTileStackPanel());
		setupListeners();
		setupObservers();
		initGameboard();
		gc.setState(State.PLACING_TILE);
	}

	@Override
	public void placing_Tile_Mode() {
		
			push(gc.getPlayers()); // push players to observers (= ToolbarPanel)

			// According to the rules, a tile that does not fit anywhere is not mixed into
			// the stack again, but simply discarded.
			if (!gc.getGameBoard().isTileAllowedAnywhere(gc.getTileStack().peekTile())) {
				gc.getTileStack().discardTopTile();
			}

			gc.getTileStack().push(gc.getTileStack()); // pushes tile stack to observers (= TileStackPanel)
			if(currentPlayer().getName().equals("AI")) {
				Tile tile = gc.getTileStack().pickUpTile();
				currentPlayer().draw(this, tile);
				//gc.getTileStack().push(gc.getTileStack());
				gc.setState(State.PLACING_MEEPLE);
				return;
			}
			gc.getGameView().getToolbarPanel().showSkipButton(false);
			gc.getGameView().setStatusbarPanel(
			MessagesConstants.playerPlacingTile(currentPlayer().getName()), currentPlayer().getColor().getMeepleColor());
			
	}

	@Override
	public void placing_Meeple_Mode() {
		// When the current player does not have any meeple left, go to next round
		// immediately.
		if (currentPlayer().getMeepleAmount() == 0) {
			nextRound();
		} else {

			Tile newestTile = gc.getGameBoard().getNewestTile();
			boolean[] meepleSpots = gc.getGameBoard().getMeepleSpots();
			if(currentPlayer().getName().equals("AI")) {
				currentPlayer().placeMeeple(this);
			}else if (meepleSpots != null && !currentPlayer().getName().equals("AI")) {
				gc.getGameBoardPanel().showTemporaryMeepleOverlay(meepleSpots, newestTile.x, newestTile.y,
						currentPlayer());
				gc.getTileStackPanel().hideTopTile();
				gc.getGameView().getToolbarPanel().showSkipButton(true);
				gc.getGameView().setStatusbarPanel(MessagesConstants.playerPlacingMeeple(currentPlayer().getName()),  currentPlayer().getColor().getMeepleColor());
				// Now waiting for user input
			} else {
				// If there are no possibilities of placing a meeple, skip to the next round
				// right away.
				nextRound();
			}
		}
	}
	
	public void game_Over_Mode() {
		gc.getGameBoard().calculatePoints(gc.getState());
		gc.getGameBoard().push(gc.getGameBoard());
		push(gc.getPlayers());
		gc.getGameView().getToolbarPanel().showSkipButton(false);
		gc.getGameView().setStatusbarPanel(MessagesConstants.getWinnersMessage(getWinners(gc.getPlayers())), WINNING_MESSAGE_COLOR);
		MessagesConstants.showWinner(getWinners(gc.getPlayers()));
		GameMethods.GoToMainMenu();
	}

	
	@Override
	public	List<Player> getWinners(List<Player>players) {
		
		List<Player> winners = new LinkedList<Player>();
		int highestScore = 0;
		
		for (Player p : players)
			
			
			if (p.getScore() > highestScore) {
				winners = new LinkedList<Player>();
				winners.add(p);
				highestScore = p.getScore();
			} else if (p.getScore() == highestScore) {
				winners.add(p);
			}
		
		//Damit Missionen 1 und 2 Beachtung finden:
		Player m1 = mission1(); 
		ArrayList<Player> m2 = mission2();
		if((m1 != null)||(m2!=null)) {
			winners = new LinkedList<Player>();
			if(m1!=null)
				winners.add(m1);
			if(m2!=null)
				winners.addAll(m2);
			
			
		}
		
		
		return winners;
	}

}
