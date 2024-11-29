Readme for 2023105_2023217 Angry Birds Submission.

-------------------------------------------------------------------------------

Steps to setup the project:
1. Unzip the zip file named "2023105_2023217.zip".
2. On unzipping the above file you will get the following : 
	(a). readme - This file.
	(b). 2023105_2023217_demo_video.mp4 - The demo video of the working game.
	(c). 2023105_2023217_uml.png - The UML diagram for the class structure of all the files of the 
				       project.
	(d). A folder named "AngryBirdsGame".
3. Open IntelliJ Idea or IntelliJ Idea Community.
4. On the left top corner click on File->Open .
5. Locate the "AngryBirdsGame" folder that you have downloaded and open it.
6. You should now be able to see "AngryBirdsGame[run]" along the top of the IDE. Press the play button
   beside it to run.
7. If you don't see the above mentioned button then navigate to AngryBirdsGame/lwjgl3/src/main/java/com.baglogic.angrybirds.lwjgl3/Lwjgl3Launcher.java.
8. Open the above mentioned file and run it.


-------------------------------------------------------------------------------


Structuring of the code:


-> The code is divided into various classes and sub-classes implementing all the essential OOPS concepts. Concepts like inheritance, polymorphism, method overloading, etc. have been of key importance and have been used extensively. Kindly refer to the UML diagram for better understanding

-> The "Core.java" class is the starting screen and is similar to the "Main.java" file in applications. From here all the other screens and methods are called.

-> There are other classes like loadingScreen - The screen displayed when loading the game.
startScreen - The screen at the start of the game where you can choose to play or quit.

-> Once you choose to play the game the levelChooseScreen is called. From here you can select which level you wish to play. You can play level 2 only after you've cleared level 1 and level 3 can only be played after clearing both level 1 and level 2.

-> Each level has a different screen that is called when executing the levels.

-> There is a "Bird.java" superclass which defines all the methods and attributes for birds. The other bird classes like Black, Blue, Red and Yellow extend this class to use the appropriate bird.

-> Similarly, there is a material superclass which is extended in Rock, Glass and Wood.

-> In the pig class, we have implemented different sizes of pigs. This is done by simply changing the radius of the pig that is needed.

-> Each level class has methods that handle collisions between various objects. The collision between pig and materials is currently set to not give any damage. Only bird provides damage to both materials or the pigs. However, there is also fall damage that a material or pig can obtain if it falls on the ground.


-------------------------------------------------------------------------------


Help from the following sites/platforms have been used for the game development:

1. Official Box2D documentation (For box2d features, collisions, input scanners,etc.)
2. Official LibGDX documentation 
3. For sprites : https://www.angrybirds.com/characters/ 
4. https://angrybirds.fandom.com/wiki/Angry_Birds_(series)/
5. Stack Overflow (for doubts/errors)


-------------------------------------------------------------------------------