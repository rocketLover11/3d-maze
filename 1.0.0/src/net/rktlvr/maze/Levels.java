package net.rktlvr.maze;

public class Levels {
    public static Level level1() throws Exception {
        Texture wall = new Texture("textures/wall.png");
        Texture exit = new Texture("textures/exit.png");
        Texture floor = new Texture("textures/floor.png");
        Texture ceil = new Texture("textures/ceiling.png");

        int[][] map = {
            {1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,3,1},
            {1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,1},
            {1,1,1,0,0,1,1,1},
            {1,0,0,0,0,0,0,1},
            {1,2,1,0,0,0,0,1},
            {1,1,1,1,1,1,1,1}
        };

        return new Level(map, wall, floor, ceil, exit, Level.NORTH);
    }

    public static Level level2() throws Exception {
        Texture wall = new Texture("textures/wall.png");
        Texture exit = new Texture("textures/exit.png");
        Texture floor = new Texture("textures/floor.png");
        Texture ceil = new Texture("textures/ceiling.png");

        int[][] map = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,3,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
            {1,0,1,0,1,1,1,0,1,0,1,1,1,0,1,1},
            {1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1},
            {1,1,1,0,1,0,1,0,1,1,1,0,1,1,0,1},
            {1,0,0,0,1,0,1,0,0,1,0,0,1,1,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1},
            {1,0,0,0,1,0,0,1,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,1,0,1,0,1,1,0,1,1,1,1},
            {1,0,1,1,0,0,0,1,0,0,1,0,0,0,0,1},
            {1,0,0,0,0,1,0,1,1,1,1,1,1,1,0,1},
            {1,0,1,0,1,1,0,1,1,0,0,0,0,0,0,1},
            {1,0,0,0,1,0,0,0,0,0,1,0,1,1,1,1},
            {1,0,1,1,1,0,1,0,1,1,1,0,0,0,0,1},
            {1,2,1,0,0,0,1,0,0,0,0,0,1,1,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        return new Level(map, wall, floor, ceil, exit, Level.NORTH);
    }
}