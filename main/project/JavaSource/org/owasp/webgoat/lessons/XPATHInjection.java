/**
 * 
 */
package org.owasp.webgoat.lessons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileInputStream;
import org.xml.sax.InputSource;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException;

import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.StringElement;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.P;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.Table;
import org.apache.ecs.html.H1;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.B;
import org.apache.ecs.html.PRE;
import org.apache.ecs.HtmlColor;

import org.owasp.webgoat.session.WebSession;
import org.owasp.webgoat.session.ECSFactory;

/**
 * @author sherif
 *
 */
public class XPATHInjection extends LessonAdapter {

	private final static Integer DEFAULT_RANKING = new Integer(74);
	private final static String USERNAME = "Username";
	private final static String PASSWORD = "Password";

	protected Element createContent(WebSession s) {

		NodeList nodes = null;
		ElementContainer ec = new ElementContainer();
		
		try{
			ec.addElement( new BR().addElement (new H1().addElement( "Welcome to WebGoat employee intranet")));
			ec.addElement( new BR());
			Table t1 = new Table().setCellSpacing(0).setCellPadding(0).setBorder(0).setWidth("90%").setAlign("center");
			
			TR tr = new TR();
			tr.addElement( new TH().addElement("Please confirm your username and password before viewing your profile.").setColSpan(2).setAlign("left"));
			t1.addElement(tr);
			
			tr = new TR();
			tr.addElement( new TD().addElement("*Required Fields").setWidth("30%").setColSpan(2).setAlign("left"));
			t1.addElement(tr);
			
			tr = new TR();
			tr.addElement( new TD().addElement("&nbsp").setWidth("30%").setColSpan(2).setAlign("left"));
			t1.addElement(tr);

			tr = new TR();
			tr.addElement( new TD( new B( new StringElement( "*User Name: " ) ) ));		
			
			Input input1 = new Input( Input.TEXT, USERNAME, "" );
			tr.addElement( new TD( input1 ) );
			t1.addElement( tr );

			tr = new TR();
			tr.addElement( new TD( new B( new StringElement( "*Password: " ) ) ));		

			Input input2 = new Input( Input.PASSWORD, PASSWORD, "" );
			tr.addElement( new TD( input2 ) );
			t1.addElement( tr );

			Element b = ECSFactory.makeButton( "Submit" );
			t1.addElement( new TR( new TD( b ) ) );
			ec.addElement( t1 );
			
			String username = s.getParser().getRawParameter(USERNAME , "");
			if (username == null || username.length() == 0)
			{
				ec.addElement( new P().addElement( new StringElement("Username is a required field")));
				return ec;
			}
			
			String password = s.getParser().getRawParameter(PASSWORD , "");
			if (password == null || password.length() == 0)
			{
				ec.addElement( new P().addElement( new StringElement("Password is a required field")));
				return ec;
			}
			
			String dir = s.getContext().getRealPath( "/lessons/XPATHInjection/EmployeesData.xml" );
			File d = new File( dir );
			XPathFactory  factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			InputSource inputSource = new InputSource(new FileInputStream(d));			
			String expression = "/employees/employee[loginID/text()='" + username + "' and passwd/text()='" + password + "']";
    	    nodes = (NodeList)xPath.evaluate(expression, inputSource, XPathConstants.NODESET);
    	    int nodesLength = nodes.getLength();
    	    
    	    Table t2 = null;
    	    if (nodesLength > 0)
    	    {
    			t2 = new Table().setCellSpacing(0).setCellPadding(0).setBorder(1).setWidth("90%").setAlign("center");
    			tr = new TR();
    			tr.setBgColor( HtmlColor.GRAY );
    			tr.addElement( new TD().addElement("Username") );
    			tr.addElement( new TD().addElement("Account No.") );
    			tr.addElement( new TD().addElement("Salary") );
    			t2.addElement(tr);
    	    }
			    	    
    	    for (int i=0; i<nodesLength; i++)
    	    {
    	    	Node node = nodes.item(i);	
    	    	String[] arrTokens = node.getTextContent().split("[\\t\\s\\n]+");
    	    	    	    	
    	    	tr = new TR();
    			tr.addElement( new TD().addElement(arrTokens[1]) );
    			tr.addElement( new TD().addElement(arrTokens[2]) );
    			tr.addElement( new TD().addElement(arrTokens[4]) );
    			t2.addElement(tr);
    	    	
	
    	    }
    	    if (nodes.getLength() > 1)
    	    {
    	    	makeSuccess( s );
    	    }
    	    if (t2 != null)
    	    {
    	    	ec.addElement( new PRE());
    	    	ec.addElement(t2);
    	    }
			 
		}
		catch (IOException e)
		{
			s.setMessage( "Error generating " + this.getClass().getName() );
			e.printStackTrace();			
		}
		catch (IllegalArgumentException e)
		{
			s.setMessage( "Error generating " + this.getClass().getName() );
			e.printStackTrace();			
		}
		catch (XPathExpressionException e)
		{
			s.setMessage( "Error generating " + this.getClass().getName() );
			e.printStackTrace();			
		}		
		return ec;
	}

	public Element getCredits() {
		
		return new StringElement("Sherif Koussa");
	}


	protected Category getDefaultCategory() {
		
		return AbstractLesson.A6;
	}

	protected boolean getDefaultHidden() {
		// TODO Auto-generated method stub
		return false;
	}

	protected Integer getDefaultRanking() {
		
		return DEFAULT_RANKING;
	}

	protected List getHints() {
		// TODO Auto-generated method stub
		List<String> hints = new ArrayList<String>();
		hints.add( "Remember that the data is stored in XML format." );
		hints.add( "The system is using XPath to query." );
		hints.add( "XPath is almost the same thing as SQL, the same hacking techniques apply too." );
		hints.add( "Try username: Smtih' or 1=1 or 'a'='a and a password: anything " );
		return hints;
	}

	public String getTitle() {
		
		return "How to Perform XPATH Injection";
	}

}
