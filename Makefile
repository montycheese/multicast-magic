
JFLAGS = -g
JC = javac
JVM= java 
BIN_DIR = FTP/bin/
SRC_DIR = FTP/src/
CLASSPATH = -cp FTP/src/


.SUFFIXES: .java .class


.java.class:
	$(JC) $(CLASSPATH) -d $(BIN_DIR) $(JFLAGS) $*.java


#our classes
CLASSES = \
        $(SRC_DIR)CoordinatorThread.java \
	$(SRC_DIR)ParticipantThread.java \
        $(SRC_DIR)ParticipantListener.java \
	$(SRC_DIR)Coordinator.java \
	$(SRC_DIR)Participant.java \
	$(SRC_DIR)Message.java \
	$(SRC_DIR)CommandCode.java 


MAIN = Coordinator 

#
# the default make target entry
# for this example it is the target classes

default: classes


#handle dependencies

classes: $(CLASSES:.java=.class)



run: $(MAIN).class
	$(JVM) $(MAIN)


# Clean up binary files

clean:
	$(RM) $(BIN_DIR)*.class
