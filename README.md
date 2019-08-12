# SVCU Management App
Download the repository, and use run.bat to run the application.  
The program will automatically read data from the google sheets, and calculate the price for each student with the discount factored in if applicable. The second part of the program will send an email to all the parents of the children.  
  
To modify the email sent, modify email.txt. The first line is the subject, and the following is the email. Use the generic tags, as the program will personalize each email to the recipient for you
IMPORTANT: In the email, you MUST use the SAME tags as they appear in the spreadsheet.  

NOTE: The spreadshee ID needs to be changed for next year, and every year moving forward according to each new sign up sheet made. After that, the response sheet should also be modified so that tags will fit nicely. 
  
## Errors
Token expired or revoked --> delete the tokens folder, then run compile.bat  
Class unsupported or Java not old enough --> run compile.bat  
  
## Developer Notes
The java folder holds all the classes that read / write data, send emails, as well as mask the password on the command line.  
The libs folder holds all the libraries needed for the application.  
The tokens folder is automatically created by the program according to the Google Sheets API.  
Credentials are needed to be able to access Google Sheets.  

## Libraries
If for some reason any of the libraries don't work, you can redownload them from the following links. Please put all the libraries into the libs folder.  
Email sending portion of the program.   
Requires external libaries:  
[javax mail](https://javaee.github.io/javamail/)  
[jaf activation](https://www.oracle.com/technetwork/java/jaf11-139815.html)  
  
Reading / writing data from google sheets portion of the program  
Requires google API libraries:  
[Google API](https://developers.google.com/api-client-library/java/google-api-java-client/download)  
[Google Sheets API](https://developers.google.com/api-client-library/java/apis/sheets/v4)  
Requires Gradle:  
[Gradle v2.3+](https://gradle.org/install/)  

## Authors
Kevin Lin & Joshua Yang  
Copyright © 2019 
