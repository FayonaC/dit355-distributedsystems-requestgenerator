# RequestGenerator

RequestGenerator is created to help stress test the entire distributed system with various loads.

**Example/default scenario:** 100 different users each submitting single booking requests for a single dental office within a time-interval of ten seconds.

## MQTT Prerequirements
* Install [Java JDK 8 or above](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
* Install [Maven 3.5 or above](https://maven.apache.org/download.cgi)
* Install MQTT Broker (e.g. Mosquitto) and run it locally on port 1883
   * For a helpful tutorial refer to [Steve's Internet Guide](http://www.steves-internet-guide.com/install-mosquitto-broker/)

## Installation Guide
1. Clone repository to your machine
2. Open a terminal window (e.g. Command Prompt) and move to the root folder of the repository. Enter command `mvn clean install` This will create a target folder.
3. To ensure that installation was successful, check target folder for RequestGenerator.jar file.
4. Move to target folder and enter command `java -jar RequestGenerator.jar`. This will enable the booking component to start publishing requests to the MQTT Broker.

## How to setup a scenario
To change: 
* the number of requests made per user, change the `NUMBER_OF_REQUESTS_PER_USER` variable.
* the time (ms) between requests, change the `INTERVAL` variable.
* the topic to publish requests to, change the `TOPIC` variable.
* the data in the request, change the variables at the top of the `fakeBooking()` method.
