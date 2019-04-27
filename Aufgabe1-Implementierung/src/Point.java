public class Point {
    double x, y;
    public Point(double x, double y){
        this.x =  x;
        this.y =  y;
    }
    public double distance(Point p){
        return Static.round(Math.hypot(this.x-p.x, this.y - p.y));
    }
    @Override
    public String toString(){
        return "("+this.x+"|"+this.y+")";
    }
    public boolean matches(Point a){
        return Static.round(this.x) == Static.round(a.x) && Static.round(this.y) == Static.round(a.y);
    }
}
