package javacvstuff;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public interface ShapeStructure {

        void drawShape(IplImage img, @ByVal CvScalar color);
        
        int[] getCoordinates();
        
        int getX();
        
        Bounds getBounds(double ratio);
}