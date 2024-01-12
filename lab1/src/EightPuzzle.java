import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EightPuzzle {
    private static final int[][] GOAL_STATE = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    private static final List<Integer> moves = new ArrayList<>();

    public static void main(String[] args) {
        // run 100x with Hamming
        for (int i = 0; i < 100; i++) {
            int[][] initialBoard = GenerateStartState();

            while (!isSolvable(initialBoard)) {
                initialBoard = GenerateStartState();
            }

            //System.out.println("Original Array: " + Arrays.deepToString(GOAL_STATE));
            //System.out.println("Shuffled Array: " + Arrays.deepToString(initialBoard));


            HeuristicType heuristicTypeToUse = HeuristicType.HAMMING;
            solvePuzzle(initialBoard, heuristicTypeToUse);
        }
        List<Integer> totalMovesHamming = new ArrayList<>(moves);
        moves.clear();

        double meanHamming = Helper.calculateMean(totalMovesHamming);
        double standardDeviationHamming = Helper.calculateStandardDeviation(totalMovesHamming);

        // run 100x with Manhattan
        for (int i = 0; i < 100; i++) {
            int[][] initialBoard = GenerateStartState();

            while (!isSolvable(initialBoard)) {
                initialBoard = GenerateStartState();
            }

            HeuristicType heuristicTypeToUse = HeuristicType.MANHATTAN;
            solvePuzzle(initialBoard, heuristicTypeToUse);
        }

        List<Integer> totalMovesManhattan = new ArrayList<>(moves);

        double meanManhattan = Helper.calculateMean(totalMovesManhattan);
        double standardDeviationManhattan = Helper.calculateStandardDeviation(totalMovesManhattan);

        String output = """
                Hamming:
                    moves: %s
                    mean: %.2f
                    standard deviation: %.2f
                                
                Manhattan:
                    moves: %s
                    mean: %.2f
                    standard deviation: %.2f
                """.formatted(totalMovesHamming.size(), meanHamming, standardDeviationHamming, totalMovesManhattan.size(), meanManhattan, standardDeviationManhattan);

        System.out.println(output);
    }

    private static int[][] GenerateStartState() {
        List<Integer> flattenedList = Arrays.stream(GOAL_STATE).flatMapToInt(Arrays::stream).boxed().collect(Collectors.toList());

        // Shuffle the flattened list
        Collections.shuffle(flattenedList);

        // Reconstruct the 2D array
        return IntStream.range(0, GOAL_STATE.length).mapToObj(i -> IntStream.range(0, GOAL_STATE[i].length).map(j -> flattenedList.get(i * GOAL_STATE[i].length + j)).toArray()).toArray(int[][]::new);
    }

    private static void solvePuzzle(int[][] initialBoard, HeuristicType heuristicTypeToUse) {
        PriorityQueue<PuzzleNode> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();

        PuzzleNode initialNode = new PuzzleNode(initialBoard, 0, heuristicTypeToUse);
        openSet.add(initialNode);

        while (!openSet.isEmpty()) {
            PuzzleNode currentNode = openSet.poll();
            closedSet.add(Arrays.deepToString(currentNode.board));

            if (Arrays.deepEquals(currentNode.board, GOAL_STATE)) {
                printSolution(currentNode);
                return;
            }

            generateNeighbors(currentNode, heuristicTypeToUse).forEach(neighbor -> {
                if (!closedSet.contains(Arrays.deepToString(neighbor.board))) {
                    openSet.add(neighbor);
                }
            });
        }

        System.out.println("No solution found.");
    }

    private static List<PuzzleNode> generateNeighbors(PuzzleNode node, HeuristicType heuristicTypeToUse) {
        List<PuzzleNode> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Possible moves: up, down, left, right

        for (int[] dir : directions) {
            int emptyRow = -1, emptyCol = -1;

            outerLoop:
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (node.board[i][j] == 0) {
                        emptyRow = i;
                        emptyCol = j;
                        break outerLoop;
                    }
                }
            }

            int newRow = emptyRow + dir[0];
            int newCol = emptyCol + dir[1];

            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                int[][] newBoard = new int[3][3];
                for (int i = 0; i < 3; i++) {
                    System.arraycopy(node.board[i], 0, newBoard[i], 0, 3);
                }

                newBoard[emptyRow][emptyCol] = newBoard[newRow][newCol];
                newBoard[newRow][newCol] = 0;

                neighbors.add(new PuzzleNode(newBoard, node.moves + 1, heuristicTypeToUse));
            }
        }

        return neighbors;
    }

    private static void printSolution(PuzzleNode node) {
        System.out.println("Solution found in " + node.moves + " moves:");
        moves.add(node.moves);
        printBoard(node.board);
    }

    private static void printBoard(int[][] board) {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static boolean isSolvable(int[][] puzzle) {
        int[] flattenedArray = Arrays.stream(puzzle).flatMapToInt(Arrays::stream).toArray();
        //System.out.println("Array: " + Arrays.toString(flattenedArray));
        int inversionCount = 0;
        int size = flattenedArray.length;

        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (flattenedArray[i] != 0 && flattenedArray[j] != 0 && flattenedArray[i] > flattenedArray[j]) {
                    inversionCount++;
                }
            }
        }

        return inversionCount % 2 == 0;
    }
}

