package javacvstuff;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.FrameGrabber;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.CanvasFrame;

public class CaptureImage_1 {

    private static void captureFrame() {
        // 0-default camera, 1 - next...so on
        final FrameGrabber grabber = new OpenCVFrameGrabber("http://10.12.59.11/mjpg/video.mjpg?resolution=640x480&req_fps=15&.mjpg");//new VideoInputFrameGrabber(0);
        System.out.println("1");
//        IplImage gray = null;
        try {
            grabber.start();
            System.out.println("2");
            CanvasFrame canvas = new CanvasFrame("WebCam");
            canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            IplImage img;
            IplImage hsv;
            IplImage dst;
            
            while(true) {
                try {
                    System.out.println("grabbing...");
                    img = grabber.grab();
                } catch (Exception e) {
                    continue;
                }
            System.out.println("2.1");
            if (img != null) {
                //cvSaveImage("originalcapture.jpg", img);
            }
            //IplImage gray = cvCreateImage(cvGetSize(img), img.depth(), 1 );
            hsv = cvCreateImage(cvGetSize(img), img.depth(), 3);
            dst = cvCreateImage(cvGetSize(img), img.depth(), 1);
            //cvCvtColor(img, gray, CV_RGB2GRAY);
            System.out.println("3");

            cvCvtColor(img, hsv, CV_BGR2HSV);
//            if(hsv != null) {
//                cvSaveImage("hsv.jpg", hsv);
//            }
            //30 20 0; 70 140 60
            // 50 175 75 //// 100 255 225
            cvInRangeS(hsv, cvScalar(50, 175, 75, 0), cvScalar(100, 255, 225, 0), dst);
            //cvCanny(gray, dst, 50, 100, 3);
            //cvCvtColor(hsv, gray, CV_RGB2GRAY);

            System.out.println(img.depth());
            System.out.println("4");
            if (dst != null) {
                //cvSaveImage("aftercapture.jpg", dst);
                canvas.showImage(img);
            }
            
            //cvReleaseImage(gray);
            //cvReleaseImage(hsv);
            cvReleaseImage(dst);
            //Thread.sleep(50);
            }
            //System.out.println("5");
            //grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("6");
    }

    public static void NotReallymain(String[] args) {
        captureFrame();
    }
}