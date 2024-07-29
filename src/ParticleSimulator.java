import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;
/*

 */
public class ParticleSimulator extends JPanel {
    private final Simulator simulator;

    public ParticleSimulator() {

        simulator = new Simulator(Constants.SIMULATION_WIDTH, Constants.SIMULATION_HEIGHT);
        Random rand = new Random();

        List<Particle> particles = simulator.getParticles();

        int i = 0;
        while (i < 5) {
            //double radius = randomIntFromRange(20, 50);
            //double mass = randomIntFromRange(5, 15);

            double radius = 75;
            double mass = 10;

            double x = randomIntFromRange((int) radius, (int) (Constants.SIMULATION_WIDTH - radius));
            double y = randomIntFromRange((int) radius, (int) (Constants.SIMULATION_HEIGHT - radius));
            double vx = randomIntFromRange((int) rand.nextDouble(-200,-100), (int) rand.nextDouble(100, 200));
            double vy = randomIntFromRange((int) rand.nextDouble(-200,-100), (int) rand.nextDouble(100, 200));

            if (i != 0) {
                for (int j = 0; j < particles.size(); j++) {
                    if (distance(x, y, particles.get(j).getX(), particles.get(j).getY()) - radius * 2 < 0) {
                        x = randomIntFromRange((int) radius, (int) (Constants.SIMULATION_WIDTH - radius));
                        y = randomIntFromRange((int) radius, (int) (Constants.SIMULATION_HEIGHT - radius));

                        j = -1;
                    }
                }
            }

            simulator.addParticle(new Particle(x, y, vx, vy, radius, mass));
            i+=1;
        }

        Timer timer = new Timer(16, e -> {
            simulator.update(0.016);
            repaint();
        });
        timer.start();
    }

    private int randomIntFromRange(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<Particle> particles = simulator.getParticles();
        for (Particle particle : particles){
            int x = (int) (particle.getX() - particle.getRadius());
            int y = (int) (particle.getY() - particle.getRadius());
            int diameter = (int) (2*particle.getRadius());
            g.fillOval(x, y, diameter, diameter);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Particle Simulator");
        ParticleSimulator panel = new ParticleSimulator();
        frame.add(panel);
        frame.setSize((int) Constants.SIMULATION_WIDTH, (int) Constants.SIMULATION_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
