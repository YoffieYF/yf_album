package plugin.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class RingProgressView extends View {
    // 当前进度
    private int currentProgress = 1;
    // 最大进度
    private int maxProgress = 100;
    // 得到控件的宽度
    private int width;
    private int ringWidth;
    // 画笔对象
    private Paint mProgressPaint;
    private Paint mStrokePaint;
    private Paint mTextPaint;
    // 上下文
    private Context context;

    // 默认的构造方法，一般取这3个就够用了
    public RingProgressView(Context context) {
        this(context, null);
    }

    public RingProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        ringWidth = dip2px(4);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(0xFFFFFFFF);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(ringWidth);
        mProgressPaint.setAntiAlias(true);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(0x33ffffff);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(ringWidth);
        mStrokePaint.setAntiAlias(true);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xFFFFFFFF);
        mTextPaint.setTextSize(dip2px(12));
        mTextPaint.setAntiAlias(true);
    }


    // 测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
    }

    // 绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1. 计算圆心坐标及半径
        float centerX = width / 2;
        float centerY = width / 2;
        float radius = width / 2 - ringWidth / 2;

        // 2. 画进度背景
        canvas.drawCircle(centerX, centerY, radius, mStrokePaint);

        // 3. 画圆弧
        RectF rectF = new RectF(ringWidth / 2, ringWidth / 2, width - ringWidth / 2, width - ringWidth / 2);
        canvas.drawArc(rectF, 0, currentProgress * 360 / maxProgress, false, mProgressPaint);

        // 3. 画文本
        String text = currentProgress * 100 / maxProgress + "%";
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);  // 得到指定文本边界的指定大小
        canvas.drawText(text, width / 2 - bounds.width() / 2, width / 2 + bounds.height() / 2, mTextPaint);
    }



    /**
     * 把dp转换成px
     *
     * @param dipValue
     * @return
     */
    private int dip2px(int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        invalidate();
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }
}
