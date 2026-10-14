import java.net.HttpURLConnection;
import java.net.URL;

public class HealthCheck {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.exit(1);
        }

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection)
                new URL(args[0]).openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            int status = connection.getResponseCode();

            if (status < 200 || status >= 300) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.exit(1);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}