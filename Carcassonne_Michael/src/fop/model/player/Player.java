package fop.model.player;

import java.util.ArrayDeque;
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
import fop.model.tile.FeatureType;
import fop.model.tile.Position;
import fop.model.tile.Tile;
import fop.model.tile.TileStack;

public class Player implements PlayerMethods{


	private MeepleColor color;
	private String name;
	private int score;
	
	//Counter für die Missionen hinzugefügt
	private int castle = 0;
	private int street = 0;
	
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
	public void setStreet(int c) {
		this.street = c;
	}

	@Override
	public int getStreet() {
		return street;
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
			
	}
	
	public void placeMeeple(GamePlay gp) {
			
		
		//Prüfe, ob meeples vorhanden sind (sind sie sowieso, einer wird immer aufgespart, falls ein graph aktuell fertig wird)
		
			if((meeples==0)||(gp.getGameController().getGameBoard().getMeepleSpots()==null))
				gp.nextRound();
		
		//Falls meeples vorhanden:	
			
			else {
				
			//Überprüfe, ob ein Gebiet durch das neuste Tile abgeschlossen wurde, falls true und die Position frei ist, setzte einen Meeple
				
				List<Position> pos = new ArrayList<Position>();
				Tile t=gp.getGameController().getGameBoard().getNewestTile();
				List<Node> nodes = new ArrayList<Node>();
				nodes.addAll(t.getNodes());
				Position p = null;
								
				for(Node n: nodes) {
					if (
							(((FeatureNode) n).hasMeepleSpot())&&
							(((FeatureNode) n).getType()!=FeatureType.FIELDS) &&
							(!gp.getGameController().getGameBoard().hasMeepleOnSubGraph((FeatureNode) n))
							){
						ArrayDeque<Node<FeatureType>> queue = new ArrayDeque<Node<FeatureType>>();
						queue.addAll(gp.getGameController().getGameBoard().getSubgraph((FeatureNode) n));
						
						ArrayList<Tile> tiles = new ArrayList<Tile>();
						for(Node<FeatureType> b: queue) {
							Tile z= new Tile(null);
							z= gp.getGameController().getGameBoard().getTileContainingNode((FeatureNode) b);
							if(! tiles.contains(z))
								tiles.add(z);
							
						}
						
						if(tiles.size() ==1)
							queue = new ArrayDeque<Node<FeatureType>>();
						
						
						
						
						if((gp.getGameController().getGameBoard().isCompleted(queue))) {
							p = t.getNodePosition((FeatureNode) n);
							
						}
					}
				}
				
								
				if(p!=null) gp.placeMeeple(p);
		
				
				
				
				//Falls kein Gebiet abgeschlossen wurde, tue dies:
				
				else {
					
					
					// Falls das Tile ein Kloster enthält und AI mehr als 4 Meeple hat, besetzt AI das Kloster
					if(meeples>4) {
						for(Node n: nodes) {
							if(((FeatureNode) n).getType() ==FeatureType.MONASTERY) {
								p = t.getNodePosition((FeatureNode) n);
								
							}
						}
					}
					
					
					if(p!=null) gp.placeMeeple(p);
				
				
					
					// Falls kein Kloster, dann setzt AI einen Meeple nach dem Zufallsprinzip(oder auch nicht). Einen meeple hält AI immer zurück, um fertige Gebiete einzunehmen
					else {
				
						if(meeples>1) {
							int rand=0;
							int max =pos.size()+2; 
							int min = 0; 
							int range = max - min; 
			  
			        
			       
							rand= (int)(Math.random() * range) + min;
			       
							if(rand>=pos.size())
							gp.nextRound();
							else
								
								
								//Felder besetzt AI nur, wenn er noch 6 oder 7 meeple hat
								
								if((meeples>5)||(((FeatureNode) nodes.get(rand)).getType()!=FeatureType.FIELDS))
									gp.placeMeeple(t.getNodePosition((FeatureNode) nodes.get(rand)));
								else gp.nextRound();
						}
						
						
						
						else
						gp.nextRound();		
					}
				}	
			
			}
				
	}

	

	
	
	//Klasse für die KI
	
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
		
	 }
	 
		
		
	
		
		
		//Klasse für die KI
		class Pos{
			int x;
			int y;
			int rot;
			
			
			Pos(int x1, int y1, int r){
				x=x1;
				y=y1;
				rot=r;
						
			}
				
		}

		
		
		
}
	 

