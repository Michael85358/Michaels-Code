package fop.model.gameplay;

import static fop.model.tile.FeatureType.CASTLE;
import static fop.model.tile.FeatureType.FIELDS;
import static fop.model.tile.FeatureType.MONASTERY;
import static fop.model.tile.FeatureType.ROAD;
import static fop.model.tile.Position.BOTTOM;
import static fop.model.tile.Position.BOTTOMLEFT;
import static fop.model.tile.Position.BOTTOMRIGHT;
import static fop.model.tile.Position.LEFT;
import static fop.model.tile.Position.RIGHT;
import static fop.model.tile.Position.TOP;
import static fop.model.tile.Position.TOPLEFT;
import static fop.model.tile.Position.TOPRIGHT;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import fop.base.Edge;
import fop.base.Node;
import fop.model.graph.FeatureGraph;
import fop.model.graph.FeatureNode;
import fop.model.player.Player;
import fop.model.tile.FeatureType;
import fop.model.tile.Position;
import fop.model.tile.Tile;
import fop.model.tile.TileType;

public class Gameboard extends Observable<Gameboard> {

	private Tile[][] board;
	private List<Tile> tiles;
	private FeatureGraph graph;
	private Tile newestTile;

	public Gameboard() {
		board = new Tile[144][144];
		tiles = new LinkedList<Tile>();
		graph = new FeatureGraph();
	}

	// kann nicht im konstrukor erfolgen, weil erst observer gesetzt werden muss
	public void initGameboard(Tile t) {
		newTile(t, 72, 72);
	}

	public void newTile(Tile t, int x, int y) {
		t.x = x;
		t.y = y;
		board[x][y] = newestTile = t;
		tiles.add(t);

		connectNodes(x, y);
		push(this); // pushes the new gameboard state to its observers (= GameBoardPanel)
	}

	/**
	 * Connects the nodes of all neighboring tiles facing the tile at given
	 * coordinates x, y. It is assumed that the tile is placed according to the
	 * rules.
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 */
	private void connectNodes(int x, int y) {
		graph.addAllNodes(board[x][y].getNodes());
		graph.addAllEdges(board[x][y].getEdges());

		Tile t = board[x][y];

		// Check top tile
		Tile Toptile = null;
		for ( int i = 0; i<tiles.size(); i++) {
			if ((tiles.get(i).x == x) && (tiles.get(i).y == y-1)) {
				Toptile = tiles.get(i);
		
			}
		}
			
		if ( Toptile != null ) {
			graph.addEdge(Toptile.getNode(BOTTOM), board[x][y].getNode(TOP));
			if(board[x][y].getNode(TOP).getValue().equals(ROAD)) {
				graph.addEdge(Toptile.getNode(BOTTOMLEFT), board[x][y].getNode(TOPLEFT));
				graph.addEdge(Toptile.getNode(BOTTOMRIGHT), board[x][y].getNode(TOPRIGHT));
			}
		}
			
				
		
			// This might be helpful:
			// As we already ensured that the tile on top exists and fits the tile at x, y,
			// we know that if the feature of its top is a ROAD, the feature at the bottom
			// of the tile on top is a ROAD aswell. As every ROAD has FIELD nodes as
			// neighbours on both sides, we can connect those nodes of the two tiles. The
			// same logic applies to the next three routines.


		// Check left tile
		Tile Lefttile = null;
		for ( int i = 0; i<tiles.size(); i++) {
			if ((tiles.get(i).x == x-1) && (tiles.get(i).y == y)) {
				Lefttile = tiles.get(i);
		
			}
		}
			
		if ( Lefttile != null ) {
			graph.addEdge(Lefttile.getNode(RIGHT), board[x][y].getNode(LEFT));
			if(t.getNode(LEFT).getValue().equals(ROAD)) {
				graph.addEdge(Lefttile.getNode(BOTTOMRIGHT), board[x][y].getNode(BOTTOMLEFT));
				graph.addEdge(Lefttile.getNode(TOPRIGHT), board[x][y].getNode(TOPLEFT));
			}
		}

		// Check right tile
		Tile Righttile = null;
		for ( int i = 0; i<tiles.size(); i++) {
			if ((tiles.get(i).x == x+1) && (tiles.get(i).y == y)) {
				Righttile = tiles.get(i);
		
			}
		}
			
		if ( Righttile != null ) {
			graph.addEdge(Righttile.getNode(LEFT), board[x][y].getNode(RIGHT));
			if(t.getNode(RIGHT).getValue().equals(ROAD)) {
				graph.addEdge(Righttile.getNode(BOTTOMLEFT), board[x][y].getNode(BOTTOMRIGHT));
				graph.addEdge(Righttile.getNode(TOPLEFT), board[x][y].getNode(TOPRIGHT));
			}
		}

		// Check bottom tile
		Tile Bottomtile = null;
		for ( int i = 0; i<tiles.size(); i++) {
			if ((tiles.get(i).x == x) && (tiles.get(i).y == y+1)) {
				Bottomtile = tiles.get(i);
			
			}
		}
				
		if ( Bottomtile != null ) {
			graph.addEdge(Bottomtile.getNode(TOP), board[x][y].getNode(BOTTOM));
			if(t.getNode(BOTTOM).getValue().equals(ROAD)) {
				graph.addEdge(Bottomtile.getNode(TOPRIGHT), board[x][y].getNode(BOTTOMRIGHT));
				graph.addEdge(Bottomtile.getNode(TOPLEFT), board[x][y].getNode(BOTTOMLEFT));
			}
		}
	}
	
	
	
	
	
	

