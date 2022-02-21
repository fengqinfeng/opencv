package cn.czyfwpla.cv_java;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class CrossPlatformTest {
    private final static String OPENCV_DLL_PATH = "D:/学习/opencv/OpenCV_4_1_1/build/java/x64/opencv_java411.dll";

    public static void main(String[] args) {
        //测试图像，需要保证该路径下一定有图像
        String srcImgPath = "D:/bizhi/Stealth_1920x1080.jpg";
        //结果生成路径
        String dstImgPath = "D:/bizhi/re.jpg";
        File file = new File(dstImgPath);
        //输出是结果图是否已经存在
        System.out.println("================>" + (file.exists() ? "result.jpg exist" : "result.jpg not exist"));
        //根据操作系统类型 选择对应的加载环境变量
        if (System.getProperty("os.name").contains("Windows")) {
            //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.load(OPENCV_DLL_PATH);
        } else {
//            System.load(System.getProperty("java.library.path") + "/libopencv_java450.so");
            //System.load(OPENCV_DLL_PATH);
        }
        //读取原图
        Mat image = imread(srcImgPath, 1);

        //进行一个简单的opencv边缘检测处理
        Mat mat = image.clone();
        Imgproc.Canny(image, mat, 60, 200);
        Imgcodecs.imwrite(dstImgPath, mat);
        //检查图片是否生成
        System.out.println("================>" + (file.exists() ? "result.jpg exist" : "result.jpg not exist"));
    }

}
