# multicast-magic
 Persistent asynchronous multicast system built on top of TCP. Coordinator and participants are both implemented

 ~Run Instructions~
 1) open two terminal windows
 
 2) *Window #1*
  1-mkdir bin
  2-make
  3-cd bin
  4-java Coordinator path/to/config/file
     e.g. $java Coordinator ../config/PP3-coordinator-conf.txt
 
 3) *Window #2*
  1-cd bin
  2-java Participant path/to/config/file
     e.g. $java Participant ../config/PP3-participant-conf.txt

This project was done in its entirety by Montana Wong, Justin Tumale, and Matthew Haneburger. We hereby
state that we have not received unauthorized help of any form. 
