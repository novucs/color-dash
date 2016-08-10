package net.novucs.colordash;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ColorDash extends Activity {

    private final MechanicsThread mechanicsThread = new MechanicsThread(this);
    private final RenderThread renderThread = new RenderThread(this);
    private GamePanel gamePanel;

    public MechanicsThread getMechanicsThread() {
        return mechanicsThread;
    }

    public RenderThread getRenderThread() {
        return renderThread;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Turn title off and set to full screen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gamePanel = new GamePanel(this);
        gamePanel.initialize();
        setContentView(gamePanel);
    }
}
