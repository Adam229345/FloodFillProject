package com.example.groupproject;

import java.util.*;

public class RescueRobotFloodFill {

    static class Cell {
        int row;
        int col;

        Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public String toString() {
            return "(" + row + ", " + col + ")";
        }
    }

    static final int WALL = 1;
    static final int PATH = 0;
    static final int INF = 999999;

    // Movement directions: up, down, left, right
    static int[] dRow = {-1, 1, 0, 0};
    static int[] dCol = {0, 0, -1, 1};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println(" Rescue Robot Shortest Path System");
        System.out.println(" BFS-Based Flood Fill Algorithm");
        System.out.println("======================================");

        System.out.println("\nChoose Maze Mode:");
        System.out.println("1. Fixed Test Maze");
        System.out.println("2. Random Disaster Maze");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();

        int[][] maze;
        Cell start;
        Cell exit;

        if (choice == 1) {
            maze = getFixedMaze();

            start = new Cell(0, 0); // robot entrance at the edge
            exit = new Cell(3, 3);  // survivor safe room at the center

            System.out.println("\nMode Selected: Fixed Test Maze");
            System.out.println("Purpose: Controlled testing, debugging, and algorithm analysis.");

        } else {
            int rows = 9;
            int cols = 9;

            start = new Cell(0, 0);              // robot entrance at the edge
            exit = new Cell(rows / 2, cols / 2); // survivor safe room at center

            maze = generateRandomMaze(rows, cols, start, exit);

            System.out.println("\nMode Selected: Random Disaster Maze");
            System.out.println("Purpose: Simulate different disaster-damaged building layouts.");
        }

        runRescueRobotSystem(maze, start, exit);

