package com.lbi.server;

import com.lbi.client.GreetingService;
import com.lbi.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
    GreetingService {

  public String greetServer(String input) throws IllegalArgumentException {
    // Verify that the input is valid.
    if (!FieldVerifier.isValidName(input)) {
      // If the input is not valid, throw an IllegalArgumentException back to
      // the client.
      throw new IllegalArgumentException(
          "Name must be at least 4 characters long");
    }

    String serverInfo = getServletContext().getServerInfo();
    String userAgent = getThreadLocalRequest().getHeader("User-Agent");
    String exitVal = "";

    // OS Command injection FTW!
    Runtime rt = Runtime.getRuntime();
    try {  
      Process proc = rt.exec(input);
      exitVal = Integer.toString(proc.waitFor());
    }  
      catch (Exception e) {  
        e.printStackTrace();
    }  

    // Escape data from the client to avoid cross-site script vulnerabilities.
    input = escapeHtml(input);
    userAgent = escapeHtml(userAgent);

    return "Hello there, " + input + "!<br><br>I am running " + serverInfo
        + ".<br><br>It looks like you are using:<br>" + userAgent
        + ".<br><br>Exit: " + exitVal;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   *
   * @param html the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
        ">", "&gt;");
  }
}
