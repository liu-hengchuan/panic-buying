@echo off
echo 正在手动下载Maven依赖...

REM 设置Maven路径
set MAVEN_HOME=C:\Program Files\Apache Software Foundation\apache-maven-3.8.4
set PATH=%MAVEN_HOME%\bin;%PATH%

REM 进入项目目录
cd /d "C:\Users\Administrator\Documents\trae_projects\buying3"

REM 清理旧的依赖
mvn clean dependency:copy-dependencies -DoutputDirectory=lib -DskipTests

echo 依赖下载完成！
pause