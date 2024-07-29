import java.util.ArrayList;
import java.util.List;
/*
This class initializes a simulator with parameters present in Constants.java. It also creates a list of all Particle
objects created and present in the simulation. Also present in this class is an update method to define the time step
and methods to check for and handle collision between particles.
 */
public class Simulator {
    private final List<Particle> particles;
    private final double width;
    private final double height;

    //constructs a simulator object of width*height
    public Simulator(double width, double height) {
        this.width = width;
        this.height = height;
        this.particles = new ArrayList<Particle>();
    }

    //adds a particle object to the list of particles
    public void addParticle(Particle p) {
        particles.add(p);
    }

    //update method responsible for updating position of particles at every time step dt
    //and also for running methods to check and resolve collision
    public void update(double dt){
        for (Particle p : particles) {
            p.updatePos(dt);
            checkBoundaryCollisions(p);
            checkParticleCollisions();
        }
    }

    //check boundary collisions and flips velocity if true
    private void checkBoundaryCollisions(Particle p) {
        if (p.getX() - p.getRadius() < 0 || p.getX() + p.getRadius() > width) {
            p.setVx(-p.getVx());
        }
        if (p.getY() - p.getRadius() < 0 || p.getY() + p.getRadius() > height) {
            p.setVy(-p.getVy());
        }
    }

    //
    private void checkParticleCollisions() {
        for (int i = 0; i < particles.size(); i++) {
            Particle p1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle p2 = particles.get(j);
                double dx = p1.getX() - p2.getX();
                double dy = p1.getY() - p2.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < p1.getRadius() + p2.getRadius()) {
                    resolveCollision(p1, p2);
                }
            }
        }
    }

    private void resolveCollision(Particle p1, Particle p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Normal vector
        double nx = dx / distance;
        double ny = dy / distance;

        // Tangent vector
        double tx = -ny;

        // Dot product tangent
        double dpTan1 = p1.getVx() * tx + p1.getVy() * nx;
        double dpTan2 = p2.getVx() * tx + p2.getVy() * nx;

        // Dot product normal
        double dpNorm1 = p1.getVx() * nx + p1.getVy() * ny;
        double dpNorm2 = p2.getVx() * nx + p2.getVy() * ny;

        // Conservation of momentum in 1D
        double m1 = (dpNorm1 * (p1.getMass() - p2.getMass()) + 2.0 * p2.getMass() * dpNorm2) / (p1.getMass() + p2.getMass());
        double m2 = (dpNorm2 * (p2.getMass() - p1.getMass()) + 2.0 * p1.getMass() * dpNorm1) / (p1.getMass() + p2.getMass());

        // Update velocities
        p1.setVx(tx * dpTan1 + nx * m1);
        p1.setVy(nx * dpTan1 + ny * m1);
        p2.setVx(tx * dpTan2 + nx * m2);
        p2.setVy(nx * dpTan2 + ny * m2);
    }

    public List<Particle> getParticles() {
        return particles;
    }
}
