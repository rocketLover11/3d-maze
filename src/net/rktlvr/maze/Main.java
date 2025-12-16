package net.rktlvr.maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Main extends JPanel implements KeyListener {

    static final int WIDTH = 800;
    static final int HEIGHT = 600;
    static final double FOV = Math.PI / 3;
    static final double MOVE_SPEED = 0.08;
    static final double ROT_SPEED = 0.04;

    double px, py;
    double angle = 0;

    boolean forward, back, left, right;
    boolean gameComplete = false;
    private long completionTime = -1; // timestamp when game finished

    LevelManager levelManager;
    Level level;
    BufferedImage screen;

    public Main() throws Exception {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        levelManager = new LevelManager();
        level = levelManager.getCurrentLevel();
        px = level.startX;
        py = level.startY;

        screen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }

    void update(double deltaTime) {
        if (gameComplete) return;

        if (left)  angle -= ROT_SPEED;
        if (right) angle += ROT_SPEED;

        double nx = px;
        double ny = py;

        if (forward) { nx += Math.cos(angle) * MOVE_SPEED; ny += Math.sin(angle) * MOVE_SPEED; }
        if (back)    { nx -= Math.cos(angle) * MOVE_SPEED; ny -= Math.sin(angle) * MOVE_SPEED; }

        if (!level.isWall((int)nx, (int)py)) px = nx;
        if (!level.isWall((int)px, (int)ny)) py = ny;

        // ===== check exit =====
        if (level.map[(int)py][(int)px] == Level.EXIT) {
            if (levelManager.nextLevel()) {
                level = levelManager.getCurrentLevel();
                px = level.startX;
                py = level.startY;
            } else {
                gameComplete = true;
                completionTime = System.currentTimeMillis();
            }
        }
    }

    private int applyShade(int color, double shade) {
        int r = (int)(((color >> 16) & 0xFF) * shade);
        int g = (int)(((color >> 8) & 0xFF) * shade);
        int b = (int)((color & 0xFF) * shade);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (screen.getWidth() != getWidth() || screen.getHeight() != getHeight()) {
            screen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2d = screen.createGraphics();
        try {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, screen.getWidth(), screen.getHeight());

            int panelWidth = screen.getWidth();
            int panelHeight = screen.getHeight();

            for (int x = 0; x < panelWidth; x++) {
                double rayAngle = (angle - FOV/2) + (x/(double)panelWidth) * FOV;
                double rayX = Math.cos(rayAngle);
                double rayY = Math.sin(rayAngle);

                // ===== DDA RAYCAST =====
                int mapX = (int) px;
                int mapY = (int) py;

                double sideDistX, sideDistY;
                double deltaDistX = Math.abs(1 / rayX);
                double deltaDistY = Math.abs(1 / rayY);
                int stepX, stepY;
                boolean hit = false;
                int side = 0;

                if (rayX < 0) { stepX = -1; sideDistX = (px - mapX) * deltaDistX; }
                else          { stepX = 1;  sideDistX = (mapX + 1.0 - px) * deltaDistX; }

                if (rayY < 0) { stepY = -1; sideDistY = (py - mapY) * deltaDistY; }
                else          { stepY = 1;  sideDistY = (mapY + 1.0 - py) * deltaDistY; }

                while (!hit) {
                    if (sideDistX < sideDistY) { sideDistX += deltaDistX; mapX += stepX; side=0; }
                    else                        { sideDistY += deltaDistY; mapY += stepY; side=1; }
                    if (level.isWall(mapX, mapY)) hit=true;
                }

                double perpWallDist;
                double wallX;
                if (side == 0) {
                    perpWallDist = (mapX - px + (1 - stepX)/2) / rayX;
                    wallX = py + perpWallDist * rayY;
                } else {
                    perpWallDist = (mapY - py + (1 - stepY)/2) / rayY;
                    wallX = px + perpWallDist * rayX;
                }
                wallX -= Math.floor(wallX);

                int lineHeight = (int)(panelHeight / perpWallDist);
                int drawStart = Math.max(0, -lineHeight/2 + panelHeight/2);
                int drawEnd = Math.min(panelHeight-1, drawStart + lineHeight);

                Texture wallTex = level.wallTex;
                for (int y = drawStart; y < drawEnd; y++) {
                    double ty = (y - drawStart) / (double)lineHeight;
                    int color = applyShade(wallTex.sample(wallX, ty), 1.0 / (1.0 + perpWallDist * 0.1));
                    if (x>=0 && x<panelWidth && y>=0 && y<panelHeight) screen.setRGB(x, y, color);
                }

                // ===== FLOOR & CEILING =====
                for (int y = drawEnd; y < panelHeight; y++) {
                    double rowDist = panelHeight / (2.0*y - panelHeight);
                    double floorX = px + rowDist * rayX;
                    double floorY = py + rowDist * rayY;

                    double tx = Math.min(0.999, floorX % 1.0);
                    double ty = Math.min(0.999, floorY % 1.0);

                    int mapFX = (int)floorX;
                    int mapFY = (int)floorY;

                    Texture floorTexture = level.floorTex;
                    if (mapFX >= 0 && mapFY >= 0 && mapFY < level.map.length && mapFX < level.map[0].length) {
                        if (level.map[mapFY][mapFX] == Level.EXIT) floorTexture = level.exitTex;
                    }

                    double shadeFactor = 1.0 / (1.0 + rowDist * 0.1);

                    int floorColor = applyShade(floorTexture.sample(tx, ty), shadeFactor);
                    int ceilColor = applyShade(level.ceilTex.sample(tx, ty), shadeFactor);

                    int ceilY = panelHeight - y - 1;
                    if (x>=0 && x<panelWidth && y>=0 && y<panelHeight) screen.setRGB(x, y, floorColor);
                    if (x>=0 && x<panelWidth && ceilY>=0 && ceilY<panelHeight) screen.setRGB(x, ceilY, ceilColor);
                }
            }

        } finally {
            g2d.dispose();
        }

        g.drawImage(screen, 0, 0, null);

        if (gameComplete) {
            g.setColor(Color.WHITE);
            g.drawString("GAME COMPLETE", getWidth()/2 - 60, getHeight()/2);
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_W) forward=true;
        if (e.getKeyCode()==KeyEvent.VK_S) back=true;
        if (e.getKeyCode()==KeyEvent.VK_A) left=true;
        if (e.getKeyCode()==KeyEvent.VK_D) right=true;
    }
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_W) forward=false;
        if (e.getKeyCode()==KeyEvent.VK_S) back=false;
        if (e.getKeyCode()==KeyEvent.VK_A) left=false;
        if (e.getKeyCode()==KeyEvent.VK_D) right=false;
    }
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) throws Exception {
        JFrame f = new JFrame("rkt_lvr's Maze Game");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);

        Main game = new Main();
        f.add(game);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        final double FPS = 60.0;
        final double frameTime = 1_000_000_000 / FPS;
        long lastTime = System.nanoTime();

        while (true) {
            long now = System.nanoTime();
            double delta = now - lastTime;

            if (delta >= frameTime) {
                game.update(delta);
                game.repaint();
                lastTime = now;

                // ===== auto-close 2 seconds after game complete =====
                if (game.gameComplete && game.completionTime > 0) {
                    if (System.currentTimeMillis() - game.completionTime >= 2000) {
                        System.exit(0);
                    }
                }

            } else {
                long sleep = (long)((frameTime - delta)/1_000_000);
                if (sleep>0) try { Thread.sleep(sleep); } catch(Exception ignored) {}
            }
        }
    }
}
