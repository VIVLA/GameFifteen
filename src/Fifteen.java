import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public final class Fifteen extends JPanel {
    private final int SIZE = 4;
    private final int MARGIN = 30;
    private final int gridSize;
    private int[] tiles;
    private final int amountOfTiles;
    private final int tileSize;
    private int blankTilePosition;
    private final Font FONT = new Font("Arial", Font.BOLD, 60);
    private final Color FOREGROUND = new Color(83, 163, 88);
    private boolean gameOver;

    private Fifteen() {
        int dimension = 550;
        gridSize = dimension - MARGIN * 2;
        amountOfTiles = SIZE * SIZE - 1;
        tileSize = gridSize / SIZE;
        gameOver = true;
        setPreferredSize(new Dimension(dimension, dimension + MARGIN));
        setBackground(new Color(196, 199, 196));
        addMouseListener(new MousePressedAdapter());
        initGame();
    }

    private void initGame() {
        createTiles();
        do {
            createTiles();
            shuffleTiles();
        } while (!isSolvable());
    }

    private boolean isSolvable() {
        int amountOfInversions = 0;
        for (int i = 0; i < tiles.length - 1; i++) {
            for (int j = 0; j < i; j++) {
                if (tiles[j] > tiles[i])
                    amountOfInversions++;
            }
        }
        return amountOfInversions % 2 == 0;
    }

    private void shuffleTiles() {
        int n = amountOfTiles;
        while (n > 0) {
           int r = new Random().nextInt(n--);
           int tmp = tiles[r];
           tiles[r] = tiles[n];
           tiles[n] = tmp;
        }
        gameOver = false;
    }

    private void createTiles() {
        tiles = new int[SIZE * SIZE];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = (i + 1) % tiles.length;
        }
        blankTilePosition = tiles.length - 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawTiles(g2D);
        if (gameOver)
        drawStartMessage(g2D);
    }

    private void drawStartMessage(Graphics2D g2D) {
        String s = "Click to start new game";
        Font f = getFont().deriveFont(Font.BOLD, 24);
        g2D.setFont(f);
        g2D.setColor(FOREGROUND);
        FontMetrics fm = getFontMetrics(f);
        int width = fm.stringWidth(s);
        int x = (getWidth() - width) / 2;
        int y = getHeight() - MARGIN;
        g2D.drawString(s, x, y);
    }

    private void drawTiles(Graphics2D g2D) {
        for (int i = 0; i < tiles.length; i++) {
            int ex = i % SIZE;
            int ey = i / SIZE;
            int x = ex * tileSize + MARGIN;
            int y = ey * tileSize + MARGIN;

            if (tiles[i] == 0) {
                if (gameOver) {
                    g2D.setColor(FOREGROUND);
                    g2D.setFont(FONT);
                    drawStringOnTile(g2D, "V", x, y);
                }
            } else {
                g2D.setColor(FOREGROUND);
                g2D.fillRoundRect(x, y, tileSize, tileSize, 25, 25);
                g2D.setStroke(new BasicStroke(3));
                g2D.setColor(Color.BLACK);
                g2D.drawRoundRect(x, y, tileSize, tileSize, 25, 25);
                g2D.setColor(Color.WHITE);
                g2D.setFont(FONT);
                drawStringOnTile(g2D, String.valueOf(tiles[i]), x, y);
            }
        }
    }

    private void drawStringOnTile(Graphics2D g, String s, int x, int y) {
        FontMetrics fm = getFontMetrics(FONT);
        int asc = fm.getAscent();
        int desc = fm.getDescent();
        int width = fm.stringWidth(s);
        g.drawString(s, x + (tileSize - width) / 2, y + (tileSize + (asc - desc)) / 2);
    }

    private class MousePressedAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (gameOver) {
                initGame();
            } else {
                int ex = e.getX() - MARGIN;
                int ey = e.getY() - MARGIN;
                if (ex < 0 || ex > gridSize || ey < 0 || ey > gridSize) return;
                int clickedX = ex / tileSize;
                int clickedY = ey / tileSize;
                int clickedTilePosition = (clickedY * SIZE) + clickedX;
                int blankTilePositionX = blankTilePosition % SIZE;
                int blankTilePositionY = blankTilePosition / SIZE;
                int dir = 0;
                if (clickedX - blankTilePositionX == 0 && Math.abs(clickedY - blankTilePositionY) > 0)
                    dir = (clickedY - blankTilePositionY) > 0 ? SIZE : -SIZE;
                else if (clickedY - blankTilePositionY == 0 && Math.abs(clickedX - blankTilePositionX) > 0)
                    dir = (clickedX - blankTilePositionX) > 0 ? 1 : -1;
                if (dir != 0) {
                    do {
                        int newBlankTilePosition = blankTilePosition + dir;
                        tiles[blankTilePosition] = tiles[newBlankTilePosition];
                        blankTilePosition = newBlankTilePosition;
                    } while (blankTilePosition != clickedTilePosition);
                    tiles[blankTilePosition] = 0;
                }
                gameOver = isSolved();
            }
            repaint();
        }

        private boolean isSolved() {
            if (tiles[tiles.length - 1] != 0) return false;
            for (int i = amountOfTiles - 1; i >= 0; i--) {
                if (tiles[i] != i + 1) return false;
            }
            return true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {
            JFrame frame = new JFrame("Fifteen the Game");
            frame.add(new Fifteen(), BorderLayout.CENTER);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }
}
