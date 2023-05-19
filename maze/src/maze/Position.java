package maze;


public class Position {
	private String symbol;
	private int row;
	private int col;
	private int maze;
	
	public Position(String paramSymbol, int paramRow, int paramCol, int paramMaze) {
		symbol = paramSymbol;
		row = paramRow;
		col = paramCol;
		maze = paramMaze;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String paramSymbol) {
		symbol = paramSymbol;
	}
	
	public int getRow() {
		return row;
	}
	
	public void setX(int paramRow) {
		row = paramRow;
	}
	
	public int getCol() {
		return col;
	}
	
	public void setCol(int paramCol) {
		col = paramCol;
	}
	
	public String toString() {
		return symbol + " " + row + " " + col + " " + maze;
	}
	
	public int getMaze() {
		return maze;
	}
}
