package maze;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Arrays;
import java.util.ArrayList;

public class p1 {
	
	public static double runTime = 0;
		
	public static boolean Stack = false;
	public static boolean Queue = false;
	public static boolean Opt = false;
	public static boolean Time = false;
	public static boolean Incoordinate = false;
	public static boolean Outcoordinate = false;
	public static boolean Help = false;
	
	
	//returns the position of W
	public static Position findW(Position[][] arr) {  
		Position w = null;
		for (int r = 0; r < arr.length; r++) {
			for (int c = 0; c < arr[0].length; c++) {
				if (arr[r][c].getSymbol().equals("W")) { 
					w = arr[r][c];
				}
			}
		}
		return w;
	}	
	
	//scans file to see how many mazes there are (can run this on either input format because it will always have (rows, cols, numMazes) as the first line
	public static int getNumMazes(String file) {  
		try {
			File f = new File(file);
			Scanner myReader = new Scanner(f); 
			String data = myReader.nextLine();
			String[] mazeInfo = data.split(" ");
			return Integer.parseInt(mazeInfo[2]);
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
			System.exit(-1);
		}
		return (Integer) null;
	}	
	
	
	
	//3 METHODS FOR SCANNING A TEXT FILE
	//This method scans a coordinate-based text file and returns an ArrayList<Position>  
	public static ArrayList<Position[][]> inputCoordinate(String str) throws IllegalMapCharacterException, IncorrectMapFormatException {
		try {
			File f = new File(str);
			Scanner myReader = new Scanner(f);
			String data = myReader.nextLine();
			String[] mazeInfo = data.split(" ");
			int numMazes = Integer.parseInt(mazeInfo[2]); //number of mazes
			int rows = Integer.parseInt(mazeInfo[0]); //number of rows
			int cols = Integer.parseInt(mazeInfo[1]); //number of columns
			
			if (rows < 1 || cols < 1 || numMazes < 1) {
				throw new IncorrectMapFormatException("Map format is not correct");
			}
			
			ArrayList<Position[][]> mazes = new ArrayList<Position[][]>();  //arraylist of every maze in the text file
			
			int mazeNum = 0;
			for (int i = 0; i < numMazes; i++) {
				Position[][] temp = new Position[rows][cols];  //create one individual maze
				for (int checkRow = 0; checkRow < temp.length; checkRow++) {
					for (int checkCol = 0; checkCol < temp[0].length; checkCol++) {
						temp[checkRow][checkCol] = new Position(".", checkRow, checkCol, mazeNum); 
					}
				}
				while (myReader.hasNextLine() && mazeNum == i) {  //while there is a next line in the file and i is the same as the maze number
					data = myReader.nextLine(); //sets 
					String[] coordinateInfo = data.split(" ");
					mazeNum = Integer.parseInt(coordinateInfo[3]);
					int R = Integer.parseInt(coordinateInfo[1]); 
					int C = Integer.parseInt(coordinateInfo[2]);
					temp[R][C] = new Position(coordinateInfo[0], R, C, mazeNum);
					if (!isLegal(temp[R][C])) {
		    			throw new IllegalMapCharacterException("TextMap has illegal character");
		    		}
					//System.out.println(temp[R][C]);
					//System.out.println(maze);
					//System.out.println();
				}
				mazes.add(temp);
			}
			return mazes;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File Not Found");
			System.exit(-1);
		}    
		return null;
	}
	
