import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.UUID;

/**
 * RequestGenerator is created to help stress test the entire distributed system with various loads.
 * Example/default scenario: 100 different users each submitting single booking requests for a single dental office
 * within a time-interval of ten seconds.
 */
public class RequestGenerator {
    private final static int NUMBER_OF_REQUESTS_PER_USER = 3;

    private final static long INTERVAL = 10000; // Milliseconds

    private final static String TOPIC = "BookingRequest";

    private final static String BROKER = "tcp://localhost:1883";

    private final static String USER_ID = "request-generator";

    private final IMqttClient middleware;

    public RequestGenerator() throws MqttException {
        middleware = new MqttClient(BROKER, USER_ID);
        middleware.connect();
    }

    public static void main(String[] args) {
        try {
            RequestGenerator requestGenerator = new RequestGenerator();
            requestGenerator.publishRequest(NUMBER_OF_REQUESTS_PER_USER);
            requestGenerator.close();
        } catch (Exception e) {
            System.err.println("RIP RequestGenerator!");
            e.printStackTrace();
        }
    }

    private void close() throws MqttException {
        middleware.disconnect();
        middleware.close();
    }

    /**
     * Publishes requests n number of times to the broker.
     * @param number n times to publish request
     * @throws MqttException
     */
    private void publishRequest(int number) throws MqttException {
        for (int i = 0; i < number; i++) {
            sleep();
            sendRequest(fakeBooking(i));
            System.out.println("Sent request number " + (i+1));
        }
    }

    /**
     * Puts the thread to sleep for as long as the INTERVAL is set to. Small modifications, such as the parameters
     * and the use of the INTERVAL variable, have been made to the example code.
     */
    private void sleep() {
        try {
            Thread.sleep(INTERVAL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Publishes a single request.
     * @param reqmsg Message to be published
     * @throws MqttException
     */
    private void sendRequest(String reqmsg) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setPayload(reqmsg.getBytes());
        middleware.publish(TOPIC, message);
    }

    /**
     * Creates a fake booking based on the number of the request.
     * TODO: Update userid, dentistid, time to be dynamic
     * @param number request number
     * @return Booking String/JSON
     */
    private String fakeBooking(int number) {
        int userid = 99999; // UUID.randomUUID() ?
        int requestid = number+1;
        int dentistid = 1;
        long issuance = System.currentTimeMillis();
        String time = "2020-12-14 14:30";

        // The print outs are just for testing purposes
        System.out.println("Possible user id: " + UUID.randomUUID());
        System.out.println("{\n \"userid\": " + userid + ",\n\"requestid\": " + requestid +
                ",\n\"dentistid\": " + dentistid + ",\n\"issuance\": " + issuance +
                ",\n\"time\": \"" + time + "\" \n}");

        return "{\n \"userid\": " + userid + ",\n\"requestid\": " + requestid +
                ",\n\"dentistid\": " + dentistid + ",\n\"issuance\": " + issuance +
                ",\n\"time\": \"" + time + "\" \n}";
    }
}
