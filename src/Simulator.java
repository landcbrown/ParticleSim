import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private final List<Particle> particles;
    private final double width;
    private final double height;
    private double temperature; // Temperature variable
    private final double tolerance = 1e-9; // Tolerance for collision detection
    private boolean isRunning = true; // Flag to stop/start simulation

    // Constructs a simulator object of width*height with default temperature
    public Simulator(double width, double height) {
        this.width = width;
        this.height = height;
        this.particles = new ArrayList<Particle>();
        this.temperature = 300; // Default temperature (300K)

        // Create a new thread for simulation
        new Thread(this::runSimulation).start();

        // Create temperature slider for adjusting the temperature
        createTemperatureSlider();
    }

    // Adds a particle object to the list of particles
    public void addParticle(Particle p) {
        particles.add(p);
    }

    // Create a temperature slider using Swing
    private void createTemperatureSlider() {
        JFrame frame = new JFrame("Temperature Control");
        frame.setSize(400, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a slider to control temperature (range from 100K to 1000K)
        JSlider tempSlider = new JSlider(JSlider.HORIZONTAL, 100, 1000, 300);
        tempSlider.setMajorTickSpacing(100);
        tempSlider.setMinorTickSpacing(10);
        tempSlider.setPaintTicks(true);
        tempSlider.setPaintLabels(true);

        // Add a listener to update temperature as the slider moves
        tempSlider.addChangeListener(e -> {
            temperature = tempSlider.getValue();
            adjustParticleVelocities();
        });

        frame.add(tempSlider);
        frame.setVisible(true);
    }

    // Adjust particle velocities based on the new temperature
    private void adjustParticleVelocities() {
        double scaleFactor = Math.sqrt(temperature / 300.0); // Scale relative to 300K

        for (Particle p : particles) {
            // Scale the velocities by the square root of the temperature ratio
            p.setVx(p.getVx() * scaleFactor);
            p.setVy(p.getVy() * scaleFactor);
        }
    }

    // Main simulation loop
    private void runSimulation() {
        double dt = 0.001; // Time step

        while (isRunning) {
            update(dt);
            try {
                Thread.sleep(10); // Sleep to simulate real-time steps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Update method responsible for updating position of particles at every time step dt
    // and also for running methods to check and resolve collisions
    public void update(double dt) {
        for (Particle p : particles) {
            p.updatePos(dt); // Update particle position
            checkBoundaryCollisions(p); // Check and resolve boundary collisions
        }
        checkParticleCollisions(); // Check and resolve particle collisions
    }

    // Check for boundary collisions and resolve them
    private void checkBoundaryCollisions(Particle p) {
        // Collision with left or right wall
        if (p.getX() - p.getRadius() < 0) {
            p.setVx(-p.getVx());    // Reverse velocity
        } else if (p.getX() + p.getRadius() > width) {
            p.setVx(-p.getVx());
        }

        // Collision with top or bottom wall
        if (p.getY() - p.getRadius() < 0) {
            p.setVy(-p.getVy());
        } else if (p.getY() + p.getRadius() > height) {
            p.setVy(-p.getVy());
        }
    }

    // Check for collisions between particles and resolve them
    private void checkParticleCollisions() {
        for (int i = 0; i < particles.size(); i++) {
            Particle p1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle p2 = particles.get(j);
                double dx = p1.getX() - p2.getX();
                double dy = p1.getY() - p2.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Check if particles are overlapping (within tolerance)
                if (distance < p1.getRadius() + p2.getRadius() + tolerance) {
                    resolveCollision(p1, p2); // Resolve the collision
                }
            }
        }
    }

    // Resolve collision between two particles
    private void resolveCollision(Particle p1, Particle p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Normal vector (nx, ny)
        double nx = dx / distance;
        double ny = dy / distance;

        // Tangent vector (tx, ty)
        double tx = -ny;
        double ty = nx;

        // Dot product of velocities with tangent vector
        double dpTan1 = p1.getVx() * tx + p1.getVy() * ty;
        double dpTan2 = p2.getVx() * tx + p2.getVy() * ty;

        // Dot product of velocities with normal vector
        double dpNorm1 = p1.getVx() * nx + p1.getVy() * ny;
        double dpNorm2 = p2.getVx() * nx + p2.getVy() * ny;

        // Conservation of momentum in 1D along the normal direction
        double m1 = (dpNorm1 * (p1.getMass() - p2.getMass()) + 2.0 * p2.getMass() * dpNorm2) / (p1.getMass() + p2.getMass());
        double m2 = (dpNorm2 * (p2.getMass() - p1.getMass()) + 2.0 * p1.getMass() * dpNorm1) / (p1.getMass() + p2.getMass());

        // Update velocities of particles
        p1.setVx(tx * dpTan1 + nx * m1);
        p1.setVy(ty * dpTan1 + ny * m1);
        p2.setVx(tx * dpTan2 + nx * m2);
        p2.setVy(ty * dpTan2 + ny * m2);
    }

    // Returns the list of particles
    public List<Particle> getParticles() {
        return particles;
    }
}

