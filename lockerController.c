#include "lockerController.h"

#ifndef	INADDR_NONE
#define	INADDR_NONE	0xffffffff
#endif	/* INADDR_NONE */

#define	BUFF_SIZ 2048	/* size of a buffer for a connection */
#define QUEUE_SIZ 10

#define h_addr h_addr_list[0]


int lockerQueue[QUEUE_SIZ];
int head = 0;
int tail = 0;

//Adds an order number to the FIFO overflow queue
void enqueueLocker(int orderNum) {
	lockerQueue[tail] = orderNum;
	if (tail + 1 == QUEUE_SIZ) tail = 0;
	if (tail + 1 == head) printf("Cannot Enqueue Order, Queue Full!\n");
	else tail++;
	return;
}

//parses lockerList, finds the locker associated with the order number
//and opens that locker, setting it as available
void openLocker(int orderNum, int numLockers, struct Locker lockerList[]) {
	for (int i = 0; i < numLockers; i++) {
		if (lockerList[i].orderNum == orderNum) {
			//set locker as available
			lockerList[i].isFree = 0;
			//open locker
			popLock(lockerList, i);
			return;
		}
	}
	printf("There is no locker assigned to this order number: %d\n", orderNum);
	return;
}

//the restaurant has scanned an admin code, assign it an open locker
//and send a notification to the client who ordered the food
//also starts the 1 hr timer to expiration of locker
void assignLocker(int orderNum, int numLockers, struct Locker lockerList[]) {
	int index = getFreeLocker(lockerList, numLockers);
	if (index < 0) {
		enqueueLocker(orderNum);
		return;
	}
	//Set locker as taken and give it the order number
	lockerList[index].isFree = 1;
	lockerList[index].orderNum = orderNum;
	//opens this locker
	popLock(lockerList, index);
	//sends message to server
	updateServer(orderNum);
	//starts locker timer
	lockerList[index].timer = time(NULL);
	return;
}

//Removes the order number at the front of the queue and returns it
int dequeueLocker() {
	int ret = lockerQueue[head];
	if (head + 1 == QUEUE_SIZ) head = 0;
	else head++;
	return ret;
}

//finds an open or expired locker and returns its index in the struct array
//if no locker is open, returns -1
int getFreeLocker(struct Locker lockerList[], int numLockers) {
	//check for an open locker first
	for (int i = 0; i < numLockers; i++) {
		if (lockerList[i].isFree == 0) return i;
	}
	//check for expired locker if no open lockers
	time_t now;
	now = time(NULL);
	for (int i = 0; i < numLockers; i++) {
		if (difftime((lockerList[i].timer), now) > 3600) {
			lockerList[i].isFree = 0;
			return i;
		}
	}
	return -1;
}

//Initliaize GPIO pins using wiringPi
void initialize(struct Locker lockerList[], int numLockers) {
	wiringPiSetupGpio();
	int pins[4];
	pins[0] = 4;
	pins[1] = 17;
	pins[2] = 27;
	pins[3] = 22;
	pinMode(pins[0], OUTPUT);
	pinMode(pins[1], OUTPUT);
	pinMode(pins[2], OUTPUT);
	pinMode(pins[3], OUTPUT);
	digitalWrite(pins[0], HIGH);
	digitalWrite(pins[1], HIGH);
	digitalWrite(pins[2], HIGH);
	digitalWrite(pins[3], HIGH);

	for (int i = 0; i < numLockers; i++) {
		lockerList[i].orderNum = 0;
		lockerList[i].isFree = 0;
		lockerList[i].pin = pins[i];
	}

	return;
}

//Actuates the lock associated with the given locker
void popLock(struct Locker lockerList[], int index) {
	printf("Opened Locker!\n");
	digitalWrite(lockerList[index].pin, LOW);
	delay(2000);
	digitalWrite(lockerList[index].pin, HIGH);
	return;
}

//Sends an order number to the server to notify customer that order is ready
void updateServer(int orderNum) {
	CURL *curl;
	CURLcode res;
	char json[200];
	struct curl_slist *headers=NULL;
	
	char url[50];
	snprintf(url, sizeof(url), "http://agglo.mooo.com:3000/api/v1/orders/%i", orderNum);
	
	curl = curl_easy_init();
	
	if(curl) {
	//headers = curl_slist_append(headers, client_id_header);
    headers = curl_slist_append(headers, "Content-Type: application/json");

    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers); 
    /* First set the URL that is about to receive our POST. This URL can
       just as well be a https:// URL if that is what should receive the
       data. */
    curl_easy_setopt(curl, CURLOPT_URL, url);
    /* Specify the user/pass */
    //curl_easy_setopt(curl,CURLOPT_USERPWD,"apikey:secretkey");
    
	snprintf(json, sizeof(json), "{\"extQr\":\"ORDER?%i\"}", orderNum);
	
	printf("JSONSTRING: %s", &json);
	
	 curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "PUT");	
	
    /* Now specify the POST data */
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, json);
    
    /* For HTTPS */
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

    /* Perform the request, res will get the return code */
    res = curl_easy_perform(curl);
	
    printf("\nResult of Operation: %d\n", res);

    /* always cleanup */
    curl_easy_cleanup(curl);
  }
}