	//This method scans a text-map-based text file and returns an ArrayList<Position>
	public static ArrayList<Position[][]> inputTextMap(String str) throws IllegalMapCharacterException, IncorrectMapFormatException {
		try {
			File f = new File(str);
			Scanner myReader = new Scanner(f);     //test different mazes here
			String data = myReader.nextLine();
			String[] mazeInfo = data.split(" ");
			int numMazes = Integer.parseInt(mazeInfo[2]); //number of mazes
			int rows = Integer.parseInt(mazeInfo[0]) * numMazes; //number of rows
			int cols = Integer.parseInt(mazeInfo[1]); //number of columns		
			
			if (rows < 1 || cols < 1 || numMazes < 1) {
				throw new IncorrectMapFormatException("Map format is not correct");
			}
			
			Position[][] arr = new Position[rows][cols]; //2D array of all positions in the text map
			
			int r = 0; //to set the rows for the for loop
			while (myReader.hasNextLine()) {
		    	//reading the next line in the file
		    	data = myReader.nextLine(); //sets to the first line of the actual maze
		    	for (int c = 0; c < cols; c++) {
		    		Position p = new Position(data.substring(c, c+1), r, c, 0);
		    		arr[r][c] = p; 
		    		if (!isLegal(arr[r][c])) {
		    			throw new IllegalMapCharacterException("TextMap has illegal character");
		    		}
		    	}
		    	if (myReader.hasNextLine()) {
	  			r++; //increments the rows of s
		    	}
		    }
			
			ArrayList<Position[][]> mazes = new ArrayList<Position[][]>();  //arraylist of every maze in the text file
			System.out.println();
			int mazeNum = 0;
			int startRow = 0;
			for (int i = 0; i < numMazes; i++) {   //adding each maze to "mazes"
				Position[][] temp = new Position[arr.length/numMazes][arr[0].length];
				for (int row = startRow; row < arr.length/numMazes + startRow; row++) {
					for (int col = 0; col < arr[0].length; col++) {
						temp[row-startRow][col] = new Position(arr[row][col].getSymbol(), row-startRow, col, mazeNum);
					}
				}
				mazeNum++;
				mazes.add(temp);
				startRow += temp.length;
			}
			return mazes;
		} catch(FileNotFoundException e) {
			System.out.println("File Not Found");
			System.exit(-1);
		}
		return null;
	}
	//scans text file (can be either coordinate-based format or text-map format) and returns 2D array
	public static ArrayList<Position[][]> scanToMazes(String str) throws Exception, IllegalMapCharacterException {
		if (Incoordinate) {
			return inputCoordinate(str);
		} 
		return inputTextMap(str);	
	}	
	
