public class StudentPlayer extends Player{
    private int MAX_DEPTH = 3;
    private int enemyIndex;


    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    @Override
    public int step(Board board) {
        enemyIndex = board.getLastPlayerIndex();
        return maxScore(board, -Integer.MAX_VALUE, Integer.MAX_VALUE, 0)[0];
    }

    private int getRockCount(int[] slots, int indx){
        int cnt = 0;
        for(int i = 0; i < slots.length; ++i) if(slots[i] == indx) cnt++;
        return cnt;
    }

    private int score(int countP, int countE){
        int value = 0;
        if(countP == 4) value += 10000;
        if(countE == 4) value -= 10000;
        if(countP == 3 && countE == 0) value += 900;
        if(countP == 2 && countE == 0) value += 100;
        if(countE == 3 && countP == 0) value -= 1200;
        if(countE == 2 && countP == 0) value -= 100;


        return value;
    }

    private int utility(Board board){
        int value = 0;
        int[][] state = board.getState();

        for(int col = 0; col < boardSize[1]; ++col){
            for(int i = 0; i < boardSize[0] - 3; ++i) {
                int pCount = getRockCount(new int[]{state[i][col], state[i+1][col], state[i+2][col],state[i+3][col]}, playerIndex);
                int eCount = getRockCount(new int[]{state[i][col], state[i+1][col], state[i+2][col],state[i+3][col]}, enemyIndex);
                value += score(pCount, eCount);
            }
        }

        for(int row = 0; row < boardSize[0]; ++row){
            for(int i = 0; i < boardSize[1] - 3; ++i){
                int pCount = getRockCount(new int[]{state[row][i], state[row][i + 1], state[row][i + 2], state[row][i + 3]}, playerIndex);
                int eCount = getRockCount(new int[]{state[row][i], state[row][i + 1], state[row][i + 2], state[row][i + 3]}, enemyIndex);
                value += score(pCount, eCount);
            }
        }

        for(int row = 0; row < boardSize[0] - 3; ++row){
            for(int col = 0; col < boardSize[1] - 3; ++col){
                int pCount = getRockCount(new int[]{state[row][col], state[row+1][col+1],state[row+2][col+2],state[row+3][col+3]}, playerIndex);
                int eCount = getRockCount(new int[]{state[row][col], state[row+1][col+1],state[row+2][col+2],state[row+3][col+3]}, enemyIndex);
                value += score(pCount, eCount);
            }
        }

        for(int row = boardSize[0] - 1; row > 3; --row){
            for(int col = 0; col < boardSize[1] - 3; ++col){
                int pCount = getRockCount(new int[]{state[row][col], state[row - 1][col + 1], state[row - 2][col + 2], state[row - 3][col + 3]}, playerIndex);
                int eCount = getRockCount(new int[]{state[row][col], state[row - 1][col + 1], state[row - 2][col + 2], state[row - 3][col + 3]}, enemyIndex);
                value += score(pCount, eCount);
            }
        }
        return value;
    }

    private int[] minScore(Board board, int alfa, int beta, int depth){
        if(depth == MAX_DEPTH || board.gameEnded()) return new int[]{board.getLastPlayerColumn(), utility(board)};
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
        if(board.gameEnded() || depth == MAX_DEPTH) return new int[]{board.getLastPlayerColumn(), utility(board)};
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
}
