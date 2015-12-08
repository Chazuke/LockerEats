#include <wiringPi.h>

int main() {
	wiringPiSetupGpio();
	pinMode(4, OUTPUT);
	int i = 0;
	while (i < 5) {
	printf("%d\n", i++);
	digitalWrite(4, LOW);
	delay(500);
	digitalWrite(4, HIGH);
	delay(500);
	}
	return 0;
}