# IoTControllerApp

A production-ready Android app for controlling and monitoring a person-carried IoT safety device using ESP32 hardware over Wi-Fi with GSM, GPS, and emergency features.

---

## Features

- **Call:** Enter a phone number in the app; a request is sent to Device A (ESP32), which routes to Device B (ESP32 + GSM) to place the call and indicate status via LED.
- **Send SMS:** Compose and send an SMS via Device A and B chain to GSM module.
- **SOS:** Instantly triggers Device A LED to blink and Device B to announce, call, and display emergency location on OLED, with two-way acknowledgement.
- **Offline Map:** View and refresh location on an offline `.mdtiles` map without internet.
- **Device Status:** Real-time connection, GSM, and device status from hardware.
- **Notifications:** Receives Android notifications for critical alerts and status.
- **Voice Channel (optional):** Streams audio over Wi-Fi if selected.

---

## Project Structure

IoTControllerApp/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── proguard-rules.pro
├── README.md
├── app/
│ ├── build.gradle
│ ├── src/
│ │ └── main/
│ │ ├── AndroidManifest.xml
│ │ ├── java/com/yourdomain/iotcontroller/
│ │ │ ├── MainActivity.kt
│ │ │ ├── comm/ESP32WifiClient.kt, AudioStreamer.kt
│ │ │ ├── util/NotificationUtils.kt
│ │ │ ├── model/DeviceStatus.kt, GpsLocation.kt
│ │ │ ├── ui/screens/CallScreen.kt, SmsScreen.kt, HomeScreen.kt, SOSScreen.kt, MapScreen.kt, StatusScreen.kt
│ │ │ ├── ui/theme/, ui/components/
│ │ ├── res/
│ │ │ ├── mipmap/ (icons), drawable/ (graphics), values/ (themes/colors)
│ │ ├── assets/maps/offline_india.mdtiles (offline map)

text

---

## Build & Run

1. **Clone** the repo and open in Android Studio.
2. Add your offline map files (`.mdtiles`) to `app/src/main/assets/maps/`.
3. Add your app icons to `app/src/main/res/mipmap/`.
4. Connect Android device to ESP32 Wi-Fi.
5. Build and install the app.

---

## ESP32/Device A/B Protocol

- App sends command strings via TCP socket to ESP32 at `192.168.4.1:5050`:
    - `CALL:<number>\n`, `SMS:<number>:<message>\n`, `SOS\n`, `SOS_CANCEL\n`, `GPS_REQUEST\n`, `STATUS\n`
- Audio streaming and device responses handled via additional ports/connections.
- Device B handles GSM, audio, OLED, TTS, and physical SOS/ack buttons.

---

## Extending & Contributing

- Add new screens, models, or hardware commands as needed.
- Write UI and unit tests in `test/` and `androidTest/`.
- Update ProGuard and permissions as you expand device features.

---

## Author

Created by Aditya Sarode and contributors.

---