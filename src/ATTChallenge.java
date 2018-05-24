import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Create a simple class using native java* (no 3rd party libraries like apache commons, spring, etc) that will accept a url as an input (either via arguments or a properties file) and consume a REST endpoint (HTTP GET) and process the output.
 1. The rest endpoint will emit an array of JSON documents, each JSON document will be a complete flat (non nested) record.
 2. For each document display all of the keys of the JSON in standard out
 3. There will be a json array of integers with the key of "numbers", sum all of the integers and display the sum on standard out, add that sum to a running total for the program
 4. Display the total of the integers that were summed for the execution
 *Because java does not have a native JSON parser you can use a JSON library of your choosing (JSON.simple, GSON, Jackson, jettison, JSONP etc)


 NOTE the phrase "emit an array of JSON documents" is awkward, I am assuming that the GET response will be a JSON array of objects like

 [
 {
 "name":"john smith",
 "age" : "33",
 "numbers" : [1,2,3,4]
 }
 ,
 {
 "name":"willma rudolph",
 "age" : "158",
 "numbers" : [10,20,30,40,3333333,333]
 }
 ]
 */

public class ATTChallenge {

    private ATTChallenge(){
    }

    /** handles URL or the path to a file
     *
     * @param url
     */
    private void handleURL(String url){
        int total = 0;
        try{
            Object   json = null;
            if (url.startsWith("http")) {
                json = getJSONFromHTTP(url);
            }else{
                json = getJSONFromFile(url);
            }
            if (json instanceof JSONArray){
                JSONArray jsonArray = (JSONArray) json;
                for (Object o : jsonArray){
                    JSONObject jsonObject = (JSONObject)o;
                    printJSON(jsonObject);
                    total += sumNumbers(jsonObject);
                }
            }
            else if (json instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)json;
                printJSON(jsonObject);
                total += sumNumbers(jsonObject);
            }
        }catch (Exception e){
            System.out.println("Exception: " + e.getClass().getName() + " " + e.getMessage());

        }
        System.out.println("Sum of all number arrays = " + total);
    }

    /**
     * handles local file
     * @param path
     * @return
     * @throws Exception
     */
    private Object getJSONFromFile(String path)throws Exception {
        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader (fileReader);
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null){
            sb.append(line);
        }
        reader.close();
        return JSONValue.parse(sb.toString());
    }

    /**
     * handles URL
     * @param urlString
     * @return
     * @throws Exception
     */
    private JSONObject getJSONFromHTTP(String urlString)throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) throw new Exception("Error in http connection " + responseCode + " for URL " + urlString);
        InputStream inputStream = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null){
            sb.append(line);
        }
        reader.close();
        return(JSONObject) JSONValue.parse(sb.toString());
    }

    private void printJSON(JSONObject jsonObject) {
        System.out.println("Keys:");
        for (Object key : jsonObject.keySet()) {
            System.out.println("   " + key);
        }

    }

    private long sumNumbers(JSONObject jsonObject){
        int sum = 0;
        JSONArray numbersArray = (JSONArray) jsonObject.get("numbers");
        if (numbersArray != null){
            for(Object o : numbersArray){
                try{
                    long number = (Long) o;
                    sum += number;
                }catch (NumberFormatException nfe){
                    System.out.println(o + " cannot be parsed to and Integer");
                }
            }
            System.out.println("Numbers Sum = " + sum);
        }else{
            System.out.println("No key named numbers");
        }
        return sum;
    }

    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("Usage: ATTChallenge URL");
            System.exit(0 );
        }
        String url = args[0];
        ATTChallenge instance = new ATTChallenge();
        instance.handleURL(url);
    }


}
