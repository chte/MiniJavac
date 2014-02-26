#!/bin/bash

echo "==COMPILE=="
#Test that should compile
FILES=./testsuit/compile/*
#Iterate over the files
for f in $FILES
do
	#echo "File: $f"
  	OUTPUT=$(java -jar ./build/jar/MiniJava.jar $f)
  	RESULT=$(echo $OUTPUT | tail -c 49)

  	if [ "$RESULT"  == "MiniJavac 1.0: Java program parsed successfully." ]; 
  	then
  	    echo "[OK] - $(basename $f)"
	else
	    echo "[FAIL] - $(basename $f)"
	fi
done

echo ""
echo "==NON COMPILE=="

#Test that should NOT Compile
FILES=./testsuit/noncompile/*
#Iterate over the files
for f in $FILES
do
	#echo "File: $f"
  	OUTPUT=$(java -jar ./build/jar/MiniJava.jar $f)
  	RESULT=$(echo $OUTPUT | tail -c 49)

  	if [ "$RESULT"  == "MiniJavac 1.0: Java program parsed successfully." ]; 
  	then
  	    echo "[FAIL] - $(basename $f)"
	else
	    echo "[OK] - $(basename $f)"
	fi
done