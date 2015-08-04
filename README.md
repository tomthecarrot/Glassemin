Glassemin
=========

Glassemin is an app for Google Glass that utilizes the light sensor to create a virtual "theremin". Move your hand back and forth from the camera to change the pitch of the sound.

Since Android only updates the light sensor every few seconds, there is a slight delay between hand movement and pitch update.

Tutorial:  
1. Plug Glass into your computer via USB.  
2. Open a terminal console or command line and type: ```adb install Glassemin.apk```  
3. Say "ok glass, play the theremin" to start.  
4. Move your hand back and forth from the camera to change the pitch of the sound.  

To uninstall:  
Open a terminal console or command line and type: ```adb uninstall com.carrotcorp.glass.glassemin```

GPL v3 license. See LICENSE file for more information.
