
public class Obstacle {
    LineSegment[] lineSegments;
    int amountOfPoints;
    int currentCapacity;
    public Obstacle(int amountOfPoints){
        this.amountOfPoints = amountOfPoints;
        lineSegments = new LineSegment[amountOfPoints];
        currentCapacity = 0;
    }
    public void addLineSegment(LineSegment ls) throws Exception {
        if(currentCapacity == lineSegments.length) throw new Exception("Obstacle exceeded original capacity at LineSegment" + ls);
        lineSegments[currentCapacity++] = ls;
    }
    public boolean intersects(LineSegment l){
        for(int i = 0; i < lineSegments.length; i++){
            if(l.intersects(lineSegments[i])) {
                return true;
            }
        }
        return false;
    }
    public boolean isInsideObstacle(LineSegment l){
        double xCoordinateMiddle = Static.round((l.start.x+l.end.x)/2), yCoordinateMiddle = Static.round((l.start.y+l.end.y)/2);
        Point middlePoint = new Point(xCoordinateMiddle, yCoordinateMiddle);
        LineSegment ray = new LineSegment(middlePoint, new Point(10e3, middlePoint.y));
        int intersections = countIntersections(ray, l);
        return intersections != -1 && (intersections % 2 != 0);
    }
    public int countIntersections(LineSegment ray, LineSegment l){
        int amountOfIntersections = 0;
        boolean currentTouches = false, previousTouches = rayTouchesLineSegment(lineSegments[lineSegments.length-1], ray);
        for(int i = 0; i < lineSegments.length; i++){
            currentTouches = rayTouchesLineSegment(ray, lineSegments[i]);
            if(l.matches(lineSegments[i])) return -1;
            if(lineSegments[i].intersects(ray) && !(currentTouches && previousTouches)) {amountOfIntersections++;}
            previousTouches = currentTouches;
        }
        return amountOfIntersections;
    }

    private boolean rayTouchesLineSegment(LineSegment ray, LineSegment ls){
        return (ls.intersects(ray) && (ls.start.y == ray.start.y || ls.end.y == ray.start.y));
    }
}
