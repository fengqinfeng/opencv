# opencv-java项目说明
该工程是一个如何在java环境使用opencv，opencv版本为4.5.0。
## 安装依赖包
依赖包已经放在resources/lib目录，安装命令
```bash
mvn install:install-file -DgroupId=org.opencv -DartifactId=opencv -Dversion=4.5.0  -Dpackaging=jar -Dfile=opencv-450.jar
```


## dll或so库
两个库都已经生成并放在resources/lib目录
+ dll库运行在windows环境  
+ so库运行在linux环境