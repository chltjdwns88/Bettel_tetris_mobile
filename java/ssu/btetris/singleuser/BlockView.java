package ssu.btetris.singleuser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import ssu.btetris.model.Matrix;

/**
 * TODO: document your custom view class.
 */
public class BlockView extends View {
    public BlockView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }
    public BlockView(Context context, AttributeSet attrs) { super(context, attrs); }
    public BlockView(Context context) { super(context); }
    private int cy, cx; // current drawing position
    private int by, bx; // unit block size
    private int blkWidth; // width of the largest block
    private Matrix block = null;
    private Paint paint = new Paint();
    private Bitmap jewel1, jewel2, jewel3;
    public void init(int w) { blkWidth = w; }
    public void accept(Matrix m) { block = m; }
    public void onDraw(Canvas canvas) {
        if (block == null) return;
        int[][] array = block.get_array();
        super.onDraw(canvas);
        by = (getHeight() - 20 - 5*(blkWidth-1)) / blkWidth;   // 20: Margin(up-10, down-10), block gap(5) * (blkWidth - 1)
        bx = (getWidth() - 20 - 5*(blkWidth-1)) / blkWidth;    // 20: Margin(up-10, down-10), block gap(5) * (blkWidth - 1)
        paint.setStyle(Paint.Style.FILL);

        Bitmap jewel1 = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        Bitmap jewel2 = BitmapFactory.decodeResource(getResources(), R.drawable.green);
        Bitmap jewel3 = BitmapFactory.decodeResource(getResources(), R.drawable.blue);

        int w1 = jewel1.getWidth() / 8;
        int h1 = jewel1.getHeight() / 10;

        int w2 = jewel2.getWidth() / 8;
        int h2 = jewel2.getHeight() / 10;

        int w3 = jewel3.getWidth() / 8;
        int h3 = jewel3.getHeight() / 10;

        cy = 10 + (blkWidth - array.length)*(by+5)/2;
        cx = 10 + (blkWidth - array.length)*(bx+5)/2;

        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[0].length; x++) {
                switch(array[y][x]) {
                    case 2:
                        Rect rect1 = new Rect(cx, cy, cx+w1, cy+h1);
                        canvas.drawBitmap(jewel1, null, rect1, null);
                        break;
                    case 3:
                        Rect rect2 = new Rect(cx, cy, cx+w2, cy+h2);
                        canvas.drawBitmap(jewel2, null, rect2, null);
                        break;
                    case 4:
                        Rect rect3 = new Rect(cx, cy, cx+w3, cy+h3);
                        canvas.drawBitmap(jewel3, null, rect3, null);
                        break;
                    default : paint.setColor(Color.WHITE); break;
                }
            }
            cx = 10 + (blkWidth - array.length)*(bx+5)/2;
            cy += (by + 5);
        }
    }
}
