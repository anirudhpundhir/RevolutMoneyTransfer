package currency;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConvertor {

    // API Provider URL
    private static final String API_PROVIDER = "https://api.exchangeratesapi.io";

    public static double getConversionRate(String fromCurrencyCode, String toCurrencyCode) {
        if (fromCurrencyCode.equals(toCurrencyCode)) {
            return 1;
        }

        if ((fromCurrencyCode != null && !fromCurrencyCode.isEmpty())
                && (toCurrencyCode != null && !toCurrencyCode.isEmpty())) {

            CurrencyConversionResponse response = getResponse(API_PROVIDER + "/latest?base=" + fromCurrencyCode);

            if (response != null) {

                String rate = response.getRates().get(toCurrencyCode);

                double conversionRate = Double.valueOf((rate != null) ? rate : "0.0");

                return conversionRate;
            }

        }

        return 0.0;
    }

    // Method to get the response from API
    private static CurrencyConversionResponse getResponse(String strUrl) {

        CurrencyConversionResponse response = null;

        Gson gson = new Gson();
        StringBuffer sb = new StringBuffer();

        if (strUrl == null || strUrl.isEmpty()) {

            System.out.println("Application Error");
            return null;
        }

        URL url;
        try {
            url = new URL(strUrl);

            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            InputStream stream = connection.getInputStream();

            int data = stream.read();

            while (data != -1) {

                sb.append((char) data);

                data = stream.read();
            }

            stream.close();

            response = gson.fromJson(sb.toString(), CurrencyConversionResponse.class);

        } catch (MalformedURLException e) {

            System.out.println(e.getMessage());
            e.printStackTrace();

        } catch (IOException e) {

            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("What is your currency code ?");
        String fromCurrencyCode = scanner.next();

        System.out.println("Enter the Amount :");
        double amount = scanner.nextDouble();

        System.out.println("Enter the Currency Code that you want to covert : ");
        String toCurrencyCode = scanner.next();

        fromCurrencyCode = fromCurrencyCode.toUpperCase();
        toCurrencyCode = toCurrencyCode.toUpperCase();

        // Currency Conversion
        double coversionRate = getConversionRate(fromCurrencyCode, toCurrencyCode);

        System.out.println(amount + " " + fromCurrencyCode + " is equivalent to " + (coversionRate * amount) + " " + toCurrencyCode + " today.");

        scanner.close();
    }

}