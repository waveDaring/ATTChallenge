import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class ATTChallenge {

    private ATTChallenge(){
    }

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
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        String url = args[0];
        ATTChallenge instance = new ATTChallenge();
        instance.handleURL(url);
    }


}
