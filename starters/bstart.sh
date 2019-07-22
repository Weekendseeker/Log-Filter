#!/bin/bash

CUR_DIR=`dirname $0`
JARFile="$CUR_DIR/LogFilter-1.0.0.jar"
export JAVA_HOME='/c/jav/OpenJdk/jdk-11.0.2/bin'

$JAVA_HOME/java -jar $JARFile