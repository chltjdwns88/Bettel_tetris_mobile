package ssu.btetris.model;

import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Vector;

import ssu.btetris.singleuser.MainActivity;

public class JTetris implements Serializable {
    public enum TetrisState { // need to be defined as inner class within Tetris, otherwise requires a separate Java file.
        Running(0), NewBlock(1), Finished(2);
        private final int value;
        private TetrisState(int value) { this.value = value; }
        public int value() { return value; }
    }
    public boolean any_block_crash = false;
    public int change_rotate = 0;
    public static int iScreenDw;		// large enough to cover the largest block
    public static int nBlockTypes;		// number of block types (typically 7)
    public static int nBlockDegrees;	// number of block degrees (typically 4)
    public static Matrix[][] setOfBlockObjects;	// Matrix object arrays of all blocks
    private static Matrix[][] createSetOfBlocks(int[][][][] setOfArrays) throws Exception {
        int ntypes = setOfArrays.length;
        int ndegrees = setOfArrays[0].length;
        Matrix[][] setOfObjects = new Matrix[nBlockTypes][nBlockDegrees];
        for (int t = 0; t < ntypes; t++)
            for (int d = 0; d < ndegrees; d++)
                setOfObjects[t][d] = new Matrix(setOfArrays[t][d]);
        return setOfObjects;
    }
    private static int max(int a, int b) { return (a > b ? a : b); }
    private static int findLargestBlockSize(int[][][][] setOfArrays) {
        int size, max_size = 0;
        for (int t = 0; t < nBlockTypes; t++) {
            for (int d = 0; d < nBlockDegrees; d++) {
                size = setOfArrays[t][d].length;
                max_size = max(max_size, size);
            }
        }
        //System.out.println("max_size = "+max_size);
        return max_size;
    }
    public static void init(int[][][][] setOfBlockArrays) throws Exception { // initialize static variables
        nBlockTypes = setOfBlockArrays.length;
        nBlockDegrees = setOfBlockArrays[0].length;
        setOfBlockObjects = createSetOfBlocks(setOfBlockArrays);
        iScreenDw = findLargestBlockSize(setOfBlockArrays);
    }
    private int iScreenDy;	// height of the background screen (excluding walls)
    private int iScreenDx;  // width of the background screen (excluding walls)
    private TetrisState state;		// game state
    private int top;		// y of the top left corner of the current block
    private int left;		// x of the top left corner of the current block
    private Matrix iScreen;	// input screen (as background)
    private Matrix oScreen;	// output screen
	public Matrix get_oScreen() {
		return oScreen;
	}
    private Matrix currBlk;	// current block
    private int idxBlockType;	// index for the current block type
    private int idxBlockDegree; // index for the current block degree
    private int[][] createArrayScreen(int dy, int dx, int dw) {
        int y, x;
        int[][] array = new int[dy + dw][dx + 2*dw];
        for (y = 0; y < array.length; y++)
            for (x = 0; x < dw; x++)
                array[y][x] = 1;
        for (y = 0; y < array.length; y++)
            for (x = dw + dx; x < array[0].length; x++)
                array[y][x] = 1;
        for (y = dy; y < array.length; y++)
            for (x = 0; x < array[0].length; x++)
                array[y][x] = 1;
        return array;
    }
    private void printMatrix(Matrix blk) { // for debugging purposes
        int dy = blk.get_dy();
        int dx = blk.get_dx();
        int array[][] = blk.get_array();
        for (int y=0; y < dy; y++) {
            for (int x=0; x < dx; x++) {
                if (array[y][x] == 0) System.out.print("□ ");
                else if (array[y][x] == 1) System.out.print("■ ");
                else System.out.print("XX ");
            }
            System.out.println();
        }
    }
    boolean garo_delete(Matrix screen){
        boolean garo_delete_return_bool = false;
        int point_1 = 0;
        int point_1_x = 0, point_1_y = 0;
        int point_2 = 0;
        int point_2_x = 0, point_2_y = 0;
        int count = 0;
        for(int i = 0; i <= 24; i++){
            for(int j = 3; j <= 17; j++){
                if(screen.get_array()[i][j] == 0){
                    point_1 = 0; point_2 = 0;
                    point_1_x = 0; point_1_y = 0; point_2_x = 0; point_2_y = 0;
                    count = 0;
                    continue;
                }
                else{
                    if(count == 0){
                        point_1 = screen.get_array()[i][j]; count++;
                        point_1_x = i; point_1_y = j;
                        continue;
                    }
                    if(count == 1){
                        if(point_1 == screen.get_array()[i][j]){
                            count++;
                            point_2 = point_1;
                            point_2_x = i; point_2_y = j;
                            continue;
                        }else{
                            count = 1; point_1 = screen.get_array()[i][j];
                            point_1_x = i; point_1_y = j;
                            continue;
                        }
                    }
                    if(count == 2){
                        if(point_2 == screen.get_array()[i][j]){
                            if(point_2 == screen.get_array()[i][j+1]){
                                screen.get_array()[i][j+1] = 0;
                            }if(point_2 == screen.get_array()[i][j+2]){
                                screen.get_array()[i][j+2] = 0;
                            }
                            count = 0; point_1 = 0; point_2 = 0;
                            screen.get_array()[point_1_x][point_1_y] = 0;
                            screen.get_array()[point_2_x][point_2_y] = 0;
                            screen.get_array()[i][j] = 0;
                            point_1_x = 0; point_1_y = 0; point_2_x = 0; point_2_y = 0;
                            garo_delete_return_bool = true;
                        }else{
                            count = 1; point_1 = screen.get_array()[i][j]; point_2 = 0;
                            point_1_x = i; point_1_y = j; point_2_x = 0; point_2_y = 0;
                            continue;
                        }
                    }
                }
            }
        }
        return garo_delete_return_bool;
    }
    boolean sero_delete(Matrix screen){
        boolean sero_delete_return_bool = false;
        int point_3 = 0;
        int point_3_x = 0, point_3_y = 0;
        int point_4 = 0;
        int point_4_x = 0, point_4_y = 0;
        int count_sero = 0;
        for(int j = 3; j <= 17; j++){
            for(int i = 0; i <= 24; i++){
                if(screen.get_array()[i][j] == 0){
                    point_3 = 0; point_4 = 0;
                    point_3_x = 0; point_3_y = 0; point_4_x = 0; point_4_y = 0;
                    count_sero = 0;
                    continue;
                }
                else{
                    if(count_sero == 0){
                        point_3 = screen.get_array()[i][j]; count_sero++;
                        point_3_x = i; point_3_y = j;
                        continue;
                    }
                    if(count_sero == 1){
                        if(point_3 == screen.get_array()[i][j]){
                            count_sero++;
                            point_4 = point_3;
                            point_4_x = i; point_4_y = j;
                            continue;
                        }else{
                            count_sero = 1; point_3 = screen.get_array()[i][j];
                            point_3_x = i; point_3_y = j;
                            continue;
                        }
                    }
                    if(count_sero == 2){
                        if(point_4 == screen.get_array()[i][j]){
                            if(point_4 == screen.get_array()[i+1][j]){
                                screen.get_array()[i+1][j] = 0;
                            }if(point_4 == screen.get_array()[i+2][j]){
                                screen.get_array()[i+2][j] = 0;
                            }
                            count_sero = 0; point_3 = 0; point_4 = 0;
                            screen.get_array()[point_3_x][point_3_y] = 0;
                            screen.get_array()[point_4_x][point_4_y] = 0;
                            screen.get_array()[i][j] = 0;
                            point_3_x = 0; point_3_y = 0; point_4_x = 0; point_4_y = 0;
                            sero_delete_return_bool = true;
                        }else{
                            count_sero = 1; point_3 = screen.get_array()[i][j]; point_4 = 0;
                            point_3_x = i; point_3_y = j; point_4_x = 0; point_4_y = 0;
                            continue;
                        }
                    }
                }
            }
        }
        return sero_delete_return_bool;
    }
    private Matrix deleteFullLines(Matrix screen, Matrix blk, int top, int dy, int dx, int dw) throws Exception {
        //가로제거 시작
        if(blk == null) return screen;
        while(true) {
            boolean garo_delete_bool = false;
            boolean sero_delete_bool = false;
            garo_delete_bool = garo_delete(screen);
            setting_wall(screen);
            sero_delete_bool = sero_delete(screen);
            setting_wall(screen);
            if(garo_delete_bool == false && sero_delete_bool == false) break;
        }
        return screen;

    }
    public void printScreen() {	Matrix screen = oScreen; // copied from oScreen
        int dy = screen.get_dy();
        int dx = screen.get_dx();
        int dw = iScreenDw;
        int array[][] = screen.get_array();
        for (int y = 0; y < dy - dw + 1; y++) {
            for (int x = dw - 1; x < dx - dw + 1; x++) {
                if (array[y][x] == 0) System.out.print("□ ");
                else if (array[y][x] == 1) System.out.print("■ ");
                else System.out.print("XX ");
            }
            System.out.println();
        }
    }
    public JTetris(int cy, int cx) throws Exception { // initialize dynamic variables
        if (cy < iScreenDw || cx < iScreenDw)
            throw new TetrisException("too small screen");
        iScreenDy = cy;
        iScreenDx = cx;
        int[][] arrayScreen = createArrayScreen(iScreenDy, iScreenDx, iScreenDw);
        state = TetrisState.NewBlock;	// The game should start with a new block needed!
        iScreen = new Matrix(arrayScreen);
        oScreen = new Matrix(iScreen);
    }
    public boolean check_garo_sero(Matrix A){
        if(A.get_array()[0][1] == '0'){
            return true;
        }else{
            return false;
        }
    }
    public boolean check_p(Matrix A, boolean garo_sero, int num_rotate){
        if(num_rotate % 2 != 0){
            if(garo_sero == true){garo_sero=false;}
            else{garo_sero=true;}
        }
        return garo_sero;
    }
    public boolean check_pp(Matrix A, Matrix B, boolean garo_sero, int num_rotate) throws Exception{
        if(num_rotate % 2 != 0){
            if(garo_sero == true){garo_sero=false;}
            else{garo_sero=true;}
        }
        if(garo_sero == true){
            for(int i = 0; i < 3; i++){
                if(A.get_array()[1][i] != B.get_array()[1][i]){
                    return false;
                }
            }
        }else{
            for(int i = 0; i < 3; i++){
                if(A.get_array()[i][1] != B.get_array()[i][1]){
                    return false;
                }
            }
        }
        return true;
    }
    public int check_wall(Matrix A) throws Exception{
        if(A.get_array()[0][0] != 0 && A.get_array()[1][0] != 0 && A.get_array()[2][0] != 0){
            return 1;
        }else if(A.get_array()[0][2] != 0 && A.get_array()[1][2] != 0 && A.get_array()[2][2] != 0) {
            return 2;
        }else{return 3;}
    }

