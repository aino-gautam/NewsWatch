/*package lighthouse.newsletter.server;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.lighthouse.newsletter.server.LhNewsletterServlet;
import com.login.server.db.AllocateResources;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class LhNewsletterServletTest {

	private static File webXml=new File("/root/workspace/Projects/NewsCenter/war/WEB-INF/web.xml");
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		
		ServletRunner sr = new ServletRunner(webXml);
		sr.registerServlet("AllocateResources", AllocateResources.class.getName());
		ServletUnitClient sc = sr.newClient();
		WebRequest request = new PostMethodWebRequest(
				"http://127.0.0.1:8888/AllocateResources");
		
//		WebResponse response=sc.getResponse(request);
		
		
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFetchNews() throws IOException, SAXException, ServletException {
		

		ServletRunner sr = new ServletRunner(webXml);
		sr.registerServlet("lhNewsletter", LhNewsletterServlet.class.getName());
		ServletUnitClient sc = sr.newClient();
		WebRequest request = new PostMethodWebRequest(
				"http://127.0.0.1:8888/lhNewsletter");
		
	
		WebResponse response=sc.getResponse(request);
		LhNewsletterServlet servlet=new LhNewsletterServlet();
		servlet.run();
		
		
	}

}
*/