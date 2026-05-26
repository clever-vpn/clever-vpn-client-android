# Clever VPN Client Android

Clever VPN Client Android is an open-source Android application designed to provide a simple and efficient VPN experience. The project leverages the power of the [clever-vpn-android-kit](https://maven.org/clever-vpn-android-kit) Maven dependency, which encapsulates all VPN communication protocols. This abstraction allows the app to remain lightweight and maintainable, focusing on user experience rather than low-level protocol implementation.

This app is built using the latest Kotlin language features and Jetpack Compose for UI development, ensuring a modern, robust, and highly maintainable codebase.

## Features
- Simple integration with clever-vpn-android-kit
- Built with Jetpack Compose for declarative UI
- Utilizes modern Kotlin features for concise and safe code
- Clean and minimal codebase
- Easy to extend and customize

## Dependency
This project depends on the `clever-vpn-android-kit` Maven package. The kit handles all VPN protocol communications, enabling rapid development and reducing complexity in the app code.

## Getting Started
1. Clone this repository.
2. Ensure your project includes the `clever-vpn-android-kit` dependency in your build configuration.
3. Build and run the app using Android Studio.

## Release Policy
Android releases are published manually through GitHub Actions and use `v`-prefixed semantic version tags such as `v1.3.2`.

The release tag is treated as immutable. If a requested version tag already exists, the workflow must build from the commit currently pointed to by that tag and may update the corresponding GitHub Release metadata or assets, but it must not move the tag to a different commit.

If a requested version tag does not exist, the workflow should use the current `main` HEAD as the target commit for the release. If no version is provided, the workflow should read the latest GitHub Release tag, increment the patch version, and use the resulting `v`-prefixed tag for the new release.

To avoid creating a dirty tag, a new tag must not be written during prepare steps. The workflow should build artifacts first against the resolved target commit, then create the missing tag only in the final publish stage immediately before creating or updating the GitHub Release. If a newly created tag cannot be paired with a successful release write, the workflow should clean up that tag before the job exits with failure.

Google Play publishing and GitHub Release asset publishing are separate outputs of the same manual release flow. The workflow only uploads to Google Play testing tracks (`internal`, `closed`, or `open`). Production rollout must be promoted manually in Play Console after validation. GitHub Releases publish ABI-specific APK assets for `arm64-v8a` and `armeabi-v7a`, while Google Play continues to receive the AAB. Secrets and signing material are loaded from Bitwarden-managed credentials exposed through GitHub Actions secrets and repository variables.

## License
This project is open-source. See the LICENSE file for details.
