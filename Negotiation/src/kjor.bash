#!/bin/bash

JADE_LIB="${HOME}/workspace/distai/iteratedprisonersdilemma/jade/lib"
CLASSPATH="${JADE_LIB}/jade.jar"

for file in ./*.java
do
	echo "Compiling $file"
	javac -cp $CLASSPATH:. $file
done

echo "Running program"
java -cp $CLASSPATH:. jade.Boot -local-host 127.0.0.1 -agents $@
