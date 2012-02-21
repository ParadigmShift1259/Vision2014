package extension;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;

public class CameraTracking extends StaticWidget {
    @Override
    public void propertyChanged(Property property) {
        
    }
    
    @Override
    public void init() {
        javacvstuff.CaptureImage capture = new javacvstuff.CaptureImage();
        capture.captureFrame();
    }
        
    //}

}
