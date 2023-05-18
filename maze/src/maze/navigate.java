package maze;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Arrays;
import java.util.ArrayList;

public class navigate {
	
	public static boolean isSafe(int row, int col, Position[][] arr, Queue<Position> mainQ, Queue<Position> visited) {
		if (visited.isEmpty()) { //can't check if visited.contains(element) when there is nothing in visited until after the first iteration
			return (row >= 0 && row < arr.length && col >= 0 && col < arr[0].length && (arr[row][col].getSymbol().equals(".") || arr[row][col].getSymbol().equals("$") || arr[row][col].getSymbol().equals("|")) 
					&& !mainQ.contains(arr[row][col])); 
		}
		return (row >= 0 && row < arr.length && col >= 0 && col < arr[0].length && (arr[row][col].getSymbol().equals(".") || arr[row][col].getSymbol().equals("$") || arr[row][col].getSymbol().equals("|"))
				&& !mainQ.contains(arr[row][col]) && !visited.contains(arr[row][col]));
	}
	
	
	public static Position findW(Position[][] arr) {
		Position w = null;
		for (int r = 0; r < arr.length; r++) {
			for (int c = 0; c < arr[0].length; c++) {
				if (arr[r][c].getSymbol().equals("W")) { //returns the position of W to use in the bfs 
					w = arr[r][c];
				}
			}
		}
		return w;
	}
	
