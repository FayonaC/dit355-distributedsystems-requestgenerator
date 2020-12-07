import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RequestGenerator {
    private final static int NUMBER_OF_REQUESTS = 3;

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
            requestGenerator.publishRequest(NUMBER_OF_REQUESTS);
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
            sendRequest(fakeBooking(i));
            System.out.println("Sent request number " + (i+1));
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
     * TODO: Update userid, dentistid, issuance, time to be dynamic
     * @param number request number
     * @return Booking String/JSON
     */
    private String fakeBooking(int number) {
        int userid = 99999;
        int requestid = number;
        int dentistid = 1;
        long issuance = 1602406766314L;
        String time = "2020-12-14 14:30";

        return "{\n \"userid\": " + userid + ",\n\"requestid\": " + requestid +
                ",\n\"dentistid\": " + dentistid + ",\n\"issuance\": " + issuance +
                ",\n\"time\": \"" + time + "\" \n}";
    }
}
