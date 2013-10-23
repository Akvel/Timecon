@echo off
del ev.txt 2> nul


wevtutil qe Security /rd:false /f:text "/q:*[System[TimeCreated[timediff(@SystemTime) <= 2678400001]] and EventData[Data[@Name='LogonType']=7 or Data[@Name='LogonType']=2] or System[Task=12548]]">events_1.txt
wevtutil qe System /rd:false /f:text "/q:*[System[TimeCreated[timediff(@SystemTime) <= 2678400001]] and System[Provider[@Name='Microsoft-Windows-Kernel-General']]]">events_2.txt

javac Timecon.java
java Timecon

del Timecon.class
del events_1.txt
del events_2.txt

call notepad ev.txt

del ev.txt


