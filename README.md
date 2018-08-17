HealthChecker
==============================

This repository contains the work for kinetikos health care app draft.

Function
------------

There are two parts. 
First part contains use of Position sensors and GPS and store them to CVS file.
Second part is questionnaire. Quesions are feeded by SQL database and store the user answer to the same database.

Purpose and fixes
--------------

This app was build for learning purpose so there could be further improvement.
This works for English and Portuguese.
There are some issues in use of text to speech and speech to text. 
I contained many hard coded sleep to prevent several TTS and STT from overlapping. 
There should be some fixes to them.
I also prepared setting activity that can contain more settings.
To make this app work for other language, modify the hard corded part.
Styling should be done.

Achievement 
---------------

1. Learned how to read open sources and documentaion. 
2. Became confortable in Android development
3. Grasped big picture of efficient way of using SQL as a database and utilization of it in java code.
4. Succeeded populating pages dynamically. That enabled the code to be flexible to uncoutable data.
   For instance, nubmer of choices of multiple choice question doesn't affect how my page look like in each activities.
5. Gained idea of how text to speech and speech to text works. To implement better way, 
   I need to study how the thread works more.
