package cn.czyfwpla.cv_java;

public class CVClient {


    public static void main(String[] args) {
        CVTools tools = CVTools.getTools();
        tools.openCalc("data/demo.jpg");
    }
}
