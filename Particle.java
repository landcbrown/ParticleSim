import java.util.List;

public class Particle {

    private static final double GRAVITY = 9.8;

    private final double radius;
    private final double mass;

    private double x;
    private double y;
    private double vx;
    private double vy;

    //constructor for a particle object
    public Particle(double x, double y, double vx, double vy, double radius, double mass) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.mass = mass;
    }

    //getter methods for each of the particle's variables
    public double getX() {return this.x;}
    public double getY() {return this.y;}
    public double getVx() {return this.vx;}
    public double getVy() {return this.vy;}
    public double getRadius() {return this.radius;}
    public double getMass() {return this.mass;}

    //setter methods
    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public void setVx(double vx) {this.vx = vx;}
    public void setVy(double vy) {this.vy = vy;}

    //updates a particles position each time step dt
    public void updatePos(double dt) {
        this.x += vx*dt;
        this.y += vy*dt;
    }
}

