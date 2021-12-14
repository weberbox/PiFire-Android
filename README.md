# ![Dashboard](app/src/main/res/mipmap-hdpi/ic_launcher.png) PiFire Android
## A native Android app for the PiFire smoker project

### Introduction
This app was created to work with the PiFire smoker project. I wanted a native Android app to control my smoker using the awesome PiFire project so I created this.

### Screenshots
Dashboard | History | Event Log | Pellet Manager | Settings | Work Mode | Admin | Drawer
----------|---------|-----------|----------------|----------|-----------|-------|-------
![Dashboard](docs/screenshots/dashboard.png) | ![History](docs/screenshots/history.png) | ![Event Log](docs/screenshots/eventlog.png) | ![Pellet Manager](docs/screenshots/pelletmanager.png) | ![Settings](docs/screenshots/settings.png) | ![Settings / Work Mode](docs/screenshots/workmode.png) | ![Admin](docs/screenshots/admin.png) | ![Drawer](docs/screenshots/drawer.png)

## Documentation

This application supports two versions of the PiFire server, the main PiFire project and another version that adds the option for a second grill probe with an updated PCB I designed located [here](https://easyeda.com/zipster85/pifire-controller-hat) More discussion on that can be found in the PiFire discussions [here](https://github.com/nebhead/PiFire/discussions/28#discussioncomment-1006962)

The main PiFire branch now supports the Android app you can find it [here](https://github.com/nebhead/PiFire)

Multi Grill Probe version [here](https://github.com/weberbox/PiFire/tree/fourprobes)


There are currently two Android versions that can be downloaded

Github Firebase – This supports push notifications directly to the application from PiFire. It was redesigned so it no longer requires custom compiling the Android source and signing up for Firebase etc. You can enable Firebase notifications from the app once you have connected to a PiFire server. This version requires the device to have Google play services installed.

Github NonFirebase – This version does not support mobile push notifications in the Android app. Pushbullet, Pushover, IFTTT, etc are still supported by the server but PiFire Android will not receive push notifications directly. This version does not require Google play services installed.

Self Compile Option

There is also another version version that can be self compiled. This is pretty straight forward like any other Android project, just download the source code from here and import the project to Android Studio. There usually will be a few prompts to download any required SDK's or dependencies. Once everything has downloaded you will need to open the Build Variants menu usually located in the lower left corner of Android Studio and change to Compile Non Firebase

If you want to self compile and use Firebase you would need to setup a Google Firebase account and download the google-services.json file as well as host an app server instance somewhere to communicate with Google Firebase. The scope of this project will not cover how to do this but it is possible if you desire. You can also utilize the Sentry crash reporting if you sign up for an account and enter your own dsn in defaults.xml

### Warnings

The creator of this project takes no responsibility for any damage that you may do to your personal property including modifications to your smoker grill if you choose to use this project.  The creator also takes no responsibility for any resulting harm or damages that may come from issues with the hardware or software design.*  ***This project is provided for educational purposes, and should be attempted only by individuals who wish to assume all risks involved.***

### Credits

Android Application created by James Weber, copyright 2021.

* **PiFire** - The core project that this app works with. An awesome project to manage and control your pellet smoker with a Raspberry pi [github](https://github.com/nebhead/PiFire)

* **Socket.IO** - [github](https://github.com/socketio/socket.io-client-java)

* **AnimatedBottomBar** - [github](https://github.com/Droppers/AnimatedBottomBar)

* **zxing-android-embedded** - [github](https://github.com/journeyapps/zxing-android-embedded)

* **EasyPrefs** - [github](https://github.com/Pixplicity/EasyPrefs)

* **PowerSpinner** - [github](https://github.com/skydoves/PowerSpinner)

* **MaterialDesign Icons** - [github](https://github.com/Templarian/MaterialDesign)

* **MPAndroidChart** - [github](https://github.com/PhilJay/MPAndroidChart)

* **AppUpdater** - [github](https://github.com/javiersantos/AppUpdater)

* **Sentry** - [github](https://github.com/getsentry/sentry-java)

* **Timber** - [github](https://github.com/JakeWharton/timber)

* **RecyclerViewFastScroller** - [github](https://github.com/quiph/RecyclerView-FastScroller)

### Licensing

This project is licensed under the MIT license.

```
MIT License

Copyright (c) 2021 James Weber

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```