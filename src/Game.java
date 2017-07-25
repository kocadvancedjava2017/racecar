import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener{

    //window vars
    private final int MAX_FPS; //maximum refresh rate
    private final int WIDTH; //window width
    private final int HEIGHT; //window height

    //double buffer strategy
    private BufferStrategy strategy;

    private ArrayList<Integer> keys = new ArrayList<>();

    //loop variables
    private boolean isRunning = true; //is the window running
    private long rest = 0; //how long to sleep the main thread

    //timing variables
    private float dt; //delta time
    private long lastFrame; //time since last frame
    private long startFrame; //time since start of frame
    private int fps; //current fps

    public Racer racer;


    private Vector obsP = new Vector(100, 200);
    private Vector obsSz = new Vector(100, 100);

    public Game(int width, int height, int fps){
        super("My Game");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    /*
     * init()
     * initializes all variables needed before the window opens and refreshes
     */
    void init(){
        //initializes window size
        setBounds(0, 0, WIDTH, HEIGHT);
        setResizable(false);

        //set jframe visible
        setVisible(true);

        //set default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create double buffer strategy
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        addKeyListener(this);
        setFocusable(true);

        //set initial lastFrame var
        lastFrame = System.currentTimeMillis();


        //set background window color
        setBackground(Color.BLACK);

        this.racer = new Racer(new Vector(WIDTH/2, HEIGHT/2), new Vector(30, 20),
                30000, 20, 0, (float)Math.toRadians(180), 0.6f, 0.999f, new Color(255, 200, 100));
    }

    /*
     * update()
     * updates all relevant game variables before the frame draws
     */
    private void update(){
        //update current fps
        fps = (int)(1f/dt);

        handleKeys();

        racer.update(dt);

        if(racer.p.x < 0) racer.p.setX(WIDTH);
        if(racer.p.x > WIDTH) racer.p.setX(0);
        if(racer.p.y < 0) racer.p.setY(HEIGHT);
        if(racer.p.y > HEIGHT) racer.p.setY(0);

        float radius1 = racer.sz.ix/2;
        float radius2 = obsSz.ix/2;
        float sqdist = Vector.sub(racer.p, obsP).sqmag();

        if(sqdist <(float)Math.pow(radius1 + radius2, 2)){
            //v = v * (p - p2)
            racer.v = Vector.mult(
                    Vector.normalize(
                            Vector.sub(racer.p, obsP)), racer.v.mag());
            racer.a = Vector.mult(
                    Vector.normalize(
                            Vector.sub(racer.p, obsP)), racer.a.mag());
        }

    }

    /*
     * draw()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
    private void draw(){
        //get canvas
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

        //clear screen
        g.clearRect(0,0,WIDTH, HEIGHT);

        racer.draw(g);

        g.setColor(Color.RED);
        g.drawOval(obsP.ix - obsSz.ix/2, obsP.iy - obsSz.iy/2, obsSz.ix, obsSz.iy);
        //g.drawOval(racer.p.ix - racer.sz.ix/2, racer.p.iy - racer.sz.ix/2, racer.sz.ix, racer.sz.ix);


        //draw fps
        g.setColor(Color.GREEN);
        g.drawString(Long.toString(fps), 10, 40);

        //release resources, show the buffer
        g.dispose();
        strategy.show();
    }

    private void handleKeys(){
        for(int i = 0; i < keys.size(); i++){
            switch(keys.get(i)){
                case KeyEvent.VK_W:
                    racer.accel();
                    break;
                case KeyEvent.VK_A:
                    racer.turnRight(dt);
                    break;
                case KeyEvent.VK_S:
                    racer.applybreak(dt);
                    break;
                case KeyEvent.VK_D:
                    racer.turnLeft(dt);
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(!keys.contains(keyEvent.getKeyCode()))
            keys.add(keyEvent.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        for(int i = keys.size() - 1; i >= 0; i--){
            if(keyEvent.getKeyCode() == keys.get(i))
                keys.remove(i);
        }
    }

    /*
         * run()
         * calls init() to initialize variables
         * loops using isRunning
            * updates all timing variables and then calls update() and draw()
            * dynamically sleeps the main thread to maintain a framerate close to target fps
         */
    public void run(){
        init();

        while(isRunning){

            //new loop, clock the start
            startFrame = System.currentTimeMillis();

            //calculate delta time
            dt = (float)(startFrame - lastFrame)/1000;

            //update lastFrame for next dt
            lastFrame = startFrame;

            //call update and draw methods
            update();
            draw();

            //dynamic thread sleep, only sleep the time we need to cap the framerate
            //rest = (max fps sleep time) - (time it took to execute this frame)
            rest = (1000/MAX_FPS) - (System.currentTimeMillis() - startFrame);
            if(rest > 0){ //if we stayed within frame "budget", sleep away the rest of it
                try{ Thread.sleep(rest); }
                catch (InterruptedException e){ e.printStackTrace(); }
            }
        }

    }

    //entry point for application
    public static void main(String[] args){
        Game game = new Game(800, 600, 60);
        game.run();
    }

}
