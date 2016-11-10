package io.reist.dali.drawables;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reist.dali.DaliUtils;
import io.reist.dali.ScaleMode;

public class FadingDaliDrawable extends DaliDrawable {

    public static final float FADE_DURATION = 300; // todo move to ImageRequest as a parameter

    private float progress = 1;
    private long startTime = -1;
    private int originalAlpha;

    boolean fadingIn;

    private final float placeholderWidth;
    private final float placeholderHeight;
    private final RectF placeholderDst = new RectF();
    private final Paint placeholderPaint = new Paint();

    public FadingDaliDrawable(
            @NonNull Bitmap bitmap,
            @NonNull ScaleMode scaleMode,
            float targetWidth,
            float targetHeight,
            @Nullable Drawable placeholder,
            @Nullable Bitmap placeholderBitmap
    ) {

        super(bitmap, scaleMode, targetWidth, targetHeight);

        fadingIn = true;

        if (placeholder == null) {

            placeholderWidth = -1f;
            placeholderHeight = -1f;

        } else {

            placeholderWidth = DaliUtils.getPlaceholderWidth(targetWidth, placeholder);
            placeholderHeight = DaliUtils.getPlaceholderHeight(targetHeight, placeholder);
            if (placeholderWidth <= 0 || placeholderHeight <= 0) {
                return;
            }

            // todo use pool
            if (placeholderBitmap == null) {
                placeholderBitmap = Bitmap.createBitmap(
                        (int) placeholderWidth,
                        (int) placeholderHeight,
                        bitmap.getConfig()
                );
            }
            Canvas canvas = new Canvas(placeholderBitmap);
            placeholder.setBounds(0, 0, (int) placeholderWidth, (int) placeholderHeight);
            placeholder.draw(canvas);
            BitmapShader placeholderShader = new BitmapShader(
                    placeholderBitmap,
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP
            );
            transform(placeholderWidth, placeholderHeight, placeholderShader, placeholderDst);
            placeholderPaint.setShader(placeholderShader);

        }

    }

    @SuppressLint("NewApi")
    @Override
    public void draw(@NonNull Canvas canvas) {

        if (fadingIn) {
            if (startTime == -1) {
                progress = 0;
                startTime = System.currentTimeMillis();
                originalAlpha = getAlpha();
                setAlpha(0);
            } else {
                progress = (System.currentTimeMillis() - startTime) / FADE_DURATION;
                if (progress >= 1f) {
                    progress = 1f;
                    startTime = -1;
                    setAlpha(originalAlpha);
                    fadingIn = false;
                } else {
                    setAlpha((int) (originalAlpha * progress));
                }
            }
        } else {
            progress = 1;
        }

        if (placeholderWidth > 0 && placeholderHeight > 0 && progress < 1f) {

            placeholderPaint.setColorFilter(getColorFilter());
            placeholderPaint.setAlpha((int) ((1f - progress) * originalAlpha));

            drawPlaceholder(canvas, placeholderDst, placeholderPaint);

        }

        super.draw(canvas);

        if (fadingIn) {
            invalidateSelf();
        }

    }

    @SuppressWarnings("WeakerAccess")
    protected void drawPlaceholder(@NonNull Canvas canvas, RectF dst, Paint paint) {
        canvas.drawRect(dst, paint);
    }

    public boolean isFadingIn() {
        return fadingIn;
    }

    public long getStartTime() {
        return startTime;
    }

    public float getProgress() {
        return progress;
    }

}
