/*
 * Copyright 2021 HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guilhermebertol.utils;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MqttMensagem {


    public static void enviarMensagem(String mensagem){

        final String host = "91934fea2397450891cbfa6e19508917.s1.eu.hivemq.cloud";
        final String username = "administrador";
        final String password = "yS.:0B9G5h2j";

        /**
         * Building the client with ssl.
         */
        final Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(8884)
                .sslWithDefaultConfig()
                .webSocketConfig()
                .serverPath("mqtt")
                .applyWebSocketConfig()
                .buildBlocking();

        /**
         * Connect securely with username, password.
         */
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();

        System.out.println("Connected successfully");

        /**
         * Subscribe to the topic "my/test/topic" with qos = 2 and print the received message.
         */
        /*client.subscribeWith()
                .topicFilter("my/test/topic")
                .qos(MqttQos.EXACTLY_ONCE)
                .send();*/

        /**
         * Set a callback that is called when a message is received (using the async API style).
         * Then disconnect the client after a message was received.
         */
       /* client.toAsync().publishes(ALL, publish -> {
            System.out.println("Received message: " + publish.getTopic() + " -> " + UTF_8.decode(publish.getPayload().get()));

            client.disconnect();
        });*/

        /**
         * Publish "Hello" to the topic "my/test/topic" with qos = 2.
         */
        client.publishWith()
                .topic("my/test/topic")
                .payload(UTF_8.encode(mensagem))
                .qos(MqttQos.EXACTLY_ONCE)
                .send();
    }

}