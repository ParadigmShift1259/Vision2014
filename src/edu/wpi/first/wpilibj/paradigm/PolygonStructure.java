package edu.wpi.first.wpilibj.paradigm;

import com.googlecode.javacpp.annotation.ByVal;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import java.util.Comparator;
import java.awt.Polygon;




public class PolygonStructure implements ShapeStructure, Comparator<PolygonStructure>{
        private Polygon polygon;
        private CvPoint[] vertices;
        private int n;
        private int size;
        
        
        public PolygonStructure(Polygon polygon, CvPoint[] vertices, int n) {
                if (vertices.length != n)
                        throw new PolygonException("Numer of given vertices is incorrrect!");
                this.polygon = polygon;
                this.vertices = vertices;
                this.n = n;
                this.size = n;
        }
        
        public PolygonStructure(Polygon polygon, int n) {
                super();
                this.polygon = polygon;
                this.n = n;
                this.vertices = new CvPoint[n];
                this.size = 0;
        }
        
        public PolygonStructure(int n) {
                super();
                this.n = n;
                this.size = 0;
                this.vertices = new CvPoint[n];
        }
        
        public void drawShape(IplImage img, @ByVal CvScalar color){
                int[] count = {this.n};
                if ( color == null )
                        color = CV_RGB(0,255,0);
                cvDrawPolyLine( img, this.vertices[0], count, 1, 1, color, 3, CV_AA, 0 );
                
        }
        
        public Polygon getPolygon() {
                return polygon;
        }
        public void setPolygon(Polygon polygon) {
                this.polygon = polygon;
        }
        public CvPoint[] getVertices() {
                return vertices;
        }
        public void setVertices(CvPoint[] vertices) {
                this.vertices = vertices;
        }
        
        public CvPoint getVertex(int index){
                return this.vertices[index];
        }
        
        public void addVertex(CvPoint vertex){
                if ( this.size == this.n)
                        throw new PolygonException("Number of specified vertices exceeded for this polygon!");
                this.vertices[this.size++] = vertex;
        }
        
        /**
         * checks if two adjacent edges are of equal length
         * returns false if the polygon does not have exactly 4 vertices
         * @return
         */
        public boolean isSquare(){
                if (n != 4)
                        return false;
                CvPoint p0 = this.vertices[0];
                CvPoint p1 = this.vertices[1];
                CvPoint p2 = this.vertices[2];
                double d1 = Math.sqrt( Math.pow(p0.x() - p1.x(), 2) + Math.pow(p0.y() - p1.y(), 2) );
                double d2 = Math.sqrt( Math.pow(p2.x() - p1.x(), 2) + Math.pow(p2.y() - p1.y(), 2) );
                double ratio = d1/d2;
                if ( ratio > 0.8 && ratio < 1.2)
                        return true;
                return false;
        }
        
        public int[] getCoordinates(){
                int[] coordinates = new int[2];
                coordinates[0] = (int) this.polygon.getBounds2D().getMinX();
                coordinates[1] = (int) this.polygon.getBounds2D().getMinY();
                return coordinates;
        }
        
        public int getX(){
                return (int) this.polygon.getBounds2D().getMinX();
        }
        
        public Bounds getBounds( double ratio ) {
                String[] bounds = new String[4];
                bounds[0] = ( new Float(this.polygon.getBounds2D().getMinX() * ratio) ).toString();
                bounds[1] = ( new Float(this.polygon.getBounds2D().getMinY() * ratio) ).toString();
                bounds[2] = ( new Float(this.polygon.getBounds2D().getMaxX() * ratio) ).toString();
                bounds[3] = ( new Float(this.polygon.getBounds2D().getMaxY() * ratio) ).toString();
                return new Bounds(bounds);
        }

    @Override
    public int compare(PolygonStructure o1, PolygonStructure o2) {
        double o1y = o1.polygon.getBounds().getCenterY();
        double o2y = o2.polygon.getBounds().getCenterY();
        double o1x = o1.polygon.getBounds().getCenterX();
        double o2x = o2.polygon.getBounds().getCenterX();
        double o1Size = o1.polygon.getBounds().getWidth()*o1.polygon.getBounds().getHeight();
        double o2Size = o2.polygon.getBounds().getWidth()*o2.polygon.getBounds().getHeight();
        if(Math.abs(o1y-o2y)>10)
        {
            if(o1y>o2y)
                return (int) -o1y;
            else
                return (int) o2y;
        }
        else if(Math.abs(o1x-o2x)>10)
        {
            if(o1x>o2x)
                return (int) o1x;
            else
                return (int) -o2x;
        }
        else if(o1Size > o2Size)
        {
            return (int) o1Size;
        }
        else if(o1Size == o2Size)
            return 0;
        else
            return (int) -o2Size;
    }
    @Override
    public boolean equals(Object ob) {
        if (this.polygon.getBounds().x + 5 < ((PolygonStructure)ob).polygon.getBounds().x
                || this.polygon.getBounds().x - 5 > ((PolygonStructure)ob).polygon.getBounds().x)
            return true;
        else
            return false;


    }
}