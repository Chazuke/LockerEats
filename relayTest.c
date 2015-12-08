#include <wiringPi.h>

int main() {
	wiringPiSetupGpio();
	pinMode(4, OUTPUT);
	pinMode(17, OUTPUT);
	pinMode(27, OUTPUT);
	pinMode(22, OUTPUT);
	printf("ONE\n");
	digitalWrite(4, LOW);
	delay(1000);
	printf("TWO\n");
	digitalWrite(17, LOW);
	delay(1000);
	printf("THREE\n");
	digitalWrite(27, LOW);
	delay(1000);
	printf("FOUR\n");
	digitalWrite(22, LOW);
	delay(1000);
	printf("Done!\n");
	digitalWrite(4, HIGH);
	digitalWrite(17, HIGH);
	digitalWrite(27, HIGH);
	digitalWrite(22, HIGH);
}