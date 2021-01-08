
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes in a list of websites from websites.txt and a list of keywords from keywords.txt. Searches the websites for
 * URLs, then finds links with the relevant keyword. Outputs results to results.txt and system. 
 * Note that most news sites use keywords in their URL names, but other sites just use random alphanumeric strings,
 * so this program will not do anything with those types of URLs.
 */
class Searcher {
    /**
     * Main function. Runs the searches and writes the results to file
     * @param args takes no parameters
     * @throws IOException If the connection fails
     */
    public static void main(String[] args) throws IOException {
        List<String> newsStrings= getArrayFromFile("websites.txt");
        List<String> keywords = getArrayFromFile("keywords.txt");
        File outFile = new File("src/results.txt");
        FileWriter writer = new FileWriter(outFile);
        for (String website : newsStrings) {
            System.out.println("\n" + website);
            writer.write("\n" + website + "\n");
            String finalString = getHTML(website);
            List<String> results = extractUrls(finalString);
            for (String keyword : keywords) {
                List<String> keywordResults = keywordSearch(results, keyword.toLowerCase());
                System.out.println(keyword);
                writer.write(keyword + "\n");
                for (String item : keywordResults) {
                    System.out.println(item);
                    writer.write(item + "\n");
                }
            }
        }
        writer.close();
    }

    /**
     * Performs a get request on a website and returns the HTML as a string
     * @param website the website. Expected to be the home page of a news site
     * @return The HTML as a string of the website
     * @throws IOException If the connection fails
     */
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
     * Parsing links correctly is more difficult than I anticipated
     * @param text the HTML string
     * @return a list with all links contained in the input
     */
    public static List<String> extractUrls(String text)
    {
        Set<String> set = new LinkedHashSet<>();
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }
        set.addAll(containedUrls);
        containedUrls.clear();
        containedUrls.addAll(set);
        return containedUrls;
    }

    /**
     * Searches the list of urls for the given keyword
     * @param urls the input list of urls
     * @param keyword the keyword to be searched
     * @return a list of urls that contain the input keyword
     */
    public static List<String> keywordSearch (List<String> urls, String keyword) {
        List<String> keywordHits = new ArrayList<String>();
        for (String item : urls) {
            if (item.contains(keyword)) {
                keywordHits.add(item);
            }
        }
        return keywordHits;
    }

    /**
     * Takes the file provided and creates a list of entries. Expects file to be line delimited.
     * @param filename the name of the file to be read
     * @return the array of items from the file
     */
    private static List<String> getArrayFromFile (String filename) {
        Set<String> set = new LinkedHashSet<>();
        List<String> arrayList = new ArrayList<String>();
        File file = new File("src/" + filename);

        try {
            Scanner sc = new Scanner(file);
            while(sc.hasNext()) {
                arrayList.add(sc.next());
            }
            set.addAll(arrayList);
            arrayList.clear();
            arrayList.addAll(set);
            return arrayList;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