	//checks if the file format is the same as the input format
	//ex: file format = map, input format = coordinate --> file format != input format
	public static void isFileEqualToInputFormat(String str) throws IllegalCommandLineInputsException {
		try {
			File f = new File(str);
			Scanner myReader = new Scanner(f);
			String data = myReader.nextLine();
			data = myReader.nextLine();
			if ((data.substring(1, 2).equals(" ") && !Incoordinate) || (!data.substring(1, 2).equals(" ") && Incoordinate)) { // every text file in the coordinate format has lines with the format: (char (length 1) + " " + rows + " " + cols + " " + numMaze) 
				throw new IllegalCommandLineInputsException("file format is not the same as input format");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File not found");
		}
	}
		
	//checks for illegal characters
	public static boolean isLegal(Position p) {
		return p.getSymbol().equals("W") || p.getSymbol().equals(".") || p.getSymbol().equals("@") || p.getSymbol().equals("$") || p.getSymbol().equals("|");
	}
	//tests if position is valid for Queue
	//tests if position object is an open path/open walkway/Diamond Wolverine buck, and can get to this position from the W
	public static boolean isSafeQ(int row, int col, Position[][] arr, Queue<Position> mainQ, Queue<Position> visited) {  
		if (visited.isEmpty()) { //can't check if visited.contains(element) when there is nothing in visited until after the first iteration
			return (row >= 0 && row < arr.length && col >= 0 && col < arr[0].length && (arr[row][col].getSymbol().equals(".") || arr[row][col].getSymbol().equals("$") || arr[row][col].getSymbol().equals("|")) 
					&& !mainQ.contains(arr[row][col])); 
		}
		return (row >= 0 && row < arr.length && col >= 0 && col < arr[0].length && (arr[row][col].getSymbol().equals(".") || arr[row][col].getSymbol().equals("$") || arr[row][col].getSymbol().equals("|"))
				&& !mainQ.contains(arr[row][col]) && !visited.contains(arr[row][col]));
	}
	//tests if position is valid for Stack
	//tests if position object is an open path/open walkway/Diamond Wolverine buck, and can get to this position from the W
	public static boolean isSafeS(int row, int col, Position[][] arr, Stack<Position> mainS, Stack<Position> visited) {  
		if (visited.isEmpty()) { //can't check if visited.contains(element) when there is nothing in visited until after the first iteration
			return (row >= 0 && row < arr.length && col >= 0 && col < arr[0].length && (arr[row][col].getSymbol().equals(".") || arr[row][col].getSymbol().equals("$") || arr[row][col].getSymbol().equals("|")) 
					&& !mainS.contains(arr[row][col])); 
		}
		return (row >= 0 && row < arr.length && col >= 0 && col < arr[0].length && (arr[row][col].getSymbol().equals(".") || arr[row][col].getSymbol().equals("$") || arr[row][col].getSymbol().equals("|"))
				&& !mainS.contains(arr[row][col]) && !visited.contains(arr[row][col]));
	}
	
	
	public static boolean isNeighbor(Position a, Position b) { //returns true if an adjacent pair of elements are directly above/below/left/right 
		if ( (a.getRow() - 1 == b.getRow() && a.getCol() == b.getCol())  ||  (a.getRow() + 1 == b.getRow() && a.getCol() == b.getCol())  ||  
				(a.getRow() == b.getRow() && a.getCol() + 1 == b.getCol())  ||  (a.getRow() == b.getRow() && a.getCol() - 1 == b.getCol()) ) {
			return true;
		}
		return false;
	}
	
	
	//reverses Queue so you can "backtrack" from the beginning of the queue (starting at the W or |) to the end not including the W)
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
	
	
	//next 2 methods are for Stacks
	//peek (first element) method for stack
	public static Position peekFirstStack(Stack<Position> s) {
		Stack<Position> popStack = new Stack<Position>();
		int len = s.size();
		for (int i = 0; i < len - 1; i++) {
			popStack.push(s.pop());
		}
		Position pop = s.peek();
		len = popStack.size();
		for (int i = 0; i < len; i++) {
			s.push(popStack.pop());
		}
		return pop;
	}
	//remove (first element) method for stack
	public static Position removeFromStack(Stack<Position> s) {
		Stack<Position> popStack = new Stack<Position>();
		int len = s.size();
		for (int i = 0; i < len - 1; i++) {
			popStack.push(s.pop());
		}
		Position pop = s.pop();
		len = popStack.size();
		for (int i = 0; i < len; i++) {
			s.push(popStack.pop());
		}
		return pop;
	}
	
	
	
	//prints input file, runs method that creates a path in the maze, finds runtime of stack-based approach (if Time switch is set to true), and prints output  
	public static void findPathS(String str) throws IllegalMapCharacterException, Exception {
		ArrayList<Position[][]> mazes = scanToMazes(str); //arraylist of every maze in the text file
		int numMazes = getNumMazes(str);
		for (int i = 0; i < numMazes; i++) {
			Position[][] temp = mazes.get(i);
			navigateWithStack(temp);
		}
		System.out.println();
		if (Time) {
			runTime /= 1000000000;
			System.out.println(runTime);
		}
	}
	//prints input file, runs method that creates a path in the maze, finds runtime of stack-based approach (if Time switch is set to true), and prints output
	public static void findPathQ(String str) throws IllegalMapCharacterException, Exception {
		ArrayList<Position[][]> mazes = scanToMazes(str); //arraylist of every maze in the text file
		int numMazes = getNumMazes(str);
		for (int i = 0; i < numMazes; i++) {
			Position[][] temp = mazes.get(i);
			navigateWithQueue(temp);
		}
		System.out.println();
		if (Time) {
			runTime /= 1000000000;
			System.out.println(runTime);
		}
	}
	
	
	
	//stack-based approach that we are finding runtime for
	public static void navigateWithStack(Position[][] arr) { 
		long startTime = System.nanoTime();
		Stack<Position> mainS = new Stack<Position>();
		Stack<Position> visited = new Stack<Position>();
		Position w = findW(arr);  //finding position of W
		int currRow = w.getRow();
		int currCol = w.getCol();
		mainS.push(w);
		int[] r = {-1, 1, 0, 0}; //north south east west
		int[] c = {0, 0, 1, -1};
		while (!arr[currRow][currCol].getSymbol().equals("$") && !arr[currRow][currCol].getSymbol().equals("|")) {
			currRow = peekFirstStack(mainS).getRow();
			currCol = peekFirstStack(mainS).getCol();
			for (int i = 0; i < 4; i++) {
				if (isSafeS(currRow+r[i], currCol+c[i], arr, mainS, visited)) { //checking if surrounding elements are valid
					mainS.push(arr[currRow+r[i]][currCol+c[i]]); //adding them to the mainS
				}
			}
			visited.add(removeFromStack(mainS)); //removing from the beginning of mainQ and adding to visited
		}
		removeFromStack(visited);  //removing the W because we aren't changing the symbol to a "+"
		
		ArrayList<Position> coordinates = new ArrayList<Position>();
		
		int len = visited.size(); //variable for visited.size() because visited.size() changes when elements are removed
		Position element = visited.pop(); //first element in the queue visited
		Position nextElement = visited.peek();  //2nd element
		for (int i = 0; i < len; i++) {
			if (isNeighbor(element, nextElement)) {
				arr[nextElement.getRow()][nextElement.getCol()].setSymbol("+");
				coordinates.add(arr[nextElement.getRow()][nextElement.getCol()]);
				element = nextElement;
			}
			if (i < len - 1) {  //this if statement is to avoid runtime errors
				nextElement = visited.pop();
			}
		}
		
		//calculating run time
		long endTime = System.nanoTime() - startTime;
		runTime += endTime;
		
		//output format
		if (Outcoordinate) {
			for (int i = coordinates.size()-1; i >= 0; i--) { 
				System.out.println(coordinates.get(i)); //prints from size-1  ->  0
			}
		} else {
			print2DArray(arr);
		}
	}
	
	//Queue-based approach that we are finding runtime for
	public static void navigateWithQueue(Position[][] arr) {
		long startTime = System.nanoTime();
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
				if (isSafeQ(currRow+r[i], currCol+c[i], arr, mainQ, visited)) { //checking if surrounding elements are valid
					mainQ.add(arr[currRow+r[i]][currCol+c[i]]); //adding them to the mainQ
				}
			}
			visited.add(mainQ.remove()); //removing from the beginning of mainQ and adding to visited
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
		
		//calculating run time of current run of the method and adding it to total runtime
		long endTime = System.nanoTime() - startTime;
		runTime += endTime;		
		
		//output format
		if (Outcoordinate) {
			//coordinates are added to ArrayList "coordinates" in reverse order
			for (int i = coordinates.size()-1; i >= 0; i--) { 
				System.out.println(coordinates.get(i)); //prints from size-1  ->  0
			}
		} else {
			//2D Map output format
			print2DArray(arr);
		}
	}
	
	
	public static void runCommands(String file) throws Exception, IllegalMapCharacterException {
		int count = 0; 
		if (Stack) {
			count++;
		}
		if (Queue) {
			count++;
		}
		if (Opt) {
			count++;
		}
		if (count > 1) { //testing to see if none or more than one option is specified
			System.out.println("Can't test more than one routing approach");
			System.exit(-1);
		} else if (count == 0) {
			System.out.println("No routing approach was selected");
			System.exit(-1);
		}
		if (Stack) {
			findPathS(file);
		} else if (Queue) {
			findPathQ(file);
		} else if (Opt) {
			//findPathOpt(file);
		}
	}
	
	
	//used for testing purposes
	public static void print2DArray(Position[][] p) {
		for (int r = 0; r < p.length; r++) {
			String output = "";
			for (int c = 0; c < p[0].length; c++) {
				output += p[r][c].getSymbol();
			}
			System.out.println(output);
		}
	}
	
