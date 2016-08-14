package net.novucs.colordash;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ColorDash extends Activity {

    private MechanicsThread mechanicsThread;
    private RenderThread renderThread;
    private GamePanel panel;
    private boolean paused;
    private boolean running;

    public MechanicsThread getMechanicsThread() {
        return mechanicsThread;
    }

    public RenderThread getRenderThread() {
        return renderThread;
    }

    public boolean isPaused() {
        return paused;
    }

    public GamePanel getPanel() {
        return panel;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Turn title off and set to full screen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        panel = new GamePanel(this);
        setContentView(panel);
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
        checkState();
    }

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        checkState();
    }

    public void checkState() {
        if (!isPaused() && panel.isSurfaceEnabled()) {
            start();
            running = true;
        } else if (running) {
            stop();
            running = false;
        }
    }

    private void start() {
        mechanicsThread = new MechanicsThread(this);
        renderThread = new RenderThread(this);
        renderThread.initialize();
        mechanicsThread.initialize();
    }

    private void stop() {
        try {
            mechanicsThread.terminate();
            mechanicsThread.join();
            renderThread.terminate();
            renderThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Free references.
        mechanicsThread = null;
        renderThread = null;
    }
}
