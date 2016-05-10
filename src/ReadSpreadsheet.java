import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
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
    public static URL SPREADSHEET_FEED_URL;

      public static void main(String[] args) throws IOException, ServiceException{
        /** Our view of Google Spreadsheets as an authenticated Google user. */
        
           SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
        
           Credential credential=null;
           try {credential=authorize();} catch (Exception e) {e.printStackTrace();}
           SpreadsheetService service =new SpreadsheetService("MySpreadsheetIntegration");
           //credential.refreshToken();
           String accessToken=credential.getAccessToken();
           System.out.println("accessToken:"+accessToken);
           //service.setUserToken(accessToken);
           service.setAuthSubToken(accessToken);
           
           
            SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
            //service.setAuthSubToken(accessToken);
            
            List<SpreadsheetEntry> spreadsheets = feed.getEntries();

            // Iterate through all of the spreadsheets returned
            for (SpreadsheetEntry spreadsheet : spreadsheets) {
            	 // Get the first worksheet of the first spreadsheet.
                // TODO: Choose a worksheet more intelligently based on your
                // app's needs.
            	
            	System.out.println(spreadsheet.getTitle().getPlainText());
            	//System.out.println(spreadsheet.getWorksheetFeedUrl());
            	spreadsheet.setTitle(new PlainTextConstruct("TESTDDD"));
            	System.out.println(spreadsheet.getTitle().getPlainText());
            	spreadsheet.setUpdated(new DateTime(new Date(), TimeZone.getTimeZone("Asia/Bangkok")));
            	
            	
            	WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
            	    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
            	    WorksheetEntry worksheet = worksheets.get(0);

            	    System.out.println(worksheet.getTitle().getPlainText());
            	    System.out.println(worksheet.getCellFeedUrl());
            	    URL cellFeedURL=worksheet.getCellFeedUrl();
            	    CellFeed cellFeed=service.getFeed(cellFeedURL, CellFeed.class);
            	    for (CellEntry cell : cellFeed.getEntries()) {
            	    	System.out.println(cell.getTitle().getPlainText()+":"+cell.getCell().getInputValue());
            	    	if(cell.getTitle().getPlainText().equals("A2")){
            	    		cell.changeInputValueLocal("10");
            	    		cell.update();
            	    	}
            	    }
            	    
            	    /*for (CellEntry cell : cellFeed.getEntries()) {
            	    	System.out.println(cell.getTitle().getPlainText()+":"+cell.getCell().getInputValue());
            		}*/
            	    
            }
        
            
      }
      /** Authorizes the installed application to access user's protected data. */
      private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,new InputStreamReader(Toolkit.getDefaultToolkit().getClass().getResourceAsStream("/client_secret2.json")));
        
        //System.out.println(clientSecrets.getDetails());
        
        // Initialize the data store factory.
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        
        List<String> SCOPES = Arrays.asList(SPREADSHEET_FEED_URL.toString());
        
        /*String[] SCOPESArray = {"https://spreadsheets.google.com/feeds", "https://spreadsheets.google.com/feeds/spreadsheets/private/full", "https://docs.google.com/feeds"};
        final List<String> SCOPES = Arrays.asList(SCOPESArray);*/
        
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        		HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
        		SCOPES).setDataStoreFactory(dataStoreFactory)
           .build();
        
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("luckymanomo2@gmail.com");
     }
     
}