import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private final List<Particle> particles;
    private final double width;
    private final double height;
    private int cellSize;
    private final List<Particle>[][] grid;

    @SuppressWarnings("unchecked")
    public Simulator(double width, double height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.particles = new ArrayList<>();
        int gridWidth = (int) Math.ceil(width / cellSize);
        int gridHeight = (int) Math.ceil(height / cellSize);
        this.grid = new ArrayList[gridWidth][gridHeight];
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                grid[i][j] = new ArrayList<>();
            }
        }
    }

    public void addParticle(Particle p) {
        particles.add(p);
        int gridX = (int) (p.getX() / cellSize);
        int gridY = (int) (p.getY() / cellSize);
        grid[gridX][gridY].add(p);
    }

    public void update(double dt) {
        // Clear the grid
        for (List<Particle>[] row : grid) {
            for (List<Particle> cell : row) {
                cell.clear();
            }
        }

        // Update particle positions and reinsert them into the grid
        for (Particle p : particles) {
            p.updatePos(dt);
            checkBoundaryCollisions(p);
            int gridX = (int) (p.getX() / cellSize);
            int gridY = (int) (p.getY() / cellSize);
            grid[gridX][gridY].add(p);
        }

        // Check for collisions
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                checkParticleCollisions(grid[i][j]);
                if (i > 0) checkParticleCollisions(grid[i][j], grid[i - 1][j]);
                if (j > 0) checkParticleCollisions(grid[i][j], grid[i][j - 1]);
                if (i > 0 && j > 0) checkParticleCollisions(grid[i][j], grid[i - 1][j - 1]);
                if (i > 0 && j < grid[i].length - 1) checkParticleCollisions(grid[i][j], grid[i - 1][j + 1]);
            }
        }
    }

    private void checkBoundaryCollisions(Particle p) {
        if (p.getX() - p.getRadius() < 0 || p.getX() + p.getRadius() > width) {
            p.setVx(-p.getVx());
        }
        if (p.getY() - p.getRadius() < 0 || p.getY() + p.getRadius() > height) {
            p.setVy(-p.getVy());
        }
    }

    private void checkParticleCollisions(List<Particle> cell) {
        for (int i = 0; i < cell.size(); i++) {
            for (int j = i + 1; j < cell.size(); j++) {
                if (collide(cell.get(i), cell.get(j))) {
                    resolveCollision(cell.get(i), cell.get(j));
                }
            }
        }
    }

    private void checkParticleCollisions(List<Particle> cell1, List<Particle> cell2) {
        for (Particle p1 : cell1) {
            for (Particle p2 : cell2) {
                if (collide(p1, p2)) {
                    resolveCollision(p1, p2);
                }
            }
        }
    }

    private boolean collide(Particle p1, Particle p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < p1.getRadius() + p2.getRadius();
    }

    private void resolveCollision(Particle p1, Particle p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Normal vector
        double nx = dx / distance;
        double ny = dy / distance;

        // Move particles apart so they no longer overlap
        double overlap = p1.getRadius() + p2.getRadius() - distance;
        double moveP1 = overlap * (p2.getMass() / (p1.getMass() + p2.getMass()));
        double moveP2 = overlap * (p1.getMass() / (p1.getMass() + p2.getMass()));

        p1.setX(p1.getX() + moveP1 * nx);
        p1.setY(p1.getY() + moveP1 * ny);
        p2.setX(p2.getX() - moveP2 * nx);
        p2.setY(p2.getY() - moveP2 * ny);

        // Tangent vector
        double tx = -ny;
        double ty = nx;

        // Dot product tangent
        double dpTan1 = p1.getVx() * tx + p1.getVy() * ty;
        double dpTan2 = p2.getVx() * tx + p2.getVy() * ty;

        // Dot product normal
        double dpNorm1 = p1.getVx() * nx + p1.getVy() * ny;
        double dpNorm2 = p2.getVx() * nx + p2.getVy() * ny;

        // Conservation of momentum in 1D
        double m1 = (dpNorm1 * (p1.getMass() - p2.getMass()) + 2.0 * p2.getMass() * dpNorm2) / (p1.getMass() + p2.getMass());
        double m2 = (dpNorm2 * (p2.getMass() - p1.getMass()) + 2.0 * p1.getMass() * dpNorm1) / (p1.getMass() + p2.getMass());

        // Update velocities
        p1.setVx(tx * dpTan1 + nx * m1);
        p1.setVy(ty * dpTan1 + ny * m1);
        p2.setVx(tx * dpTan2 + nx * m2);
        p2.setVy(ty * dpTan2 + ny * m2);
    }

    public List<Particle> getParticles() {
        return particles;
    }
}