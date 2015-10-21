#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <unistd.h>
#include <stdlib.h>
#include <netdb.h>
#include <string.h>
#include <wiringPi.h>
#include <signal.h>
#include <curl/curl.h>



void updateServer(int orderN) {
	CURL *curl;
	CURLcode res;
	char json[200];
	struct curl_slist *headers=NULL;
	
	char url[50];
	snprintf(url, sizeof(url), "http://agglo.mooo.com:3000/api/v1/orders/%i", orderN);
	
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
		
		snprintf(json, sizeof(json), "{\"extQr\":\"ORDER?%i\"}", orderN);
		
		printf("JSONSTRING: %s", &json);
		
		 curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "PUT");	
		
		/* Now specify the POST data */
		curl_easy_setopt(curl, CURLOPT_POSTFIELDS, json);
		
		/* For HTTPS */
		curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

		
		//curl_easy_setopt(curl, CURLOPT_NOBODY, 1L);
		/* Perform the request, res will get the return code */
		res = curl_easy_perform(curl);
		
		long http_code = 0;
		curl_easy_getinfo (curl, CURLINFO_RESPONSE_CODE, &http_code);
		
		printf("\nResult of Operation: %ld\n", http_code);

		/* always cleanup */
		curl_easy_cleanup(curl);
	}
}

int main(int argc, char **argv) {
	updateServer(atoi(argv[1]));
}

