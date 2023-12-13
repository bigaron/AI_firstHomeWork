public class StudentPlayer extends Player{
    private int MAX_DEPTH = 3;
    private int enemyIndex;

    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    @Override
    public int step(Board board) {
        enemyIndex = board.getLastPlayerIndex();
        return 0;
    }

    private boolean endState(Board board){
        int usedTiles = 0;
        int[][] state = board.getState();
        for(int row = 0; row < boardSize[0]; ++row){
            for(int col = 0; col < boardSize[1]; ++col){
                if(isNDiagonally(row, col, playerIndex, 4, state) || isNSkewDiagonally(row, col, playerIndex, 4, state) 
                    || isNInACol(row, col, playerIndex, 4, state) || isNInARow(row, col, playerIndex, 4, state)) return true;
                if(isNDiagonally(row, col, enemyIndex, 4, state) || isNSkewDiagonally(row, col, enemyIndex, 4, state) 
                    || isNInACol(row, col, enemyIndex, 4, state) || isNInARow(row, col, enemyIndex, 4, state)) return true;
                if(state[row][col] != 0) usedTiles++;    
            }   
        }

        if(usedTiles == boardSize[0] * boardSize[1]) return true;
        
        return false;
    }

    private int utility(Board board){
        return 0;
    }

    private int[][][] getStates(Board board){
        return new int[1][1][1];
    }

    private int minScore(Board board, int alfa, int beta, int depth){
        if(endState(board) || depth == MAX_DEPTH) return utility(board);
        int v = Integer.MAX_VALUE;
        for(int[][] nextState: getStates(board)){
            v = Math.min(v, maxScore(board, alfa, beta, depth + 1));
            if(v <= alfa) return v;
            beta = Math.min(v, beta);
        }

        return v;
    }

    private int maxScore(Board board, int alfa, int beta, int depth){
        if (endState(board) || depth == MAX_DEPTH) return utility(board);
        int v = -Integer.MAX_VALUE;
        for(int[][] nextState: getStates(board)){
            v = Math.max(v, minScore(board, alfa, beta, depth + 1));
            if(v >= beta) return v;
            alfa = Math.max(alfa, v);
        }

        return v;
    }

    private boolean isNInARow(int row, int col, int playerIndex, int nToConnect, int[][] state) {
        int nInARow = 0;

        int startCol = Math.max(0, col - nToConnect + 1);
        int endCol = Math.min(boardSize[1], col + nToConnect);

        for (int c = startCol; c < endCol; c++) {
            if (state[row][c] == playerIndex) {
                nInARow++;
                if (nInARow == nToConnect) {
                    return true;
                }
            } else
                nInARow = 0;
        }
        return false;
    }

    private boolean isNInACol(int row, int col, int playerIndex, int nToConnect, int[][] state) {
        int nInACol = 0;

        int startRow = Math.max(0, row - nToConnect + 1);
        int endRow = Math.min(boardSize[0], row + nToConnect);

        for (int r = startRow; r < endRow; r++) {
            if (state[r][col] == playerIndex) {
                nInACol++;
                if (nInACol == nToConnect) {
                    return true;
                }
            } else
                nInACol = 0;
        }
        return false;
    }

    private boolean isNDiagonally(int row, int col, int playerIndex, int nToConnect, int[][] state) {
        int nInADiagonal = 0;

        int stepLeftUp = Math.min(nToConnect - 1, Math.min(row, col));
        int stepRightDown = Math.min(nToConnect, Math.min(boardSize[0] - row, boardSize[1] - col));

        if ((stepLeftUp + stepRightDown) < nToConnect)
            return false;

        for (int diagonalStep = -stepLeftUp; diagonalStep < stepRightDown; diagonalStep++) {
            if (state[row + diagonalStep][col + diagonalStep] == playerIndex) {
                nInADiagonal++;
                if (nInADiagonal == nToConnect) {
                    return true;
                }
            } else {
                nInADiagonal = 0;
            }
        }
        return false;
    }

    private boolean isNSkewDiagonally(int row, int col, int playerIndex, int nToConnect, int[][] state) {
        int nInASkewDiagonal = 0;

        int stepLeftDown = Math.min(nToConnect - 1, Math.min(boardSize[0] - row - 1, col));
        int stepRightUp = Math.min(nToConnect, Math.min(row + 1, boardSize[1] - col));

        if ((stepRightUp + stepLeftDown) < nToConnect)
            return false;

        for (int skewDiagonalStep = -stepLeftDown; skewDiagonalStep < stepRightUp; skewDiagonalStep++) {
            if (state[row - skewDiagonalStep][col + skewDiagonalStep] == playerIndex) {
                nInASkewDiagonal++;
                if (nInASkewDiagonal == nToConnect) {
                    return true;
                }
            } else
                nInASkewDiagonal = 0;
        }
        return false;
    }
}
