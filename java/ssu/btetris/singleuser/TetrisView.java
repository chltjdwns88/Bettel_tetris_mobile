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
import android.util.Log;

import ssu.btetris.model.Matrix;

public class TetrisView extends View {
    private int dy, dx;   // view size in unit blocks
    private int by = 0, bx = 0;     // unit block size
    private int color = Color.BLACK;
    private int skip = 0;
    private Matrix screen = null;
    private Paint paint = new Paint();
    public int ondraw_calls = 0;
    private Bitmap jewel1, jewel2, jewel3;
    public TetrisView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }
    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TetrisView(Context context) {
        super(context);
    }
    public void init(int y, int x, int w) {
        dy = y; dx = x;
        skip = w;
    }
    public void accept(Matrix m)  { screen = m; }
    public void onDraw(Canvas canvas) {
        int cy = 10;
        int cx = 10;
        ondraw_calls++;
        if (screen == null) return;
        int[][] array = screen.get_array();
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        bx = (getWidth() - 20 - ((dx-1)*5))/dx;
        by = (getHeight() - 20 - ((dy-1)*5))/dy;

        Bitmap jewel1 = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        Bitmap jewel2 = BitmapFactory.decodeResource(getResources(), R.drawable.green);
        Bitmap jewel3 = BitmapFactory.decodeResource(getResources(), R.drawable.blue);

        int w1 = jewel1.getWidth() / 15;
        int h1 = jewel1.getHeight() / 10;

        int w2 = jewel2.getWidth() / 15;
        int h2 = jewel2.getHeight() / 10;

        int w3 = jewel3.getWidth() / 15;
        int h3 = jewel3.getHeight() / 10;

        for (int y = 0; y < dy; y++) {
            for (int x = skip; x < skip + dx; x++) {
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
                cx += (bx + 5);
            }
            cx = 10;
            cy += (by + 5);
        }
        jewel1.recycle();
    }
}