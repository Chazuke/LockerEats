#include <arpa/inet.h>
#include <curl/curl.h>
#include <dirent.h>
#include <fcntl.h>
#include <netdb.h>
#include <netinet/in.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>
#include <wiringPi.h>

#include "inflection.h"

typedef struct Locker{
	time_t timer;
	int orderNum;
	int isFree;
	int pin;
} Locker;

void openLocker(int orderNum, int numLockers, struct Locker lockerList[]);

void assignLocker(int orderNum, int numLockers, struct Locker lockerList[]);

void enqueueLocker(int orderNum);

int dequeueLocker();

int getFreeLocker(struct Locker lockerList[], int numLockers);

void initializePins();

void popLock(struct Locker lockerList[], int index);

int sendNotification(int orderNum);