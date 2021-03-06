package edu.wpi.first.wpilibj.paradigm;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.FrameGrabber;

import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.CanvasFrame;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class CaptureImage {

    public static void captureFrame() {
        //---------These objects allow us to edit the variables used in the scalar polygon recognition------
        JLabel blueMaxValueLabel = new JLabel("Max Blue Value");
        JTextField blueMaxValueField = new JTextField("255.0", 5);

        JLabel blueMinValueLabel = new JLabel("Min Blue Value");
        JTextField blueMinValueField = new JTextField("100.0", 5);

        JLabel greenMaxValueLabel = new JLabel("Max Green Value");
        JTextField greenMaxValueField = new JTextField("255.0", 5);

        JLabel greenMinValueLabel = new JLabel("Min Green Value");
        JTextField greenMinValueField = new JTextField("215.0", 5);

        JLabel redMaxValueLabel = new JLabel("Max Red Value");
        JTextField redMaxValueField = new JTextField("45.0", 5);

        JLabel redMinValueLabel = new JLabel("Min Red Value");
        JTextField redMinValueField = new JTextField("0.0", 5);

        JLabel yLabel = new JLabel("Y");
        JTextField yValue = new JTextField(5);
        boolean hotZone = false;

        //---------------------------------End object lists-------------------------------------
        String source = "http://FRC:FRC@10.12.59.11/mjpg/video.mjpg";
        FrameGrabber grabber = new OpenCVFrameGrabber(source);//new VideoInputFrameGrabber(0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CaptureImage.class.getName()).log(Level.SEVERE, null, ex);
        }

        CanvasFrame canvas = new CanvasFrame("WebCam");
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        CanvasFrame before = new CanvasFrame("before");
        before.setDefaultCloseOperation(javax.swing.JFrame.HIDE_ON_CLOSE);
        GridBagLayout gridBag = new GridBagLayout();
        before.setLayout(gridBag);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gridBag.setConstraints(before.getComponent(0), gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 0;
        before.add(blueMaxValueLabel, gbc);
        gbc.gridx = 2;
        before.add(blueMaxValueField, gbc);
        gbc.gridx = 3;
        before.add(blueMinValueLabel, gbc);
        gbc.gridx = 4;
        before.add(blueMinValueField, gbc);
        //before.add(blueMinValueField);
        gbc.gridx = 1;
        gbc.gridy = 2;
        before.add(greenMaxValueLabel, gbc);
        gbc.gridx = 2;
        before.add(greenMaxValueField, gbc);
        gbc.gridx = 3;
        before.add(greenMinValueLabel, gbc);
        gbc.gridx = 4;
        before.add(greenMinValueField, gbc);
        //before.add(greenMinValueField);
        gbc.gridx = 1;
        gbc.gridy = 3;
        before.add(redMaxValueLabel, gbc);
        gbc.gridx = 2;
        before.add(redMaxValueField, gbc);
        gbc.gridx = 3;
        before.add(redMinValueLabel, gbc);
        gbc.gridx = 4;
        before.add(redMinValueField, gbc);
        before.add(yLabel);
        before.add(yValue);
        //before.add(redMinValueField);

        int numOfScreens = 39;
//        String loadingPic = "C:\\loadingScreen\\loadingScreen"
//                + (((int) (Math.random() * numOfScreens)) + 1) + ".bmp";
        int failedGrabs = 0;
        NetworkTable.setTeam(1259); //Capture image requires the setTeam method because it does not have use Smartdashboard which already assigns a team number in the from of a network table
        NetworkTable.setIPAddress("10.12.59.2");
        NetworkTable.setClientMode();
        NetworkTable.getTable("SmartDashboard").putBoolean("TestTable", hotZone);
        while (true) {
            try {
//                try {
//                    IplImage splashScreen = new IplImage(cvLoadImage(loadingPic));
//                    canvas.showImage(splashScreen);
//                } catch (Exception e) {
//                }
                grabber.start();
                IplImage img;
                //IplImage hsv;
                //IplImage canny;
                IplImage displayImg;
                IplImage dst;
                PolygonFinder polyFind = new PolygonFinder();

//                try {
//                    NetworkTable.getTable("camera").putNumber("distance", 0);
//                    NetworkTable.getTable("camera").putBoolean("hotZone", false);
//                } catch (Exception e) {
//                }

                while (true) {

                    while (true) {
                        try {
                            //System.out.println("grabbing...");
                            img = new IplImage(grabber.grab());
                            displayImg = new IplImage(cvCreateImage(img.cvSize(), img.depth(), img.nChannels()));
                            cvCopy(img, displayImg, null);
                            //System.out.println("Frame GRABBED!");
                            break;
                        } catch (Exception e) {
                            failedGrabs++;
                            System.out.println(failedGrabs);
                            if (failedGrabs > 10) {
                                grabber = new OpenCVFrameGrabber(source);
                                grabber.start();
                                failedGrabs = 0;
                            }
                            continue;
                        }
                    }

//            PolygonFinder polyFind = new PolygonFinder(img);
//            ArrayList<PolygonStructure> polygons = polyFind.findPolygons(4);
//            for (int i = 0; i < polygons.size(); i++) {
//                polygons.get(i).drawShape(img, CvScalar.MAGENTA);
//                }
//            canvas.showImage(img);
//            return;
                    if (img != null) {
                        //cvSaveImage("originalcapture.jpg", img);
                    }
                    //IplImage gray = cvCreateImage(cvGetSize(img), img.depth(), 1 );
                    //hsv = new IplImage(cvCreateImage(cvGetSize(img), img.depth(), 3));
                    dst = new IplImage(cvCreateImage(cvGetSize(img), img.depth(), 1));
                    //cvCvtColor(img, gray, CV_RGB2GRAY);

                    //cvCvtColor(img, hsv, CV_BGR2HSV);
//            if(hsv != null) {
//                cvSaveImage("hsv.jpg", hsv);
//            }
                    //30 20 0; 70 140 60
                    // 50 175 75 //// 100 255 225
                    //cvInRangeS(hsv, cvScalar(0, 200, 0, 0), cvScalar(150, 255, 255, 0), dst);
//cvDrawLine(img, new CvPoint(0, 360), new CvPoint(639, 360), CvScalar.BLACK, 240, 8, 0);
                    //cvInRangeS(img, cvScalar(100, 215, 0, 0), cvScalar(255, 255, 45, 0), dst); This is the original
                    //Code used to set max and min values for bgr scale in scalars VVV
                    cvInRangeS(img, cvScalar((new Double(blueMinValueField.getText())).doubleValue(),
                            (new Double(greenMinValueField.getText())).doubleValue(),
                            (new Double(redMinValueField.getText())).doubleValue(), 0),
                            cvScalar((new Double(blueMaxValueField.getText())).doubleValue(),
                                    (new Double(greenMaxValueField.getText())).doubleValue(),
                                    (new Double(redMaxValueField.getText())).doubleValue(), 0), dst);
                    //NEED TO FLIP MAX IN MIN POSITION, MIN IS IN MAX POSITION

                    //cvInRangeS(img, cvScalar(0, 0, 0, 0), cvScalar(255, 255, 255, 0), dst);
                    //cvDilate( dst, dst, null, 1 );
                    cvSmooth(dst, dst, CV_MEDIAN, 1, 1, 0, 0);
                    //cvCanny(gray, dst, 50, 100, 3);
                    //cvCvtColor(hsv, gray, CV_RGB2GRAY);
                    //canvas.showImage(img);
                    //before.showImage(dst);
                    IplImage newDst = new IplImage(cvCloneImage(img));//cvCreateImage(cvGetSize(img), img.depth(), 3));
                    cvCvtColor(dst, newDst, CV_GRAY2BGR);
                    //cvConvexHull2(newDst, null, CV_CLOCKWISE, 0);
                    before.showImage(newDst);
                    polyFind.setImage(newDst);
                    ArrayList<PolygonStructure> polygons = new ArrayList<PolygonStructure>();
                    int i;
                    //before.showImage(newDst);
                    for (i = 4; i < 5; i++) {
                        for (PolygonStructure ps : polyFind.findPolygons(i)) {
                            polygons.add(ps);
                        }
                    }
                    for (int c = 0; c < polygons.size(); c++) {
                        for (int d = 0; d < polygons.size(); d++) {
                            if (c == d) {
                                break;
                            }
                            if (polygons.get(c).getPolygon().contains(polygons.get(d).getPolygon().getBounds2D())) {
                                polygons.remove(d);
                                if (d < c) {
                                    c--;
                                }
                                d--;
                            }
                        }
                    }
                    for (int c = 0; c < polygons.size(); c++) {
                        for (int d = 0; d < polygons.size(); d++) {
                            if (c == d) {
                                break;
                            }
                            if (polygons.get(c).getPolygon().contains(polygons.get(d).getPolygon().getBounds().getCenterX(), polygons.get(d).getPolygon().getBounds().getCenterY())) {
                                polygons.remove(d);
                                if (d < c) {
                                    c--;
                                }
                                d--;
                            }
                        }
                    }
//                    if (tempPoly.getVertex(0).x()<vertices[0].x()&&tempPoly.getVertex(1).x()>vertices[0].x()
//                            &&tempPoly.getVertex(1).y()<vertices[0].y()&&tempPoly.getVertex(2).y()>vertices[0].y())
//                    {
//                        isInside = true;
//                        break;
//                    }
                    for (i = 0; i < polygons.size(); i++) {
                        if (polygons.size() < 2) {
                            hotZone = false;
                        } else {
                            hotZone = true;
                        }
                        CvScalar polyColor = CvScalar.MAGENTA;
                        switch (i) {
                            case 0: {
                                polyColor = CvScalar.RED;
//                            System.out.println("Center X: "
//                                    + (320 - ((polygons.get(i).getVertex(3).x() + polygons.get(i).getVertex(2).x()) / 2))
//                                    + "\tCenter Y: "
//                                    + (240 - ((polygons.get(i).getVertex(3).y() + (polygons.get(i).getVertex(2).y())) / 2)));
                                double x = (320 - ((polygons.get(i).getVertex(3).x() + polygons.get(i).getVertex(2).x()) / 2));
                                double angle = (480 - ((polygons.get(i).getVertex(3).y() + (polygons.get(i).getVertex(2).y())) / 2));
                                //double distance = 5182.2043151825 * Math.pow(angle, -1);

                                double distance = 514.7318 * Math.pow(angle - 220, -1.2);
                                //double distance;
                                if (angle < 317) {
                                    distance = -0.370786516853933 * angle + 133.977528089888;
                                } else if (angle > 316 && angle < 325) {
                                    distance = -0.184697808038669 * angle + 74.9375184489134;
                                } else if (angle > 324 && angle < 362) {
                                    distance = -0.140145789191636 * angle + 60.5198480748917;
                                } else if (angle > 360) {
                                    distance = -0.0702258215380136 * angle + 35.3150512441271;
                                }

                                for (int c = 0; c < 4; c++) {
                                    System.out.println("Vertex " + c + ":" + polygons.get(i).getVertex(c).y());
                                }
                                yValue.setText("" + angle);

                                //hotZone = true;

                                break;
                            }
                            case 1: {
                                //Hot zone
                                polyColor = CvScalar.BLUE;
                                break;
                            }
                            case 2: {
                                polyColor = CvScalar.GREEN;
                                break;
                            }
                            case 3: {
                                polyColor = CvScalar.YELLOW;
                                break;
                            }
                        }
                        //polygons.get(i).drawShape(img, polyColor);
                        cvLine(displayImg, polygons.get(i).getVertices()[3], polygons.get(i).getVertices()[0], polyColor, 3, CV_AA, 0);
                        cvLine(displayImg, polygons.get(i).getVertices()[0], polygons.get(i).getVertices()[1], polyColor, 3, CV_AA, 0);
                        cvDrawCircle(displayImg, polygons.get(i).getVertices()[0], 3, CvScalar.GRAY, 1, 8, 0);
                        cvLine(displayImg, polygons.get(i).getVertices()[1], polygons.get(i).getVertices()[2], polyColor, 3, CV_AA, 0);
                        cvDrawCircle(displayImg, polygons.get(i).getVertices()[1], 3, CvScalar.MAGENTA, 1, 8, 0);
                        cvLine(displayImg, polygons.get(i).getVertices()[2], polygons.get(i).getVertices()[3], polyColor, 3, CV_AA, 0);
                        cvDrawCircle(displayImg, polygons.get(i).getVertices()[2], 3, CvScalar.BLACK, 1, 8, 0);
                        cvDrawCircle(displayImg, polygons.get(i).getVertices()[3], 3, CvScalar.CYAN, 1, 8, 0);
                        //System.out.println("Polygon " + i + "\t" + polygons.get(i).getVertices()[0]);
                    }

                    try {
                        //NetworkTable.getTable("camera").beginTransaction();
//                        NetworkTable.getTable("camera").putNumber("distance", distance);
//                        NetworkTable.getTable("camera").putNumber("x", x);
                        NetworkTable.getTable("camera").putBoolean("hotZone", hotZone);
                            //NetworkTable.getTable("camera").endTransaction();

                    } catch (Exception e) {
                    }

                    if (displayImg != null) {
                        //cvSaveImage("aftercapture.jpg", dst);
                        cvDrawLine(displayImg, new CvPoint(300, 240), new CvPoint(340, 240), CvScalar.WHITE, 2, 8, 0);
                        cvDrawLine(displayImg, new CvPoint(320, 220), new CvPoint(320, 260), CvScalar.WHITE, 2, 8, 0);
                        canvas.showImage(displayImg);
                    } else {
                        //System.out.println("Null Image");
                    }

                    //cvReleaseImage(gray);
                    cvReleaseImage(newDst);
                    //cvReleaseImage(img);
                    //cvReleaseImage(hsv);
                    cvReleaseImage(displayImg);
                    cvReleaseImage(dst);
                    //Thread.sleep(50);
                }
                //System.out.println("5");
                //grabber.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            grabber = new OpenCVFrameGrabber(source);
        }
    }

    public static void main(String[] args) {
        captureFrame();
    }
}
