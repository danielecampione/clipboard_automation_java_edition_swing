import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;

/** Java 1.8 Swing UI with explicit HiDPI support. */
public class ClipboardGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color BACKGROUND = new Color(248, 249, 250);
    private static final Color PANEL_BACKGROUND = new Color(236, 240, 241);
    private static final Color TEXT = new Color(44, 62, 80);
    private static final Color GREEN = new Color(39, 174, 96);

    private final I18nManager i18n = I18nManager.getInstance();

    private ClipboardAutomation automation;
    private AnimatedButton startButton;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private JLabel instructionLabel;
    private JLabel elementsLabel;
    private JSpinner elementsSpinner;
    private JCheckBox separatorCheckBox;
    private JCheckBox effectsCheckBox;
    private JMenu languageMenu;
    private JMenuItem italianMenuItem;
    private JMenuItem englishMenuItem;

    public ClipboardGUI() {
        try {
            automation = new ClipboardAutomation();
            buildWindow();
        } catch (Exception exception) {
            showError(
                    i18n.getText("error.init.title"),
                    i18n.getText("error.init.message") + exception.getMessage());
        }
    }

    private void buildWindow() {
        setTitle(i18n.getText("window.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setJMenuBar(createMenuBar());

        JPanel root = new JPanel();
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(
                UiScale.px(28), UiScale.px(30), UiScale.px(30), UiScale.px(30)));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        titleLabel = createLabel(
                i18n.getText("title.label"), 18, Font.BOLD, TEXT);
        instructionLabel = createLabel(
                toHtml(i18n.getText("instruction.label")),
                12,
                Font.PLAIN,
                new Color(52, 73, 94));

        startButton = new AnimatedButton(i18n.getText("start.button"));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(event -> startAutomation());

        statusLabel = createLabel(
                i18n.getText("status.ready"), 12, Font.BOLD, GREEN);

        root.add(titleLabel);
        root.add(Box.createVerticalStrut(UiScale.px(15)));
        root.add(instructionLabel);
        root.add(Box.createVerticalStrut(UiScale.px(22)));
        root.add(createConfigurationPanel());
        root.add(Box.createVerticalStrut(UiScale.px(25)));
        root.add(startButton);
        root.add(Box.createVerticalStrut(UiScale.px(18)));
        root.add(statusLabel);

        setContentPane(root);
        setPreferredSize(new Dimension(UiScale.px(500), UiScale.px(510)));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createConfigurationPanel() {
        RoundedPanel panel = new RoundedPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), UiScale.px(1)),
                BorderFactory.createEmptyBorder(
                        UiScale.px(20), UiScale.px(20), UiScale.px(20), UiScale.px(20))));
        panel.setLayout(new GridBagLayout());
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(UiScale.px(430), UiScale.px(190)));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = UiScale.insets(6, 5, 11, 5);

        JPanel elementsRow = createRow();
        elementsLabel = new JLabel(i18n.getText("elements.label"));
        elementsLabel.setFont(scaledFont(elementsLabel.getFont(), Font.BOLD, 12));
        elementsLabel.setForeground(TEXT);

        elementsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
        elementsSpinner.setPreferredSize(
                new Dimension(UiScale.px(88), UiScale.px(32)));
        elementsSpinner.setFont(scaledFont(elementsSpinner.getFont(), Font.PLAIN, 12));
        elementsSpinner.setEditor(new JSpinner.NumberEditor(elementsSpinner, "#"));
        scaleSpinnerEditor();

        elementsRow.add(elementsLabel);
        elementsRow.add(elementsSpinner);
        panel.add(elementsRow, constraints);

        constraints.gridy++;
        JPanel separatorRow = createRow();
        separatorCheckBox = new JCheckBox(
                i18n.getText("separator.checkbox"), true);
        styleCheckBox(separatorCheckBox, TEXT);
        separatorRow.add(separatorCheckBox);
        panel.add(separatorRow, constraints);

        constraints.gridy++;
        JPanel effectsRow = createRow();
        effectsCheckBox = new JCheckBox(i18n.getText("effects.checkbox"), false);
        styleCheckBox(effectsCheckBox, new Color(142, 68, 173));
        effectsCheckBox.setFont(
                scaledFont(effectsCheckBox.getFont(), Font.BOLD, 12));
        effectsCheckBox.addActionListener(
                event -> startButton.setSpecialEffects(effectsCheckBox.isSelected()));
        effectsRow.add(effectsCheckBox);
        panel.add(effectsRow, constraints);

        return panel;
    }

    private void scaleSpinnerEditor() {
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) elementsSpinner.getEditor();
        editor.getTextField().setFont(
                scaledFont(editor.getTextField().getFont(), Font.PLAIN, 12));
        editor.getTextField().setMargin(UiScale.insets(2, 4, 2, 4));
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(scaledFont(menuBar.getFont(), Font.PLAIN, 12));

        languageMenu = new JMenu(i18n.getText("menu.language"));
        languageMenu.setFont(scaledFont(languageMenu.getFont(), Font.PLAIN, 12));

        italianMenuItem = new JMenuItem(i18n.getText("menu.italian"));
        englishMenuItem = new JMenuItem(i18n.getText("menu.english"));
        italianMenuItem.setFont(scaledFont(italianMenuItem.getFont(), Font.PLAIN, 12));
        englishMenuItem.setFont(scaledFont(englishMenuItem.getFont(), Font.PLAIN, 12));

        italianMenuItem.addActionListener(event -> {
            i18n.setLanguage("it");
            updateTexts();
        });
        englishMenuItem.addActionListener(event -> {
            i18n.setLanguage("en");
            updateTexts();
        });

        languageMenu.add(italianMenuItem);
        languageMenu.add(englishMenuItem);
        menuBar.add(languageMenu);
        return menuBar;
    }

    private void startAutomation() {
        startButton.setEnabled(false);
        statusLabel.setText(i18n.getText("status.running"));
        statusLabel.setForeground(Color.ORANGE.darker());

        try {
            elementsSpinner.commitEdit();
        } catch (Exception exception) {
            startButton.setEnabled(true);
            return;
        }

        final int numberOfElements =
                ((Number) elementsSpinner.getValue()).intValue();
        final boolean addSeparator = separatorCheckBox.isSelected();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(2000);
                automation.executeAutomation(numberOfElements, addSeparator);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    statusLabel.setText(i18n.getText("status.completed"));
                    statusLabel.setForeground(GREEN.darker());
                } catch (Exception exception) {
                    Throwable cause = exception.getCause() == null
                            ? exception
                            : exception.getCause();
                    statusLabel.setText(i18n.getText("status.error"));
                    statusLabel.setForeground(Color.RED);
                    showError(
                            i18n.getText("error.automation.title"),
                            i18n.getText("error.automation.message") + cause.getMessage());
                } finally {
                    startButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void updateTexts() {
        setTitle(i18n.getText("window.title"));
        titleLabel.setText(i18n.getText("title.label"));
        instructionLabel.setText(toHtml(i18n.getText("instruction.label")));
        elementsLabel.setText(i18n.getText("elements.label"));
        separatorCheckBox.setText(i18n.getText("separator.checkbox"));
        effectsCheckBox.setText(i18n.getText("effects.checkbox"));
        startButton.setText(i18n.getText("start.button"));
        statusLabel.setText(i18n.getText("status.ready"));
        languageMenu.setText(i18n.getText("menu.language"));
        italianMenuItem.setText(i18n.getText("menu.italian"));
        englishMenuItem.setText(i18n.getText("menu.english"));
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(
                this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private static JLabel createLabel(
            String text, int size, int style, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", style, Math.round(UiScale.font(size))));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private static Font scaledFont(Font base, int style, float logicalSize) {
        String family = base == null ? "SansSerif" : base.getFamily();
        return new Font(family, style, Math.round(UiScale.font(logicalSize)));
    }

    private static JPanel createRow() {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, UiScale.px(10), 0));
        panel.setOpaque(false);
        return panel;
    }

    private static void styleCheckBox(JCheckBox checkBox, Color color) {
        checkBox.setOpaque(false);
        checkBox.setForeground(color);
        checkBox.setFocusPainted(false);
        checkBox.setFont(scaledFont(checkBox.getFont(), Font.PLAIN, 12));
    }

    private static String toHtml(String value) {
        return "<html><div style='text-align:center'>"
                + value.replace("\n", "<br>")
                + "</div></html>";
    }

    public static void main(String[] args) {
        // Must be set before Swing/AWT initializes on Java 8.
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("sun.java2d.uiScale.enabled", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // The cross-platform look and feel remains a safe fallback.
            }
            new ClipboardGUI().setVisible(true);
        });
    }

    private static final class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private RoundedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D copy = (Graphics2D) graphics.create();
            copy.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            copy.setColor(getBackground());
            copy.fillRoundRect(
                    0, 0, getWidth(), getHeight(), UiScale.px(14), UiScale.px(14));
            copy.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class AnimatedButton extends JButton {
        private static final long serialVersionUID = 1L;

        private boolean specialEffects;
        private boolean hovering;
        private float scale = 1.0f;
        private float angle;
        private float verticalOffset;
        private float glow;
        private float ripple = 1.0f;
        private Point clickPoint = new Point();
        private Timer entranceTimer;
        private Timer exitTimer;
        private Timer glowTimer;
        private Timer clickTimer;

        private AnimatedButton(String text) {
            super(text);
            setFont(new Font(
                    "SansSerif", Font.BOLD, Math.round(UiScale.font(14))));
            setForeground(Color.WHITE);
            setBackground(GREEN);
            setBorder(BorderFactory.createEmptyBorder(
                    UiScale.px(14), UiScale.px(30), UiScale.px(14), UiScale.px(30)));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    hovering = true;
                    if (specialEffects) {
                        playCinematicEntrance();
                    } else {
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    hovering = false;
                    stopGlow();
                    returnHome();
                }

                @Override
                public void mousePressed(MouseEvent event) {
                    if (specialEffects && isEnabled()) {
                        playClick(event.getPoint());
                    }
                }
            });
        }

        private void setSpecialEffects(boolean enabled) {
            specialEffects = enabled;
            if (!enabled) {
                stopGlow();
                returnHome();
            } else if (hovering) {
                playCinematicEntrance();
            }
            repaint();
        }

        private void playCinematicEntrance() {
            stopTimer(entranceTimer);
            stopTimer(exitTimer);

            final float initialScale = scale;
            final float initialAngle = angle;
            final float initialYOffset = verticalOffset;
            final long started = System.currentTimeMillis();

            entranceTimer = new Timer(12, event -> {
                float time = Math.min(
                        1.0f, (System.currentTimeMillis() - started) / 650.0f);

                if (time < 0.22f) {
                    float phase = time / 0.22f;
                    scale = interpolate(initialScale, 0.82f, easeInOut(phase));
                    angle = interpolate(initialAngle, -7.0f, easeInOut(phase));
                    verticalOffset = interpolate(
                            initialYOffset, UiScale.px(8), easeInOut(phase));
                } else if (time < 0.62f) {
                    float phase = (time - 0.22f) / 0.40f;
                    scale = interpolate(0.82f, 1.18f, easeOutBack(phase));
                    angle = interpolate(-7.0f, 3.0f, easeOutCubic(phase));
                    verticalOffset = interpolate(
                            UiScale.px(8), -UiScale.px(5), easeOutCubic(phase));
                } else {
                    float phase = (time - 0.62f) / 0.38f;
                    scale = interpolate(1.18f, 1.10f, easeOutElastic(phase));
                    angle = interpolate(3.0f, 0.0f, easeOutElastic(phase));
                    verticalOffset = interpolate(
                            -UiScale.px(5), 0.0f, easeOutElastic(phase));
                }

                repaint();
                if (time >= 1.0f) {
                    scale = 1.10f;
                    angle = 0.0f;
                    verticalOffset = 0.0f;
                    ((Timer) event.getSource()).stop();
                    startGlow();
                }
            });
            entranceTimer.start();
        }

        private void returnHome() {
            stopTimer(entranceTimer);
            stopTimer(exitTimer);

            final float initialScale = scale;
            final float initialAngle = angle;
            final float initialYOffset = verticalOffset;
            final long started = System.currentTimeMillis();

            exitTimer = new Timer(15, event -> {
                float time = Math.min(
                        1.0f, (System.currentTimeMillis() - started) / 260.0f);
                float eased = easeOutCubic(time);
                scale = interpolate(initialScale, 1.0f, eased);
                angle = interpolate(initialAngle, 0.0f, eased);
                verticalOffset = interpolate(initialYOffset, 0.0f, eased);
                repaint();
                if (time >= 1.0f) {
                    ((Timer) event.getSource()).stop();
                }
            });
            exitTimer.start();
        }

        private void startGlow() {
            stopGlow();
            final long started = System.currentTimeMillis();
            glowTimer = new Timer(25, event -> {
                glow = 0.5f + 0.5f * (float) Math.sin(
                        (System.currentTimeMillis() - started) / 240.0);
                repaint();
            });
            glowTimer.start();
        }

        private void stopGlow() {
            stopTimer(glowTimer);
            glow = 0.0f;
            repaint();
        }

        private void playClick(Point point) {
            clickPoint = point;
            ripple = 0.0f;
            stopTimer(clickTimer);
            final long started = System.currentTimeMillis();

            clickTimer = new Timer(12, event -> {
                float time = Math.min(
                        1.0f, (System.currentTimeMillis() - started) / 440.0f);
                ripple = time;
                if (time < 0.18f) {
                    scale = 1.10f - (time / 0.18f) * 0.15f;
                } else {
                    float phase = (time - 0.18f) / 0.82f;
                    scale = 0.95f
                            + (hovering ? 0.15f : 0.05f) * easeOutBack(phase);
                }
                repaint();
                if (time >= 1.0f) {
                    ripple = 1.0f;
                    scale = hovering ? 1.10f : 1.0f;
                    ((Timer) event.getSource()).stop();
                }
            });
            clickTimer.start();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            int width = getWidth();
            int height = getHeight();
            Graphics2D copy = (Graphics2D) graphics.create();
            copy.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            copy.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            copy.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            double centerX = width / 2.0;
            double centerY = height / 2.0;
            copy.translate(centerX, centerY + verticalOffset);
            copy.rotate(Math.toRadians(angle));
            copy.scale(scale, scale);
            copy.translate(-centerX, -centerY);

            paintGlow(copy, width, height);
            paintButtonBody(copy, width, height);
            paintRipple(copy, width, height);

            copy.setComposite(AlphaComposite.SrcOver);
            super.paintComponent(copy);
            copy.dispose();
        }

        private void paintGlow(Graphics2D graphics, int width, int height) {
            if (!specialEffects || glow <= 0.0f) {
                return;
            }
            for (int logical = 11; logical > 0; logical--) {
                int spread = UiScale.px(logical);
                float alpha = Math.min(
                        0.16f, (0.006f + 0.012f * glow) * (12 - logical));
                graphics.setComposite(AlphaComposite.SrcOver.derive(alpha));
                graphics.setColor(new Color(46, 204, 113));
                graphics.fillRoundRect(
                        -spread,
                        -spread,
                        width + 2 * spread,
                        height + 2 * spread,
                        UiScale.px(28 + logical),
                        UiScale.px(28 + logical));
            }
        }

        private void paintButtonBody(Graphics2D graphics, int width, int height) {
            graphics.setComposite(AlphaComposite.SrcOver);
            graphics.setColor(isEnabled()
                    ? (hovering ? new Color(35, 180, 94) : GREEN)
                    : new Color(160, 160, 160));
            graphics.fillRoundRect(
                    0, 0, width, height, UiScale.px(28), UiScale.px(28));

            if (specialEffects && hovering) {
                graphics.setComposite(AlphaComposite.SrcOver.derive(0.18f));
                graphics.setColor(Color.WHITE);
                int margin = UiScale.px(3);
                graphics.fillRoundRect(
                        margin,
                        margin,
                        width - 2 * margin,
                        height / 2,
                        UiScale.px(24),
                        UiScale.px(24));
            }
        }

        private void paintRipple(Graphics2D graphics, int width, int height) {
            if (ripple >= 1.0f) {
                return;
            }
            float radius = (float) Math.hypot(width, height)
                    * easeOutCubic(ripple);
            float alpha = 0.55f * (1.0f - ripple);
            graphics.setComposite(AlphaComposite.SrcOver.derive(alpha));
            graphics.setColor(Color.WHITE);
            int diameter = Math.round(2.0f * radius);
            graphics.fillOval(
                    clickPoint.x - diameter / 2,
                    clickPoint.y - diameter / 2,
                    diameter,
                    diameter);
        }

        private static void stopTimer(Timer timer) {
            if (timer != null) {
                timer.stop();
            }
        }

        private static float interpolate(float from, float to, float amount) {
            return from + (to - from) * amount;
        }

        private static float easeInOut(float value) {
            return value * value * (3.0f - 2.0f * value);
        }

        private static float easeOutCubic(float value) {
            return 1.0f - (float) Math.pow(1.0f - value, 3.0);
        }

        private static float easeOutBack(float value) {
            float shifted = value - 1.0f;
            return 1.0f
                    + 2.70158f * shifted * shifted * shifted
                    + 1.70158f * shifted * shifted;
        }

        private static float easeOutElastic(float value) {
            if (value == 0.0f || value == 1.0f) {
                return value;
            }
            return (float) (Math.pow(2.0, -10.0 * value)
                    * Math.sin((value * 10.0 - 0.75) * (2.0 * Math.PI / 3.0))
                    + 1.0);
        }
    }
}
