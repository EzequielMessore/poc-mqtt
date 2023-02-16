## MQTT Sample

### About MQTT
This repository was created to learn about the protocol **MQTT (Message Queuing Telemetry Transport)** and it use.

`MQTT` is an OASIS standard messaging protocol for the Internet of Things (IoT). It is designed as an extremely lightweight publish/subscribe messaging transport that is ideal for connecting remote devices with a small code footprint and minimal network bandwidth.

![mqtt diagram](https://mqtt.org/assets/img/mqtt-publish-subscribe.png)

References in [mqtt.org](https://mqtt.org/)

### MQTT in Android
The most famous library is [`paho.mqtt.android`](https://github.com/eclipse/paho.mqtt.android) but this library isn't supporting Android 12 and have many issues and PR's to accept, so I found other to use.

As a alternative I used library [MQTT Android Service](https://github.com/hannesa2/paho.mqtt.android) that has been created to provide reliable open-source implementations of open and standard messaging protocols.
And was focused on Kotlin which facilitates integration at some points.

### Importants libs
These libraries was essential to development of this PoC

```
// MQTT - Main library to use the protocol
implementation("com.github.hannesa2:paho.mqtt.android:3.5.1")

// SSL Certificates - Used in tests with certificates (in this case I tested with aws which requires a certificate)
implementation ("org.bouncycastle:bcpkix-jdk15on:1.67")

// Coroutines - To lead with assincronous tasks
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

// Hilt - To facilitate the concept of Dependency Injection
implementation("com.google.dagger:hilt-android:2.44")
kapt("com.google.dagger:hilt-android-compiler:2.44")
```

### Architecture
I tried to separate the packages as in the clean architecture, where:

**core** - Contains extensions, utils and others things.
**data** - In this module the Remote and Local DataSources are declared, and the implementation of the repositories according to the necessary logic.
**domain** - In this module, the use cases of the application and our models are declared.
**di** - It is responsible for managing our dependency injection
**ui** - Contains our features (we only have one screen to interact with our protocol) and our theme.

### Tests
This repository don’t have any tests for now. This will be an improvement for the future.
