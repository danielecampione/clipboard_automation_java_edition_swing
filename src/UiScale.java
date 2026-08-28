import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

/**
 * Centralized HiDPI measurements for Java 8 Swing.
 *
 * Java 8 may expose either physical DPI or an already scaled graphics
 * transform, depending on the operating system and JRE update. This class
 * avoids applying the same scaling twice.
 */
public final class UiScale {
    private static final double BASE_DPI = 96.0;
    private static final double SCALE = detectScale();

    private UiScale() {
    }

    public static double factor() {
        return SCALE;
    }

    public static int px(int logicalPixels) {
        return Math.max(1, (int) Math.round(logicalPixels * SCALE));
    }

    public static float font(float logicalPoints) {
        return (float) (logicalPoints * SCALE);
    }

    public static Insets insets(int top, int left, int bottom, int right) {
        return new Insets(px(top), px(left), px(bottom), px(right));
    }

    private static double detectScale() {
        if (GraphicsEnvironment.isHeadless()) {
            return 1.0;
        }

        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        GraphicsConfiguration configuration = device.getDefaultConfiguration();
        AffineTransform transform = configuration.getDefaultTransform();

        double graphicsScale = Math.max(transform.getScaleX(), transform.getScaleY());

        // Newer runtimes may already scale the complete Swing graphics context.
        if (graphicsScale > 1.01) {
            return 1.0;
        }

        double dpiScale = Toolkit.getDefaultToolkit().getScreenResolution() / BASE_DPI;
        return clamp(dpiScale, 1.0, 3.0);
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