	/**
	 * Checks if the given tile could be placed at position x, y on the board
	 * according to the rules.
	 * 
	 * @param t The tile
	 * @param x The x position on the board
	 * @param y The y position on the board
	 * @return True if it would be allowed, false if not.
	 */
	public boolean isTileAllowed(Tile t, int x, int y) {
		int top = 0, left = 0, right = 0, down = 0;
		// Check top tile
		Tile Toptile = null;
		for ( int i = 0; i<tiles.size(); i++) {
			if ((tiles.get(i).x == x) && (tiles.get(i).y == y-1)) {
				Toptile = tiles.get(i);
		
			}
		}
			
		if ( Toptile != null ) {
				
					if(Toptile.getNodeAtPosition(Position.BOTTOM).getValue()==( t.getNodeAtPosition(Position.TOP).getValue() )) {
						top =1;
					}
			
		}
		else top=1;
			
		
		
			

		// Check left tile
		Tile Lefttile = null;
		for ( int i = 0; i<tiles.size(); i++) {
			if ((tiles.get(i).x == x-1) && (tiles.get(i).y == y)) {
				Lefttile = tiles.get(i);
			}
		}
		if ( Lefttile != null ) {
			
					if(Lefttile.getNodeAtPosition(Position.RIGHT).getValue().equals( t.getNodeAtPosition(Position.LEFT).getValue() )) {
						left =1;
					}
				
		}
		else left=1;
		
		

		// Check right tile
		Tile Righttile = null;
		for ( int i = 0; i<tiles.size(); i++) {
			if ((tiles.get(i).x == x+1) && (tiles.get(i).y == y)) {
				Righttile = tiles.get(i);
			}
		}
		if ( Righttile != null ) {
			
				if(Righttile.getNodeAtPosition(Position.LEFT).getValue().equals( t.getNodeAtPosition(Position.RIGHT).getValue() ) ){
					right =1;
					
				}
	
		}	
		else right = 1;
			
			
		

		// Check bottom tile
		Tile Bottomtile = null;
		for ( int i = 0; i<tiles.size(); i++) {
			if ((tiles.get(i).x == x) && (tiles.get(i).y == y+1)) {
				Bottomtile = tiles.get(i);
			}
		}
		if ( Bottomtile != null ) {
		
				if(Bottomtile.getNodeAtPosition(Position.TOP).getValue().equals( t.getNodeAtPosition(Position.BOTTOM).getValue() ) ){
					down =1;
					
				}
	
		}		
		else down=1;
			

		
		
		if (down+right+left+top ==4) return true;
		
		
		else return false;
		
		
	
}

	
	
