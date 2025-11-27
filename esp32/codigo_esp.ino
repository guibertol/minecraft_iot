#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <LiquidCrystal.h>
#include <DHT.h>
#include <ESP32Servo.h>

// initialize the library with the numbers of the interface pins
LiquidCrystal lcd(19, 23, 18, 17, 16, 15);
                            //TX, RX

#define DHT11_PIN  21
DHT dht11(DHT11_PIN, DHT11);

//Definindo porta do servo
static const int servoPin = 13;
Servo servo1;

//potenciometro do dia/noite
#define POTENTIOMETER_PIN  33 

//Definindo porta do LED
const int PINO_LED = 12; // PINO D15

//Definindo porta do BUZZER
const int PINO_BUZZER = 27;

// WiFi
//const char* ssid = "Wifi_Unifique_522";         // The SSID (name) of the Wi-Fi network you want to connect
//const char* password = "94503387"; // The password of the Wi-Fi network

const char* ssid = "Gemeos_2GHz";         // The SSID (name) of the Wi-Fi network you want to connect
const char* password = "@Gl05081999"; // The password of the Wi-Fi network

//const char* ssid = "AndroidAP36a8";
//const char* password = "qcel2231";

// MQTT Broker
const char *mqtt_broker = "1a441860d5394eccbe038e6f1fc58a10.s1.eu.hivemq.cloud";
const char *topic = "iot/mensagem";
const char *mqtt_username = "ESP32";
const char *mqtt_password = "cv)SkM{2i045";
const int mqtt_port = 8883;

WiFiClientSecure espClient;
PubSubClient client(espClient);

void setup() {
  
  // Set software serial baud to 115200;
  Serial.begin(115200);
  
  delay(2000);
  
  lcd.begin(16, 2);
  lcd.clear(); 
  lcd.setCursor(0, 0);
  lcd.print("                ");
  lcd.setCursor(0, 1);
  lcd.print("                ");
  lcd.setCursor(0,0);
  lcd.print("MINECRAFT E IOT");

  delay(2000);

  // Connecting to a WiFi network
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.println("Connecting to WiFi..");
  }
  Serial.println("Connected to the Wi-Fi network");
  //connecting to a mqtt broker

    // desativa validação do certificado (bom para teste inicial)
  espClient.setInsecure();
  client.setServer(mqtt_broker, mqtt_port);
  client.setCallback(callback);
  while (!client.connected()) {
      String client_id = "esp32-client-";
      client_id += String(WiFi.macAddress());
      Serial.printf("The client %s connects to the public MQTT broker\n", client_id.c_str());
      if (client.connect(client_id.c_str(), mqtt_username, mqtt_password)) {
          Serial.println("Public EMQX MQTT broker connected");
      } else {
          Serial.println("failed with state ");
          Serial.println(client.state());
          delay(2000);
      }
  }

  // Publish and subscribe
  client.subscribe(topic);
  client.subscribe("acao/porta");
  client.subscribe("acao/alavanca");
  client.subscribe("acao/botao");

  dht11.begin();

  servo1.attach(servoPin);
  servo1.write(0);

  pinMode(PINO_LED, OUTPUT);
  pinMode(PINO_BUZZER, OUTPUT);

}

void callback(char *topic, byte *payload, unsigned int length) {

  String msg = "";
  
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);

  if (String(topic) == "iot/mensagem") {

    Serial.print("Message:");
    for (int i = 0; i < length; i++) {
        Serial.print((char) payload[i]);
        msg += (char) payload[i];
    }
    Serial.println();
    Serial.println("-----------------------");
    
    deslizante(msg);
  
  }else if(String(topic) == "acao/porta"){
    if(payload[0] == '0'){
      servo1.write(0);
    }else{
      servo1.write(90);
    }
  }else if(String(topic) == "acao/alavanca"){
    if(payload[0] == '0'){
      digitalWrite(PINO_LED, LOW);
    }else{
      digitalWrite(PINO_LED, HIGH);
    }
  }else if(String(topic) == "acao/botao"){
    if(payload[0] == '1'){
      tocarBuzzer();
    }
  }

}

void loop() {

  client.loop();
  envTemperatura();
  envPotenciometro();

}

void deslizante(String texto){

  texto = texto+" ";
  int textLength = texto.length();

  lcd.setCursor(0, 0);
  lcd.print("                ");

  lcd.setCursor(0, 1);
  lcd.print("                ");

  if(textLength > 16){

    for (int i = 0; i < textLength; i++) {
      String toDisplay = "";

      // pega 16 caracteres a partir do índice i
      for (int j = 0; j < 16; j++) {
        int charIndex = (i + j) % textLength; // loop circular
        toDisplay += texto[charIndex];
      }

      lcd.setCursor(0, 0);
      lcd.print(toDisplay);

      delay(450); // velocidade do scroll (ajuste para mais rápido/lento)
    }

  }else{
    lcd.setCursor(0, 0);
    lcd.print(texto);
    delay(750);
  }

  lcd.clear(); 
  lcd.setCursor(0, 0);
  lcd.print("                ");
  lcd.setCursor(0, 1);
  lcd.print("                ");
  lcd.setCursor(0,0);
  lcd.print("MINECRAFT E IOT");

}

void envTemperatura(){

  static unsigned long ultimaLeitura = 0;
  const unsigned long intervalo = 5000; // a cada 5 segundos

  if (millis() - ultimaLeitura < intervalo) return;
  ultimaLeitura = millis();

  // read humidity
  float humi  = dht11.readHumidity();
   // read temperature in Celsius
  float temp = dht11.readTemperature();

  char payload[50];
  snprintf(payload, sizeof(payload), "Temperatura: %.1f_ | Umidade: %.1f", temp, humi);

  client.publish("sensor/dht11", payload);

}

void envPotenciometro(){

  static unsigned long ultimaLeitura = 0;
  const unsigned long intervalo = 5000; // a cada 5 segundos

  if (millis() - ultimaLeitura < intervalo) return;
  ultimaLeitura = millis();

  
  int analogValue = analogRead(POTENTIOMETER_PIN);
  int brightness = map(analogValue, 0, 4095, 0, 255);

  char msg[10];
  sprintf(msg, "%d", brightness);
  client.publish("sensor/potenciometro", msg);

}

void tocarBuzzer(){

  digitalWrite(PINO_BUZZER, HIGH); // Ligar o buzzer
  delay(150); // Deixa o buzzer ligado por 1 segundo
  digitalWrite(PINO_BUZZER, LOW); // Desligar o buzzer
  delay(150);
  digitalWrite(PINO_BUZZER, HIGH); // Ligar o buzzer
  delay(150); // Deixa o buzzer ligado por 1 segundo
  digitalWrite(PINO_BUZZER, LOW); // Desligar o buzzer
  
}
