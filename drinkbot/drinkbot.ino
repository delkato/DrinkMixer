#include <SoftwareSerial.h>

int led_on;
int pastState;
int buttonState;

int relay = 12;

int ledGreen = 4;
int ledRed = 3;
int pushButton = 5;

int bluetoothTx = 2;  // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3;  // RX-I pin of bluetooth mate, Arduino D3

int incomingByte;
int measure1;
int measure2;
int measure3;
int number;
int count;

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
  pinMode(pushButton, INPUT_PULLUP);
  pinMode(relay, INPUT);
  pinMode(ledGreen, OUTPUT);
  pinMode(ledRed, OUTPUT);
  count = 0;
}

// the loop routine runs over and over again forever:
void loop() {
  if (bluetooth.available()) {
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
    } else {
      number = (number * 10) + (incomingByte - '0');
    }
  }
  
  // read the input pin:
  pastState = buttonState;
  buttonState = digitalRead(pushButton);

  int relayState = digitalRead(relay);
  Serial.println(relayState);

  if (buttonState == 0 && pastState == 1) {
    led_on = led_on ^ 1;
    digitalWrite(ledGreen, led_on);    
  }

  if (digitalRead(ledGreen) == LOW) {
    digitalWrite(ledRed, LOW);
  } else {
    digitalWrite(ledRed, HIGH);
  }
  
  // print out the state of the button:
  delay(1);        // delay in between reads for stability
}
