import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Feed;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;
 
public class ReadSpreadsheet {
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport(); 
	public static final JsonFactory JSON_FACTORY = new JacksonFactory();
	  /** Directory to store user credentials. */
	 private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/credentials_storage");

	private static FileDataStoreFactory dataStoreFactory;
	public static final GoogleClientSecrets googleClientSecrets=null;
    public static final String GOOGLE_ACCOUNT_USERNAME = "luckymanomo2@gmail.com"; // Fill in google account username
    //public static final String GOOGLE_ACCOUNT_PASSWORD = "aaaAAA111"; // Fill in google account password
      //public static final String SPREADSHEET_URL = "https://spreadsheets.google.com/feeds/spreadsheets/1L8xtAJfOObsXL-XemliUV10wkDHQNxjn6jKS4XwzYZ8"; //Fill in google spreadsheet URI
      //public static final String SPREADSHEET_URL = "https://docs.google.com/spreadsheets/d/1ePabkKybbOW2BzAmpqqUXTTG49LzZ3-39G2qtzJMtjo";
      //public static final String SPREADSHEET_URL = "https://spreadsheets.google.com/feeds/spreadsheets/1ePabkKybbOW2BzAmpqqUXTTG49LzZ3-39G2qtzJMtjo";
	public static URL SPREADSHEET_FEED_URL;

      public static void main(String[] args) throws IOException, ServiceException{
        /** Our view of Google Spreadsheets as an authenticated Google user. */
        
            // Define the URL to request.  This should never change.
           //SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
           SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/worksheets/1ePabkKybbOW2BzAmpqqUXTTG49LzZ3-39G2qtzJMtjo/public/full");
           try {
			authorize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        SpreadsheetService service =new SpreadsheetService("MySpreadsheetIntegration");
            SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
            
            List<SpreadsheetEntry> spreadsheets = feed.getEntries();

            // Iterate through all of the spreadsheets returned
            for (SpreadsheetEntry spreadsheet : spreadsheets) {
              // Print the title of this spreadsheet to the screen
              System.out.println(spreadsheet.getTitle().getPlainText());
              //spreadsheet.setTitle(new PlainTextConstruct("SheetCool"));
              WorksheetFeed worksheetFeed = service.getFeed(SPREADSHEET_FEED_URL, WorksheetFeed.class);
              List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
              WorksheetEntry worksheet = worksheets.get(0);
           // Send the local representation of the worksheet to the API for
              // modification.
              System.out.println("worksheet.getTitle():"+worksheet.getTitle().getPlainText());
              worksheet.setTitle(new PlainTextConstruct("Updated Worksheet"));
              worksheet.update();
              System.out.println("after:"+spreadsheet.getTitle().getPlainText());
              
            }
            
           

                // Update the local representation of the worksheet.
      }
      /** Authorizes the installed application to access user's protected data. */
      private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,new InputStreamReader(Toolkit.getDefaultToolkit().getClass().getResourceAsStream("/client_secret_.json")));
        
        System.out.println(clientSecrets.getDetails());
        
        // Initialize the data store factory.
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        		HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
            Collections.singleton(SPREADSHEET_FEED_URL.toString())).setDataStoreFactory(dataStoreFactory)
           .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("beckham");
     }
     
}