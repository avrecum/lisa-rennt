import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Main {
    static Point startingPoint;
    static Obstacle[] obstacles;
    static double angle;

    public static void main(String[] args) {
        //Einlesen der Kommandozeilenargumente
        if(args.length < 2){System.out.println("Benutzung: lisarennt EingabedateiPfad.txt AusgabedateiPfad [lisaGeschwindigkeit] [busGeschwindigkeit]"); System.exit(0);}
        String inputPath = args[0];
        String outputPath = args[1];
        double geschwindigkeitMaedchen = 15.0;
        double geschwindigkeitBus = 30.0;
        if(args.length == 4){
            geschwindigkeitMaedchen = Double.parseDouble(args[2]);
            geschwindigkeitBus = Double.parseDouble(args[3]);
        }
        //Errechnen des optimalen Winkels
        angle = computeAngle(geschwindigkeitMaedchen, geschwindigkeitBus);
        // Erstelle Graph
        WeightedUndirectedGraph graph = new WeightedUndirectedGraph(10);
        //Uebergebe Graph die Geschwindigkeiten
        graph.geschwindigkeitMaedchen = geschwindigkeitMaedchen;
        graph.geschwindigkeitBus = geschwindigkeitBus;
        int startingPointIndex = 0;
        //Beginne Daten einzulesen
        try {
            BufferedReader b = new BufferedReader(new FileReader(new File(inputPath)));
            // Lese Anzahl von Hindernissen
            int amountOfObstacles = Integer.parseInt(b.readLine());
            obstacles = new Obstacle[amountOfObstacles];
            for (int i = 0; i < amountOfObstacles; i++) {
                //Erstelle String Array mit Hinderniskoordinaten
                String[] l = b.readLine().split(" ");
                // Lese Anzahl von Punkten im Hindernis
                int amountOfPointsInObstacle = Integer.parseInt(l[0]);
                //Erstelle Hindernis mit nötiger Anzahl von Punkten
                obstacles[i] = new Obstacle(amountOfPointsInObstacle);
                Point firstPoint = new Point(Static.round(Double.parseDouble(l[1])), Static.round(Double.parseDouble(l[2])));
                //füge ersten Knoten hinzu
                graph.addNode(firstPoint, i);
                //Der erste Punkt des Hindernisses ist der aktuelle Punkt, der nachste Punkt wird in der Schleife initialisiert und nach jeder Iteration erneuert
                Point currentPoint = firstPoint, nextPoint;
                for (int j = 3; j <= amountOfPointsInObstacle * 2; j += 2) {
                    // Erstellen des naechsten Knotens
                    nextPoint = new Point(Double.parseDouble(l[j]), Double.parseDouble(l[j + 1]));
                    //Füge Knoten dem Graphen hinzu
                    graph.addNode(nextPoint, i);
                    //Verbinde aktuellen Knoten mit dem naechsten Knoten
                    obstacles[i].addLineSegment(new LineSegment(currentPoint, nextPoint));
                    currentPoint = nextPoint;
                }
                //Verbinde letzten Knoten des Hindernisses mit dem ersten Knoten
                obstacles[i].addLineSegment(new LineSegment(currentPoint, firstPoint));
            }
            String[] startingPointValues = b.readLine().split(" ");
            startingPoint = new Point(Double.parseDouble(startingPointValues[0]), Double.parseDouble(startingPointValues[1]));
            b.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input-Datei konnte nicht gefunden werden.");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Daten.");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Ein Fehler ist beim Lesen der Daten aufgetreten.");
            e.printStackTrace();
            System.exit(1);
        }
        // Beende Daten einzulesen

        //Beginne Sichtbarkeitsgraph-Algorithmus
        //Beginne Sichtbarkeitsgraph-Algorithmus fuer P vereinigt mit S
        //Anzahl der Knoten im Graphen
        int graphSize = 0;
        //Knoten (zum verbinden miteinander)
        WeightedUndirectedGraph.Node[] nodes = null;
        try {
            // Der Startpunkt wird dem Graphen (der Menge V) hinzugefuegt. Er gehoert zu keinem Hindernis
            startingPointIndex = graph.addNode(startingPoint, -1);
            nodes = graph.vertices;
            graphSize = graph.size();
            // Iteration durch alle Knoten des Graphen i
            for (int i = 0; i < graphSize; i++) {
                //Iteration durch alle Partnerknoten j
                for (int j = 0; j < graphSize; j++) {
                    // Knoten nicht mit sich selbst verbinden.
                    if (i == j) continue;
                    //Gleiche (ueberlappende) Knoten nicht verbinden
                    if(nodes[i].point.equals(nodes[j].point)) continue;
                    //Erstellen der Strecke IJ
                    LineSegment currentLineSegment = new LineSegment(nodes[i].point, nodes[j].point);
                    //Per default schneidet die Strecke kein Polygon.
                    //falls Punkt I und Punkt J im gleichen Hindernis sind, wird inSameObstacle true
                    boolean intersects = false, inSameObstacle = (nodes[i].obstacleIndex == nodes[j].obstacleIndex);
                    //Ueberpruefung, ob IJ im Hindernis liegt, falls I und J teil des gleichen Hindernisses sind. Falls ja, verwerfe IJ
                    if (inSameObstacle && obstacles[nodes[i].obstacleIndex].isInsideObstacle(currentLineSegment))
                        continue;
                    intersects = false;
                    for (int k = 0; k < obstacles.length; k++) {
                        //Falls IJ ein Hindernis schneidet, intersects auf true und Schleife beenden.
                        if (obstacles[k].intersects(currentLineSegment)) {
                            intersects = true;
                            break;
                        }
                    }
                    //Nur wenn IJ keines der Hindernisse schneidet, werden I und J im Graphen verbunden
                    if (!intersects) {
                        graph.connect(i, j, nodes[i].point.distance(nodes[j].point));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Beende Sichtbarkeitsgraph-Algorithmus fuer P vereinigt mit S
        //Beginne Sichtbarkeitsgraph-Algorithmus fuer X

        //Wir iterieren durch alle Knoten und versuchen, Knoten I unter dem effizientesten Winkel mit der y-Achse zu verbinden
        for (int i = 0; i < graphSize; i++) {
            //errechnen der y-Koordinate anhand von I's x-Koordinate und dem effizientesten Winkel
            double yCoordinate = Static.round(nodes[i].point.y + computeYShift(nodes[i].point.x));
            //Erstelle y-Schnittpunkt
            Point newPoint = new Point(0, yCoordinate);
            //erstelle Strecke von I zum y-Schnittpunkt
            LineSegment currentSegmentToStreet = new LineSegment(nodes[i].point, newPoint);
            //ueberpruefe, ob Strecke von I zum moeglichen y-Schnittpunkt kein Hindernis schneidet
            boolean intersects = false;
            //Iteration durch alle Hindernisse
            for (int j = 0; j < obstacles.length; j++) {
                //Iteration durch alle Strecken der Hindernisse
                if (obstacles[j].intersects(currentSegmentToStreet)) {
                    intersects = true;
                    break;
                }
            }
            //Falls die Strecke kein Hindernis schneidet, füge sie dem Graphen hinzu
            if (!intersects) {
                try {
                    int a = graph.addNode(newPoint, -1);
                    graph.connect(i, a, Static.round(nodes[i].point.distance(newPoint)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        //Beende Sichtbarkeitsgraph-Algorithmus fuer X
        //Beende Sichtbarkeitsgraph-Algorithmus
        nodes=graph.vertices;
        //Beginne Algorithmus von Dijkstra
        graph.dijkstra(startingPointIndex);
        //Beende Algorithmus von Dijkstra
        //Beginne Suche des optimalen Knotens
        ArrayList<Integer> shortestPath = graph.getShortestPath();
        //Beende Suche des optimalen Knotens
        //Beginne Ausgabe der Ergebnisse
        DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_TIME;
        LocalTime t = LocalTime.parse("07:30:00");
        LocalTime leavingTime, meetingTime;
        double latestLeavingTime = Math.round(graph.getLatestLeavingTime());
        double meetingTimeNum = Math.round(graph.getMeetingTime());
        leavingTime=t.plusSeconds((long) latestLeavingTime);
        meetingTime = t.plusSeconds((long) meetingTimeNum);
        System.out.println("Lisa muss anfangen zu rennen um "+ format.format(leavingTime));
        System.out.println("Lisa trifft den Bus um " + format.format(meetingTime));
        System.out.println("Sie trifft den Bus an der y-Koordinate " + graph.getMeetingYCoord());
        System.out.println("Lisa braucht " + format.format(meetingTime.minusSeconds(leavingTime.toSecondOfDay())));
        System.out.println("Ihr Weg hat eine Länge von " + Static.round(nodes[graph.getShortestPathNodeIndex()].distanceToRoot));
        System.out.println("Sie geht über folgende Knoten:");
        int counter = 0;
        for(Integer index: shortestPath){
            System.out.println("Knoten " + nodes[index].point + " ID:" + (nodes[index].obstacleIndex==-1?counter==0?" L":counter==shortestPath.size()-1?" ENDE":" P"+(nodes[index].obstacleIndex+1):" P"+(nodes[index].obstacleIndex+1)));
            counter++;
        }
        SVGOutputter s = new SVGOutputter(graph, obstacles, startingPoint, shortestPath);
        s.constructDocument();
        s.outputSVG(outputPath+".svg");
    }

    public static double computeAngle(double geschwindigkeitMaedchen, double geschwindigkeitBus) {
        //unsere Formel
        return Static.round(Math.toDegrees(Math.acos(Math.sqrt(-1 * Math.pow((geschwindigkeitBus / geschwindigkeitMaedchen), -2) + 1))));
    }

    public static double computeYShift(double xDistance) {
        //noetiger y-Shift mit Tangens
        return Static.round(Math.abs(Math.tan(Math.toRadians(angle)) * xDistance));
    }
}