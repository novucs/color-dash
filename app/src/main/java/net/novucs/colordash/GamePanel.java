package net.novucs.colordash;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.atomic.AtomicReference;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private final ColorDash game;
    private final AtomicReference<ClickType> lastClickType = new AtomicReference<>(ClickType.NONE);
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

    public ClickType getLastClickType() {
        return lastClickType.get();
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
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            lastClickType.set(ClickType.NONE);
            return false;
        }

        lastClickType.set(event.getX() > (getWidth() / 2) ? ClickType.RIGHT : ClickType.LEFT);
        return true;
    }
}
