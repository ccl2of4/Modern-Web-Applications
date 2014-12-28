<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Assignment 3</title>
    </head>
    <body>
    
    	<!-- input section -->
    
 		<form action="search" method="POST">
            Search:<br />
            Type:<input type="text" name="type" /><br />
            Project:<input type="text" name="project" /><br />
            Year:<input type="text" name="year" /><br />
            <input type="submit" value="Submit" />
        </form>
        
        <!-- history section -->
        <hr>
		History<p>
		<%
		java.util.ArrayList<String> history = (java.util.ArrayList)session.getAttribute ("history");
		if (history != null) for (String str : history)
			out.println ("<a href = " + str + ">" + str + "</a><br>");
		%>
        </p>
        <form action = "closeSession" method="POST">
        	<input type = "submit" value = "Close Session" />
        </form>
        
        <!-- data section -->
        <hr>
        <% 
        
    	String pageContent = (String)session.getAttribute ("pageContent");
        if (pageContent != null) out.println (pageContent);
		session.setAttribute ("pageContent", null);
		
        %>

    </body>
</html>
