package net.rktlvr.maze;

import java.util.*;

public class LevelManager {
    private List<Level> levels;
    private int currentIndex = 0;

    public LevelManager() throws Exception {
        levels = new ArrayList<>();
        levels.add(Levels.level1());
        levels.add(Levels.level2());
    }

    public Level getCurrentLevel() {
        if (currentIndex < levels.size())
            return levels.get(currentIndex);
        return null;
    }

    public boolean nextLevel() {
        currentIndex++;
        return currentIndex < levels.size();
    }

    public boolean hasMoreLevels() {
        return currentIndex < levels.size();
    }
}
