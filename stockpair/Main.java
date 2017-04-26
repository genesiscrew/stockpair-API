package stockpair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

	static int quoteCode;
	static ArrayList<Integer> optionCodes = new ArrayList<Integer>();
	static Random r = new Random();
	static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	static Date date = new Date();
    static String datee = dateFormat.format(date);
    static String str = datee.replace("\\", "");
	static File file1 = new File(Integer.toString(r.nextInt(1000))+".txt");

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException, JSONException, ParseException {
		
		
		
		String path = file1.getAbsolutePath();
		System.out.println(path);
		file1.createNewFile();
		

		Login();
		getQuoteCode("EUR/USD");
		checkPrice();

	}

	private static void checkPrice() throws IOException, JSONException {
		// System.out.println(" the quote code is " + quoteCode);
		 String  options = null;

		for (int v = 0; v < optionCodes.size(); v++) {
			if (v < optionCodes.size() - 1) {
				if (options == null) {
					options = Integer.toString(optionCodes.get(v)) + ",";
				}
				options += Integer.toString(optionCodes.get(v)) + ",";
			} else {
				options += Integer.toString(optionCodes.get(v));
			}

		}
		
		final String options1 = options;
		
		int delay = 0;   // delay for 5 sec.
		  int interval = 1000*60;  // iterate every sec.
		  Timer timer = new Timer();
		   
		  timer.scheduleAtFixedRate(new TimerTask() {
		          public void run() {
		        	  String urlString3 = "https://www.stockpair.com/tapi2/update?apikey=ADCD7EDC52DD44F490248214CB521E67&o="
		      				+ options1;
		      		// System.out.println(urlString3);
		      		URL url3 = null;
					try {
						url3 = new URL(urlString3);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		      		String output = null;
		      		BufferedReader br3 = null;
					try {
						br3 = new BufferedReader(new InputStreamReader(url3.openStream()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		      		String strTemp3 = "";
		      		try {
						while (null != (strTemp3 = br3.readLine())) {
							// System.out.println(strTemp3);
							output = strTemp3;

						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

		      		JSONObject json = null;
					try {
						json = new JSONObject(output.toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

		      		JSONObject model = null;
					try {
						model = (JSONObject) json.get("model");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		      		JSONArray optionsOutput = null;
					try {
						optionsOutput = (JSONArray) model.get("PricedOptionMessage");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		      		JSONObject spotPrice = null;
					try {
						spotPrice = (JSONObject) optionsOutput.get(0);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		      		Double spot = null;
					try {
						spot = (Double) spotPrice.get("spot0A");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		      		System.out.println("spot price is " + spot);
		      		try {
						FileUtils.writeStringToFile(file1, Double.toString(spot)+", ", true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		          }
		      }, delay, interval);


		

	}

	private static void getQuoteCode(String target) throws JSONException, IOException {

		String urlString2 = "https://www.stockpair.com/tapi2/tradingMetadata/apikey=ADCD7EDC52DD44F490248214CB521E67&lng=en";
		URL url2 = new URL(urlString2);
		String output = null;
		BufferedReader br2 = new BufferedReader(new InputStreamReader(url2.openStream()));
		String strTemp2 = "";
		while (null != (strTemp2 = br2.readLine())) {
			// System.out.println(strTemp2);
			output = strTemp2;

		}

		JSONObject json = new JSONObject(output.toString());

		JSONObject model = (JSONObject) json.get("model");
		JSONArray pairGroups = (JSONArray) model.get("pairGroups");
		for (int i = 0; i < pairGroups.length(); i++) {
			JSONObject group = (JSONObject) pairGroups.get(i);
			if (group.get("name").equals("Currencies")) {
				JSONArray currencypairs = (JSONArray) group.get("pairs");
				for (int x = 0; x < currencypairs.length(); x++) {
					JSONObject underlyings = (JSONObject) currencypairs.get(x);
					JSONArray currencyunderlyings = (JSONArray) underlyings.get("underlyings");
					for (int y = 0; y < currencyunderlyings.length(); y++) {
						JSONObject underlying = (JSONObject) currencyunderlyings.get(y);
						if (underlying.get("symbol").equals(target)) {
							quoteCode = (int) underlyings.get("id");
							JSONArray options = (JSONArray) underlyings.get("options");
							for (int u = 0; u < options.length(); u++) {
								JSONObject option = (JSONObject) options.get(u);
								optionCodes.add(option.getInt("id"));

							}
						}

					}
				}
			}
		}
		// TODO Auto-generated method stub

	}

	private static void Login() throws IOException {
		String urlString = "https://www.stockpair.com/session/authenticate?apikey=ADCD7EDC52DD44F490248214CB521E67&email=hamid.abubakr@gmail.com&password=ad318717&json&wlf&lng=en";
		URL url = new URL(urlString);

		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		String strTemp = "";
		while (null != (strTemp = br.readLine())) {
			// System.out.println(strTemp);
		}

	}

	// System.out.println(json.getJSONObject("message"));

}