	public static boolean has$(Position[][] arr) {
		for (int r = 0; r < arr.length; r++) {
			for (int c = 0; c < arr[0].length; c++) {
				if (arr[r][c].getSymbol().equals("$")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void navigateMazesQ(Position[][] arr, int numMazes) {
		ArrayList<Position[][]> mazes = new ArrayList<Position[][]>();
		int startRow = 0;
		for (int i = 0; i < numMazes; i++) {   //adding each maze to "mazes"
			Position[][] temp = new Position[arr.length/numMazes][arr[0].length];
			for (int r = startRow; r < arr.length/numMazes + startRow; r++) {
				for (int c = 0; c < arr[0].length; c++) {
					temp[r-startRow][c] = new Position(arr[r][c].getSymbol(), r-startRow, c);
				}
			}
			mazes.add(temp);
			startRow += temp.length;
		}
//		for (int i = 0; i < numMazes; i++) {
//			Position[][] maze = mazes.get(i);
//			for (int j = 0; j < maze.length; j++) {
//				System.out.println(Arrays.toString(maze[j]));
//			}
//			System.out.println();
//		}
		System.out.println("Maze with path: ");
		for (int i = 0; i < numMazes; i++) {
			Position[][] maze = mazes.get(i);
			navigateWithQueue(maze);
		}
		
	}
	
	public static void navigateWithQueue(Position[][] arr) {
		Queue<Position> mainQ = new ArrayDeque<Position>();
		Queue<Position> visited = new ArrayDeque<Position>();
		Position w = findW(arr);  //finding position of W
		int currRow = w.getRow();
		int currCol = w.getCol();
		mainQ.add(w);  //adding w into mainQ
		
		int[] r = {-1, 1, 0, 0}; //north south east west
		int[] c = {0, 0, 1, -1};
		while (!arr[currRow][currCol].getSymbol().equals("$") && !arr[currRow][currCol].getSymbol().equals("|")) {
			currRow = mainQ.element().getRow();
			currCol = mainQ.element().getCol();
			for (int i = 0; i < 4; i++) {
				if (isSafe(currRow+r[i], currCol+c[i], arr, mainQ, visited)) { //checking if surrounding elements are valid
					mainQ.add(arr[currRow+r[i]][currCol+c[i]]); //adding them to the mainQ
				}
			}
			visited.add(mainQ.remove()); //removing from the beginning of mainQ and adding to visited
//			System.out.println3arr[currRow][currCol]);
//			System.out.println(visited);
		}
		
		visited.remove();  //removing the W because we aren't changing the symbol to a "+"
		
		visited = reverseQueue(visited); //reverse the Queue so you can backtrack from the $ to the W
		
		ArrayList<Position> coordinates = new ArrayList<Position>();
		
		int len = visited.size(); //variable for visited.size() because visited.size() changes when elements are removed
		Position element = visited.remove(); //first element in the queue visited
		Position nextElement = visited.element();  //2nd element
		for (int i = 0; i < len; i++) {
			if (isNeighbor(element, nextElement)) {
				arr[nextElement.getRow()][nextElement.getCol()].setSymbol("+");
				coordinates.add(arr[nextElement.getRow()][nextElement.getCol()]);
				element = nextElement;
			}
			if (i < len - 1) {  //this if statement is to avoid runtime errors
				nextElement = visited.remove();
			}
		}
		
		String[][] output = new String[arr.length][arr[0].length]; //2D output format
		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < output[0].length; j++) {
				output[i][j] = arr[i][j].getSymbol();
			}
			System.out.println(Arrays.toString(output[i]));
		}
		
//		System.out.println("Coordinates of path: "); //coordinates are added to ArrayList "coordinates" in reverse order
//		for (int i = coordinates.size()-1; i >= 0; i--) { 
//			System.out.println(coordinates.get(i)); //prints from size-1  ->  0
//		}
	}
	
	
	public static boolean isNeighbor(Position a, Position b) { //returns true if an adjacent pair of elements are directly above/below/left/right 
		if ( (a.getRow() - 1 == b.getRow() && a.getCol() == b.getCol())  ||  (a.getRow() + 1 == b.getRow() && a.getCol() == b.getCol())  ||  
				(a.getRow() == b.getRow() && a.getCol() + 1 == b.getCol())  ||  (a.getRow() == b.getRow() && a.getCol() - 1 == b.getCol()) ) {
			return true;
		}
		return false;
	}
	
	
	public static Queue<Position> reverseQueue(Queue<Position> q) {
		Queue<Position> remove = new ArrayDeque<Position>();
		Queue<Position> reverse = new ArrayDeque<Position>(); 
		int len = q.size() - 1;
		while (len >= 0) {
			for (int i = 0; i < len; i++) {
				remove.add(q.remove());
			}
			reverse.add(q.remove());
			int removeLength = remove.size();
			for (int i = 0; i < removeLength; i++) {
				q.add(remove.remove());
			}
			len--;
		}
		return reverse;
	}
	
	public void findPathStack(Position[][] arr) {
		
	}
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Scanner myReader = new Scanner(new BufferedReader(new FileReader("TestDoor")));     //test different mazes here
		String data = myReader.nextLine();
		String[] mazeInfo = data.split(" ");
		int numMazes = Integer.parseInt(mazeInfo[2]); //number of mazes
		int rows = Integer.parseInt(mazeInfo[0]) * numMazes; //number of rows
		int cols = Integer.parseInt(mazeInfo[1]); //number of columns
		Position[][] arr = new Position[rows][cols];
		int r = 0; //to set the rows for the for loop
		while (myReader.hasNextLine()) {
	    	  //reading the next line in the file
	    	  data = myReader.nextLine(); //sets to the first line of the actual maze
	    	  for (int c = 0; c < cols; c++) {
	    		  arr[r][c] = new Position(data.substring(c, c+1), r, c); 
	    	  }
	    	  if (myReader.hasNextLine()) {
  			  r++; //increments the rows of s
	    	  }
	    }
		
		
		System.out.println("Original Maze: ");
		String[][] original = new String[arr.length][arr[0].length]; //2D output format
		for (int i = 0; i < original.length; i++) {
			for (int j = 0; j < original[0].length; j++) {
				original[i][j] = arr[i][j].getSymbol();
			}
			System.out.println(Arrays.toString(original[i]));
		}
		System.out.println();
		navigateMazesQ(arr, numMazes);
	}
}
