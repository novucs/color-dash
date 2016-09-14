package net.novucs.colordash.state.menu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.SurfaceHolder;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.R;
import net.novucs.colordash.entity.Entity;
import net.novucs.colordash.entity.EntityType;
import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.state.RenderTask;
import net.novucs.colordash.state.Snapshot;

import java.util.Collection;
import java.util.Map;

public class MenuRenderTask implements RenderTask {

    private final ColorDash game;
    private static final Paint paint = new Paint();

    public MenuRenderTask(ColorDash game) {
        this.game = game;
    }

    @Override
    public void render(Snapshot snapshot) {
        MenuSnapshot menuSnapshot = (MenuSnapshot) snapshot;
        SurfaceHolder surfaceHolder = game.getPanel().getHolder();
        Canvas canvas = surfaceHolder.lockCanvas();

        // Wipe with white color.
        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        renderAll(canvas, menuSnapshot);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    public void renderAll(Canvas canvas, MenuSnapshot snapshot) {
        for (Map.Entry<EntityType, Collection<Entity.Snapshot>> entry : snapshot.getEntities().asMap().entrySet()) {
            renderObstacles(canvas, entry.getValue());
        }
        drawUI(canvas, snapshot);
    }

    public void drawUI(Canvas canvas, MenuSnapshot snapshot) {
        drawPlayButton(canvas, snapshot);
        drawTrophyButton(canvas, snapshot);
        drawSettingsButton(canvas, snapshot);
    }


    public void drawPlayButton(Canvas canvas, MenuSnapshot snapshot) {
        Bitmap b = BitmapFactory.decodeResource(game.getResources(), R.drawable.playarrow);
        Bitmap resizedBitmap = getResizedBitmap(b, canvas.getWidth() / 4, canvas.getHeight() / 6);

        ColorFilter filter = new PorterDuffColorFilter(snapshot.getPlayColor(), PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        float x = canvas.getWidth() * 0.65f;
        float y = canvas.getHeight() * 0.40f;
        canvas.drawBitmap(resizedBitmap, x, y, paint);
    }

    public void drawTrophyButton(Canvas canvas, MenuSnapshot snapshot) {
        Bitmap b = BitmapFactory.decodeResource(game.getResources(), R.drawable.trophy);
        Bitmap resizedBitmap = getResizedBitmap(b, canvas.getWidth() / 4, canvas.getHeight() / 6);

        ColorFilter filter = new PorterDuffColorFilter(snapshot.getTrophyColor(), PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        float x = canvas.getWidth() * 0.20f;
        float y = canvas.getHeight() * 0.40f;
        canvas.drawBitmap(resizedBitmap, x, y, paint);
    }

    public void drawSettingsButton(Canvas canvas, MenuSnapshot snapshop) {
        Bitmap b = BitmapFactory.decodeResource(game.getResources(), R.drawable.settings);
        Bitmap resizedBitmap = getResizedBitmap(b, (int) (canvas.getWidth() * 0.1f), (int) (canvas.getWidth() * 0.1f));

        ColorFilter filter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        float x = canvas.getWidth() * 0.90f;
        float y = canvas.getHeight() * 0.90f;
        canvas.drawBitmap(resizedBitmap, x, y, paint);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void renderObstacles(Canvas canvas, Collection<Entity.Snapshot> obstacles) {
        for (Entity.Snapshot entity : obstacles) {
            renderObstacle(canvas, (Obstacle.Snapshot) entity);
        }
    }

    private void renderObstacle(Canvas canvas, Obstacle.Snapshot obstacle) {
        float left = obstacle.getLocation().getX();
        float top = obstacle.getLocation().getY();
        float right = left + obstacle.getWidth();
        float bottom = top + obstacle.getHeight();

        if (top < canvas.getHeight() * 0.75f && top > canvas.getHeight() * 0.30f) {
            return;
        }

        paint.setColorFilter(null);
        paint.setColor(obstacle.getColor());

        if (obstacle.isLeft() && right != 0) {
            float radius = obstacle.getHeight() / 2;
            right -= radius;
            canvas.drawCircle(right, (bottom + top) / 2, radius, paint);
        } else if (!obstacle.isLeft() && left != game.getPanel().getWidth()) {
            float radius = obstacle.getHeight() / 2;
            left += radius;
            canvas.drawCircle(left, (bottom + top) / 2, radius, paint);
        }

        canvas.drawRect(left, top, right, bottom, paint);
    }
}
