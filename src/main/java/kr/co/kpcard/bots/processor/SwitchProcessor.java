/**
 * 
 */
package kr.co.kpcard.bots.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author happymoney
 *
 */
@Component
public class SwitchProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(SwitchProcessor.class);
    private static final String LOGTAG = "SwitchProcessor";
    
    @Value("${switch.api.url}")
	private String	remoteURL;	// http://61.251.167.9:8080/cgi-bin/xmlrpc_server
    
    @Value("${switch.api.method}")
	private String	methodName; // arrayos_cli_config
	
    @Value("${switch.api.enable}")
	private String	enablePassword; // enable password
	
	private static final String	COMMAND_PREFIX_SHOW = "show";
	private static final String	COMMAND_PREFIX_ADD = "add";
	private static final String	COMMAND_PREFIX_REMOVE = "remove";
	private static final String	COMMAND_ALTERNATIVE_SHOW = "show slb group member";
	private static final String	COMMAND_ALTERNATIVE_ADD = "slb group member";
	private static final String	COMMAND_ALTERNATIVE_REMOVE = "no slb group member";
	private static final String	ALIAS_SWITCH_GROUP_WWW = "www-80-gr";
	private static final String ALIAS_SWITCH_OBJECT_WWW = "www-80-";
	
	public SwitchProcessor() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String executeCommand(String[] arguments) {
		String body, response, result = String.join(" ", arguments);
		String command;
		
		try {
			command = makeCommand(arguments);
			logger.info("Request command : " + command);
			body = createBody(command);
			response = sendPOST(body);
			result = parseResponse(response);
	        logger.info("Response result : " + result);
		} catch (Exception e) {
			logger.error(LOGTAG, e);
			result = "Error on execute.";
        }

		return result;
	}
	
	private String makeCommand(String[] arguments) {
		String	command = String.join(" ", arguments);
		
		if ( COMMAND_PREFIX_SHOW.equals(arguments[0]) == true && arguments.length > 1 ) {
			if ( "www".equalsIgnoreCase(arguments[1]) )
				command = String.format("%s %s", COMMAND_ALTERNATIVE_SHOW, ALIAS_SWITCH_GROUP_WWW);
			else
				command = String.format("%s %s", COMMAND_ALTERNATIVE_SHOW, arguments[1]);
		} else if ( COMMAND_PREFIX_ADD.equals(arguments[0]) == true && arguments.length > 2 ) {
			if ( "www".equalsIgnoreCase(arguments[1]) )
				command = String.format("%s %s %s%s", COMMAND_ALTERNATIVE_ADD, ALIAS_SWITCH_GROUP_WWW, ALIAS_SWITCH_OBJECT_WWW, arguments[2]);
			else
				command = String.format("%s %s %s ", COMMAND_ALTERNATIVE_ADD, arguments[1], arguments[2]);
		} else if ( COMMAND_PREFIX_REMOVE.equals(arguments[0]) == true && arguments.length > 2 ) {
			if ( "www".equalsIgnoreCase(arguments[1]) )
				command = String.format("%s %s %s%s", COMMAND_ALTERNATIVE_REMOVE, ALIAS_SWITCH_GROUP_WWW, ALIAS_SWITCH_OBJECT_WWW, arguments[2]);
			else
				command = String.format("%s %s %s ", COMMAND_ALTERNATIVE_REMOVE, arguments[1], arguments[2]);
		}
		
		return command;
	}

	public String createBody(String argument) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
        StringWriter sw = new StringWriter();
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
    	icBuilder = icFactory.newDocumentBuilder();
        Document doc = icBuilder.newDocument();
//      Element mainRootElement = doc.createElementNS("http://crunchify.com/CrunchifyCreateXMLDOM", "Companies");
//      doc.appendChild(mainRootElement);
 
        Element	rootElement = doc.createElement("methodCall");
        doc.appendChild(rootElement);
        Element elMethodName = doc.createElement("methodName");
        rootElement.appendChild(elMethodName);
        elMethodName.appendChild(doc.createTextNode(methodName));
        Element elParams = doc.createElement("params");
        rootElement.appendChild(elParams);
        Element elParam = doc.createElement("param");
        elParams.appendChild(elParam);
        Element elValue = doc.createElement("value");
        elParam.appendChild(elValue);
        Element elStruct = doc.createElement("struct");
        elValue.appendChild(elStruct);
        
        
        // append child elements to root element
        elStruct.appendChild(createMember(doc, "enable_passwd", enablePassword));
        elStruct.appendChild(createMember(doc, "num", 1));
        elStruct.appendChild(createMember(doc, "cli_string0", argument));
//        elStruct.appendChild(createMember(doc, "cli_string1", "exit"));
 
            // output DOM XML to console 
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult sr = new StreamResult(sw);
        transformer.transform(source, sr);
        logger.debug(sw.toString());
 
		return sw.toString();
	}
	
	private Node createMember(Document doc, String name, String value) {
		Element	elMember = doc.createElement("member");
		Element elName = doc.createElement("name");
		elName.appendChild(doc.createTextNode(name));
		
		Element	elValue = doc.createElement("value");
		Element elString = doc.createElement("string");
		Text text = doc.createTextNode(value);
		elString.appendChild(text);
		elValue.appendChild(elString);
		
		elMember.appendChild(elName);
		elMember.appendChild(elValue);
		
		return elMember;
	}

	private Node createMember(Document doc, String name, Integer value) {
		Element	elMember = doc.createElement("member");
		Element elName = doc.createElement("name");
		elName.appendChild(doc.createTextNode(name));
		
		Element	elValue = doc.createElement("value");
		Element elString = doc.createElement("int");
		Text text = doc.createTextNode(value.toString());
		elString.appendChild(text);
		elValue.appendChild(elString);
		
		
		elMember.appendChild(elName);
		elMember.appendChild(elValue);
		
		return elMember;
	}
	
	private String sendPOST(String body) throws IOException {

		CloseableHttpClient httpClient = HttpClients.createDefault();
//		CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		HttpPost httpPost = new HttpPost(remoteURL);
/*
		httpPost.addHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("userName", "Pankaj Kumar"));

		HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
		httpPost.setEntity(postParams);
*/
		httpPost.setHeader("Content-type", "text/xml");
		HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
		httpPost.setEntity(entity);
        
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

		logger.debug("POST Response Status:: "
				+ httpResponse.getStatusLine().getStatusCode());
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				httpResponse.getEntity().getContent()));

		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = reader.readLine()) != null) {
			response.append(inputLine).append(System.getProperty("line.separator"));
		}
		reader.close();

		// print result
		logger.debug(response.toString());
		
		httpClient.close();

		return response.toString();
	}

	private String parseResponse(String input) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        InputSource is = new InputSource();
        String result = "";
        
        is.setCharacterStream(new StringReader(input));
        /*        
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
        
        doc.getDocumentElement().normalize();
        doc.getDocumentElement().getNodeName();
        Element elResult = doc.getElementById("string");
        if ( result != null )
        	result = elResult.getTextContent();
*/
        XPathFactory  factory=XPathFactory.newInstance();
        XPath xPath=factory.newXPath();
        XPathExpression  xPathExpression=
        	    xPath.compile("/methodResponse/params/param/value/string");
        result = 
        		  xPathExpression.evaluate(is);
     
        return result;
	}


}
