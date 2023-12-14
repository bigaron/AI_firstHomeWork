public class StudentPlayer extends Player{
    private int MAX_DEPTH = 2;
    private int enemyIndex;

    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    @Override
    public int step(Board board) {
        enemyIndex = board.getLastPlayerIndex();
        return maxScore(board, -Integer.MAX_VALUE, Integer.MAX_VALUE, 0)[0];
    }

    private int longestCon(Board board, int index){
        int usedTiles = 0;
        int longestCon = 0;
        int[][] state = board.getState();
        for(int row = 0; row < boardSize[0]; ++row){
            for(int col = 0; col < boardSize[1]; ++col){
                for(int con = 1; con < 5; con++){
                    if(isNDiagonally(row, col, index, con, state) || isNSkewDiagonally(row, col, index, con, state) 
                        || isNInACol(row, col, index, con, state) || isNInARow(row, col, index, con, state)) if(con > longestCon) longestCon = con;
                    if(state[row][col] != 0) usedTiles++;    
                }
            }
        }

        if(usedTiles == boardSize[0] * boardSize[1]) return -1;

        return longestCon;
    }

    private int utility(Board board){
        int value = 0;
        int longestPlayer = longestCon(board, playerIndex), longestEnemy = longestCon(board, enemyIndex);

        if(board.getLastPlayerIndex() == playerIndex){
            if(longestPlayer == 4 && longestEnemy < 3) value += 300;
        }else{
            if(longestEnemy == 4) value -= 1000;
        }


        return value;
    }

    private int[] minScore(Board board, int alfa, int beta, int depth){
        int val = board.getLastPlayerColumn();
        if(longestCon(board, playerIndex) == 4 || longestCon(board, enemyIndex) == 4 || depth == MAX_DEPTH) return new int[]{board.getLastPlayerColumn(), utility(board)};
        int[] v = new int[]{1 ,Integer.MAX_VALUE};
        for(int col: board.getValidSteps()){
            Board testBoard = new Board(board);
            testBoard.step(enemyIndex, col);
            int[] maxScr = maxScore(testBoard, alfa, beta, depth + 1);
            if(v[1] > maxScr[1]){
                v = maxScr;
            }
            if(v[1] <= alfa) return v;
            beta = Math.min(v[1], beta);
        }

        return v;
    }

    private int[] maxScore(Board board, int alfa, int beta, int depth){
        if (longestCon(board, playerIndex) == 4 || longestCon(board, enemyIndex) == 4 || depth == MAX_DEPTH) return new int[]{board.getLastPlayerColumn(), utility(board)};
        int[] v = new int[]{1, -Integer.MAX_VALUE};
        for(int col: board.getValidSteps()){
            Board testBoard = new Board(board);
            testBoard.step(playerIndex, col);
            int[] minScr = minScore(testBoard, alfa, beta, depth + 1);
            if(v[1] < minScr[1]){
                v = minScr;
            }
            if(v[1] >= beta) return v;
            alfa = Math.max(alfa, v[1]);
        }

        return v;
    }

    private boolean isNInARow(int row, int col, int playerIndex, int nToConnect, int[][] state) {
        int nInARow = 0;

        int startCol = Math.max(0, col - nToConnect + 1);
        int endCol = Math.min(boardSize[1], col + nToConnect);
        boolean badVal = false;
    
        if(startCol == 0 && endCol != boardSize[1]) badVal = state[row][endCol] != 0;
        if(endCol == boardSize[1] && startCol != 0) badVal = state[row][startCol - 1] != 0;
        if(startCol != 0 && endCol != boardSize[1]) badVal = state[row][startCol - 1] != 0 && state[row][endCol] != 0;

        if(badVal) return false;
        if(startCol != 0 && endCol != boardSize[1] && (state[row][startCol - 1] != 0 && state[row][endCol] != 0)) return false;
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
        
        boolean badVal = false;
    
        if(startRow == 0 && endRow != boardSize[0]) badVal = state[endRow][col] != 0;
        if(endRow == boardSize[0] && startRow != 0) badVal = state[startRow - 1][col] != 0;
        if(startRow != 0 && endRow != boardSize[0]) badVal = state[startRow - 1][col] != 0 && state[endRow][col] != 0;

        if(badVal) return false;

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
