# SVCU Management App
Currently, download SVCU Test App to have an entire working program, then run run.bat on the command line to use.

## Email Tester
Email sending portion of the program.   
Requires external libaries:  
[javax mail](https://javaee.github.io/javamail/)  
[jaf activation](https://www.oracle.com/technetwork/java/jaf11-139815.html)  

## TakeFromSheets
Reading data from google sheets portion of the program  
Requires google API libraries:  
[Google API](https://developers.google.com/api-client-library/java/google-api-java-client/download)  
[Google Sheets API](https://developers.google.com/api-client-library/java/apis/sheets/v4)  
Requires Gradle:  
[Gradle v2.3+](https://gradle.org/install/)  

## Errors
Token expired or revoked --> delete the tokens folder, then run compile.bat  
Class unsupported or Java not old enough --> run compile.bat, then run run.bat
