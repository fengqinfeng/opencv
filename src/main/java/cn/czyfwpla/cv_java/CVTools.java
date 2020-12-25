package cn.czyfwpla.cv_java;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//opencv-java工具包
public class CVTools {


    private volatile static CVTools tools;

    private CVTools() {
    }

    //单例模式
    public static CVTools getTools() {

        if (tools == null) {
            synchronized (CVTools.class) {

                if (tools == null) {
                    //初始化opencv环境
                    if (System.getProperty("os.name").contains("Windows")) {
                        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                    } else {
                        System.load(System.getProperty("java.library.path") + "/libopencv_java450.so");
                    }
                    tools = new CVTools();
                }
            }
        }
        return tools;
    }

    //查找轮廓
    public void findCounters(String imgPath) {

        Mat srcImg = Imgcodecs.imread(imgPath);

        Mat bitWiseNotMat = new Mat();
        //颜色取反
        Core.bitwise_not(srcImg, bitWiseNotMat);

        //转为灰度图
        Mat grayMat = new Mat();
        Imgproc.cvtColor(bitWiseNotMat, grayMat, Imgproc.COLOR_BGR2GRAY, 0);

        //腐蚀后膨胀，去除周边的黑色小轮廓
        //腐蚀卷积核
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));
        Mat erodeMat = new Mat();
        Imgproc.dilate(grayMat, erodeMat, element);
        //膨胀卷积核
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 8));
        Mat dilateMat = new Mat();
        Imgproc.dilate(grayMat, dilateMat, element2);


        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Mat hierarchy = new Mat();
        //查找轮廓
        Imgproc.findContours(dilateMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        //将所有的轮廓画出来，-1表示画出所有的轮廓,contours是轮廓的集合，每个MatOfPoint都是轮廓上的点
        Imgproc.drawContours(srcImg, contours, -1, new Scalar(0, 255, 0), 3);

        //保存结果
        Imgcodecs.imwrite("data/cnt/result.jpg", srcImg);
    }

    //去除四周留白：扫描思路，以从上向下扫描为例，扫描到某一列像素包含有像素0即停止扫描
    public void removeEdgeBlank(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath, 0);
        Mat dstImg = new Mat();
        //黑色和白色颜色取反
        Core.bitwise_not(srcImg, dstImg);

        //扫描有图案部分与上下空白的分割线
        Boolean flag = true;
        int top = 0, bot = 0;
        for (int i = 0; i < dstImg.height(); i++) {
            int rowsPx = calcRowsPx(i, dstImg);
            if (flag) {
                if (rowsPx > 0) {
                    top = i;
                    flag = false;
                }
            } else {

                if (rowsPx == 0) {
                    bot = i;
                    break;
                }
            }
        }
        System.out.println("上边空白与图案部分的分隔线：" + top);
        System.out.println("上边空白与图案部分的分隔线：" + bot);

        //提取图案部分
        Rect rect = new Rect(0, top, dstImg.width(), bot - top);
        Mat result = new Mat(srcImg, rect);
        Imgcodecs.imwrite("data/roi/result.jpg", result);

    }


    //计算一行像素和
    public int calcRowsPx(int rowId, Mat mat) {
        int width = mat.width();
        int rowsPx = 0;
        for (int i = 0; i < width; i++) {
            rowsPx += (int) mat.get(rowId, i)[0];
        }
        return rowsPx;
    }

    //均值滤波
    public void meanBlur(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath);
        Mat dstImg = new Mat();
        Imgcodecs.imwrite("data/blur/mean/01.jpg", srcImg);
        Imgproc.blur(srcImg, dstImg, new Size(5, 5));

        Imgcodecs.imwrite("data/blur/mean/02.jpg", dstImg);

    }

    //高斯双边滤波
    public void bilateralFilterBlur(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath);
        Mat dstImg = new Mat();
        Imgcodecs.imwrite("data/blur/bilateralFilterBlur/01.jpg", srcImg);
        /**
         d：像素的邻域直径，可有 sigmaColor 和 sigmaSpace 计算可得；
         sigmaColor：颜色空间的标准方差，一般尽可能大；
         sigmaSpace：坐标空间的标准方差(像素单位)，一般尽可能小。
         */
        Imgproc.bilateralFilter(srcImg, dstImg, 0, 300, 5);
        Imgcodecs.imwrite("data/blur/bilateralFilterBlur/02.jpg", dstImg);

    }

    //高斯滤波
    public void guassianBlur(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath);
        Mat dstImg = new Mat();
        Imgcodecs.imwrite("data/blur/guassianBlur/01.jpg", srcImg);
        Imgproc.GaussianBlur(srcImg, dstImg, new Size(5, 5), 0);
        Imgcodecs.imwrite("data/blur/guassianBlur/02.jpg", dstImg);

    }

    //中值滤波
    public void medianBlur(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath);
        Mat dstImg = new Mat();
        Imgcodecs.imwrite("data/blur/medianBlur/01.jpg", srcImg);
        Imgproc.medianBlur(srcImg, dstImg, 5);
        Imgcodecs.imwrite("data/blur/medianBlur/02.jpg", dstImg);

    }

    //开运算：先腐蚀后膨胀
    public void openCalc(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath, 0);
        Mat thresh = new Mat();
        //二值化
        Imgproc.adaptiveThreshold(
                srcImg,
                thresh,
                255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY,
                11,
                2);
        //卷积核
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Mat rsImg = new Mat();

        Imgcodecs.imwrite("data/open_close_calc/01thresh.jpg", thresh);
        //dilate原本是膨胀，可能是由于我选的版本太新，这里变为了腐蚀操作
        Imgproc.dilate(thresh, rsImg, element);
        Imgcodecs.imwrite("data/open_close_calc/02erode_img.jpg", rsImg);
        //同样的，erode原本是腐蚀操作，但实际效果确实膨胀操作
        Imgproc.erode(rsImg, rsImg, element);

        Imgcodecs.imwrite("data/open_close_calc/03dilate_img.jpg", rsImg);
    }

    //直方图阈值
    public void ostuThreshImg(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath, 0);
        Mat thresh1 = new Mat();
        Imgproc.threshold(srcImg, thresh1, 0, 255, Imgproc.THRESH_OTSU);
        Imgcodecs.imwrite("data/thresh1/thresh3.jpg", thresh1);
    }

    //自适应阈值
    public void adaptThreshImg(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath, 0);
        Mat thresh1 = new Mat();
        Mat thresh2 = new Mat();

        Imgproc.adaptiveThreshold(
                srcImg,
                thresh1,
                255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY,
                11,
                2);
        Imgproc.adaptiveThreshold(
                srcImg,
                thresh2,
                255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY,
                11,
                2);
        Imgcodecs.imwrite("data/thresh1/thresh1.jpg", thresh1);
        Imgcodecs.imwrite("data/thresh1/thresh2.jpg", thresh2);

    }

    //二值化
    public void thresholdImg(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath, 0);
        Mat thresh1 = new Mat();
        Mat thresh2 = new Mat();
        Mat thresh3 = new Mat();
        Mat thresh4 = new Mat();
        Mat thresh5 = new Mat();
        //第一种，当像素超过 127 时，将该像素变为 255
        Imgproc.threshold(srcImg, thresh1, 127, 255, Imgproc.THRESH_BINARY);
        //在第一种的基础上，将二值进行反转
        Imgproc.threshold(srcImg, thresh2, 127, 255, Imgproc.THRESH_BINARY_INV);
        //当像素超过 127 时，将该像素设置为阀值，我们这里所选的阀值为 127
        Imgproc.threshold(srcImg, thresh3, 127, 255, Imgproc.THRESH_TRUNC);
        //当像素超过阀值时，保持原来的像素点不变，小于阀值的像素点取值为 0
        Imgproc.threshold(srcImg, thresh4, 127, 255, Imgproc.THRESH_TOZERO);
        //当像素超过阀值时，像素为 0，小于阀值的像素点取值为原始像素
        Imgproc.threshold(srcImg, thresh5, 127, 255, Imgproc.THRESH_TOZERO_INV);
        Imgcodecs.imwrite("data/thresh/thresh1.jpg", thresh1);
        Imgcodecs.imwrite("data/thresh/thresh2.jpg", thresh2);
        Imgcodecs.imwrite("data/thresh/thresh3.jpg", thresh3);
        Imgcodecs.imwrite("data/thresh/thresh4.jpg", thresh4);
        Imgcodecs.imwrite("data/thresh/thresh5.jpg", thresh5);

    }

    //读取图像转换hsv，并提取红色部分
    public void readHsvAndTakeRed(String imgPath) {
        Mat srcImg = Imgcodecs.imread(imgPath);
        Mat hsvImg = new Mat();
        Imgproc.cvtColor(srcImg, hsvImg, Imgproc.COLOR_BGR2HSV);

        Scalar min = new Scalar(0, 43, 46);
        Scalar max = new Scalar(10, 255, 255);
        Mat mask = new Mat();

        Core.inRange(hsvImg, min, max, mask);
        Mat resImg = new Mat();
        Core.bitwise_and(srcImg, srcImg, resImg, mask);
        Imgcodecs.imwrite("data/hsv/hsv_red_img.jpg", resImg);
    }


    //读取图像为灰度图
    public void readGrayImg(String imgPath) {
        Mat imread = Imgcodecs.imread(imgPath, 0);
        HighGui.imshow("gray_img", imread);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
    }

    //读取图像后再转换为灰度图
    public void readAndconvertGray(String imgPath) {
        Mat imread = Imgcodecs.imread(imgPath);
        Mat dstMat = new Mat();
        Imgproc.cvtColor(imread, dstMat, Imgproc.COLOR_BGR2GRAY, 0);
        HighGui.imshow("gray_img", dstMat);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
    }

    //读取图像并显示
    public void readImageAndShow(String imgPath, String winName) {
        Mat imread = Imgcodecs.imread(imgPath);
        HighGui.imshow(winName, imread);
        HighGui.waitKey();
        HighGui.destroyAllWindows();
    }

    //读取视频并显示
    public void readVideoAndShow(String videoPath) {

        VideoCapture cap = new VideoCapture();
        cap.open(videoPath);
        if (!cap.isOpened()) {
            System.out.println("could not load video data...");
            return;
        }
        //显示图像
        Mat frame = new Mat();
        while (true) {
            boolean read = cap.read(frame);
            if (!read) break;
            if (!frame.empty()) {
                HighGui.imshow("video", frame);
                HighGui.waitKey(25);
            }
        }
        HighGui.destroyAllWindows();

    }


}
