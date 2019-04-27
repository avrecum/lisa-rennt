public class LineSegment {
    Point start, end;
    double digits = 4;
    static double errorTolerance;
    public LineSegment(Point start, Point end){
        if(start == null) throw new IllegalArgumentException("Starting point was null.");
        if(end == null) throw new IllegalArgumentException("Ending point was null.");
        this.start = start;
        this.end = end;
        errorTolerance = Math.pow(10.0, digits);

    }
    public boolean intersects(LineSegment ls){

        if(this.start.matches(ls.start)||this.start.matches(ls.end)||this.end.matches(ls.start)||this.end.matches(ls.end)) return false;
        int a = orientation(this.start, this.end, ls.start);
        int b = orientation(this.start, this.end, ls.end);
        int c = orientation(ls.start, ls.end, this.start);
        int d = orientation(ls.start, ls.end, this.end);
        if(a != b && c != d) {
            return true;
        }
        return false;
    }

    private int orientation(Point a, Point b, Point c){
        double val = Static.round((b.y-a.y)*(c.x-b.x)-(b.x-a.x)*(c.y-b.y));
        if (val == 0)
            return 0;
        else if(val < 0)
            return 2;
        return 1;
    }
    public boolean matches(LineSegment a){
        return (this.start.matches(a.start) && this.end.matches(a.end)) || (this.end.matches(a.start)&& this.start.matches(a.end));
    }

    @Override
    public String toString(){
    return start + " --> " + end;
    }

}
