import java.awt.*;

public class Racer {

    public Vector p;
    public Vector v;
    public Vector a;
    public Vector sz;

    Polygon shape;
    Color color;

    public float sp;
    public float br;

    public float d;
    public float ts;

    public float frV;
    public float frA;

    public boolean isAccel = false;

    public Racer(Vector p, Vector sz, float sp, float br, float d, float ts, float frV, float frA, Color c){
        this.p = p;
        this.sz = sz;
        this.sp = sp;
        this.br = br;
        this.d = d;
        this.ts = ts;
        this.frV = frV;
        this.frA = frA;

        this.color = c;
        int[] x = {0, sz.ix, 0};
        int[] y = {0, sz.iy/2, sz.iy};
        this.shape = new Polygon(x, y, x.length);

        this.v = new Vector(0, 0);
        this.a = new Vector(0, 0);
    }

    public void update(float dt){
        if(isAccel){
            a.add(Vector.mult(Vector.rotate(new Vector(sp, 0), d), dt));
            //System.out.println(a);
            isAccel = false;
        }

        a.mult(frA - (frA * dt));
        v.add(Vector.mult(a, dt));
        v.mult( frV - (frV * dt) );
        p.add(Vector.mult(v, dt));

    }

    public void draw(Graphics2D g){

        g.translate(p.ix, p.iy);
        g.rotate(d);

        g.translate(-sz.ix/2, -sz.iy/2);
        g.setColor(color);
        g.fillPolygon(shape);
        g.translate(sz.ix/2, sz.iy/2);

        g.rotate(-d);
        g.translate(-p.ix, -p.iy);

    }

    public void accel(){
        isAccel = true;
    }

    public void applybreak(float dt){
        //v.mult((frV - (frV*dt)));
    }

    public void turnLeft(float dt){
        d -= ts * dt;
    }

    public void turnRight(float dt){
        d += ts * dt;
    }

}