    void setting_wall(Matrix A) {
        for(int i = 17 ; i >= 3 ; i--) {
            for(int j = 24 ; j >= 0 ; j--) {
                if(A.get_array()[j][i] == 0) continue;
                int cur = A.get_array()[j][i], cur_height = j + 1;
                while(cur_height <= 24) {
                    if(A.get_array()[cur_height][i] != 0) break;
                    cur_height++;
                }
                A.get_array()[j][i] = 0;
                A.get_array()[cur_height - 1][i] = cur;
            }
        }
    }

    public TetrisState accept(char key) throws Exception {
        Matrix tempBlk;
        if (state == TetrisState.NewBlock) {
            change_rotate = 0;
            oScreen = deleteFullLines(oScreen, currBlk, top, iScreenDy, iScreenDx, iScreenDw);
            iScreen.paste(oScreen, 0, 0);
            state = TetrisState.Running;
            idxBlockType = key - '0'; // copied from key
            idxBlockDegree = 0;
            currBlk = setOfBlockObjects[idxBlockType][idxBlockDegree];
            top = 0;
            left = iScreenDw + iScreenDx / 2 - (currBlk.get_dx()+1) / 2;
            tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
            tempBlk = tempBlk.add(currBlk);
            oScreen.paste(iScreen, 0, 0);
            oScreen.paste(tempBlk, top, left); System.out.println();
            boolean temp_in_accept = check_garo_sero(currBlk);
            boolean temp_in_accept_crash = check_pp(tempBlk, currBlk, temp_in_accept, change_rotate);
            if (!temp_in_accept_crash) {
                state = TetrisState.Finished;	// System.out.println("Game Over!");
                return state;	// System.exit(0);
            }
            return state;		// should require a key input
        }
        // while ((key = getKey()) != 'q') {
        switch(key) {
            case 'a':
                left--;
                boolean check_in_a = check_garo_sero(currBlk);
                boolean temp_in_key_a_check_rotate = check_p(currBlk, check_in_a, change_rotate);
                tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                tempBlk = tempBlk.add(currBlk);
                if(temp_in_key_a_check_rotate){
                    if(tempBlk.get_array()[1][0] != currBlk.get_array()[1][0]){
                        any_block_crash = true;
                        break;
                    }
                }else{
                    boolean temp_in_a_else = check_pp(tempBlk, currBlk, check_in_a, change_rotate);
                    if(!temp_in_a_else){any_block_crash = true;}
                }
                break; // move left
            case 'd':
                left++;
                boolean check_in_d = check_garo_sero(currBlk);
                boolean temp_in_key_d_check_rotate = check_p(currBlk, check_in_d, change_rotate);
                tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                tempBlk = tempBlk.add(currBlk);
                if(temp_in_key_d_check_rotate){
                    if(tempBlk.get_array()[1][2] != currBlk.get_array()[1][2]){
                        any_block_crash = true;
                        break;
                    }
                }else{
                    boolean temp_in_d_else = check_pp(tempBlk, currBlk, check_in_d, change_rotate);
                    if(!temp_in_d_else){any_block_crash = true;}
                }
                break; // move right

            case 's':
                top++;
                boolean check_in_s = check_garo_sero(currBlk);
                boolean temp_in_key_s_check_rotate = check_p(currBlk, check_in_s, change_rotate);
                tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                tempBlk = tempBlk.add(currBlk);
                if(temp_in_key_s_check_rotate){
                    boolean temp_in_s_if = check_pp(tempBlk, currBlk, check_in_s, change_rotate);
                    if(!temp_in_s_if){any_block_crash = true;}
                }else{
                    if(tempBlk.get_array()[2][1] != currBlk.get_array()[2][1]){
                        any_block_crash = true;
                        break;
                    }
                }
                break;
            case 'w': // rotateCW
                tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                tempBlk = tempBlk.add(currBlk);
                int temp_in_w_wall = check_wall(tempBlk);
                change_rotate++;  //짝수면 그대로 홀수면 (가로세로바뀐거)
                idxBlockDegree = (idxBlockDegree+1)%nBlockDegrees;
                currBlk = setOfBlockObjects[idxBlockType][idxBlockDegree];
                if(temp_in_w_wall == 1){
                    any_block_crash = true;
                }else if(temp_in_w_wall == 2){
                    any_block_crash = true;
                }else{
                    any_block_crash = false;
                }
                break;
            case ' ': // drop the block
                boolean garo_true_sero_false;
                boolean temp_bool = true;
                if(currBlk.get_array()[0][1] == '0'){
                    garo_true_sero_false = true;
                }else{
                    garo_true_sero_false = false;
                }
                do {
                    top++;
                    tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
                    tempBlk = tempBlk.add(currBlk);
                    temp_bool = check_pp(tempBlk, currBlk, garo_true_sero_false, change_rotate);
                } while (temp_bool);
                any_block_crash = true;
                break;
            default: System.out.println("unknown key!");
        }
        tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
        tempBlk = tempBlk.add(currBlk);
        if (any_block_crash) {
            any_block_crash = false;
            switch(key) {
                case 'a': left++; break; // undo: move right
                case 'd': left--; break; // undo: move left
                case 's': top--; state = TetrisState.NewBlock; break; // undo: move up
                case 'w': // undo: rotateCCW
                    change_rotate--;
                    idxBlockDegree = (idxBlockDegree+nBlockDegrees-1)%nBlockDegrees;
                    currBlk = setOfBlockObjects[idxBlockType][idxBlockDegree];
                    break;
                case ' ': top--; state = TetrisState.NewBlock; break; // undo: move up
            }
            tempBlk = iScreen.clip(top, left, top+currBlk.get_dy(), left+currBlk.get_dx());
            tempBlk = tempBlk.add(currBlk);
        }
        oScreen.paste(iScreen, 0, 0);
        oScreen.paste(tempBlk, top, left);
        // printScreen(oScreen); System.out.println();
        return state;
        // if (newBlockNeeded) { ... }
        // } end of while
    }

    class TetrisException extends Exception {
        public TetrisException() { super("Tetris Exception"); }
        public TetrisException(String msg) { super(msg); }
    }
}


