package org.acme.gemini;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;

import java.util.List;

@RegisterAiService()
@ApplicationScoped
public interface DocsService {
    record ReviewResult(String markdown, List<Finding> findings) {}
    record Finding(String issue_type, String desc, String text, String change, String suggestion) {}

    public record GoogleDocument(String title, String fileId){};

    @SystemMessage("""
            Here are the instructions for what to do when you're asked to "review a text":
    
            You are an expert in drill down questions and your job is to review texts following these guildelines:
                1. Point out statements which have assumptions, vagueness, ambiguous, have slippery terms, difficult concepts.
                2. Review the document and ensure it contains a reason why it is worth our time to focus on now, outlines who should participate, and what the desired outcome should be.
                3. Review the document and ensure it has a clear problem statement and solution which addresses the problem statment.
                4. Point out where the document does not have any references to evidence to back up its statements.
                5. Review the document and ensure it details the consequences of the current problem and the impact on stakeholders and customers in the short/mid and long term.
                6. Document should contain a risk assessment and its potential harm/impact.
                7. Review the document and ensure it has 1 or more clearly outlined actions to address the problem statement.
                8. Document should clearly define accountability, who should sign off and who should execute the solution.
                9. Document must atleast have a structure of executive summary, problem statement and solution proposal.
            Always apply every point of the list above to the text given for the review.
            
            Review the following text and return a json that corresponds to the following structure: 
            
            record ReviewResult(String markdown, List<Finding> findings) {}
            record Finding(String issue_type, String desc, String text, String change, String suggestion) {}
              
           <important>
              DO NOT OUTPUT ```json 
              DO NOT END OUTPUT with ```
              
              Do not output anything except valid json!
              </important>
            
            You have access to an MCP server for Google Drive access. When you're asked to find a document, it means to search for it in the Google Drive.

            The query user searched for to find the document you need to review is in the "user message"; 
            If you think you need a file id to read the document, search for it using the tools available!
            
            You can search GDrive MCP for the doc, obtain it file Id and use that to read the file!
            """)
    String review(@UserMessage String question);

    @SystemMessage("""
            You're an assitant and you have access to tools. 
            Search for the asked query in the Google Drive files.  
            
            Return the found docs in a json that maps into: 
            List<GoogleDocument> list;  
            where GoogleDocument is record GoogleDocument(String title, String fileId){};

            DO NOT return any other text than the json object.
            DO NOT include ```json at the beginning of the json object.
            DO NOT include ``` at the end of the json object.
            
            The query to use is in the User message part.
            """)
    String search(@UserMessage String query);


}