	/**
	 * Checks if the given tile would be allowed anywhere on the board adjacent to
	 * other tiles and according to the rules.
	 * 
	 * @param newTile The tile.
	 * @return True if it is allowed to place the tile somewhere on the board, false
	 *         if not.
	 */
	public boolean isTileAllowedAnywhere(Tile newTile) {
		
	boolean test = false;	
	int u =0;
	
	while((u<4) && (test==false)) { 
		newTile.rotateRight();
		for(int i=0; i<tiles.size(); i++) {
			if(this.isTileAllowed(newTile, tiles.get(i).x, tiles.get(i).y-1)) test = true;
			if(this.isTileAllowed(newTile, tiles.get(i).x, tiles.get(i).y+1)) test = true;
			if(this.isTileAllowed(newTile, tiles.get(i).x-1, tiles.get(i).y)) test = true;
			if(this.isTileAllowed(newTile, tiles.get(i).x+1, tiles.get(i).y)) test = true;
		}	
		u=u+1;
	}
			
	
		return test;
	}

	/**
	 * Calculates points for monasteries (one point for the monastery and one for
	 * each adjacent tile).
	 */
	public void calculateMonasteries(State state) {
		
		int score =0;
		int x,y;
		for ( int i = 0; i<tiles.size(); i++) {
			
			if( (tiles.get(i).getType()==TileType.A)|| (tiles.get(i).getType()==TileType.B)) {
					
					
				if(tiles.get(i).getNodeAtPosition(Position.CENTER).hasMeeple() == true) {
					
				
					score=1; x=tiles.get(i).x; y=tiles.get(i).y;
					
					if(x>0 && y>0) {
						if(board[x-1][y-1]!=null){
							score = score +1;
						}
					}
					
					if(y>0) {
						if(board[x][y-1]!=null){
							score = score +1;
						}
					}
					
					if(x<144 && y>0) {
						if(board[x+1][y-1]!=null){
							score = score +1;
						}
					}
					
					if(x<144) {
						if(board[x+1][y]!=null){
							score = score +1;
						}
					}
					
					if(x<144 && y<144) {
						if(board[x+1][y+1]!=null){
							score = score +1;
						}
					}
					
					if(y<144) {
						if(board[x][y+1]!=null){
							score = score +1;
						}
					}
					
					if(x>0 && y<144) {
						if(board[x-1][y+1]!=null){
							score = score +1;
						}
					}
					
					if(x>0) {
						if(board[x-1][y]!=null){
							score = score +1;
						}
					}
					
					if(score==9) {
						tiles.get(i).getMeeple().addScore(9);
						tiles.get(i).getMeeple().returnMeeple();
						tiles.get(i).getNode(Position.CENTER).removeMeeple();
						
					}
					else {
						if(state == State.GAME_OVER) {
							tiles.get(i).getMeeple().addScore(score);
							tiles.get(i).getNode(Position.CENTER).removeMeeple();
						}
					}
						
				}
			}
		}
				
		score =0;
							
			
		
		//the methods getNode() and getType of class Tile and FeatureNode might be helpful
		
		//Check all surrounding tiles and add the points
				
		//Points are given if the landscape is complete or the game is over
		//Meeples are just returned in case of state == State.GAME_OVER
				
		//After adding the points to the overall points of the player, set the score to 1 again
	}

	/**
	 * Calculates points and adds them to the players score, if a feature was
	 * completed. FIELDS are only calculated when the game is over.
	 * 
	 * @param state The current game state.
	 */
	public void calculatePoints(State state) {
		// Fields are only calculated on final scoring.
		//if (state == State.GAME_OVER)
			calculatePoints(FIELDS, state);

		calculatePoints(CASTLE, state);
		calculatePoints(ROAD, state);
		calculateMonasteries(state);
	}

	/**
	 * Calculates and adds points to the players that scored a feature. If the given
	 * state is GAME_OVER, points are added to the player with the most meeple on a
	 * subgraph, even if it is not completed.
	 * 
	 * @param type  The FeatureType that is supposed to be calculated.
	 * @param state The current game state.
	 */
	
	
	
