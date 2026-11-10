# Ahmad Keyboard (Android)

A Lebanese Arabizi keyboard that shortens words when you press space:
`kifak → kfk`, `tamem → tmm`, `3amel → 3ml`, `inshallah → isa`.

## Get the APK without installing anything (GitHub, free)

1. Create a free account at github.com and make a new repository (e.g. `ahmad-keyboard`).
2. On the repository page, choose **uploading an existing file**, and drag in everything inside this folder, including the hidden `.github` folder. If your computer hides it, upload from a desktop browser with hidden files shown.
3. Click **Commit changes**. GitHub starts building automatically.
4. Open the **Actions** tab and wait about 3 minutes until the build shows a green tick.
5. Click the build, scroll to **Artifacts**, download **AhmadKeyboard-apk**, and unzip it to get `app-debug.apk`.

## Or build it with Android Studio

Open this folder in Android Studio, wait for it to sync, then press **Run** with your phone plugged in (USB debugging on).

## Install on your phone

1. Send `app-debug.apk` to your phone and tap it. Allow "Install unknown apps" for your Files app or browser if asked.
2. Open **Ahmad Keyboard** from your apps.
3. Tap **Enable keyboard** and switch it on. Android shows a standard warning that keyboards can read what you type. This keyboard has no internet permission and sends nothing anywhere.
4. Tap **Choose keyboard** and pick Ahmad Keyboard.

## Using it

- Type normally. When you press space, comma, period, ? or enter, the last word shortens.
- The strip above the keys shows what the current word will become; tap it to shorten right away.
- ⚡ turns shortening on or off (for English messages).
- Hold ⌫ to delete quickly.

## Changing the words

Edit `app/src/main/java/com/ahmad/keyboard/Shortener.java`. Each pair is
`"full word","short form"`. Commit the change on GitHub and a new APK builds automatically.