	public static void navigateWithOpt(String file) {
		
	}
	
	public static void findPathOpt(String file) {
		
	}
	
	public static void main(String[] args) throws Exception, IllegalMapCharacterException, IllegalCommandLineInputsException {
		// TODO Auto-generated method stub
		for (int i = 0; i < args.length-1; i++) {
			switch (args[i]) {
				case "--Stack": 
					Stack = true;
					break;
				case "--Queue":
					Queue = true;
					break;
				case "--Opt":
					Opt = true;
					break;
				case "--Incoordinate": 
					Incoordinate = true;
					break;
				case "--Outcoordinate":
					Outcoordinate = true;
					break;
				case "--Time": 
					Time = true;
					break; 
				default: 
					throw new IllegalCommandLineInputsException("illegal command line input");
			}
		}
		isFileEqualToInputFormat(args[args.length-1]);
		int count = 0; 
		if (Stack) {
			count++;
		}
		if (Queue) {
			count++;
		}
		if (Opt) {
			count++;
		}
		
		if (count > 1) { //testing to see if none or more than one option is specified
			throw new IllegalCommandLineInputsException("Can't test more than one routing approach");
		} else if (count == 0) {
			throw new IllegalCommandLineInputsException("No routing approach was selected");
		}
		
		if (Stack) {
			findPathS(args[args.length-1]);
		} else if (Queue) {
			findPathQ(args[args.length-1]);
		} else if (Opt) {
			//findPathOpt(args.length-1);
		}
		
	}
}
