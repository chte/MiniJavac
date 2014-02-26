#!/bin/bash

TOTAL_ERRORS=0
ERRORS_COMPILE=0
ERRORS_NOCOMPILE=0
TOTAL_TEST=0
FAIL_FILES_COMPILE="Compile:"
FAIL_FILES_NOCOMPILE="Non Compile:"

echo "=== COMPILE ==="
#Test that should compile
FILES=./testsuit/compile/*
#Iterate over the files
for f in $FILES
do
	#echo "File: $f"
  	OUTPUT=$(java -jar ./build/jar/MiniJava.jar $f)
  	RESULT=$(echo $OUTPUT | tail -c 49)
    ((TOTAL_TEST++))

  	if [ "$RESULT"  == "MiniJavac 1.0: Java program parsed successfully." ]; 
  	then
  	    echo "[OK] - $(basename $f)"
	else
	    echo "[FAIL] - $(basename $f)"
        #Increase the counters
        ((ERRORS_COMPILE ++))
        ((TOTAL_ERRORS ++))
        #Add the file to the error list
        FAIL_FILES_COMPILE="$FAIL_FILES_COMPILE\n    - $(basename $f)"
	fi
done

echo ""
echo "=== NON COMPILE ==="

#Test that should NOT Compile
FILES=./testsuit/noncompile/*
#Iterate over the files
for f in $FILES
do
  	OUTPUT=$(java -jar ./build/jar/MiniJava.jar $f)
  	RESULT=$(echo $OUTPUT | tail -c 49)
    ((TOTAL_TEST++))

  	if [ "$RESULT"  == "MiniJavac 1.0: Java program parsed successfully." ]; 
  	then
  	    echo "[FAIL] - $(basename $f)"
        #Increase the counters
        ((TOTAL_ERRORS++))
        ((ERRORS_NOCOMPILE++))
        #Add the file to the error list
        FAIL_FILES_NOCOMPILE="$FAIL_FILES_NOCOMPILE\n    - $(basename $f)"
	else
	    echo "[OK] - $(basename $f)"
	fi
done

#Test summary

echo ""
echo "==== SUMMARY ===="

if [ $TOTAL_ERRORS -eq 0 ];
then
    echo "All $TOTAL_TEST passed"
else
    echo "$TOTAL_ERRORS test failed"
    
    #Check if errors was from compile
    if [ $ERRORS_COMPILE -gt 0 ];
    then
        echo ""
        echo -e $FAIL_FILES_COMPILE
    fi
    
    #Check if errors was from nocompile
    if [ $ERRORS_NOCOMPILE -gt 0 ];
    then
        echo ""
        echo -e $FAIL_FILES_NOCOMPILE
    fi
fi

echo ""