	public void calculatePoints(FeatureType type, State state) {
		
		List<Node<FeatureType>> nodeList = new ArrayList<>(graph.getNodes(type));
		while (! nodeList.isEmpty()) {
			List<Node<FeatureType>> visitedNodes = new ArrayList<>();
			ArrayDeque<Node<FeatureType>> queue = new ArrayDeque<>();
			visitedNodes.add(nodeList.get(0));
			queue.push(nodeList.remove(0));
			
			while (!queue.isEmpty()) {
				FeatureNode node = (FeatureNode) queue.pop();
				List<Edge<FeatureType>> edges = graph.getEdges(node);
				
				for (Edge<FeatureType> edge : edges) {
					Node<FeatureType> nextNode = edge.getOtherNode(node);
					
					if (!visitedNodes.contains(nextNode)) {
						queue.push(nextNode);
						visitedNodes.add(nextNode);
					}
					nodeList.remove(nextNode);
				}
			}
						
			ArrayDeque<Node<FeatureType>> newque = new ArrayDeque<Node<FeatureType>>();
			newque.addAll(visitedNodes);
			visitedNodes=new ArrayList<>();
			teilgraphPunkte(newque, type, state);
			newque = new ArrayDeque<Node<FeatureType>>();;
		}
		
	}
		
			
			
	
	
	
	public boolean isCompleted(ArrayDeque<Node<FeatureType>> queue) {
		
		ArrayList<Node<FeatureType>> nodes = new ArrayList<Node<FeatureType>>();
		nodes.addAll(queue);
		List<TileType> types = new ArrayList<TileType>();
		boolean completed = true;
		
		while(!nodes.isEmpty()) {
			FeatureNode node = (FeatureNode) nodes.remove(0);
		
			Tile tile = getTileContainingNode(node);
			Position po = tile.getNodePosition(node);
	
		
		
			int x = tile.x;
			int y = tile.y;
		
		
		
			if(po == TOP) {
				if(y==0) completed= false;
				else {
					if(board[x][y-1]==null) {
						types.add(TileType.FLIPSIDE);
						}
					}
					
			}	
			
	
						
			
			
			if(po == BOTTOM) {
				if(y==144) completed= false;
				else {
					if(board[x][y+1]==null) {
						types.add(TileType.FLIPSIDE);
						}
					}
					
			}	
	
			
		
			if(po == LEFT) {
				if(x==0) completed= false;
				else {
					if(board[x-1][y]==null) {
						types.add(TileType.FLIPSIDE);
					}
				}
					
			}	
	
			
			
			if(po == RIGHT) {
				if(x==144) completed= false;
				else {
					if(board[x+1][y]==null) {
						types.add(TileType.FLIPSIDE);
						}
					}
					
			}	
			
		}
			
			
	

		if(types.contains(TileType.FLIPSIDE)) {completed = false;}
	
		return completed;
	
	}
	
	
	
	
			
	public void teilgraphPunkte(ArrayDeque<Node<FeatureType>> queue, FeatureType type, State state) {
			//Seperate methode Schreiben: teilgraphen auswerten.
		
		ArrayList<Tile> visited = new ArrayList<Tile>();
		int score = 0;
		ArrayDeque<Node<FeatureType>> newque = new ArrayDeque<Node<FeatureType>>();
		newque.addAll(queue);
		boolean completed = isCompleted(newque);
	
		while(queue.size()>0) {
			for(int t = 0; t<tiles.size(); t++) {
			
				if(
						(tiles.get(t).getNodes().contains(queue.getFirst()))&&
						(visited.contains(tiles.get(t)) ==false)
						) {
							visited.add(tiles.get(t));
							score = score +1;
									
				}
					
				
					
			}
			queue.removeFirst();
		}
					
		if(type==ROAD) {
				if(completed == true) {
						setPointsOfPlayerX(newque, score);
								
				}
							
				if((completed == false)&&(state == State.GAME_OVER)) {
						setPointsOfPlayerX(newque, score);
				}
								
		}
								
		if(type == CASTLE) {
				ArrayDeque<Node<FeatureType>> castle = new ArrayDeque<Node<FeatureType>>();
				castle.addAll(newque);
				ArrayList<Tile> tiles = new ArrayList<Tile>();
				
				while(!castle.isEmpty()) {
					Tile t = getTileContainingNode((FeatureNode) castle.pop());
					if(!tiles.contains(t)) tiles.add(t);
				}
				
				for(int i=0; i<tiles.size(); i++) {
					if(tiles.get(i).hasPennant()) score=score+1;
				}
			
			
			
				if(completed == true) {
					score=score*2;
					setPointsOfPlayerX(newque, score);
				}
						
				if((completed == false)&&(state == State.GAME_OVER)) {
					setPointsOfPlayerX(newque, score);
				}
						
		}
		if(type ==FeatureType.FIELDS) {
			score = fieldsScore(newque);setPointsOfPlayerX(newque, score);
			if(state == State.GAME_OVER) {
					setPointsOfPlayerX(newque, score);
			}
			score=0;
							
		}
					
		
								
	}
		
	
	
	
		 // Is the feature completed? Is set to false if a node is visited that does not
									// connect to any other tile

		
		// Iterate as long as the queue is not empty
		// Remember: queue defines a connected graph
		
	
		
