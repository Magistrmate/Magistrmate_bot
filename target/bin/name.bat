@REM ----------------------------------------------------------------------------
@REM Copyright 2001-2004 The Apache Software Foundation.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM ----------------------------------------------------------------------------
@REM

@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:repoSetup


if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%BASEDIR%\repo

set CLASSPATH="%BASEDIR%"\etc;"%REPO%"\org\telegram\telegrambots\6.1.0\telegrambots-6.1.0.jar;"%REPO%"\org\telegram\telegrambots-meta\6.1.0\telegrambots-meta-6.1.0.jar;"%REPO%"\com\google\guava\guava\31.1-jre\guava-31.1-jre.jar;"%REPO%"\com\google\guava\failureaccess\1.0.1\failureaccess-1.0.1.jar;"%REPO%"\com\google\guava\listenablefuture\9999.0-empty-to-avoid-conflict-with-guava\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;"%REPO%"\com\google\code\findbugs\jsr305\3.0.2\jsr305-3.0.2.jar;"%REPO%"\org\checkerframework\checker-qual\3.12.0\checker-qual-3.12.0.jar;"%REPO%"\com\google\errorprone\error_prone_annotations\2.11.0\error_prone_annotations-2.11.0.jar;"%REPO%"\com\google\j2objc\j2objc-annotations\1.3\j2objc-annotations-1.3.jar;"%REPO%"\com\fasterxml\jackson\core\jackson-annotations\2.13.2\jackson-annotations-2.13.2.jar;"%REPO%"\com\fasterxml\jackson\jaxrs\jackson-jaxrs-json-provider\2.13.2\jackson-jaxrs-json-provider-2.13.2.jar;"%REPO%"\com\fasterxml\jackson\jaxrs\jackson-jaxrs-base\2.13.2\jackson-jaxrs-base-2.13.2.jar;"%REPO%"\com\fasterxml\jackson\module\jackson-module-jaxb-annotations\2.13.2\jackson-module-jaxb-annotations-2.13.2.jar;"%REPO%"\com\fasterxml\jackson\core\jackson-core\2.13.2\jackson-core-2.13.2.jar;"%REPO%"\jakarta\xml\bind\jakarta.xml.bind-api\2.3.3\jakarta.xml.bind-api-2.3.3.jar;"%REPO%"\com\fasterxml\jackson\core\jackson-databind\2.13.2.2\jackson-databind-2.13.2.2.jar;"%REPO%"\org\glassfish\jersey\inject\jersey-hk2\2.35\jersey-hk2-2.35.jar;"%REPO%"\org\glassfish\jersey\core\jersey-common\2.35\jersey-common-2.35.jar;"%REPO%"\org\glassfish\hk2\osgi-resource-locator\1.0.3\osgi-resource-locator-1.0.3.jar;"%REPO%"\org\glassfish\hk2\hk2-locator\2.6.1\hk2-locator-2.6.1.jar;"%REPO%"\org\glassfish\hk2\external\aopalliance-repackaged\2.6.1\aopalliance-repackaged-2.6.1.jar;"%REPO%"\org\glassfish\hk2\hk2-api\2.6.1\hk2-api-2.6.1.jar;"%REPO%"\org\glassfish\hk2\hk2-utils\2.6.1\hk2-utils-2.6.1.jar;"%REPO%"\org\javassist\javassist\3.25.0-GA\javassist-3.25.0-GA.jar;"%REPO%"\org\glassfish\jersey\media\jersey-media-json-jackson\2.35\jersey-media-json-jackson-2.35.jar;"%REPO%"\org\glassfish\jersey\ext\jersey-entity-filtering\2.35\jersey-entity-filtering-2.35.jar;"%REPO%"\org\glassfish\jersey\containers\jersey-container-grizzly2-http\2.35\jersey-container-grizzly2-http-2.35.jar;"%REPO%"\org\glassfish\hk2\external\jakarta.inject\2.6.1\jakarta.inject-2.6.1.jar;"%REPO%"\org\glassfish\grizzly\grizzly-http-server\2.4.4\grizzly-http-server-2.4.4.jar;"%REPO%"\org\glassfish\grizzly\grizzly-http\2.4.4\grizzly-http-2.4.4.jar;"%REPO%"\org\glassfish\grizzly\grizzly-framework\2.4.4\grizzly-framework-2.4.4.jar;"%REPO%"\jakarta\ws\rs\jakarta.ws.rs-api\2.1.6\jakarta.ws.rs-api-2.1.6.jar;"%REPO%"\org\glassfish\jersey\core\jersey-server\2.35\jersey-server-2.35.jar;"%REPO%"\org\glassfish\jersey\core\jersey-client\2.35\jersey-client-2.35.jar;"%REPO%"\jakarta\annotation\jakarta.annotation-api\1.3.5\jakarta.annotation-api-1.3.5.jar;"%REPO%"\jakarta\validation\jakarta.validation-api\2.0.2\jakarta.validation-api-2.0.2.jar;"%REPO%"\org\json\json\20220320\json-20220320.jar;"%REPO%"\org\apache\httpcomponents\httpclient\4.5.13\httpclient-4.5.13.jar;"%REPO%"\org\apache\httpcomponents\httpcore\4.4.13\httpcore-4.4.13.jar;"%REPO%"\commons-logging\commons-logging\1.2\commons-logging-1.2.jar;"%REPO%"\commons-codec\commons-codec\1.11\commons-codec-1.11.jar;"%REPO%"\org\apache\httpcomponents\httpmime\4.5.13\httpmime-4.5.13.jar;"%REPO%"\commons-io\commons-io\2.11.0\commons-io-2.11.0.jar;"%REPO%"\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar;"%REPO%"\org\telegram\telegrambotsextensions\6.1.0\telegrambotsextensions-6.1.0.jar;"%REPO%"\org\mongodb\mongodb-driver-sync\4.7.1\mongodb-driver-sync-4.7.1.jar;"%REPO%"\org\mongodb\bson\4.7.1\bson-4.7.1.jar;"%REPO%"\org\mongodb\mongodb-driver-core\4.7.1\mongodb-driver-core-4.7.1.jar;"%REPO%"\org\mongodb\bson-record-codec\4.7.1\bson-record-codec-4.7.1.jar;"%REPO%"\ch\qos\logback\logback-classic\1.4.3\logback-classic-1.4.3.jar;"%REPO%"\ch\qos\logback\logback-core\1.4.3\logback-core-1.4.3.jar;"%REPO%"\org\telegram\telegraph\1.0\telegraph-1.0.jar;"%REPO%"\org\telegram\telegraph-meta\1.0\telegraph-meta-1.0.jar;"%REPO%"\com\google\inject\guice\4.1.0\guice-4.1.0.jar;"%REPO%"\javax\inject\javax.inject\1\javax.inject-1.jar;"%REPO%"\aopalliance\aopalliance\1.0\aopalliance-1.0.jar;"%REPO%"\javax\validation\validation-api\1.1.0.Final\validation-api-1.1.0.Final.jar;"%REPO%"\org\example\magistrmate\1.0-SNAPSHOT\magistrmate-1.0-SNAPSHOT.jar
set EXTRA_JVM_ARGUMENTS=
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %JAVA_OPTS% %EXTRA_JVM_ARGUMENTS% -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dapp.name="name" -Dapp.repo="%REPO%" -Dbasedir="%BASEDIR%" Main %CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal

:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
