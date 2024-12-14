import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

public class BlockPuzzleGame extends JPanel {
    private final int GRID_SIZE = 10;
    private final int CELL_SIZE = 40;
    private int[][] grid = new int[GRID_SIZE][GRID_SIZE];
    private Color[][] colors = new Color[GRID_SIZE][GRID_SIZE];
    private int currentBlockX = 0, currentBlockY = GRID_SIZE / 2;
    private int[][] currentBlockShape;
    private Color currentBlockColor;
    private Random random = new Random();
    private Timer timer;
    private int score = 0;

    public BlockPuzzleGame() {
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        setFocusable(true); // Pastikan panel bisa menerima input keyboard
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    moveBlockLeft();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    moveBlockRight();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    moveBlockDown();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    rotateBlock();
                }
            }
        });
        startGame(); // Mulai game saat objek BlockPuzzleGame dibuat
    }

    public void startGame() {
        // Reset game
        grid = new int[GRID_SIZE][GRID_SIZE]; // Reset grid
        colors = new Color[GRID_SIZE][GRID_SIZE]; // Reset warna grid
        score = 0; // Reset skor
        spawnNewBlock(); // Spawn blok pertama
        if (timer != null) {
            timer.stop(); // Stop timer jika sudah ada
        }
        timer = new Timer(500, e -> {
            moveBlockDown();
            repaint();
        });
        timer.start(); // Mulai timer untuk blok jatuh
    }

    public void stopGame() {
        if (timer != null) {
            timer.stop(); // Stop timer ketika game dihentikan
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == 0) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(colors[i][j]);
                }
                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        g.setColor(currentBlockColor);
        for (int[] cell : currentBlockShape) {
            int x = currentBlockX + cell[0];
            int y = currentBlockY + cell[1];
            g.fillRect(y * CELL_SIZE, x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(y * CELL_SIZE, x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    private void spawnNewBlock() {
        currentBlockX = 0;
        currentBlockY = GRID_SIZE / 2;
        currentBlockShape = generateRandomBlock();
        currentBlockColor = getRandomColor();
        if (!canMove(currentBlockX, currentBlockY)) {
            endGame(); // Akhiri permainan jika blok baru tidak bisa dipindahkan
        }
    }

    private void moveBlockDown() {
        if (canMove(currentBlockX + 1, currentBlockY)) {
            currentBlockX++;
        } else {
            mergeBlockToGrid();
            checkAndClearFullRows();
            spawnNewBlock();
        }
        repaint();
    }

    private void moveBlockLeft() {
        if (canMove(currentBlockX, currentBlockY - 1)) {
            currentBlockY--;
            repaint();
        }
    }

    private void moveBlockRight() {
        if (canMove(currentBlockX, currentBlockY + 1)) {
            currentBlockY++;
            repaint();
        }
    }

    private void rotateBlock() {
        int[][] rotatedBlock = new int[currentBlockShape.length][2];
        for (int i = 0; i < currentBlockShape.length; i++) {
            rotatedBlock[i][0] = -currentBlockShape[i][1];
            rotatedBlock[i][1] = currentBlockShape[i][0];
        }
        int[][] originalBlockShape = currentBlockShape;
        currentBlockShape = rotatedBlock;

        if (!canMove(currentBlockX, currentBlockY)) {
            currentBlockShape = originalBlockShape; // Batalkan rotasi jika tidak valid
        }
        repaint();
    }

    private void mergeBlockToGrid() {
        for (int[] cell : currentBlockShape) {
            int x = currentBlockX + cell[0];
            int y = currentBlockY + cell[1];
            grid[x][y] = 1;
            colors[x][y] = currentBlockColor;
        }
    }

    private void checkAndClearFullRows() {
        for (int i = 0; i < GRID_SIZE; i++) {
            boolean fullRow = true;
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == 0) {
                    fullRow = false;
                    break;
                }
            }
            if (fullRow) {
                clearRow(i);
                score += 10;
            }
        }
        repaint();
    }

    private void clearRow(int row) {
        for (int i = row; i > 0; i--) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = grid[i - 1][j];
                colors[i][j] = colors[i - 1][j];
            }
        }
        for (int j = 0; j < GRID_SIZE; j++) {
            grid[0][j] = 0;
            colors[0][j] = null;
        }
    }

    private boolean canMove(int newX, int newY) {
        for (int[] cell : currentBlockShape) {
            int x = newX + cell[0];
            int y = newY + cell[1];
            if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE || grid[x][y] == 1) {
                return false;
            }
        }
        return true;
    }

    private void endGame() {
        stopGame();
        JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score);
        startGame(); // Mulai ulang permainan setelah game over
    }

    private int[][] generateRandomBlock() {
        int[][][] shapes = {
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}}, // Bentuk I
            {{0, 0}, {1, 0}, {1, 1}, {1, 2}}, // Bentuk L
            {{0, 0}, {0, 1}, {0, 2}, {1, 1}}, // Bentuk T
        };
        return shapes[random.nextInt(shapes.length)];
    }

    private Color getRandomColor() {
        Color[] possibleColors = {Color.ORANGE, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA};
        return possibleColors[random.nextInt(possibleColors.length)];
    }
}
