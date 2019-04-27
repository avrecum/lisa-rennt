import java.util.ArrayList;

public class WeightedUndirectedGraph{
    private int CURRENT_CAPACITY;
     Node[] vertices;
     Edge[][] adjMat;
    private int size;
    double geschwindigkeitMaedchen;
    double geschwindigkeitBus;
    private double latestLeavingTime;
    private double meetingTime;
    private double meetingYCoord;
    private int shortestPathNodeIndex;
    public WeightedUndirectedGraph(int size){
        this.CURRENT_CAPACITY = size;
        this.size = -1;
        this.adjMat = new Edge[size][size];
        this.vertices = new Node[size];
        this.latestLeavingTime =Double.NEGATIVE_INFINITY;
    }

    public void connect(int startNode, int endNode, double weight) throws Exception{
        if(vertices[startNode] != null && vertices[endNode]!= null) {
            Edge e = new Edge(weight);
            adjMat[startNode][endNode] = e;
            adjMat[endNode][startNode] = e;
        }else{
            throw new Exception("Cannot connect: Node doesn't exist.");
        }
    }
    public void disconnect(int startNode, int endNode) throws Exception{
        if(vertices[startNode] != null && vertices[endNode]!= null) {
            adjMat[startNode][endNode] = null;
            adjMat[endNode][startNode] = null;
        }else{
            throw new Exception("Cannot disconnect: Node doesn't exist.");
        }
    }
    public boolean isConnected(int startNode, int endNode){
        return vertices[startNode] != null && vertices[endNode]!= null || startNode == endNode;
    }
    public double getWeight(int startNode, int endNode)throws Exception{
        if(vertices[startNode] != null && vertices[endNode]!= null && adjMat[startNode][endNode]!= null) {
            return adjMat[startNode][endNode].getWeight();
        }
        else{
            throw new Exception("Cannot get weight: Node or edge doesn't exist. startNode: " + startNode+ ", endNode: "+ endNode);
        }
    }
    public int addNode(Point a, int oIndex) throws Exception {
        if (size+1 <= CURRENT_CAPACITY/2) {
            vertices[++size] = new Node(a, oIndex);
            return size;
        } else {
            Node[] currentVertices = this.vertices;
            CURRENT_CAPACITY*=2;
            vertices = new Node[CURRENT_CAPACITY];
            for(int i = 0; i < currentVertices.length; i++){
                if(currentVertices[i] != null) vertices[i] = currentVertices[i];
                else break;
            }
            Edge[][] currentAdjMat = adjMat;
            adjMat = new Edge[CURRENT_CAPACITY][CURRENT_CAPACITY];
            for(int i = 0; i < currentAdjMat.length; i++){
                for(int j = 0; j< currentAdjMat.length; j++){
                    adjMat[i][j] = currentAdjMat[i][j];
                }
            }
            vertices[++size] = new Node(a, oIndex);
            return size;
        }
    }
    public int findUnvisitedNeighbor(int index){
        for(int i = vertices[index].lastUncheckedNode; i <= size; i++){
            if(adjMat[index][i]!= null){
                vertices[index].lastUncheckedNode = i+1;
                return i;
            }
        }
        return -1;
    }
    public void dijkstra(int startIndex){
        long startTime = System.currentTimeMillis();
        Set sptSet = new Set(size+1);
        sptSet.add(startIndex);
        int v = startIndex;
        vertices[startIndex].distanceToRoot = 0;
        vertices[startIndex].path.add(startIndex);
        int u = -1;
        int current;
        double currentDistance;
        while(sptSet.size() < this.size()) {
            while ((current = this.findUnvisitedNeighbor(v)) != -1) {
                if(vertices[current].distanceToRoot > (currentDistance = vertices[v].distanceToRoot + adjMat[v][current].getWeight()) || vertices[current].distanceToRoot == -1){
                    vertices[current].distanceToRoot = currentDistance;
                    vertices[current].path = (ArrayList) vertices[v].path.clone();
                    vertices[current].path.add(current);
                }
            }
            currentDistance = -1;
            for(int i = 0; i <= this.size; i++){
                if(!sptSet.contains(i) && (currentDistance == -1 || (vertices[i].distanceToRoot < currentDistance && vertices[i].distanceToRoot >= 0))) {
                    currentDistance = vertices[i].distanceToRoot;
                    u = i;
                }
            }
            sptSet.add(u);
            v = u;
        }
        System.out.println("Dijkstra brauchte " + (System.currentTimeMillis()-startTime) + " Millisekunden.");
    }

    public ArrayList<Integer> getShortestPath(){
        ArrayList<Integer> shortestPath = null;
        double currentLeavingTime = 0.0;
        for(int i = 0; i < this.size(); i++){
            if( vertices[i].point.x == 0 &&(currentLeavingTime = vertices[i].determineLeavingTime()) > latestLeavingTime){ latestLeavingTime = currentLeavingTime;
            shortestPath = vertices[i].path;
            meetingTime = vertices[i].point.y/(geschwindigkeitBus/3.6);
            meetingYCoord = vertices[i].point.y;
            shortestPathNodeIndex = i;
            }
        }
        return shortestPath;
    }
    public double getLatestLeavingTime(){
        return this.latestLeavingTime;
    }
    public double getMeetingTime(){
        return this.meetingTime;
    }
    public double getMeetingYCoord(){
        return this.meetingYCoord;
    }
    public int getShortestPathNodeIndex(){
        return this.shortestPathNodeIndex;
    }

    public void resetNodes(){
        for(int i = 0; i < vertices.length; i++){
            vertices[i].distanceToRoot = -1;
            vertices[i].lastUncheckedNode = 0;
            vertices[i].path = new ArrayList<Integer>();
        }
    }

    class Node{
        Point point;
        int lastUncheckedNode;
        double distanceToRoot;
        int obstacleIndex;
        ArrayList<Integer> path;
        public Node(Point p, int oIndex){
            obstacleIndex = oIndex;
            this.point = p;
            this.lastUncheckedNode = 0;
            path = new ArrayList<Integer>();
            distanceToRoot = -1;
        }
        @Override
        public String toString(){
            return "Node: " + point + " Distance to root "+ this.distanceToRoot + " have to start walking after " + determineLeavingTime() + " secs.";
        }
        public double determineLeavingTime(){
            return this.point.y/(geschwindigkeitBus/3.6)-this.distanceToRoot/(geschwindigkeitMaedchen/3.6);
        }
    }
     class Edge{
        private double WEIGHT;
        private boolean visited;
        public Edge(double weight){
            this.WEIGHT = weight;
            this.visited = false;
        }
        public double getWeight(){
            return WEIGHT;
        }
        public void setVisited(boolean visited){
            this.visited = visited;
        }
        public boolean getVisited(){
            return visited;
        }
    }
    public int size(){return size+1;}

}