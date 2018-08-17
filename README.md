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

This app was build for learning purpose so there could be further improvement. <br />
This works for English and Portuguese. <br />
There are some issues in use of text to speech and speech to text.  <br />
I contained many hard coded sleep to prevent several TTS and STT from overlapping.  <br />
There should be some fixes to them. <br />
I also prepared setting activity that can contain more settings. <br />
To make this app work for other language, modify the hard corded part. <br />
Styling should be done.
I didn't delete some files that might be useful in the future for learning purpose. <br />

Achievement 
---------------

1. Learned how to read open sources and documentaion. 
2. Became confortable in Android development
3. Grasped big picture of efficient way of using SQL as a database and utilization of it in java code.
4. Succeeded populating pages dynamically. That enabled the code to be flexible to uncoutable data.
   For instance, nubmer of choices of multiple choice question doesn't affect how my page look like in each activities.
5. Gained idea of how text to speech and speech to text works. To implement better way, 
   I need to study how the thread works more.

![screenshot_healthchecker](https://user-images.githubusercontent.com/32227575/44286108-d2618780-a25f-11e8-8290-799f43e65fcf.jpeg)
