all:
	gcc -std=c99 -o lockerController lockerController.c -lwiringPi -lcurl