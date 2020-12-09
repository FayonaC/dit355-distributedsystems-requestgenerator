import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * RequestGenerator is created to help stress test the entire distributed system with various loads.
 * Example/default scenario: 100 different users each submitting single booking requests for a single dental office
 * within a time-interval of ten seconds.
 */
public class RequestGenerator {
    private final static int NUMBER_OF_REQUESTS_PER_USER = 1;

    private final static int NUMBER_OF_USERS = 100;

    private final static int DENTIST_ID = 1;

    private final static String TOPIC = "BookingRequest";

    private final static String BROKER = "tcp://localhost:1883";

    private final static String CLIENT_ID = "request-generator";

    private final IMqttClient middleware;

    public RequestGenerator() throws MqttException {
        middleware = new MqttClient(BROKER, CLIENT_ID);
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
     * Publishes requests n number of times to the broker per NUMBER_OF_USERS.
     * j represents userid. userid will start at 1 and increment from there.
     * i represents requestid. requestid will start at 1 and increment from there.
     * @param number n times to publish request
     * @throws MqttException
     */
    private void publishRequest(int number) throws MqttException {
        for (int i = 1; i < number+1; i++) {
            for (int j = 1; j < NUMBER_OF_USERS+1; j++) {
                sendRequest(fakeBooking(j, i));
            }
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
     * Creates a fake booking.
     * TODO: should userid = UUID.randomUUID()?
     * @param userid user id
     * @param requestid request id/number of the request
     * @return Booking String/JSON
     */
    private String fakeBooking(int userid, int requestid) {
        long issuance = System.currentTimeMillis();
        String time = getVaryingDate();

        String booking = "{\n \"userid\": " + userid + ",\n\"requestid\": " + requestid +
                            ",\n\"dentistid\": " + DENTIST_ID + ",\n\"issuance\": " + issuance +
                            ",\n\"time\": \"" + time + "\" \n}";

        System.out.println(booking);
        return booking;
    }

    /**
     * Varies the date based on the current date.
     * Randomizes days and hours to add to the current date and time.
     * Minutes will always be 00, e.g. 12:00, to ensure a time slot is found.
     * @return String with a varied date and time.
     */
    private String getVaryingDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime variedDate = now.plusDays(new Random().nextInt((30-1) +1))
                .plusHours(new Random().nextInt((60-1) + 1))
                .withMinute(0);

        return variedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
