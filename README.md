# Hotkey (client application)
Hotkey is a cross platform solution for controlling your PC from a mobile device.

(This is a university project for the course CSE-2216 (Application Development Lab) at the [Department of Computer Science and Engineering, University of Dhaka](http://www.cse.du.ac.bd/).)

This is the repository for the client application (Android). It is an Android Studio (gradle) project. For more information refer the parent repository [hotkey](https://github.com/hoenchioma/hotkey).

## Building
In order to build the source code yourself you will need to first clone the repository. Open the terminal (for windows `cmd` or `git bash`) in the directory of your choice and run:
```
git clone https://github.com/hoenchioma/hotkey-client.git
```
#### Command line
Then in order to build enter the directory you just cloned into and run a gradle build:
```
cd hotkey-client
./gradlew assembleDebug
```
This creates an APK named `app-debug.apk` in `hotkey-client/app/build/outputs/apk/`. The file is already signed with the debug key and aligned with zipalign, so you can immediately install it on a device. For more info see [this](https://developer.android.com/studio/build/building-cmdline).

#### GUI
Alternatively you can open this folder in `Android Studio` and build/run the android application. For more info see [this](https://developer.android.com/studio/run)
