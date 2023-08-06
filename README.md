# Survey App With Java Servlet
A survey app using java servlet. 
In this app there is a servlet that handles get and post requests for getting survey data and voting in survey. The request and response are in json format.
There is also a html file that offer a simple UI to vote from the browser.
After voting from a browser the app set a "voted" cookie in the browser's cookies so you cannot vote again.
The question and options for the survey are written in the survey.txt and can be changed, 
but there should be at least question and 2 answers. 
Note that this app doesn't use any DB so the information is stored just as long as the app running.

## Run this app with apache tomcat 9
1. If you don't have apache tomcat 9 yet you can download it from [here](https://tomcat.apache.org/download-90.cgi).
2. Download this project and unzip it, then move it under the webapps folder in apache home.
3. Start apache server. You can do it by running startup.sh (for bash) or startup.bat (for cmd), both under bin folder in apache home.
4. Go to your browser and go to ```http://localhost:8080/surveyApp/index.html``` and see the survey page.
5. You can also vote or get survey data via api:
     - Getting data - GET request to ```http://localhost:8080/surveyApp/api/survey/```
     - Voting - POST data with json of the form ```{"vote": "some-option"}``` to ```http://localhost:8080/surveyApp/api/survey/```