		// Hint:
		// If there is one straight positioned node that does not connect to another
		// tile, the feature cannot be completed.


	
	public int fieldsScore(ArrayDeque<Node<FeatureType>> que){
		
		int score=0;
		System.out.println("anzahl:     " + que);
		ArrayList<Node<FeatureType>> fields = new ArrayList<Node<FeatureType>>();
		
		fields.addAll(que);
		
		ArrayList<Node<FeatureType>> castle1 = new ArrayList<Node<FeatureType>>();
		ArrayList<Node<FeatureType>> castle2 = new ArrayList<Node<FeatureType>>();
		
		for(int i=0; i<fields.size(); i++) {
			
			
				Tile t = getTileContainingNode((FeatureNode) fields.get(i));
				
				castle1.addAll(t.getNeighbours((FeatureNode) fields.get(i)));
		}
		
		while(!castle1.isEmpty()) {
			if(
					(!castle2.contains(castle1.get(0)))&&
					(((FeatureNode) castle1.get(0)).
							getType()==CASTLE)
					) {
				
			
					castle2.add(castle1.get(0));
			}
			castle1.remove(0);
			
		}
		
		List<Node<FeatureType>> nodeList = castle2;
		while (! nodeList.isEmpty()) {
			List<Node<FeatureType>> visitedNodes = new ArrayList<>();
			ArrayDeque<Node<FeatureType>> queue = new ArrayDeque<>();
			
			visitedNodes.add(nodeList.get(0));
			queue.push(nodeList.remove(0));
			
			while (!queue.isEmpty()) {
				FeatureNode node = (FeatureNode) queue.pop();
			
			

				List<Edge<FeatureType>> edges = graph.getEdges(node);
				for (Edge<FeatureType> edge : edges) {
					Node<FeatureType> nextNode = edge.getOtherNode(node);
					if (!visitedNodes.contains(nextNode)) {
						queue.push(nextNode);
						visitedNodes.add(nextNode);
						
					}
					nodeList.remove(nextNode);
				}
			}
			
			
			ArrayDeque<Node<FeatureType>> newque = new ArrayDeque<Node<FeatureType>>();
			
			newque.addAll(visitedNodes);
						
			visitedNodes=new ArrayList<>();
			
			
			
			if(isCompleted(newque)) score = score +3;
			newque = new ArrayDeque<Node<FeatureType>>();
		}
			System.out.println("f:    " + score);
		return score;	
		
	}
	
	
	
	
	
