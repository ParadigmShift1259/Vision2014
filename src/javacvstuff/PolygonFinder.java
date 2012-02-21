package javacvstuff;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PolygonFinder {

    public IplImage img;
    private static int thresh = 50;
    private CvMemStorage storage;
    public ArrayList<PolygonStructure> polygons;

    public PolygonFinder(IplImage image) {
        this.img = image.clone();
        if (this.img == null) {
            //System.out.println(image.toString());
        }
        this.storage = cvCreateMemStorage(0);
    }

    public PolygonFinder() {
        this.storage = cvCreateMemStorage(0);
    }

    /**
     * finds cosine of angle between vectors pt0->pt1 and from pt0->pt2
     * @param pt1
     * @param pt2
     * @param pt0
     * @return
     */
    private double angle(CvPoint pt1, CvPoint pt2, CvPoint pt0) {
        double dx1 = pt1.x() - pt0.x();
        double dy1 = pt1.y() - pt0.y();
        double dx2 = pt2.x() - pt0.x();
        double dy2 = pt2.y() - pt0.y();
        double num = (dx1 * dx2 + dy1 * dy2);

        return num
                / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    /**
     * Finds polygons with the specified number of vertices
     * @param n
     */
    public ArrayList<PolygonStructure> findPolygons(int n) {

        IplImage timg = cvCloneImage(this.img); // make a copy of input image
        IplImage gray;
        IplImage tgray = new IplImage(cvCreateImage(cvGetSize(timg), 8, 1));
        IplImage pyr = new IplImage(cvCreateImage(cvSize(img.width() / 2, img.height() / 2), 8, 3));
        this.polygons = new ArrayList<PolygonStructure>();
        //System.out.println("poly1");
        // down-scale and upscale to filter out the noise
        cvPyrDown(timg, pyr, 7);
        cvPyrUp(pyr, timg, 7);
        //CanvasFrame canvas = new CanvasFrame("Testing");
        //frame.showImage(timg);
        // find squares in every color plane of the image
        int[] colours = {0, 1};                         // cam/wand: 0,1,2 fenster: 0,1
        int[] threshs = {25, 50};                       // cam/wand: 25, 50     fenster: 100, 150
        for (int i : colours) {
            if (i == 0) {
                cvCvtColor(timg, tgray, CV_BGR2GRAY);
            } else {
                cvSetImageCOI(timg, i);
                cvCopy(timg, tgray, null);
            }

            // try several threshold levels
            //for (int j : threshs) {
            // Canny helps to catch squares with gradient shading*/
            gray = new IplImage(tgray);
            /*
            if ( j == 0) {
            cvCanny(gray, gray, 0, this.thresh, 3);
            // remove potential holes between edge segments*/
            //cvDilate(gray, gray, null, 1);
            /* } else {
            // possibly several corners
            //cvThreshold( tgray, gray, j, 255, CV_THRESH_BINARY);*/
            //}
            //canvas.showImage(tgray);
            processContours(n, gray);
            // }
            //cvReleaseImage(gray);
        }
//        for (int i = 0; i < this.polygons.size(); i++) {
//            if ((this.polygons.get(i).getPolygon().getBounds().getHeight() * this.polygons.get(i).getPolygon().getBounds().getWidth())
//                    < 1000) {
//                System.out.println("Removed Polygon: " + this.polygons.get(i).getPolygon().getBounds().getHeight() * this.polygons.get(i).getPolygon().getBounds().getWidth());
//                this.polygons.remove(i);
//            }
//        }

        // release all the temporary images
        cvReleaseImage(pyr);
        cvReleaseImage(timg);
        cvReleaseImage(tgray);
        //gray.release();
        //pyr.release();
        //timg.release();
        //tgray.release();

        //System.out.println(this.polygons.size());
        this.filterOuterBorder(n);
        //this.filterRedundancy(n);
        //System.out.println(this.polygons.size());

        // clear memory storage - reset free space position
        cvClearMemStorage(this.storage);
//        PolygonStructure[] polyArray = new PolygonStructure[polygons.size()];
//        polyArray = this.polygons.toArray(polyArray);
        if (!this.polygons.isEmpty()) {
            Collections.sort((List) polygons, polygons.get(0));
        }
//        this.polygons = (ArrayList<PolygonStructure>) Arrays.asList(polyArray);
        return this.polygons;

    }

    /**
     * Collects contours of current filter modi via cvFindCountours
     * checks if they have the right number of edges and are convex
     * @param n
     * @param gray
     */
    public void processContours(int n, IplImage gray) {

        CvSeq contoursPointer = new CvSeq();
        try {

            cvFindContours(gray, this.storage, contoursPointer,
                    Loader.sizeof(CvContour.class), CV_RETR_LIST,
                    CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

            //cvConvexHull2(contoursPointer, this.storage, CV_CLOCKWISE, 0);
            //System.out.println("AHA" + n);

        } catch (Error e1) {
            return;
        }
        CvSeq contours = contoursPointer;
        // test each contour
        int count;
        try {
            count = contours.total();
        } catch (Exception e) {
            //e.printStackTrace();
            return;
        }
        for (int j = 0; j < count; j++) {
            Polygon p = new Polygon();
            CvPoint[] vertices = new CvPoint[4];

            CvPoint pointArray = new CvPoint(contours.total());
            cvCvtSeqToArray(contours, pointArray, CV_WHOLE_SEQ);
            //CvMat pointMatrix = cvMat(1,contours.total(), CV_32SC1, pointArray);
            CvMat hullMatrix = cvCreateMat(1, contours.total(), CV_32SC1);
            cvConvexHull2(contours, hullMatrix, CV_COUNTER_CLOCKWISE, 0);
                Polygon q = new Polygon();

            int l = 0;
            if (hullMatrix != null && !hullMatrix.isNull()) {
                int hullCount = hullMatrix.cols();
                //System.out.println(hullCount);
                //for (int k = 0; k< 5; k++) {
                CvPoint pt = pointArray.position((int) hullMatrix.get(hullCount - 1));
                CvPoint pt0 = new CvPoint(pt.x(), pt.y());
                //p.addPoint(pt.x(),pt.y());
                //ArrayList<CvPoint> pts = new ArrayList<CvPoint>();
                //Polygon q = new Polygon();
                for (int i = 0; i < hullCount; i++) {
                    q.addPoint(pt.x(), pt.y());
                    //System.out.println(j+"\thullCount: "+i + "\t" + (int) hullMatrix.get(i));
                    //System.out.println("x:"+pt0.x()+" y:"+pt0.y());
                    pt = pointArray.position((int) hullMatrix.get(i));
//                    cvLine(img, pt0, pt, CV_RGB(0, 255, i * 255 / 13), 3, CV_AA, 0);
                    pt0 = new CvPoint(pt.x(), pt.y());
                    //angle(pt,pt,pt);
//                        CvPoint p0 = new CvPoint(pointArray.position((int) hullMatrix.get(i)));
//                        CvPoint p1 = new CvPoint(pointArray.position((int) hullMatrix.get((i+1)%hullCount)));
//                        double dpx = p0.x()-p1.x();
//                        double dpy = p0.y()-p1.y();
//                        double dp = dpx*dpx+dpy*dpy;
//                        int k=2;
//                        for(;(i+k)<hullCount&&dp<25;k++)
//                        {
//                            p1 = new CvPoint(pointArray.position((int) hullMatrix.get((i+k)%hullCount)));
//                            dpx = p0.x()-p1.x();
//                            dpy = p0.y()-p1.y();
//                            dp = dpx*dpx+dpy*dpy;
//                        }
//                        pts.add(p0);
//                        i+=k;
                }
                Rectangle2D r = q.getBounds2D();
                double minx = r.getMinX();
                double miny = r.getMinY();
                double maxx = r.getMaxX();
                double maxy = r.getMaxY();
                CvPoint cul = new CvPoint(pointArray.position((int) hullMatrix.get(hullCount - 1)));
                double dulx = (cul.x() - minx);
                double duly = (cul.y() - miny);

                CvPoint cur = new CvPoint(pointArray.position((int) hullMatrix.get(hullCount - 1)));
                double durx = (cur.x() - maxx);
                double dury = (cur.y() - miny);

                CvPoint clr = new CvPoint(pointArray.position((int) hullMatrix.get(hullCount - 1)));
                double dlrx = (clr.x() - maxx);
                double dlry = (clr.y() - maxy);

                CvPoint cll = new CvPoint(pointArray.position((int) hullMatrix.get(hullCount - 1)));
                double dllx = (cll.x() - minx);
                double dlly = (cll.y() - maxy);
                for (int i = 1; i < hullCount; i++) {
                    CvPoint tPoint = new CvPoint(pointArray.position((int) hullMatrix.get(i)));
                    //upper left
                    double dpx = (tPoint.x() - minx);
                    double dpy = (tPoint.y() - miny);
                    double dp = dpx * dpx + dpy * dpy;
                    if (dp < dulx * dulx + duly * duly) {
                        cul = tPoint;
                        dulx = dpx;
                        duly = dpy;
                    }
                    //upper right
                    dpx = (tPoint.x() - maxx);
                    dpy = (tPoint.y() - miny);
                    dp = dpx * dpx + dpy * dpy;
                    if (dp < durx * durx + dury * dury) {
                        cur = tPoint;
                        durx = dpx;
                        dury = dpy;
                    }
                    //lower right
                    dpx = (tPoint.x() - maxx);
                    dpy = (tPoint.y() - maxy);
                    dp = dpx * dpx + dpy * dpy;
                    if (dp < dlrx * dlrx + dlry * dlry) {
                        clr = tPoint;
                        dlrx = dpx;
                        dlry = dpy;
                    }
                    //lower left
                    dpx = (tPoint.x() - minx);
                    dpy = (tPoint.y() - maxy);
                    dp = dpx * dpx + dpy * dpy;
                    if (dp < dllx * dllx + dlly * dlly) {
                        cll = tPoint;
                        dllx = dpx;
                        dlly = dpy;
                    }
                }
                p.addPoint(cul.x(), cul.y());
                vertices[0] = cul;
                p.addPoint(cur.x(), cur.y());
                vertices[1] = cur;
                p.addPoint(clr.x(), clr.y());
                vertices[2] = clr;
                p.addPoint(cll.x(), cll.y());
                vertices[3] = cll;
//                    int ptsCount=pts.size();
//                    for(int i=0; i<ptsCount;i++)
//                    {
//                        double s=0;
//                        do
//                        {
//                            CvPoint p1 = pts.get((i-1+ptsCount)%ptsCount);
//                            CvPoint p2 = pts.get((i+ptsCount)%ptsCount);
//                            CvPoint p3 = pts.get((i+1+ptsCount)%ptsCount);
//                            s = angle(p1,p3,p2);
//                            i++;
//                        } while(s>0.8&&i<ptsCount);
//                        if(l<n&&s<0.8)
//                        {
//                            CvPoint v = pts.get(i-1);
//                            p.addPoint(v.x(), v.y());
//                            System.out.println("\t"+j+"\t"+i+"\t"+l+"\t"+v.x()+" "+v.y());
//                            vertices[l] = v;
//                            l++;
//                        }
//                    }
            }
//            if(l==n)
            if (Math.abs((vertices[1].x() - vertices[0].x()) * (vertices[2].y() - vertices[1].y())) > 200) {
                boolean isInside = false;
//                for (PolygonStructure tempPoly: this.polygons) {
////                    if (tempPoly.getPolygon().getBounds().contains(vertices[0].x(), vertices[0].y())) {
////                        isInside = true;
////                        System.out.println("Inside polygon removed");
////                        break;
////                    }
//                    if (tempPoly.getVertex(0).x()<vertices[0].x()&&tempPoly.getVertex(1).x()>vertices[0].x()
//                            &&tempPoly.getVertex(1).y()<vertices[0].y()&&tempPoly.getVertex(2).y()>vertices[0].y())
//                    {
//                        isInside = true;
//                        System.out.println("Inside polygon removed");
//                        break;
//                    }
//                }
                if(!isInside)
                    this.polygons.add(new PolygonStructure(q, vertices, 4));
            } else {
                //System.out.println("Removed Polygon: " + (vertices[1].x() - vertices[0].x()) * (vertices[2].y() - vertices[1].y()));
            }
            contours = contours.h_next();
            if (contours == null) {
                break;
            }
        }
        /*while (count > 0) {
        //cvConvexHull2(contours, this.storage, CV_CLOCKWISE, 0);
        CvSeq result = cvApproxPoly(contours,
        CV_SEQ_CONTOUR, this.storage, CV_POLY_APPROX_DP,
        cvContourPerimeter(contours) * 0.02, 0);

        // approximation relatively large area (filter out noisy contours)
        if ((result.total() == n)
        && Math.abs(cvContourArea(result, CV_WHOLE_SEQ, 0)) > 1000
        && cvCheckContourConvexity(result) != 0) {

        double s = 0;

        for (int j = 0; j < result.total(); j++) {
        // find minimum angle between joint
        //if (j >= 2) {
        if (true) {
        CvPoint p1 = new CvPoint(cvGetSeqElem(result, j));
        CvPoint p2 = new CvPoint(cvGetSeqElem(result, j - 2));
        CvPoint p3 = new CvPoint(cvGetSeqElem(result, j - 1));
        double t = Math.abs(angle(p1, p2, p3));
        s = s > t ? s : t;
        }
        }
        // if cosines of all angles are small
        // vertices to resultant sequence
        if (s < 0.8) {
        Polygon p = new Polygon();
        CvPoint[] vertices = new CvPoint[n];
        for (int j = 0; j < result.total(); j++) {
        CvPoint v = new CvPoint(cvGetSeqElem(result, j));
        p.addPoint(v.x(), v.y());
        vertices[j] = v;
        }
        this.polygons.add(new PolygonStructure(p, vertices, n));
        }
        }
        // take the next contour
        contours = contours.h_next();
        count--;
        if (contours == null) {
        break;
        }
        }*/
    }

    public void setImage(IplImage image) {
        if (this.img != null) {
            cvReleaseImage(this.img);
        }
        this.img = new IplImage(cvCloneImage(image));
        if (this.img == null) {
            //System.out.println(image.toString());
        }
    }

    /**
     * Open Image in pop up using canvas facility
     */
    /*public void showCanvas(){
    CanvasFrame canvas = new CanvasFrame("pic");
    canvas.showImage( this.img );

    // release both image
    cvReleaseImage(this.img);
    }*/
    /**
     * Removes Polygons at the border of the image (outer frame)
     * @param n
     */
    private void filterOuterBorder(int n) {

        HashSet<PolygonStructure> borders = new HashSet<PolygonStructure>();
        for (int i = 0; i < this.polygons.size(); i++) {
            if (this.polygons.get(i).getPolygon().getBounds2D().getWidth() > this.img.width() / 2) {
                borders.add(this.polygons.get(i));
            }
        }
        this.polygons.removeAll(borders);
    }

    /**
     * removes overlapping polygons that were found due to
     * trying several thresholds and colors
     * @param n
     */
    private void filterRedundancy(int n) {
        // remove rectangle at the border of the image
        HashSet<PolygonStructure> duplicates = new HashSet<PolygonStructure>();
        for (int i = 0; i < this.polygons.size() - 1; i++) {
            duplicates = new HashSet<PolygonStructure>();
            // check for all remaining polygons if they include one of the vertices
            // or the center of the i-th polygon
            for (int j = i + 1; j < this.polygons.size(); j++) {

                if (this.polygons.get(i).getPolygon().intersects(this.polygons.get(j).getPolygon().getBounds2D())) {
                    duplicates.add(this.polygons.get(j));
                }
            }
            // remove duplicates from list of polygons and from list of polygon vertices
            this.polygons.removeAll(duplicates);

        }

    }
}
