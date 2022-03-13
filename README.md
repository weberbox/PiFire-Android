# ![Dashboard](app/src/main/res/mipmap-hdpi/ic_launcher.png) PiFire Android
## A native Android app for the PiFire smoker project

### Introduction
This app was created to work with the PiFire smoker project. I wanted a native Android app to control my smoker using the awesome PiFire project so I created this. 

**Note:** I am not a developer by trade and this is just a hobby for me. While I try my best to keep the app stable you may find bugs or experience a crash. If you see something in the code that could be done better please let me know, I am always willing to learn more.

## Screenshots
Dashboard | History | Event Log | Pellet Manager
----------|---------|-----------|----------------
![Dashboard](docs/screenshots/dashboard.png) | ![History](docs/screenshots/history.png) | ![Event Log](docs/screenshots/eventlog.png) | ![Pellet Manager](docs/screenshots/pelletmanager.png)

Recipes | Updater | Admin | Drawer
--------|---------|-------|--------
![Recipes](docs/screenshots/recipes.png) | ![Updater](docs/screenshots/updater.png) | ![Admin](docs/screenshots/admin.png) | ![Drawer](docs/screenshots/drawer.png)

## Documentation

This application supports both the three probe and four probe versions of PiFire. You can find more information on the four probe PCB I designed located [here](https://github.com/nebhead/PiFire/discussions/28#discussioncomment-1006962) as well as the design located [here](https://easyeda.com/zipster85/pifire-controller-hat)

[Main PiFire branch](https://github.com/nebhead/PiFire)


There are currently two Android versions that can be downloaded

**Github Firebase** – This supports push notifications directly to the application from PiFire. It was redesigned and no longer requires custom compiling the Android source and signing up for Firebase or OneSignal. This version requires the device to have Google play services installed.

**Github NonFirebase** – This version does not support mobile push notifications in the Android app. Pushbullet, Pushover, IFTTT, etc are still supported by the server but PiFire Android will not receive push notifications directly. This version does not require Google play services installed.

**Self Compile Option**

There is also another version version that can be self compiled. This is pretty straight forward like any other Android project, just download the source code from here and import the project to Android Studio. There usually will be a few prompts to download any required SDK's or dependencies. Once everything has downloaded you will need to open the Build Variants menu usually located in the lower left corner of Android Studio and change to Compile NonFirebase

If you want to self compile and use OneSignal you will need to setup a Google Firebase account, a OneSignal Account and download the google-services.json file as well as get a OneSignal AppID/API Key. The scope of this project will not cover how to do this but it is possible if you desire. You can also utilize the Sentry crash reporting if you sign up for an account and enter your own dsn in defaults.xml

### Warnings

The creator of this project takes no responsibility for any damage that you may do to your personal property including modifications to your smoker grill if you choose to use this project.  The creator also takes no responsibility for any resulting harm or damages that may come from issues with the hardware or software design.*  ***This project is provided for educational purposes, and should be attempted only by individuals who wish to assume all risks involved.***

### Credits

Android Application created by James Weber, copyright 2021.

This app used the following open-source libraries and/or code inspired from these projects.

- [PiFire](https://github.com/nebhead/PiFire)
- [Socket.IO](https://github.com/socketio/socket.io-client-java)
- [AnimatedBottomBar](https://github.com/Droppers/AnimatedBottomBar)
- [zxing-android-embedded](https://github.com/journeyapps/zxing-android-embedded)
- [EasyPrefs](https://github.com/Pixplicity/EasyPrefs)
- [MaterialDesign Icons](https://github.com/Templarian/MaterialDesign)
- [RecyclerViewFastScroller](https://github.com/quiph/RecyclerView-FastScroller)
- [KeyboardVisibilityEvent](https://github.com/yshrsmz/KeyboardVisibilityEvent)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [AppUpdater](https://github.com/javiersantos/AppUpdater)
- [Sentry](https://github.com/getsentry/sentry-java)
- [Timber](https://github.com/JakeWharton/timber)
- [OverlappingPanels](https://github.com/discord/OverlappingPanels)
- [TouchImageView](https://github.com/MikeOrtiz/TouchImageView)
- [uCrop](https://github.com/Yalantis/uCrop)
- [TedPermission](https://github.com/ParkSangGwon/TedPermission)
- [AndroidVeil](https://github.com/skydoves/AndroidVeil)
- [Alerter](https://github.com/Tapadoo/Alerter)
- [MaterialRatingBar](https://github.com/zhanghai/MaterialRatingBar)
- [Material Dialog](https://github.com/PatilShreyas/MaterialDialog-Android)
- [Android-Stepper](https://github.com/acefalobi/android-stepper)
- [Lottie](https://github.com/airbnb/lottie-android)
- [Changelog Lib](https://github.com/weberbox/changeloglib)
- [OneSignal](https://github.com/OneSignal/OneSignal-Android-SDK)
- [OkHttp](https://github.com/square/okhttp)
- [Glide](https://github.com/bumptech/glide)

### Licensing

This project is licensed under the GNU GPLv3 license.

```
PiFire Android - Native Android app for the PiFire smoker project

Copyright (c) 2021 James Weber

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```