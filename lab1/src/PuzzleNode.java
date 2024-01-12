class PuzzleNode implements Comparable<PuzzleNode> {
    int[][] board;
    int moves;
    int priority;

    public PuzzleNode(int[][] board, int moves, HeuristicType heuristicType) {
        this.board = board;
        this.moves = moves;
        this.priority = calculatePriority(heuristicType);
    }

    private int calculatePriority(HeuristicType heuristicType) {
        switch (heuristicType) {
            case MANHATTAN -> {
                return moves + getManhattanDistance();
            }
            case HAMMING -> {
                return moves + getHammingDistance();
            }
            case null, default -> {
                return 0;
            }
        }
    }

    private int getHammingDistance() {
        int hammingDistance = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = board[i][j];
                if (value != 0 && value != i * 3 + j + 1) {
                    hammingDistance++;
                }
            }
        }
        return hammingDistance;
    }

    private int getManhattanDistance() {
        int manhattanDistance = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = board[i][j];
                if (value != 0) {
                    int targetRow = (value - 1) / 3;
                    int targetCol = (value - 1) % 3;
                    manhattanDistance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
        }
        return manhattanDistance;
    }

    @Override
    public int compareTo(PuzzleNode other) {
        return Integer.compare(this.priority, other.priority);
    }
}