package cn.czyfwpla.mytest;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class CVideoTest {
    private final static String OPENCV_DLL_PATH = "D:/学习/opencv/OpenCV_4_1_1/build/java/x64/opencv_java411.dll";

    public static void main(String[] args) {

        VideoCapture capture = new VideoCapture("D:/视频/信息集成负载均衡.mp4");//里面放图片的地址
        System.load(OPENCV_DLL_PATH);
        //ps:如果里面为0[不要"",就是整数0],则就对默认摄像头进行抓取
        Mat mat = new Mat();
        //读取下一帧
        capture.read(mat);//返回值为boolean类型

        Mat img_mat = new Mat();
        //mat表示要要转换的图片[Mat类型],img_mat表示转换后的图片
        Imgproc.cvtColor(mat, img_mat, Imgproc.COLOR_RGB2GRAY);


        String ascii = "#8XOHLTI)i=+;:,. ";//字符串由复杂到简单

        StringBuilder result = new StringBuilder();
        //使用for循环获得图像每一个地方的灰度值
        for (int i = 0; i < img_mat.rows(); i += 14) {
            //i+14 j+14 每隔14取一个点 是防止视频过大,产生的result过长,整个屏幕无法显示完全
            for (int j = 0; j < img_mat.cols(); j += 14) {
                //StringBuilder result2 = new StringBuilder();
                int gray = (int) img_mat.get(i, j)[0];
                //Math.round进行四舍五入
                int index = Math.round(gray * (ascii.length() + 1) / 255);
                result.append(index >= ascii.length() ? "." : String.valueOf(ascii.charAt(index)));
            }
            result = result.append("\n");
        }

        //Ubuntu 进行清屏操作
        System.out.print("\033c");
        System.out.println(result);


    }
}
