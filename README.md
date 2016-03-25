# multicast-magic
 Persistent asynchronous multicast system built on top of TCP. Coordinator and participants are both implemented

 ~Run Instructions~
 1) open two terminal windows
 
 2) *Window #1*
  -mkdir bin
  -make
  -cd bin
  -java Coordinator ../config/PP3-coordinator-conf.txt
 
 3) *Window #2*
  -cd bin 
  -java Participant ../config/1001-message-log.txt

This project was done in its entirety by Montana Wong, Justin Tumale, and Matthew Haneburger. We hereby
state that we have not received unauthorized help of any form. 
