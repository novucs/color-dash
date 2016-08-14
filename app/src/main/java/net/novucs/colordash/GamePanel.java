package net.novucs.colordash;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private final ColorDash game;
    private boolean surfaceEnabled;

    public GamePanel(ColorDash game) {
        super(game);
        this.game = game;
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public boolean isSurfaceEnabled() {
        return surfaceEnabled;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfaceEnabled = true;
        game.checkState();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceEnabled = false;
        game.checkState();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
