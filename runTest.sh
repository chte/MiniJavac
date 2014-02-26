#!/bin/bash

#Test that should compile
FILES=./testsuit/compile/*
#Iterate over the files
for f in $FILES
do
  	# echo "Processing $f file..."
  	# take action on each file. $f store current file name
  	#cat $f
  	OUTPUT=$(java -jar ./build/jar/MiniJava.jar $f)
  	# echo $OUTPUT
  	RESULT=$(echo $OUTPUT | tail -c 49)
  	#echo $RESULT
   	# echo $TAILOUTPUT
  	# TAILOUTPUT= "$($OUTPUT | tail -c 39)"
  	# echo $TAILOUTPUT

  	if [ "$RESULT"  == "MiniJavac 1.0: Java program parsed successfully." ]; 
  	then
  	    echo "[OK] $f"
	else
	    echo "[FAIL] $f"
	fi

  	#TAILOUTPUT= "$($OUTPUT | tail -c 49)"
  	#echo $TAILOUTPUT
  	#echo $OUTPUT
  	# S="MiniJavac 1.0: Java program parsed successfully."
	# if [[ "$S" == *def ]]
	# then
	#     echo true
	# else
	#     echo false
	# fi
	#cat $OUTPUT
done