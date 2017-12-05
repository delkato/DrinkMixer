#include <SoftwareSerial.h>

int led_on;
int pastState;
int buttonState;

int relay = 12;
int pumpOne = 8;
int pumpTwo = 9;
int pumpThree = 10;

int ledGreen = 4;
int ledRed = 6;

int bluetoothTx = 2;
int bluetoothRx = 3;

int incomingByte;
int measure1;
int measure2;
int measure3;
int number;
int count;
boolean busy;
boolean readyToPump;

SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

// the setup routine runs once when you press reset:
void setup() {
 Serial.begin(9600);  // Begin the serial monitor at 9600bps

  bluetooth.begin(115200);  // The Bluetooth Mate defaults to 115200bps
  bluetooth.print("$");  // Print three times individually
  bluetooth.print("$");
  bluetooth.print("$");  // Enter command mode
  delay(100);  // Short delay, wait for the Mate to send back CMD
  bluetooth.println("U,9600,N");  // Temporarily Change the baudrate to 9600, no parity
  // 115200 can be too fast at times for NewSoftSerial to relay the data reliably
  bluetooth.begin(9600);  // Start bluetooth serial at 9600

  
  // make the pushbutton's pin an input:
  pinMode(relay, INPUT);
  pinMode(ledGreen, OUTPUT);
  pinMode(ledRed, OUTPUT);
  pinMode(pumpOne, OUTPUT);
  pinMode(pumpTwo, OUTPUT);
  pinMode(pumpThree, OUTPUT);
  count = 0;
  busy = false;
  readyToPump = false;
  measure1 = 0;
  measure2 = 0;
  measure3 = 0;
  digitalWrite(pumpOne, HIGH); // set to high for pumps to be off
  digitalWrite(pumpTwo, HIGH);
  digitalWrite(pumpThree, HIGH);
  digitalWrite(ledGreen, LOW); // led green when pumping
  digitalWrite(ledRed, HIGH); // led red when not pumping
}

// the loop routine runs over and over again forever:
void loop() {
  if (bluetooth.available()) {
    readyToPump = false;
    char incomingByte = (char)bluetooth.read();
    // new drink measurement marker
    if (incomingByte == 'D') {
      count++;
      // we got to the second measurement
      if (count == 2) {
        measure1 = number;
        number = 0;
      // we got to the third measurement
      } else if (count == 3) {
        measure2 = number;
        number = 0;
      }
    // no more numbers
    } else if (incomingByte == 'E') {
      measure3 = number;
      number = 0;
      count = 0;
      readyToPump = true;
    } else {
      number = (number * 10) + (incomingByte - '0');
    }
  }

  int relayState = digitalRead(relay);

  // wait for all bluetooth data to be received before pumping
  if (readyToPump) {
    // different delays for each pump to accomodate for different tubing lengths & pump speeds
    digitalWrite(ledGreen, HIGH);
    digitalWrite(ledRed, LOW);
    if ((measure1 != 0) and !busy) {
      delay(1000);
      digitalWrite(pumpOne, LOW);
      busy = true;
      Serial.println("ingredient one");
      for (int i = 0; i < measure1; i++) {
        delay(1100);
        Serial.println(i);
      }
      digitalWrite(pumpOne, HIGH);
      measure1 = 0;
      busy = false;
    }
    if ((measure2 != 0) and !busy) {
      delay(1000);
      digitalWrite(pumpTwo, LOW);
      busy = true;
      Serial.println("ingredient two");
      for (int i = 0; i < measure2; i++) {
        delay(2000);
        Serial.println(i);
      }
      digitalWrite(pumpTwo, HIGH);
      measure2 = 0;
      busy = false;
    }
    if ((measure3 != 0) and !busy) {
      delay(1000);
      digitalWrite(pumpThree, LOW);
      busy = true;
      Serial.println("ingredient two");
      for (int i = 0; i < measure3; i++) {
        delay(1750);
        Serial.println(i);
      }
      digitalWrite(pumpThree, HIGH);
      measure3 = 0;
      busy = false;
      readyToPump = false;
      digitalWrite(ledGreen, LOW);
      digitalWrite(ledRed, HIGH);
    }
  }
  
  delay(100);        // delay in between reads for stability
}
