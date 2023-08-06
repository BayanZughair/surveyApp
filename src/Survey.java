import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.JSONObject;
import org.json.JSONException;

public class Survey extends HttpServlet {
    //arrays for survey options and number of votes for any option
    private HashMap<String, Integer> optionsAndVotes;

    //string for survey question
    String question;

    //is survey available flag
    boolean isSurveyAvailable;

    public void init() throws ServletException {
        try
        {
            //create new options-votes map instance
            optionsAndVotes = new HashMap<String, Integer>();

            //read survey file
            ArrayList<String> lines = readSurveyFile();

            //check that there is a question and at least two answers
            if(lines.size() < 3)
                throw new Exception();
            
            //initialize question, options and votes
            question = lines.get(0);
            for(int i = 1;i < lines.size();i++)
                optionsAndVotes.put(lines.get(i), 0);

            //set survey available flag to true
            isSurveyAvailable = true;
        }
        catch(Exception e)
        {
            //set survey available flag to false
            isSurveyAvailable = false;
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try{
            if(isSurveyAvailable == false)
                sendResponse(response, setResponseErrorJson("Survey is not availabl"));
            else
            {
                try{
                    //vote if user didn't vote already
                    if(cookieExists(request.getCookies(), "voted") == false){
                        vote(parseRequestJson(request).getString("vote"));
                        addVotedCookie(response);
                    }
                }
                catch(JSONException e){
                    //send error that parsing request failed
                    sendResponse(response, setResponseErrorJson("Could not parse request as json!"));
                }
                catch(IllegalArgumentException e){
                    //send error that vote failed
                    sendResponse(response, setResponseErrorJson("Failed to vote! " + e));
                }
            }

            //send response json
            sendResponse(response, setResponseJson(true));
        }
        catch(Exception e)
        {
            //print exception in server logs
            System.out.println(e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try{
            if(isSurveyAvailable == false)
                sendResponse(response, setResponseErrorJson("Survey is not availabl"));
            else
            {
                //send response json
                sendResponse(response, setResponseJson(cookieExists(request.getCookies(), "voted")));
            }
        }
        catch(Exception e)
        {
            //print exception in server logs
            System.out.println(e);
        }
    }

    private ArrayList<String> readSurveyFile() throws FileNotFoundException {
        Scanner sc = new Scanner(getServletContext().getResourceAsStream("/" + getServletContext().getInitParameter("SURVEY")));
        ArrayList<String> lines = new ArrayList<String>();
        while(sc.hasNextLine())
            lines.add(sc.nextLine());
        return lines;
    }

    private void vote(String vote) throws IllegalArgumentException {
        if(optionsAndVotes.containsKey(vote) == false)
            throw new IllegalArgumentException("Option not found!");
        optionsAndVotes.put(vote, optionsAndVotes.get(vote) + 1);
    }

    private void addVotedCookie(HttpServletResponse response) {
        Cookie votedCookie = new Cookie("voted", "true");
        votedCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(votedCookie);
    }

    private boolean cookieExists(Cookie[] cookies, String cookieName) {
        if(cookies == null)
            return false;
        for (int i = 0;i < cookies.length;i++)
            if(cookies[i].getName().equals(cookieName))
                return true;
        return false;
    }
    
    private JSONObject parseRequestJson(HttpServletRequest request) throws JSONException, IOException {
        StringBuilder reqStringBuilder = new StringBuilder();
        String line = null;
        while ((line = request.getReader().readLine()) != null)
            reqStringBuilder.append(line);
        return new JSONObject(reqStringBuilder.toString());
    }

    private JSONObject setResponseJson(boolean voted) throws JSONException {
        JSONObject resJson = new JSONObject();
        resJson.put("question", question);
        resJson.put("optionsAndVotes", optionsAndVotes);
        resJson.put("voted", voted);
        return resJson;
    }

    private JSONObject setResponseErrorJson(String error) throws JSONException {
        JSONObject resJson = new JSONObject();
        resJson.put("error", error);
        return resJson;
    }

    private void sendResponse(HttpServletResponse response, JSONObject resJson) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(resJson);
        out.close();
    }
}