int getProcByName(const char* procName) {
	int pid = -1;
	DIR *dp = opendir("/proc");	
	if (dp != NULL) {
		struct dirent *dirp;
		int nr = 1;
		FILE *fp;
		char filename[25];
		char procCmdLine[100];
		while (pid < 0 && (dirp = readdir(dp))) {
			int id = atoi(dirp->d_name);
            if (id > 0) {				
				snprintf(filename, sizeof filename, "/proc/%i/cmdline", id);
				fp = fopen(filename,"r");
				fgets(procCmdLine, sizeof(procCmdLine), fp); 
				fclose(fp);
				if(strncmp(procName, procCmdLine, strlen(procName)) == 0) {
					pid = id;
				}
			}
		}
	}	
	return pid;
}

//Contains the while loop to take pictures and scan for QR codes
int main() {
	FILE* file;
	int numLockers;
	int orderNum;
	int err;
	pid_t raspistillProcessId;
	char* QRstring;
	char* qmark;
	char* isAdmin;
	char* isOrder;
	int check = 0;
	//char* webcam = "raspistill -t 1 -w 400 -h 300 -o /home/pi/qrcode/image.jpg";
	char* zbar = "zbarimg -q /home/pi/qrcode/image.jpg > /home/pi/qrcode/input.txt";

	// Start Raspistill or get U
	if((raspistillProcessId = getProcByName("raspistill")) == -1) {
		system("raspistill -t 0 -s -w 400 -h 300 -o /home/pi/qrcode/image.jpg &");
		//printf("have to kill it\n");
		//kill(raspistillProcessId, SIGKILL);
	}
	
	//raspistillProcessId = getProcByName("raspistill");
	printf("ProcId:\t%i\n", raspistillProcessId);
	
	numLockers = 4;

	
	//initialize lockerList
	struct Locker lockerList[numLockers];
	for (int i = 0; i < numLockers; i++) {
		lockerList[i].orderNum = 0;
		lockerList[i].isFree = 0;
	}
	

	//make sure the pins on the Pi are ready for use
	initialize(lockerList, numLockers);

	while (1) {
		//must malloc new string to erase old one
		QRstring = malloc(1024*sizeof(char));

		//take a picture and attempt to decode a QR image
		system("date");
		if (raspistillProcessId != -1) kill(raspistillProcessId, SIGUSR1);
		if (check == 1) {
			check = 0;
			sleep(3);
		}
		system(zbar);

		//read the string from the text file
		file = fopen("/home/pi/qrcode/input.txt", "r");
		fgets(QRstring, 1024, (FILE*)file);
		
		//if there was nothing in the file, skip this and take another picture
		//if zbar detected a QR code, the line "QR-Code:" followed by the decoded string
		//would be in the text file
		if (strstr(QRstring, "QR-Code:") != NULL) {
			check = 1;
			//check for ADMIN code
			isAdmin = strstr(QRstring, "ADMIN");
			//check for ORDER code
			isOrder = strstr(QRstring, "ORDER");
			//if neither of these, invalid code
			if (isAdmin == NULL && isOrder == NULL) {
				printf("Invalid QR code.\n");
			}
			else {
				//skip past the "QR-Code:" part
				QRstring = QRstring + 8;
				printf("QRstring after increments: %s\n", QRstring);
				//search for the ?
				qmark = strstr(QRstring, "?");
				printf("qmark: %s\n", qmark);
				//get order number
				qmark++;
				printf("qmark after increment: %s\n", qmark);
				orderNum = atoi(qmark);
				printf("orderNum: %d\n", orderNum);
				//take special action for ADMIN code
				if (isAdmin != NULL) {
					int i;
					//admin code, assign a new locker
					for (i = 0; i < numLockers; i++) {
						if (lockerList[i].orderNum == orderNum) {
							openLocker(orderNum, numLockers, lockerList);
							break;
						}
					}
					if (i == numLockers) {
						printf("You have scanned an ADMIN code: %s\n", QRstring);
						assignLocker(orderNum, numLockers, lockerList);
					}
				}
				else {
					//open locker associated with the QR code
					printf("You have scanned an ORDER code: %s\n", QRstring);
					openLocker(orderNum, numLockers, lockerList);
				}
			}
		}

		//clean-up
		fclose(file);

		//check for new open lockers
		if (head != tail) {
			int index = getFreeLocker(lockerList, numLockers);
			if (index > 0) {
				int order = dequeueLocker();
				assignLocker(order, numLockers, lockerList);
			}
		}
	}


}