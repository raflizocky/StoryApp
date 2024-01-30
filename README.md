# Story App
Story App is an Android application designed to help users save their stories and explore others' narratives. Build with [Android Studio](https://developer.android.com/studio).

## Features
- **Authentication:** Secure login, instant password error feedback, and convenient logout option
- **List Story:** Displays a list of personal and others' stories with REST API integration
- **Add Story:** Adds a new story accessible from the list of stories with various options
- **Animation:** Includes animations for a dynamic user experience
- **Google Maps:** Utilizes Google Maps to show the location where other users upload their stories
- **Paging List:** Implements paging for an infinite scroll experience
- **Testing:** Incorporates testing to ensure the success of feature flows in the application

## Demo 
![Story App GIF](https://github.com/raflizocky/StoryApp/blob/main/image/story-app.gif)

## Tech Stack
- [Kotlin](https://kotlinlang.org/) – language
- [MVVM](https://www.youtube.com/watch?v=FrteWKKVyzI) – architectural pattern
- [Room](https://developer.android.com/training/data-storage/room) - database
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - data storage
- [Animation](https://developer.android.com/reference/android/view/animation/Animation) - animation library
- [Service](https://developer.android.com/reference/android/app/Service.html) - background handling
- [Media](https://medium.com/developer-student-clubs/android-kotlin-camera-using-gallery-ff8591c26c3e) - media handling
- [Geo Location](https://developer.android.com/develop/sensors-and-location/location) - geolocation services
- [Advanced Testing](https://developer.android.com/studio/test/) - testing framework

## Getting Started
### Prerequisites
Here's what you need to be able to run GitHub App:
- Android Studio (https://developer.android.com/studio/install)
- Android Device/Emulator (I use Devices, if [Emulator](https://developer.android.com/studio/run/emulator))
- Gradle (https://gradle.org/releases/)
- JDK (I use [OpenJDK](https://openjdk.org/), if [OracleJDK](https://www.oracle.com/java/technologies/downloads/))
- Dicoding Story REST API (https://story-api.dicoding.dev/v1/)

### 1. Clone the repository
```shell
https://github.com/raflizocky/StoryApp.git
```

### 2. Configure the variables in `local.properties`
| Variable | Value |
|---|---|
| sdk_dir | < Your sdk location > |
| BASE_URL | < [Dicoding Story REST API](https://docs.github.com/en/rest?apiVersion=2022-11-28) > |

### 3. Run the app
Fill in the necessary details in the ```local.properties``` file, then [run the app](https://developer.android.com/studio/run).

## Contributing

Story App is an Android project and welcomes contributions from the community.

If you'd like to contribute, please fork the repository and make changes as you'd like. Pull requests are warmly welcome.

### Our Contributors ✨

<a href="https://github.com/raflizocky/github-app/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=raflizocky/github-app" />
</a>

## Inspiration

- [Android Developer guides](https://developer.android.com/guide) -  Reference materials and documentation for Android app development
- [Belajar Pengembangan Aplikasi Android Intermediate](https://www.dicoding.com/academies/352) - Android Application Intermediate Course