	public void setPointsOfPlayerX(ArrayDeque<Node<FeatureType>> queue, int score) {
		ArrayList<Player> Players = new ArrayList<Player>();
		ArrayList<Player> Winners = new ArrayList<Player>();
		while(queue.size()>0) {
			FeatureNode fnode = (FeatureNode) queue.getFirst();
			if(fnode.hasMeeple()) { 
				Players.add(fnode.getPlayer());
				fnode.getPlayer().returnMeeple();
				fnode.removeMeeple();
			}
			queue.removeFirst();
		}
		
		ArrayList<Player> AllPlayers = new ArrayList<Player>();
	
		
		int meepleMax=0;
		if(Players.size()>0) {
			for(int j=0; j<Players.size(); j++) {
				if(AllPlayers.contains(Players.get(j))==false) AllPlayers.add(Players.get(j));
			}
			
			for(int j=0; j<Players.size(); j++) {
				int anKing=anzahl(Players, Players.get(j));
				if (anKing>meepleMax) meepleMax=anKing;
			}
		}
		
		if(meepleMax>0) {
			for(int j=0; j<AllPlayers.size(); j++) {
				if((anzahl(Players, AllPlayers.get(j) )>=meepleMax)&&(!Winners.contains(AllPlayers.get(j)))) Winners.add(AllPlayers.get(j));
			
			}
		}
		
		
		for(int h=0; h<Winners.size(); h++)	{
			Winners.get(h).addScore(score); 
		}
	}
	
	
	public int anzahl(ArrayList<Player> lst, Player player) {
		int anPlayer=0;
		for(int i=0; i<lst.size(); i++) {
			if(player.equals(lst.get(i))) anPlayer= anPlayer+1;
		}
			
			return anPlayer;
	}
	
	
	

	/**
	 * Returns all Tiles on the Gameboard.
	 * 
	 * @return all Tiles on the Gameboard.
	 */
	public List<Tile> getTiles() {
		return tiles;
	}

	/**
	 * Returns the Tile containing the given FeatureNode.
	 * 
	 * @param node A FeatureNode.
	 * @return the Tile containing the given FeatureNode.
	 */
	private Tile getTileContainingNode(FeatureNode node) {
		for (Tile t : tiles) {
			if (t.containsNode(node))
				return t;
		}
		return null;
	}

	/**
	 * Returns the spots on the most recently placed tile on which it is allowed to
	 * place a meeple .
	 * 
	 * @return The spots on which it is allowed to place a meeple as a boolean array
	 *         representing the tile split in nine cells from top left, to right, to
	 *         bottom right. If there is no spot available at all, returns null.
	 */
	public boolean[] getMeepleSpots() {
		boolean[] positions = new boolean[9];
		boolean anySpot = false; // if there is not a single spot, this remains false

		for (Position p : Position.values()) {
			FeatureNode n = newestTile.getNodeAtPosition(p);
			if (n != null)
				if (n.hasMeepleSpot() && !hasMeepleOnSubGraph(n))
					positions[p.ordinal()] = anySpot = true;
		}

		if (anySpot)
			return positions;
		else
			return null;
	}

	/**
	 * Checks if there are any meeple on the subgraph that FeatureNode n is a part
	 * of.
	 * 
	 * @param n The FeatureNode to be checked.
	 * @return True if the given FeatureNode has any meeple on its subgraph, false
	 *         if not.
	 */
	private boolean hasMeepleOnSubGraph(FeatureNode n) {
		List<Node<FeatureType>> visitedNodes = new ArrayList<>();
		ArrayDeque<Node<FeatureType>> queue = new ArrayDeque<>();

		queue.push(n);
		while (!queue.isEmpty()) {
			FeatureNode node = (FeatureNode) queue.pop();
			if (node.hasMeeple())
				return true;

			List<Edge<FeatureType>> edges = graph.getEdges(node);
			for (Edge<FeatureType> edge : edges) {
				Node<FeatureType> nextNode = edge.getOtherNode(node);
				if (!visitedNodes.contains(nextNode)) {
					queue.push(nextNode);
					visitedNodes.add(nextNode);
				}
			}
		}
		return false;
	}

	/**
	 * Returns the newest tile.
	 * 
	 * @return the newest tile.
	 */
	public Tile getNewestTile() {
		return newestTile;
	}

	/**
	 * Places a meeple of given player at given position on the most recently placed
	 * tile (it is only allowed to place meeple on the most recent tile).
	 * 
	 * @param position The position the meeple is supposed to be placed on on the
	 *                 tile (separated in a 3x3 grid).
	 * @param player   The owner of the meeple.
	 */
	public void placeMeeple(Position position, Player player) {
		board[newestTile.x][newestTile.y].getNode(position).setPlayer(player);
		player.removeMeeple();
	}

	public Tile[][] getBoard() {
		return board;
	}
	
	public FeatureGraph getGraph() {
		return this.graph;
	}
	public void setFeatureGraph(FeatureGraph graph) {
		this.graph = graph; 
	}
}
