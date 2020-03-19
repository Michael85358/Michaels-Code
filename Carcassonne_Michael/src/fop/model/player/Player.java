package fop.model.player;

import java.util.ArrayList;
import java.util.List;

import fop.base.Node;
import fop.controller.GameController;
import fop.model.gameplay.GamePlay;
import fop.model.gameplay.Gameboard;
import fop.model.gameplay.State;
import fop.model.graph.FeatureGraph;
import fop.model.graph.FeatureNode;
import fop.model.interfaces.GameConstants;
import fop.model.interfaces.Observer;
import fop.model.interfaces.PlayerMethods;
import fop.model.tile.Position;
import fop.model.tile.Tile;
import fop.model.tile.TileStack;

public class Player implements PlayerMethods{


	private MeepleColor color;
	private String name;
	private int score;
	private int castle = 0;
	private int meeples; // the amount of meeples
	

	
	

	public Player(String name, MeepleColor color) {
		this.color = color;
		this.name = name;
		this.score = 0;
		this.meeples = GameConstants.NUMBER_OF_MEEPLES;
	}

	
	@Override
	public MeepleColor getColor() {
		return color;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void addScore(int score) {
		this.score += score;
	}

	@Override
	public int getScore() {
		return score;
	}
	
	@Override
	public void addCastle(int c) {
		this.castle += c;
	}

	@Override
	public int getCastle() {
		return castle;
	}

	@Override
	public int getMeepleAmount() {
		return meeples;
	}
	
	@Override
	public void removeMeeple() {
		meeples--;
	}
	
	@Override
	public void returnMeeple() {
		meeples++;
	}
	
	public void draw(GamePlay gp, Tile tile) {
		
		
		
		Tile t = gp.getGameController().getTileStack().cardstack.get(0);
		t=tile;
			TilePositions tp = new TilePositions(t, gp);
					
			Pos p = tp.positionList.get(0);
						
			while(p.rot!=t.getRotation()) {
				t.rotateRight();
				//gp.rotateTopTile();
				
			}
			
			
			
			
			
			
			gp.getGameController().getGameBoard().newTile(t, p.x, p.y);
			//gp.getGameController().getTileStack().cardstack.remove(0);
			//System.out.println(tp.Best.scoreComplete);
		
		
		
	}
	
	public void placeMeeple(GamePlay gp) {
	
			if(meeples==0)
				gp.nextRound();
			
			boolean[] ms=gp.getGameController().getGameBoard().getMeepleSpots();
			Position[] po = Position.getAllPosition();
			
			if(ms==null)
				gp.nextRound();
			
			
			
			
			List<Position> pos = new ArrayList<Position>();
			for(int i = 0; i<9; i++) 
				
				if(ms[i]==true) 
					pos.add((po[i]));
				 
			
				
			
					
			int rand=0;
			int max =pos.size()+2; 
	        int min = 0; 
	        int range = max - min; 
	  
	        
	       
	        rand= (int)(Math.random() * range) + min;
	       
			if(rand>=pos.size())
				gp.nextRound();
			else 
				 
				 gp.placeMeeple(pos.get(rand));
				
			
		}

	

	
	
	
	
	 class TilePositions{
		
		ArrayList<Pos> positionList = new ArrayList<Pos>();
		Pos Best = null;
		
		
		TilePositions(Tile t, GamePlay gp){
			
			
			
			positionList.addAll(returnPos(t, gp.getGameController().getGameBoard()));
			
			t.rotateRight();
			positionList.addAll(returnPos(t, gp.getGameController().getGameBoard()));
			
			t.rotateRight();
			positionList.addAll(returnPos(t, gp.getGameController().getGameBoard()));
			
			t.rotateRight();
			positionList.addAll(returnPos(t, gp.getGameController().getGameBoard()));
			
			int rand=0;
			int max = positionList.size()-1; 
	        int min = 0; 
	        int range = max - min + 1; 
	  
	         
	       
	              rand= (int)(Math.random() * range) + min; 
	  
	           
	           
	         
	        
			Best = positionList.get(rand);
			
			for(Pos p: positionList) {
				t.x = p.x;
				t.y = p.y;
				//p.finScore=scoreTester(gp.getGameController().getGameBoard(), t);
			}
			
			
		}
		
		
		
		public ArrayList<Pos> returnPos(Tile t, Gameboard gb){
			ArrayList<Pos> position = new ArrayList<Pos>();
			
				
			
				
				for(int i=0; i<gb.getTiles().size(); i++) {
					if(gb.isTileAllowed(t, gb.getTiles().get(i).x, gb.getTiles().get(i).y-1)) 
						position.add(new Pos(gb.getTiles().get(i).x, gb.getTiles().get(i).y-1, t.getRotation()));
					
					if(gb.isTileAllowed(t, gb.getTiles().get(i).x, gb.getTiles().get(i).y+1)) 
						position.add(new Pos(gb.getTiles().get(i).x, gb.getTiles().get(i).y+1, t.getRotation()));
					
					if(gb.isTileAllowed(t, gb.getTiles().get(i).x-1, gb.getTiles().get(i).y)) 
						position.add(new Pos(gb.getTiles().get(i).x-1, gb.getTiles().get(i).y, t.getRotation()));
					
					if(gb.isTileAllowed(t, gb.getTiles().get(i).x+1, gb.getTiles().get(i).y)) 
						position.add(new Pos(gb.getTiles().get(i).x+1, gb.getTiles().get(i).y, t.getRotation()));
				}	
			
			
						
							
				
			
			return position;
			
		
		}
	 
		
		
		public void scoreHelper(List<Player> Players, Tile ti, GameController gc, Pos po) { 
			int kiOld=0; int othersOld=0;
			for(Player p:Players) {
				if(p.getName().equals("AI"))
					kiOld = kiOld + p.getScore();
				else
					othersOld=othersOld + p.getScore();
			}
						
			while(po.rot!=ti.getRotation())
				ti.rotateRight();
			
			gc.getGameBoard().newTile(ti, po.x, po.y);
			gc.getGameBoard().calculatePoints(State.GAME_START);
			
			
			int kiNew=0; int othersNew=0;
			for(Player p:Players) {
				if(p.getName().equals("AI"))
					kiNew = kiNew + p.getScore();
				else
					othersNew=othersNew + p.getScore();
			}
			
		
			
			if(kiOld + kiNew - othersOld - othersNew > this.Best.scoreComplete)
				this.Best.scoreComplete = kiOld + kiNew - othersOld - othersNew;
			
			
			po.scoreComplete = kiOld + kiNew - othersOld - othersNew;  
		}
		
			
			
	
			
			
			
			
		
			
		
		
	 
	 }
		
		
		
		
		class Pos{
			int x;
			int y;
			int rot;
			int scoreComplete;
			int scoreComplete2;
			int scoreAllg;
			int finScore;
			Position setMeepleHereToCompleteRealm;
			FeatureNode setMeepleHere;
			
			Pos(int x1, int y1, int r){
				x=x1;
				y=y1;
				rot=r;
				scoreComplete =0;
				scoreComplete2=0;
				scoreAllg = 0;
				finScore=0;
				
				
			}
		
			
		}
		
		
	
	 
	
		
	
		

	
}
	 

