package maze;

public class Position {
	private String symbol;
	private int row;
	private int col;
	
	
	public Position(String paramSymbol, int paramRow, int paramCol) {
		symbol = paramSymbol;
		row = paramRow;
		col = paramCol;
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
		return symbol + " " + row + " " + col;
	}
}
