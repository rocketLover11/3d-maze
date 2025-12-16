package net.rktlvr.maze;

public class Level {
    public static final int WALL = 1;
    public static final int EMPTY = 0;
    public static final int START = 2;
    public static final int EXIT = 3;

    public static final double NORTH = 3 * Math.PI / 2;
    public static final double EAST = 0;
    public static final double SOUTH = Math.PI / 2;
    public static final double WEST = Math.PI;

    public int[][] map;
    public Texture wallTex, floorTex, ceilTex, exitTex;

    public double startX, startY;
    public double startAngle = 3 * Math.PI / 2; // default north

    // Optional: pass start angle in constructor
    public Level(int[][] map, Texture wall, Texture floor, Texture ceil, Texture exit, Double angle) {
        this.map = map;
        this.wallTex = wall;
        this.floorTex = floor;
        this.ceilTex = ceil;
        this.exitTex = exit;

        if (angle != null) this.startAngle = angle;

        // Scan map for the start tile
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == START) {
                    this.startX = x + 0.5;
                    this.startY = y + 0.5;
                }
            }
        }
    }

    public boolean isWall(int x, int y) {
        if (y < 0 || y >= map.length || x < 0 || x >= map[0].length) return true;
        return map[y][x] == WALL;
    }
}
