@ECHO OFF
SETLOCAL

set DIR=%~dp0
IF "%DIR%" == "" SET DIR=.

set APP_HOME=%DIR%

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

set JAVA_EXE=java.exe
IF NOT "%JAVA_HOME%" == "" set JAVA_EXE=%JAVA_HOME%\bin\java.exe

"%JAVA_EXE%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
