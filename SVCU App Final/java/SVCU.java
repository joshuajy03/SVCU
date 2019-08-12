import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public class SVCU 
{
    private static final String APPLICATION_NAME = "SVCU App";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    
    private static ArrayList<String> tags = new ArrayList<String>();
    private static Map<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();
    private static String subject = "";
    private static String body = "";
    private static String emailFile = "email.txt";
    private static String user = "calotto.alva.v.johnson";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException 
    {
        // Load client secrets.
        InputStream in = SVCU.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) 
        {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static int findSibling(String email) 
    {
        for (int i = 0; i < emails.size(); i++) 
        {
            if (discounted.get(i) == 0 && email.equals(emails.get(i))) 
            {
                return i;
            }
        }
        return -1;
    }
    
    public static void addPrices() throws IOException, GeneralSecurityException{
        List<List<Object>> add = new ArrayList();
        for (int i = 0; i < prices.size(); i++) {
            add.add(new ArrayList());
            add.get(i).add(Double.valueOf(prices.get(i)));
        }
        
        ValueRange body = new ValueRange().setValues(add);
        Sheets sheetsService = createSheetsService();
        Sheets.Spreadsheets.Values.Update request =
        sheetsService.spreadsheets().values().update("1myoEKpAo3P1RkdUHAmUx2Pwq1Uxfh3hydmtjOoBCx0A", "M2", body);
        request.setValueInputOption("RAW");

        UpdateValuesResponse response = request.execute();
    }
    
    public static Sheets createSheetsService() throws IOException, GeneralSecurityException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // TODO: Change placeholder below to generate authentication credentials. See
        // https://developers.google.com/sheets/quickstart/java#step_3_set_up_the_sample
        //
        // Authorize using one of the following scopes:
        //   "https://www.googleapis.com/auth/drive"
        //   "https://www.googleapis.com/auth/drive.file"
        //   "https://www.googleapis.com/auth/spreadsheets"
        Credential credential = getCredentials(GoogleNetHttpTransport.newTrustedTransport());

        return new Sheets.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName("Google-SheetsSample/0.1")
            .build();
      }
    
    public static ArrayList<String> emails;
    public static ArrayList<Integer> discounted;
    public static ArrayList<Double> prices;
    public static List<List<Object>> values;
    
    public static void main(String... args) throws IOException, GeneralSecurityException 
    {	
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1myoEKpAo3P1RkdUHAmUx2Pwq1Uxfh3hydmtjOoBCx0A";
        final String range = "B2:L";
        final int price = 300;
        final double discount = 0.9;
        
        //takes information from sheet
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        values = response.getValues();
        
        
        final String range2 = "B1:M1";
        Sheets service2 = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response2 = service2.spreadsheets().values()
                .get(spreadsheetId, range2)
                .execute();
        List<List<Object>> values2 = response2.getValues();
        PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter("data.txt")));
        for(List row : values2)
        {
        	for(Object s : row)
        	{
        		tags.add((String) s);
        		data.put((String) s, new ArrayList<String>());
        	}
        }
        
        
        //store values into easier to acess data structures and do price calculations
        if (values == null || values.isEmpty()) 
        {
            System.out.println("No data found.");
        }
        else 
        {
            emails = new ArrayList();
            discounted = new ArrayList();
            prices = new ArrayList();
            int counter = 0;
            for (List row : values) {
                //take dates from column I (#8) and email from column L (11)
                final String dates = String.format("%s", row.get(7));
                String newEmail = String.format("%s", row.get(10));
                
                //calculate price
                double a = 0;
                for (int i = 1; i <= dates.length(); i++) 
                {
                    if (dates.substring(i-1, i).equals("-")) 
                    {
                        a++;
                    }
                }
                a = a * price;
                
                //search for sibling
                int sibling = findSibling(newEmail);
                if (counter != 0 && sibling > -1) 
                {
                    if (prices.get(sibling) < a) 
                    {
                        prices.set(sibling, prices.get(sibling) * discount);
                        discounted.set(sibling, 1);
                        discounted.add(0);
                    }
                    else 
                    {
                        a = a * discount;
                        discounted.add(1);
                    }
                }
                else 
                {
                    discounted.add(0);
                }
                emails.add(newEmail);
                prices.add(a);
                counter++;
            }
             
            //print out
            counter = 0;
            for (List row: values) 
            {
                System.out.printf("%s, %s, %s, ", row.get(0), row.get(10), row.get(7));
                System.out.print("$" + prices.get(counter) + "\n");
                counter++;
            }
            
            addPrices();
            
            //Email sending portion
            Scanner scan = new Scanner(System.in);
        	
        	System.out.println("Email Sender");
        	System.out.println("\n>> Instructions for Email Sender");
    		System.out.println(">> Enter the file name for the email format.\n"
    				+ "   Email tags should match those of sheet (first line of sheet).\n"
    				+ "   First line of the file should be the subject.");
    		System.out.println(">> Enter your gmail username (without @gmail.com)\n   and password for the system.");
    		System.out.println(">> The program will then send the email(s).\n");
    		
    		/*
    		System.out.print(">> DATA FILE: ");
    		String tempData = scan.nextLine();
    		if(!tempData.equals("")) dataFile = tempData;
    		else System.out.println("Using default: " + dataFile);
    		logData(dataFile);
    		*/
    		logData();
    		
    		System.out.print(">> EMAIL FILE: ");
    		String tempEmail = scan.nextLine();
    		if(!tempEmail.equals("")) emailFile = tempEmail;
    		else System.out.println("Using default: " + emailFile);
    		createEmail(emailFile);
    		
    		System.out.print(">> USERNAME: ");
    		String from = scan.nextLine();
    		if(from.equals("")) 
    		{
    			System.out.println("Using default: " + user);
    			from = user;
    		}
    		
    		String pass = PasswordField.readPassword(">> PASSWORD: ");

    		System.out.println("");
    		
    		ArrayList<String> to = data.get("#email");
    		
    		String tempSubject = subject;
    		String tempBody = body;
    		for(int i = 0; i < to.size(); i++)
        	{
        		for(int j = 0; j < tags.size(); j++)
        		{
        			tempSubject = tempSubject.replaceAll(tags.get(j), data.get(tags.get(j)).get(i));
        			tempBody = tempBody.replaceAll(tags.get(j), data.get(tags.get(j)).get(i));
        		}
        		sendFromGMail(from, pass, to.get(i), tempSubject, tempBody);
        		tempSubject = subject;
        		tempBody = body;
        	}
        	
    		System.out.println("Finished.\n");
        }
    }
    
    private static void sendFromGMail(String from, String pass, String to, String subject, String body) 
    {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try 
        {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(subject);
            message.setText(body);
           
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
          	transport.sendMessage(message, message.getAllRecipients());
           	transport.close();
            
            System.out.println(">> Email sent to " + to + ".");
        }
        catch (AddressException ae) 
        {
            ae.printStackTrace();
        }
        catch (MessagingException me) 
        {
            me.printStackTrace();
        }
    }
    
    private static Scanner open(String file)
    {
    	Scanner input = null;
    	
    	try
		{
			input = new Scanner(new File(file));
		}
		catch(FileNotFoundException e)
		{
			System.err.println(">> File not found.");
		}
    	
    	return input;
    }
    
    private static void logData()
    {
        int counter = 0;
        for (List row : values) 
        {
        	for(int i = 0; i < tags.size() - 1; i++)
        	{
        		String tag = tags.get(i);
        		ArrayList<String> temp = data.get(tag);
            	temp.add((String) row.get(i));
            	data.put(tag, temp);
        	}
        }
        
        ArrayList<String> temp = data.get(tags.get(tags.size() - 1)); 	
        for(Double p : prices)
        {
        	temp.add(Double.toString(p));
        }
        data.put(tags.get(tags.size() - 1), temp);
        
        for(String key : data.keySet())
    	{
    		System.out.print(key + " : ");
    		System.out.print(data.get(key));
    		System.out.println();
    	}
    	
    }
    
    private static void createEmail(String emailFile)
    {
    	Scanner scan = open(emailFile);
    	subject = scan.nextLine();
    	
    	while(scan.hasNextLine())
    	{
    		body += scan.nextLine() + "\n";
    	}
    	
    	System.out.println(subject);
    	System.out.println(body);
    }
}
