package net.novucs.colordash;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private final ColorDash colorDash;

    public GamePanel(ColorDash colorDash) {
        super(colorDash);
        this.colorDash = colorDash;
    }

    public void initialize() {
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        colorDash.getMechanicsThread().initialize();
        colorDash.getRenderThread().initialize();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        colorDash.getRenderThread().terminate();
        colorDash.getMechanicsThread().terminate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }
}
