#!bin/sh

clear
RED="\033[31m"
GREEN="\033[32m"
WHITE="\033[00m"

echo "${WHITE}Testing minijava compiler. Please wait...\n"
# TEST=$(java -jar ./../mjc.jar ./compile/1xi.java)
# echo $TEST
echo "${WHITE}Testing compilable files..."

FOLDER=./testcases/compile/*
rm $FOLDER.java~ >/dev/null 2>&1 #Clear temp files

CFAILS=0
for file in $FOLDER
do
	OUTPUT=$(java -jar ./mjc.jar $file) >/dev/null 2>&1
	STATUS=$?
	if [ $STATUS -eq 0 ]
	then
		echo "${GREEN}	[PASS]${WHITE} $file"
	else
		file=$(readlink -f $file)
		file="file://$file"
		echo "${RED}	[FAIL]${WHITE} $file"
		CFAILS=$(($CFAILS+1))
	fi
done

echo "\n${WHITE}Testing noncompilable files..."

FOLDER=./testcases/noncompile/*
rm $FOLDER.java~ >/dev/null 2>&1 #Clear temp files

NCFAILS=0
for file in $FOLDER
do
	OUTPUT=$(java -jar ./mjc.jar $file) >/dev/null 2>&1
	STATUS=$?
	if [ $STATUS -ne 0 ]
	then
		echo "${GREEN}	[PASS]${WHITE} $file"
	else
		file=$(readlink -f $file)
		file="file://$file"
		echo "${RED}	[FAIL]${WHITE} $file"
		NCFAILS=$(($NCFAILS+1))
	fi
done

echo "\n${WHITE}Testing execute files..."

FOLDER=./testcases/execute/*
rm $FOLDER.java~ >/dev/null 2>&1 #Clear temp files

NCFAILS=0
for file in $FOLDER
do
	OUTPUT=$(java -jar ./mjc.jar $file) >/dev/null 2>&1
	STATUS=$?
	if [ $STATUS -ne 0 ]
	then
		echo "${GREEN}	[PASS]${WHITE} $file"
	else
		file=$(readlink -f $file)
		file="file://$file"
		echo "${RED}	[FAIL]${WHITE} $file"
		NCFAILS=$(($NCFAILS+1))
	fi
done

echo "\n${WHITE}Testing execute files..."

FOLDER=./testcases/nonexecute/*
rm $FOLDER.java~ >/dev/null 2>&1 #Clear temp files

NCFAILS=0
for file in $FOLDER
do
	OUTPUT=$(java -jar ./mjc.jar $file) >/dev/null 2>&1
	STATUS=$?
	if [ $STATUS -ne 0 ]
	then
		echo "${GREEN}	[PASS]${WHITE} $file"
	else
		file=$(readlink -f $file)
		file="file://$file"
		echo "${RED}	[FAIL]${WHITE} $file"
		NCFAILS=$(($NCFAILS+1))
	fi
done


message="\nTesting done."
if [ $(($CFAILS+$NCFAILS)) -ne 0 ]
then
	echo "${WHITE}$message ${RED}$(($CFAILS+$NCFAILS)) error(s).${WHITE}\n"
else
	echo "${WHITE}$message ${GREEN}All tests passed.${WHITE}\n"
fi
