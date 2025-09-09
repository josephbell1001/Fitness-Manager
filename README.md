# Fitness Manager App

### *What does it do?*
The Fitness Manager App will provide a way for someone to track their exercise. More specifically, it will be used to keep track of different exercises in a persons training routine, including the exercise name, targetting muscle group, and the amount of reps and weight (lbs) that the exercise should be executed for. The Fitness Manager App will allow a person to store all of their created exercises into a specific training session, for example, I would be able to create a few exercises that I enjoy and target my upper body, and then add all of those exercises to a training session called upper body day or something, also specifying how many sets that I want to do each exercise for in that particular training session.

### *Who will use it?*
Anyone trying to organize their workouts will be able to use the Fitness Manager App to do so, as they would be able to organize all of their training exercises, the exercises within them, and the amount of volume that they want to do each exercise for in a given training session. Furthermore, being able to edit the amount of reps and weight for a particular exercise is a great way for people who use this app to track their progress and get more fit.

### *Why does this project interest me?*
I am very passionate about my health and physical fitness, with a particular interest in weight-lifting and going to the gym. After going to the gym consistently for the last 3 years, I know that it is crucial to be able to organize your workouts, especially the amount of reps and weight for a given exercise, in order to properly progress and get stronger. Otherwise, without any organization of how many reps you can do for a given exercise for a particular weight, it's difficult to push past those limits and progress if you don't even know what they are.

## User Stories:
As a user, I want to be able to be able to create a training session with a name and add exercises and the corresponding amount of sets that exercise should be executed for to that particular training session.

As a user, I want to be able to view the list of exercises and their corresponding amount of sets in a particular training session, as well as view all of the exercises in my exercise list.

As a user, I want to be able to create an exercise with a name, muscle group that the exercise targets, amount of reps the exercise should be executed for, and the weight (in pounds (lbs)) that should be used for the particular exercise.

As a user, I want to have the option to be able to save the entire state of the FitnessManagerApp.

As a user, I want to have the option to load the entire state of the FitnessManagerApp from the programs most recent manually saved state.

## Instructions for End User:

- You can create a new exercise by clicking the "Create Exercise" button and entering the exercises name, target muscle group, weight (lbs), and reps for that exercise. You can also edit the details of any exercise with the "Edit Exercise" button, as well as view the details of any exercise by clicking "View Sessions and Exercises".

- You can create a new training session by clicking the "Create Training Session" button and entering the training session name. You can then click the "Add Exercise to Session" button to add an exercise (or multiple) to a training session by entering the exercise name, an amount of sets for that exercise, and the training session name. You can also clear the training session of all exercises by clicking the "Clear Training Session" button, and view all the contents of a particular training session by clicking the "View Sessions and Exercises" button.

- You can save the entire state of the application to file by clicking the "Save Program" button, you can also load the most recently saved state of the application from file by clicking the "Load Program" button, and lastly, you can quit the application by clicking the "Quit Program" button.

- The visual component can be seen immediately after opening the application, including images on both the left and right sides of the "Fitness Manager" title.

## Phase 4: Task 2

Example of an Event Log:

"Event Log:
Fri Mar 21 23:00:21 PDT 2025
Created new exercise Peck Deck!
Fri Mar 21 23:00:38 PDT 2025
Changed exercise name to Squats!
Fri Mar 21 23:00:38 PDT 2025
Changed exercise target muscle to Legs!
Fri Mar 21 23:00:38 PDT 2025
Changed exercise weight to 225lbs!
Fri Mar 21 23:00:38 PDT 2025
Changed exercise reps to 8 per set!
Fri Mar 21 23:00:54 PDT 2025
Added Peck Deck to Push!
Fri Mar 21 23:00:54 PDT 2025
Added 2sets of Peck Deck to Push!
Fri Mar 21 23:01:04 PDT 2025
Cleared session Legs!"


## Phase 4: Task 3

If I had more time to work on this project, some refactoring that I might do to improve my design would be adding sets into the Exercise class for convenience, since typically, exercises are done for the same amount of reps and sets across different training sessions (so making the process of adding exercises to training sessions less specific, but more quick), but other than that, there was not much duplicate code that needed to be condensed into a parent class.

Additionally, I would probably have worked on the design of the GUI more if I had more time to improve this project, and make it a lot more fancy looking. Further, I would also potentially have less buttons on the main screen, and instead, have them sort of like folders, with sections to add, edit, and etc, which would then take you to another menu of buttons that tailor to that general action, rather than having most of the possible actions immediately present upon opening the application. Although this would make it require more clicks to do a particular action, it could help the program look less overwhelming to new users, so along with making the homepage and rest of the application look better, this change would also make it look better, so in short, just making the app look better aesthetically.

