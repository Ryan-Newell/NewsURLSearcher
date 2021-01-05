
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Searcher {
    public static void main(String[] args) throws IOException {
        String[] newsStrings= {"https://www.cnn.com", "https://abcnews.go.com"};
        String[] keywords = {"trump", "georgia"};

        for (String website : newsStrings) {
            System.out.println("\n" + website);
            String finalString = getHTML(website);
            List<String> results = extractUrls(finalString);
            for (String keyword : keywords) {
                List<String> keywordResults = keywordSearch(results, keyword);
                System.out.println(keyword);
                for (String item : keywordResults) {
                    System.out.println(item);
                }
            }
        }
    }

    public static String getHTML(String website) throws IOException {
        URL url = new URL(website);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int status = connection.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
        {
            String current = inputLine;
            content.append(current);
        }
        in.close();
        connection.disconnect();
        return content.toString();
    }

    /**
     * From: https://stackoverflow.com/questions/5713558/detect-and-extract-url-from-a-string
     * Parsing links is more difficult than I anticipated
     * Returns a list with all links contained in the input
     */
    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    public static List<String> keywordSearch (List<String> urls, String keyword) {
        List<String> keywordHits = new ArrayList<String>();
        for (String item : urls) {
            if (item.contains(keyword)) {
                keywordHits.add(item);
            }
        }
        return keywordHits;
    }
}
