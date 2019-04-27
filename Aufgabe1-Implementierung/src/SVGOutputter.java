import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.svg2svg.OutputManager;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.util.ArrayList;


public class SVGOutputter {
    private WeightedUndirectedGraph graph;
    private Obstacle[] obstacles;
    private Document doc;
    private Point startingPoint;
    private ArrayList<Integer> shortestPath;
    public SVGOutputter(WeightedUndirectedGraph graph, Obstacle[] obstacles, Point startingPoint, ArrayList<Integer> shortestPath){
        this.graph = graph;
        this.obstacles = obstacles;
        this.startingPoint = startingPoint;
        this.shortestPath = shortestPath;
    }

    public void constructDocument(){
        int documentHeight = 1200;
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = impl.createDocument(svgNS, "svg", null);

        Element svgRoot = doc.getDocumentElement();
        this.doc = doc;
        Element g1 = doc.createElementNS(svgNS, "g");
        g1.setAttributeNS(null, "transform", "scale(1 -1)");
        svgRoot.appendChild(g1);
        Element g2 = doc.createElementNS(svgNS, "g");
        g2.setAttributeNS(null, "transform", "translate(0 -" + Integer.toString(documentHeight)+")");
        g1.appendChild(g2);
        ArrayList<Element> elems = new ArrayList<Element>();
// Set the width and height attributes on the root 'svg' element.
        svgRoot.setAttributeNS(null, "width", "1200");
        svgRoot.setAttributeNS(null, "height", "1200");
        //create line
        Element lisaPath = doc.createElementNS(svgNS, "line");
        lisaPath.setAttributeNS(null, "id", "y");
        lisaPath.setAttributeNS(null, "x1", "0");
        lisaPath.setAttributeNS(null, "x2", "0");
        lisaPath.setAttributeNS(null, "y1", "0");
        lisaPath.setAttributeNS(null, "y2", "1200");
        lisaPath.setAttributeNS(null, "stroke", "#212121");
        lisaPath.setAttributeNS(null, "stroke-width", "3");
        elems.add(lisaPath);
        Element lisaStartingPoint = doc.createElementNS(svgNS, "circle");
        lisaStartingPoint.setAttributeNS(null, "cx", Integer.toString((int) startingPoint.x));
        lisaStartingPoint.setAttributeNS(null, "cy", Integer.toString((int) startingPoint.y));
        lisaStartingPoint.setAttributeNS(null, "r", "10");
        lisaStartingPoint.setAttributeNS(null, "fill", "#f42121");
        lisaStartingPoint.setAttributeNS(null, "stroke", "#000080");
        elems.add(lisaStartingPoint);

        //create obstacles
        for(int i = 0; i < obstacles.length; i++){
            LineSegment[] lineSegments = obstacles[i].lineSegments;
            Element e = doc.createElementNS(svgNS, "polygon");
            e.setAttributeNS(null, "id", Integer.toString(i+1));
            String points = "";
            for(int j = 0; j < lineSegments.length; j++){
                points += lineSegments[j].start.x + " " + lineSegments[j].start.y + " ";
            }
            e.setAttributeNS(null, "points", points);
            e.setAttributeNS(null, "fill", "#6B6B6B");
            e.setAttributeNS(null, "stroke", "#212121");
            e.setAttributeNS(null, "stroke-width", "3");
            elems.add(e);
        }
        /*
        // create paths
        WeightedUndirectedGraph.Node [] nodes = graph.vertices;
        WeightedUndirectedGraph.Edge[][] edges = graph.adjMat;
        for(int i = 0; i < nodes.length; i++){
            for(int j = 0; j < edges.length; j++){
                if(edges[i][j] != null){
                    Element line = doc.createElementNS(svgNS, "line");
                    line.setAttributeNS(null, "x1",Integer.toString((int)nodes[i].point.x));
                    line.setAttributeNS(null, "x2",Integer.toString((int)nodes[j].point.x));
                    line.setAttributeNS(null, "y1",Integer.toString((int)nodes[i].point.y));
                    line.setAttributeNS(null, "y2",Integer.toString((int)nodes[j].point.y));
                    line.setAttributeNS(null, "stroke", "#ff0000");
                    line.setAttributeNS(null, "stroke-width", "2");
                    elems.add(line);
                }
            }
        }
        */
        // create shortest path
        Element shortestPathLine = doc.createElementNS(svgNS, "polyline");
        shortestPathLine.setAttributeNS(null,"stroke", "#000080");
        shortestPathLine.setAttributeNS(null, "stroke-width", "4");
        shortestPathLine.setAttributeNS(null, "fill", "none");
        String points = "";
        for(Integer j: shortestPath){
            points += graph.vertices[j].point.x + " " + graph.vertices[j].point.y + " ";
        }
        shortestPathLine.setAttributeNS(null, "points", points);
        elems.add(shortestPathLine);
        for(Element e: elems){
            g2.appendChild(e);
        }
    }

    public void outputSVG(String path){
        try {
            SVGTranscoder s = new SVGTranscoder();
            TranscoderInput input = new TranscoderInput(doc);
            OutputStream os = new FileOutputStream(path);
            OutputStreamWriter w = new OutputStreamWriter(os, "UTF-8");
            TranscoderOutput output = new TranscoderOutput(w);
            s.transcode(input, output);
            os.flush();
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
