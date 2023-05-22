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
	
	public static boolean solutionExists = false;
	
	public static double runTime = 0;
		
	public static boolean Stack = false;
	public static boolean Queue = false;
	public static boolean Opt = false;
	public static boolean Time = false;
	public static boolean Incoordinate = false;
	public static boolean Outcoordinate = false;
	public static boolean Help = false;
	
	
	//returns the position of W
	public static Position findW(Position[][] arr) throws Exception {  
		Position w = null;
		int count = 0;
		for (int r = 0; r < arr.length; r++) {
			for (int c = 0; c < arr[0].length; c++) {
				if (arr[r][c].getSymbol().equals("W")) { 
					w = arr[r][c];
					count++;
				}
			}
		}
		if (count == 0) {
			throw new Exception("No W found");
		} else if (count > 1) {
			throw new Exception("No W found");
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
	//scans text file (can be either coordinate-based format or text-map format) and returns 2D array
	public static ArrayList<Position[][]> scanToMazes(String str) throws Exception, IllegalMapCharacterException, IncompleteMapException {
		if (Incoordinate) {
			return inputCoordinate(str);
		} 
		return inputTextMap(str);	
	}		
	//This method scans a coordinate-based text file and returns an ArrayList<Position>  
	public static ArrayList<Position[][]> inputCoordinate(String str) throws IllegalMapCharacterException, Exception {
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
					if (R >= rows || C >= cols) {
						throw new Exception("Coordinate doesn't fit inside of the maze");
					}
					temp[R][C] = new Position(coordinateInfo[0], R, C, mazeNum);
					if (!isLegal(temp[R][C])) {
		    			throw new IllegalMapCharacterException("TextMap has illegal character");
		    		}
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
	public static ArrayList<Position[][]> inputTextMap(String str) throws IllegalMapCharacterException, IncorrectMapFormatException, IncompleteMapException {
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
		    	if (data.length() + 1 <= cols) {
		    		throw new IncompleteMapException("Map doesn't have enough columns");
		    	}
		    	for (int c = 0; c < cols; c++) {
		    		Position p = new Position(data.substring(c, c+1), r, c, 0);
		    		arr[r][c] = p; 
		    		if (!isLegal(arr[r][c])) {
		    			throw new IllegalMapCharacterException("TextMap has illegal character");
		    		}
		    	}
		    	if (myReader.hasNextLine()) {
	  			r++; //increments the rows of s
		    	} else {
		    		if (r + 1 < rows) {
			    		throw new IncompleteMapException("Map doesn't have enough rows");
			    	}
		    	}
		    }
			
			ArrayList<Position[][]> mazes = new ArrayList<Position[][]>();  //arraylist of every maze in the text file
			
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
	
	//checks if the file format is the same as the input format
	//ex: file format = map, input format = coordinate --> file format != input format
	public static void isFileEqualToInputFormat(String str) throws Exception {
		try {
			File f = new File(str);
			Scanner myReader = new Scanner(f);
			String data = myReader.nextLine();
			data = myReader.nextLine();
			if ((data.substring(1, 2).equals(" ") && !Incoordinate) || (!data.substring(1, 2).equals(" ") && Incoordinate)) { // every text file in the coordinate format has lines with the format: (char (length 1) + " " + rows + " " + cols + " " + numMaze) 
				throw new Exception("File format is not the same as input format");
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
	//tests if position is valid for mainQ and ArrayList of Positions
	//tests if position object is an open path/open walkway/Diamond Wolverine buck, and can get to this position from the W
	public static boolean isSafeList(int row, int col, Position[][] arr, Queue<Position> mainQ, ArrayList<Position> visited) {
		if (visited.isEmpty()) {
			return (row >= 0 && row < arr.length && col >= 0 && col < arr[0].length && (arr[row][col].getSymbol().equals(".") || arr[row][col].getSymbol().equals("$") || arr[row][col].getSymbol().equals("|")) 
					&& !mainQ.contains(arr[row][col]));
		}
		return (row >= 0 && row < arr.length && col >= 0 && col < arr[0].length && (arr[row][col].getSymbol().equals(".") || arr[row][col].getSymbol().equals("$") || arr[row][col].getSymbol().equals("|")) 
				&& !mainQ.contains(arr[row][col]) && !visited.contains(arr[row][col]));
	}
	//returns true if an adjacent pair of elements are directly above/below/left/right
	public static boolean isNeighbor(Position a, Position b) {  
		if ( (a.getRow() - 1 == b.getRow() && a.getCol() == b.getCol())  ||  (a.getRow() + 1 == b.getRow() && a.getCol() == b.getCol())  ||  
				(a.getRow() == b.getRow() && a.getCol() + 1 == b.getCol())  ||  (a.getRow() == b.getRow() && a.getCol() - 1 == b.getCol()) ) {
			return true;
		}
		return false;
	}
	
	
	
	//prints input file, runs method that creates a path in the maze, finds runtime of stack-based approach (if Time switch is set to true), and prints output  
	public static void findPathS(String str) throws Exception, IllegalMapCharacterException, IncompleteMapException {
		ArrayList<Position[][]> mazes = scanToMazes(str); //arraylist of every maze in the text file
		int numMazes = getNumMazes(str);
		long startTime = System.nanoTime();
		for (int i = 0; i < numMazes; i++) {
			Position[][] temp = mazes.get(i);
			testContains$(temp);
		}
		long endTime = System.nanoTime() - startTime;
		runTime += endTime;
		if (!solutionExists) {
			System.out.println("The Wolverine Store is closed.");
		}
		for (int i = 0; i < numMazes; i++) {
			Position[][] temp = mazes.get(i);
			navigateWithStack(temp);
		}
		System.out.println();
		if (Time) {
			runTime /= 1000000000;
			System.out.println("Total Runtime: " + runTime + " seconds");
		}
	}
	//prints input file, runs method that creates a path in the maze, finds runtime of stack-based approach (if Time switch is set to true), and prints output
	public static void findPathQ(String str) throws IllegalMapCharacterException, Exception, IncompleteMapException {
		ArrayList<Position[][]> mazes = scanToMazes(str); //arraylist of every maze in the text file
		int numMazes = getNumMazes(str);
		long startTime = System.nanoTime();
		for (int i = 0; i < numMazes; i++) {
			Position[][] temp = mazes.get(i);
			testContains$(temp);
		}
		long endTime = System.nanoTime() - startTime;
		runTime += endTime;
		if (!solutionExists) {
			System.out.println("The Wolverine Store is closed.");
		}
		for (int i = 0; i < numMazes; i++) {
			Position[][] temp = mazes.get(i);
			navigateWithQueue(temp);
		}
		System.out.println();
		if (Time) {
			runTime /= 1000000000;
			System.out.println("Total Runtime: " + runTime + " seconds");
		}
	}
	
	public static void findPathOpt(String file) throws Exception, IllegalMapCharacterException, IncompleteMapException {
		ArrayList<Position[][]> mazes = scanToMazes(file);
		int numMazes = getNumMazes(file);
		long startTime = System.nanoTime();
		for (int i = 0; i < numMazes; i++) {
			Position[][] temp = mazes.get(i);
			testContains$(temp);
		}
		long endTime = System.nanoTime() - startTime;
		runTime += endTime;
		if (!solutionExists) {
			System.out.println("The Wolverine Store is closed.");
		}
		for (int i = 0; i < numMazes; i++) {
			Position[][] temp = mazes.get(i);
			navigateWithOpt(temp);
		}
		System.out.println();
		if (Time) {
			runTime /= 1000000000;
			System.out.println("Total Runtime: " + runTime + " seconds");
		}
	}
	
	
	
	//stack-based approach that we are finding runtime for
	public static void navigateWithStack(Position[][] arr) throws Exception { 
		long startTime = System.nanoTime();
		Stack<Position> mainS = new Stack<Position>();
		Stack<Position> visited = new Stack<Position>();
		Position w = findW(arr);  //finding position of W
		int currRow = w.getRow();
		int currCol = w.getCol();
		mainS.push(w);
		int[] r = {-1, 1, 0, 0}; //north south east west
		int[] c = {0, 0, 1, -1};
		while (!arr[currRow][currCol].getSymbol().equals("$") && !arr[currRow][currCol].getSymbol().equals("|") && !mainS.isEmpty()) {
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
		if (Outcoordinate && solutionExists) {
			for (int i = coordinates.size()-1; i >= 0; i--) { 
				System.out.println(coordinates.get(i)); //prints from size-1  ->  0
			}
		} else if (solutionExists){
			print2DArray(arr);
		}
	}
	
	//Queue-based approach that we are finding runtime for
	public static void navigateWithQueue(Position[][] arr) throws Exception {
		long startTime = System.nanoTime();
		Queue<Position> mainQ = new ArrayDeque<Position>();
		Queue<Position> visited = new ArrayDeque<Position>();
		Position w = findW(arr);  //finding position of W
		int currRow = w.getRow();
		int currCol = w.getCol();
		mainQ.add(w);  //adding w into mainQ
		
		int[] r = {-1, 1, 0, 0}; //north south east west
		int[] c = {0, 0, 1, -1};
		while (!arr[currRow][currCol].getSymbol().equals("$") && !arr[currRow][currCol].getSymbol().equals("|") && !mainQ.isEmpty()) {
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
		if (Outcoordinate && solutionExists) {
			for (int i = coordinates.size()-1; i >= 0; i--) { 
				System.out.println(coordinates.get(i)); //prints from size-1  ->  0
			}
		} else if (solutionExists){
			print2DArray(arr);
		}		
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
	
	//Optimal approach that we are finding runtime for
	public static void navigateWithOpt(Position[][] arr) throws Exception {
		long startTime = System.nanoTime();
		Queue<Position> mainQ = new ArrayDeque<Position>();
		ArrayList<Position> visited = new ArrayList<Position>();
		Position w = findW(arr);  //finding position of W
		int currRow = w.getRow();
		int currCol = w.getCol();
		mainQ.add(w);  //adding w into mainQ
		int[] r = {-1, 1, 0, 0}; //north south east west
		int[] c = {0, 0, 1, -1};
		while (!arr[currRow][currCol].getSymbol().equals("$") && !arr[currRow][currCol].getSymbol().equals("|") && !mainQ.isEmpty()) {
			currRow = mainQ.element().getRow();
			currCol = mainQ.element().getCol();
			for (int i = 0; i < 4; i++) {
				if (isSafeList(currRow+r[i], currCol+c[i], arr, mainQ, visited)) { //checking if surrounding elements are valid
					mainQ.add(arr[currRow+r[i]][currCol+c[i]]); //adding them to the mainQ
				}
			}
			visited.add(mainQ.remove()); //removing from the beginning of mainQ and adding to visited
		}
		visited.remove(0);
		ArrayList<Position> coordinates = new ArrayList<Position>();
		int len = visited.size(); //variable for visited.size() because visited.size() changes when elements are removed
		Position element = visited.remove(visited.size()-1); //first element in the queue visited
		Position nextElement = visited.get(visited.size()-1);  //2nd element
		for (int i = 0; i < len; i++) {
			if (isNeighbor(element, nextElement)) {
				arr[nextElement.getRow()][nextElement.getCol()].setSymbol("+");
				coordinates.add(arr[nextElement.getRow()][nextElement.getCol()]);
				element = nextElement;
			}
			if (i < len - 1) {  //this if statement is to avoid runtime errors
				nextElement = visited.remove(visited.size()-1);
			}
		}
		//calculating run time
		long endTime = System.nanoTime() - startTime;
		runTime += endTime;
		//output format
		if (Outcoordinate && solutionExists) {
			for (int i = coordinates.size()-1; i >= 0; i--) { 
				System.out.println(coordinates.get(i)); //prints from size-1  ->  0
			}
		} else if (solutionExists){
			print2DArray(arr);
		}	
	}
	
	
	//checks if visited arraylist has a $
	public static void arrContains$(ArrayList<Position> arr) {
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getSymbol().equals("$")) {
				solutionExists = true;
			}
		}
	}
	//run this method on every maze in ArrayList<Position> mazes to see if there is a solution
	public static void testContains$(Position[][] arr) throws Exception {
		long startTime = System.nanoTime();
		Queue<Position> mainQ = new ArrayDeque<Position>();
		ArrayList<Position> visited = new ArrayList<Position>();
		Position w = findW(arr);  //finding position of W
		int currRow = w.getRow();
		int currCol = w.getCol();
		mainQ.add(w);  //adding w into mainQ
		int[] r = {-1, 1, 0, 0}; //north south east west
		int[] c = {0, 0, 1, -1};
		while (!arr[currRow][currCol].getSymbol().equals("$") && !arr[currRow][currCol].getSymbol().equals("|") && !mainQ.isEmpty()) {
			currRow = mainQ.element().getRow();
			currCol = mainQ.element().getCol();
			for (int i = 0; i < 4; i++) {
				if (isSafeList(currRow+r[i], currCol+c[i], arr, mainQ, visited)) { //checking if surrounding elements are valid
					mainQ.add(arr[currRow+r[i]][currCol+c[i]]); //adding them to the mainQ
				}
			}
			visited.add(mainQ.remove()); //removing from the beginning of mainQ and adding to visited
		}
		arrContains$(visited);
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
	//next 2 methods are for Queues
	//popMethod (remove last) for Queue
	public static Position queuePop(Queue<Position> q) {
		Queue<Position> popQ = new ArrayDeque<Position>();
		int len = q.size();
		for (int i = 0; i < len - 1; i++) {
			popQ.add(q.remove());
		}
		Position p = q.remove();
		len = popQ.size();
		for (int i = 0; i < len; i++) {
			q.add(popQ.remove());
		}
		return p;
	}
	//peek (last element) for queues
	public static Position queuePeek(Queue<Position> q) {
		Queue<Position> popQ = new ArrayDeque<Position>();
		int len = q.size();
		for (int i = 0; i < len - 1; i++) {
			popQ.add(q.remove());
		}
		Position p = q.peek();
		popQ.add(q.remove());
		len = popQ.size();
		for (int i = 0; i < len; i++) {
			q.add(popQ.remove());
		}
		return p;
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
	
	public static void help() {
		System.out.println("This program is supposed to take in a text file with the first line: (# of rows)+' '+(# of columns)+' '+(# of mazes).");
		System.out.println("For the lines below the first line, the text file can either be in a text-map format or a coordinate format.");
		System.out.println("The text-map format is made up of characters placed in rows and columns.");
		System.out.println("An example of a text file in the text-map format will look something like this: ");
		System.out.println("5 4 1\r\n"
				+ "@@@@\r\n"
				+ "W.@$\r\n"
				+ "@...\r\n"
				+ "...@\r\n"
				+ "@..@");
		System.out.println("The coordinate format is made up of coordinates with the format, (char symbol)+' '+(int rowIndex)+' '+(int columnIndex)+' '+(mapNumber).");
		System.out.println("An example of a text file in the coordinate format will look something like this: ");
		System.out.println("5 4 1\r\n"
				+ "@ 0 0 0\r\n"
				+ "@ 0 1 0\r\n"
				+ "@ 0 2 0\r\n"
				+ "@ 0 3 0\r\n"
				+ "@ 3 3 0\r\n"
				+ "W 1 0 0\r\n"
				+ "@ 2 0 0\r\n"
				+ "@ 1 2 0\r\n"
				+ "$ 1 3 0\r\n"
				+ "@ 4 0 0\r\n"
				+ "@ 4 3 0");
		System.out.println("There can be multiple mazes within each text file and every maze has 1 'W' character and either 1 '|' or 1 '$' character.");
		System.out.println("The 'W' character is where you start in each maze, the '|' character is where you must go to get to the next maze, and the '$' \r\n" 
				+ "character is where the path ends.");
		System.out.println("The rest of the characters in each maze are either open paths or walls (open path = '.' and wall = '@').");
		System.out.println("There are methods in this program that will help find a path from the 'W' to the '$' even if it has to go through more than 1 maze.");
		System.out.println("To call these methods you will have to use the command line and call the commands '--Stack', '--Queue', and '--Opt'.");
		System.out.println("Additionally, all commands that are called in the command line must be separated by spaces and must not be surrounded by quotations, ' ', ( ), etc.");
		System.out.println("There are 3 algorithms to find a path from the 'W' to the '$' and only 1 approach can be called at a time.");
		System.out.println("When the '--Stack' command is called, the maze will be solved with the Stack-based approach.");
		System.out.println("When the '--Queue' command is called, the maze will be solved with the Queue-based approach.");
		System.out.println("When the '--Opt' command is called, the maze will be solved with the optimal approach (using ArrayLists).");
		System.out.println("When the '--Time' command is called, the runtime of either of these 3 path-finding methods (not including the time it takes to read the input or \r\n" 
				+ "write the output) will be returned to the console");
		System.out.println("In this program there are 2 methods to scan a text file and with the command, '--Incoordinate', the text file will be scanned \r\n"
				+ "in the coordinate format. Otherwise, the text file will be scanned in the text-map format (DO NOT CALL '--Incoordinate' WHEN DEALING WITH \r\n"
				+ "A TEXT FILE IN THE TEXT-MAP FORMAT!).");
		System.out.println("There are 2 output formats and with the command, 'Outcoordinate', the text file will be returned to the console in the coordinate format.");
		System.out.println("Otherwise, the text file will be returned to the console in the text-map format. Unlike the '--Incoordinate' command, this command \r\n" 
				+ "can be called at anytime and will not have any issues");
		System.out.println("The last command you can call is the command, '--Help', which calls a method and returns a message to the console");
		System.out.println("In order to read a file and find a path for it, you must type the name of the file at the end of the command line \r\n" 
				+ "(still separated from other commands with a space).");
		System.out.println();
	}
	
	public static void main(String[] args) throws Exception, IllegalMapCharacterException, IllegalCommandLineInputsException, IncompleteMapException {
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
				case "--Help":
					Help = true;
					break;
				default: 
					throw new IllegalCommandLineInputsException("illegal command line input");
			}
		}
		if (Help) {
			help();
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
			findPathOpt(args[args.length-1]);
		}
		
	}
}
