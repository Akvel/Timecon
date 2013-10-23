@echo off
del ev.txt 2> nul


wevtutil qe Security /rd:false /f:xml "/q:*[System[TimeCreated[timediff(@SystemTime) <= 2678400001]] and EventData[Data[@Name='LogonType']=7 or Data[@Name='LogonType']=2]]">events_1.xml
wevtutil qe System /rd:false /f:xml "/q:*[System[Provider[@Name='Microsoft-Windows-Kernel-General']]]">events_2.xml


javac TimeconXML.java
java TimeconXML

del TimeconXML.class
del TimeconXML$SAXPars.class
rem del events_1.xml
rem del events_2.xml

call notepad ev.txt

del ev.txt


