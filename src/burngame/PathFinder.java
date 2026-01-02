// PathFinder.java
package burngame; // <- change package if your project uses a different package

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @date December 30, 2025 (almost a year since last game update)
 * Simple A* pathfinder on a tile grid. Returns world-space waypoint centers.
 * Uses Lvl.isTileWalkable(tx,ty) and Lvl.isInBoundsTile(tx,ty).
 */
public class PathFinder {

    public static final int STRAIGHT_COST = 10;
    public static final int DIAG_COST = 14;

    private final Lvl lvl;
    private final int tileSize;

    public PathFinder(Lvl lvl, int tileSize) {
        this.lvl = lvl;
        this.tileSize = tileSize;
    }

    /**
     * Returns list of world-space points (tile centers) or null if no path.
     */
    public List<Point> findPath(double startWx, double startWy, double goalWx, double goalWy) {
        return findPath(startWx, startWy, goalWx, goalWy, null);
    }
    
    /**
     * Returns list of world-space points (tile centers) or null if no path.
     * Includes enemy parameter to avoid other enemies.
     */
    public List<Point> findPath(double startWx, double startWy, double goalWx, double goalWy, Enemy enemy) {
        if (lvl == null) return null;
        
        // Convert to grid coordinates
        Point startGrid = lvl.worldToGrid(startWx, startWy);
        Point goalGrid = lvl.worldToGrid(goalWx, goalWy);
        
        // Check if start or goal are out of bounds or unwalkable
        if (!lvl.isInBoundsTile(startGrid.x, startGrid.y) || 
            !lvl.isInBoundsTile(goalGrid.x, goalGrid.y)) {
            return null;
        }
        
        // If goal is unwalkable, find nearest walkable tile
        if (!lvl.isTileWalkableWithEnemyCheck(goalGrid.x, goalGrid.y, enemy)) {
            goalGrid = findNearestWalkable(goalGrid.x, goalGrid.y, enemy);
            if (goalGrid == null) return null;
        }
        
        List<Point> tilePath = findPathTiles(startGrid.x, startGrid.y, goalGrid.x, goalGrid.y, enemy);
        if (tilePath == null) return null;
        
        // Convert back to world coordinates
        List<Point> worldPath = new ArrayList<>();
        for (Point tile : tilePath) {
            Point worldPoint = lvl.gridToWorld(tile.x, tile.y);
            worldPath.add(worldPoint);
        }
        
        return worldPath;
    }

    private Point findNearestWalkable(int gx, int gy, Enemy enemy) {
        // Search in expanding circles for a walkable tile
        for (int radius = 1; radius <= 5; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    int nx = gx + dx;
                    int ny = gy + dy;
                    if (Math.abs(dx) == radius || Math.abs(dy) == radius) {
                        if (lvl.isInBoundsTile(nx, ny) && lvl.isTileWalkableWithEnemyCheck(nx, ny, enemy)) {
                            return new Point(nx, ny);
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<Point> findPathTiles(int sx, int sy, int gx, int gy, Enemy enemy) {
        NodeKey start = new NodeKey(sx, sy);
        NodeKey goal = new NodeKey(gx, gy);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        HashMap<NodeKey, Node> all = new HashMap<>();
        HashSet<NodeKey> closed = new HashSet<>();

        Node s = new Node(start, 0, heuristic(sx, sy, gx, gy), null);
        all.put(start, s);
        open.add(s);

        while (!open.isEmpty()) {
            Node cur = open.poll();
            if (cur.key.equals(goal)) {
                return reconstruct(cur);
            }
            closed.add(cur.key);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int nx = cur.key.x + dx;
                    int ny = cur.key.y + dy;

                    if (!lvl.isInBoundsTile(nx, ny)) continue;
                    if (!lvl.isTileWalkableWithEnemyCheck(nx, ny, enemy)) continue;

                    // prevent corner cutting: if moving diagonally, both orthogonals must be walkable
                    if (dx != 0 && dy != 0) {
                        if (!lvl.isTileWalkableWithEnemyCheck(cur.key.x + dx, cur.key.y, enemy) ||
                                !lvl.isTileWalkableWithEnemyCheck(cur.key.x, cur.key.y + dy, enemy)) {
                            continue;
                        }
                    }

                    int gCost = cur.g + ((dx == 0 || dy == 0) ? STRAIGHT_COST : DIAG_COST);
                    NodeKey nk = new NodeKey(nx, ny);

                    if (closed.contains(nk)) continue;

                    Node existing = all.get(nk);
                    if (existing == null || gCost < existing.g) {
                        int h = heuristic(nx, ny, gx, gy);
                        Node next = new Node(nk, gCost, h, cur);
                        all.put(nk, next);
                        if (existing != null) open.remove(existing);
                        open.add(next);
                    }
                }
            }
        }
        return null;
    }

    private int heuristic(int x, int y, int gx, int gy) {
        int dx = Math.abs(gx - x);
        int dy = Math.abs(gy - y);
        // octile heuristic
        return STRAIGHT_COST * (dx + dy) + (DIAG_COST - 2 * STRAIGHT_COST) * Math.min(dx, dy);
    }

    private List<Point> reconstruct(Node cur) {
        ArrayList<Point> path = new ArrayList<>();
        while (cur != null) {
            path.add(0, new Point(cur.key.x, cur.key.y));
            cur = cur.parent;
        }
        return path;
    }

    private static class NodeKey {
        final int x, y;
        NodeKey(int x, int y) { this.x = x; this.y = y; }
        @Override public int hashCode() { return x * 31 + y; }
        @Override public boolean equals(Object o) {
            if (!(o instanceof NodeKey)) return false;
            NodeKey k = (NodeKey)o;
            return k.x == x && k.y == y;
        }
    }

    private static class Node {
        final NodeKey key;
        final int g;
        final int h;
        final int f;
        final Node parent;
        Node(NodeKey key, int g, int h, Node parent) { this.key = key; this.g = g; this.h = h; this.f = g + h; this.parent = parent; }
    }
    
    public int getTileSize(){
        return this.tileSize;
    }
}
