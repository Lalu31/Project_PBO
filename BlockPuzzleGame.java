import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

public class BlockPuzzleGame extends JFrame {
    private final int GRID_SIZE = 10; // Ukuran grid 10x10
    private final int CELL_SIZE = 40; // Ukuran setiap sel grid dalam piksel
    private int[][] grid = new int[GRID_SIZE][GRID_SIZE]; // Grid untuk menyimpan data permainan
    private Color[][] colors = new Color[GRID_SIZE][GRID_SIZE]; // Warna blok
    private int currentBlockX = 0, currentBlockY = GRID_SIZE / 2; // Posisi blok aktif
    private int[][] currentBlockShape; // Bentuk blok aktif
    private Color currentBlockColor; // Warna blok aktif
    private Random random = new Random(); // Untuk memilih blok dan warna secara acak
    private Timer timer; // Untuk mengatur kecepatan blok jatuh

    public BlockPuzzleGame() {
        setTitle("Block Puzzle Jewel");
        setSize(GRID_SIZE * CELL_SIZE + 20, GRID_SIZE * CELL_SIZE + 40);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama untuk menampilkan grid
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g); // Menggambar grid
            }
        };
        gamePanel.setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        add(gamePanel);
        pack();

        // Tambahkan key listener untuk kontrol pemain
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        // Mengisi baris bawah grid dengan blok berbentuk acak
        initializeBottomRow();

        // Membuat blok pertama
        spawnNewBlock();

        // Timer untuk membuat blok jatuh
        timer = new Timer(500, e -> {
            moveBlockDown();
            gamePanel.repaint();
        });
        timer.start();
    }

    // Mengisi baris paling bawah dengan blok berbentuk acak
    private void initializeBottomRow() {
        int[][][] shapes = {
            {{0, 0}, {0, 1}, {0, 2}, {0, 3}}, // I-shape
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}}, // Square
            {{0, 0}, {1, 0}, {1, 1}, {1, 2}}, // L-shape
            {{0, 0}, {0, 1}, {1, 1}, {1, 2}}, // Z-shape
            {{0, 0}, {0, 1}, {0, 2}, {1, 1}}  // T-shape
        };

        int yOffset = 0; // Posisi awal horizontal
        for (int i = 0; i < GRID_SIZE; i++) {
            if (yOffset >= GRID_SIZE) break;

            int[][] shape = shapes[random.nextInt(shapes.length)]; // Pilih bentuk random
            Color blockColor = getRandomColor(); // Pilih warna random

            // Tempatkan blok di grid
            for (int[] cell : shape) {
                int x = GRID_SIZE - 1 - cell[0];
                int y = yOffset + cell[1];
                if (y < GRID_SIZE) { // Pastikan tidak keluar dari batas grid
                    grid[x][y] = 1;
                    colors[x][y] = blockColor;
                }
            }

            yOffset += shape[0].length; // Pindahkan posisi ke kanan
        }
    }

    // Menggambar grid
    private void drawGrid(Graphics g) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == 0) {
                    g.setColor(Color.LIGHT_GRAY); // Warna sel kosong
                } else {
                    g.setColor(colors[i][j]); // Warna sel terisi
                }
                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Menggambar blok aktif
        g.setColor(currentBlockColor);
        for (int[] cell : currentBlockShape) {
            int x = currentBlockX + cell[0];
            int y = currentBlockY + cell[1];
            g.fillRect(y * CELL_SIZE, x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(y * CELL_SIZE, x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    // Membuat blok baru
    private void spawnNewBlock() {
        currentBlockX = 0;
        currentBlockY = GRID_SIZE / 2;
        currentBlockShape = generateRandomBlock();
        currentBlockColor = getRandomColor();
    }

    // Menggerakkan blok ke bawah
    private void moveBlockDown() {
        if (canMove(currentBlockX + 1, currentBlockY)) {
            currentBlockX++;
        } else {
            mergeBlockToGrid();
            spawnNewBlock();
        }
    }

    // Menggabungkan blok aktif ke grid
    private void mergeBlockToGrid() {
        for (int[] cell : currentBlockShape) {
            int x = currentBlockX + cell[0];
            int y = currentBlockY + cell[1];
            grid[x][y] = 1;
            colors[x][y] = currentBlockColor;
        }
    }

    // Memeriksa apakah blok bisa bergerak
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

    // Menangani input keyboard
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (canMove(currentBlockX, currentBlockY - 1)) {
                    currentBlockY--;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (canMove(currentBlockX, currentBlockY + 1)) {
                    currentBlockY++;
                }
                break;
            case KeyEvent.VK_DOWN:
                moveBlockDown();
                break;
        }
        repaint();
    }

    // Menghasilkan bentuk blok acak
    private int[][] generateRandomBlock() {
        int[][][] shapes = {
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}}, // I-shape
            {{0, 0}, {1, 0}, {1, 1}, {1, 2}}, // L-shape
            {{0, 0}, {0, 1}, {0, 2}, {1, 1}}, // T-shape
        };
        return shapes[random.nextInt(shapes.length)];
    }

    // Menghasilkan warna acak
    private Color getRandomColor() {
        Color[] possibleColors = {Color.ORANGE, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA};
        return possibleColors[random.nextInt(possibleColors.length)];
    }

    // Metode main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlockPuzzleGame game = new BlockPuzzleGame();
            game.setVisible(true);
        });
    }
}
