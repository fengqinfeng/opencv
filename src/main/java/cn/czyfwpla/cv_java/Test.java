package cn.czyfwpla.cv_java;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.net.URL;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class Test {

    /**
     * 在项目resource目录的lib目录下放置opencv_java450.dll
     */
    public void doDllInProject() {
        URL url = ClassLoader.getSystemResource("lib/opencv_java450.dll");
        System.load(url.getPath());
        Mat image = imread("data/demo.jpg", 1);
        Mat mat = image.clone();
        Imgproc.Canny(image, mat, 60, 200);
        Imgcodecs.imwrite("data/demo_copy.jpg", mat);
    }

    /**
     * 在系统盘Windows/System32目录下放置opencv_java450.dll
     */
    public void doDllInSystem32() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = imread("data/demo.jpg", 1);
        Mat mat = image.clone();
        Imgproc.Canny(image, mat, 60, 200);
        Imgcodecs.imwrite("data/demo_copy_2.jpg", mat);
    }

    public static void main(String[] args) throws Exception {
        Test test = new Test();
        test.doDllInProject();
        test.doDllInSystem32();
    }

}