        scanner.close();
    }

    // Main system flow
    public static void runRescueRobotSystem(int[][] maze, Cell start, Cell exit) {
        System.out.println("\nOriginal Disaster-Damaged Maze:");
        printMaze(maze, start, exit, new ArrayList<>());

        int[][] distance = floodFill(maze, exit);

        System.out.println("\nDistance Matrix After Flood Fill:");
        printDistanceMatrix(distance, maze);

        List<Cell> shortestPath = traceShortestPath(maze, distance, start, exit);

        if (shortestPath.isEmpty()) {
            System.out.println("\nNo valid path found. The survivor cannot be reached.");
        } else {
            System.out.println("\nShortest Path Coordinates:");
            printPath(shortestPath);

            System.out.println("\nMinimum Steps Required: " + (shortestPath.size() - 1));

            System.out.println("\nMaze With Shortest Path:");
            printMaze(maze, start, exit, shortestPath);

            System.out.println("\nRobot Movement Step by Step:");
            printRobotMovement(maze, start, exit, shortestPath);

            System.out.println("\nAlgorithm Result:");
            System.out.println("The rescue robot successfully reached the survivor using the shortest path.");
        }
    }

    // Fixed maze for controlled testing and portfolio screenshots
    public static int[][] getFixedMaze() {
        return new int[][]{
                {0, 1, 0, 0, 0, 0, 0},
                {0, 1, 0, 1, 1, 1, 0},
                {0, 0, 0, 1, 0, 0, 0},
                {1, 1, 0, 0, 0, 1, 0},
                {0, 0, 0, 1, 0, 1, 0},
                {0, 1, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0}
        };
    }

    // Random disaster maze generation
    // It keeps generating until at least one valid path exists from start to exit
    public static int[][] generateRandomMaze(int rows, int cols, Cell start, Cell exit) {
        Random random = new Random();

        int[][] maze;
        int[][] distance;

        double wallProbability = 0.30; // 30% chance of wall/rubble

        do {
            maze = new int[rows][cols];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (random.nextDouble() < wallProbability) {
                        maze[i][j] = WALL;
                    } else {
                        maze[i][j] = PATH;
                    }
                }
            }

            // Make sure start and exit are always open
            maze[start.row][start.col] = PATH;
            maze[exit.row][exit.col] = PATH;

            // Check if start can reach exit
            distance = floodFill(maze, exit);

        } while (distance[start.row][start.col] == INF);

        return maze;
    }

    // BFS-based Flood Fill starting from the survivor's location
    public static int[][] floodFill(int[][] maze, Cell exit) {
        int rows = maze.length;
        int cols = maze[0].length;

        int[][] distance = new int[rows][cols];

        // Initialize all cells as infinity
        for (int i = 0; i < rows; i++) {
            Arrays.fill(distance[i], INF);
        }

        Queue<Cell> queue = new LinkedList<>();

        distance[exit.row][exit.col] = 0;
        queue.add(exit);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            for (int i = 0; i < 4; i++) {
                int newRow = current.row + dRow[i];
                int newCol = current.col + dCol[i];

                if (isValidCell(maze, newRow, newCol)) {
                    if (distance[newRow][newCol] > distance[current.row][current.col] + 1) {
                        distance[newRow][newCol] = distance[current.row][current.col] + 1;
                        queue.add(new Cell(newRow, newCol));
                    }
                }
            }
        }

        return distance;
    }

    // Trace shortest path from start to exit by following smaller distance values
    public static List<Cell> traceShortestPath(int[][] maze, int[][] distance, Cell start, Cell exit) {
        List<Cell> path = new ArrayList<>();

        if (distance[start.row][start.col] == INF) {
            return path;
        }

        Cell current = start;
        path.add(current);

        while (!(current.row == exit.row && current.col == exit.col)) {
            Cell nextCell = null;
            int smallestDistance = distance[current.row][current.col];

            for (int i = 0; i < 4; i++) {
                int newRow = current.row + dRow[i];
                int newCol = current.col + dCol[i];

                if (isValidCell(maze, newRow, newCol)) {
                    if (distance[newRow][newCol] < smallestDistance) {
                        smallestDistance = distance[newRow][newCol];
                        nextCell = new Cell(newRow, newCol);
                    }
                }
            }

            if (nextCell == null) {
                path.clear();
                return path;
            }

            current = nextCell;
            path.add(current);
        }

        return path;
    }

    // Check whether a cell is inside the maze and not a wall
    public static boolean isValidCell(int[][] maze, int row, int col) {
        return row >= 0 &&
                row < maze.length &&
                col >= 0 &&
                col < maze[0].length &&
                maze[row][col] == PATH;
    }

    // Print maze using ASCII symbols
    public static void printMaze(int[][] maze, Cell start, Cell exit, List<Cell> path) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {

                if (i == start.row && j == start.col) {
                    System.out.print("S ");
                } else if (i == exit.row && j == exit.col) {
                    System.out.print("E ");
                } else if (isInPath(path, i, j)) {
                    System.out.print("* ");
                } else if (maze[i][j] == WALL) {
                    System.out.print("# ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    // Print distance matrix after Flood Fill
    public static void printDistanceMatrix(int[][] distance, int[][] maze) {
        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < distance[0].length; j++) {

                if (maze[i][j] == WALL) {
                    System.out.print(" # ");
                } else if (distance[i][j] == INF) {
                    System.out.print(" X ");
                } else {
                    System.out.printf("%2d ", distance[i][j]);
                }
            }
            System.out.println();
        }
    }

    // Print shortest path coordinates
    public static void printPath(List<Cell> path) {
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));

            if (i != path.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    // Check whether a cell is part of the shortest path
    public static boolean isInPath(List<Cell> path, int row, int col) {
        for (Cell cell : path) {
            if (cell.row == row && cell.col == col) {
                return true;
            }
        }
        return false;
    }

    // Print robot movement step by step
    public static void printRobotMovement(int[][] maze, Cell start, Cell exit, List<Cell> path) {
        for (int step = 0; step < path.size(); step++) {
            Cell robot = path.get(step);

            System.out.println("\nStep " + step + ": Robot is at " + robot);

            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[0].length; j++) {

                    if (i == robot.row && j == robot.col) {
                        System.out.print("R ");
                    } else if (i == start.row && j == start.col) {
                        System.out.print("S ");
                    } else if (i == exit.row && j == exit.col) {
                        System.out.print("E ");
                    } else if (maze[i][j] == WALL) {
                        System.out.print("# ");
                    } else {
                        System.out.print(". ");
                    }
                }
                System.out.println();
            }
        }
    }
}

