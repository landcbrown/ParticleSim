import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private final List<Particle> particles;
    private final double width;
    private final double height;

    public Simulator(double width, double height) {
        this.width = width;
        this.height = height;
        this.particles = new ArrayList<Particle>();
    }

    public void addParticle(Particle p) {
        particles.add(p);
    }

    public void update(double dt){
        for (Particle p : particles) {
            p.updatePos(dt);
            checkBoundaryCollisions(p);
            //checkParticleCollisions();
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

    private void checkParticleCollisions() {

    }


    public List<Particle> getParticles() {
        return particles;
    }
}
