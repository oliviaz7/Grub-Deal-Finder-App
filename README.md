## Team 18 - Meowmeow

| Name        | Quest ID | Student ID | GitHub username(s)           |
|-------------|----------|------------|-------------------------------|
| Daniel Van  | dvan     | 20907407   | https://github.com/Daniel-Van |
| Grace Guo   | g7guo    | 20938948   | https://github.com/gr4ceG     |
| Joyce Jin   | j68jin   | 20952534   | https://github.com/joycej8    |
| Angela Law  | a26law   | 20942488   | https://github.com/angelllaw  |
| Olina Wang  | o6wang   | 20932664   | https://github.com/euilooo    |
| Olivia Zhang| o5zhang  | 20963643   | https://github.com/oliviaz7   |

## Set up
1. Clone repo
2. Open repo in Android Studio, wait for the gradle sync to complete
3. You might be missing Java 17, (see what running `java -version` gives you in the terminal) if so install it: https://stackoverflow.com/questions/69875335/macos-how-to-install-java-17
4. Add the `MAPS_API_KEY` in `local.properties` file in the root directory (search for MAPS_API_KEY in the discord)
5. In your terminal, run `./gradlew assembleDebug`
5. Run `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android` in the terminal and add your SHA1 to the meowmeow project on the Google developers (sent invites to everyone)
6. On the right side bar, click on `Device Manager`. Connect to a device emulator.
7. Try pressing the green run button next to app at the top right of the window (if the module is not found, run `./gradlew assembleDebug` in the terminal, then `./gradlew installDebug` in the terminal in the root directory)

## Meeting minutes
Feb 25:
Deliverables for Tuesday March 4
https://www.notion.so/Product-Meeting-1a471a5515b880e8b919c114853c8d83?pvs=4