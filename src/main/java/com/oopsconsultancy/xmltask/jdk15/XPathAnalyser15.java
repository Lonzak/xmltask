package com.oopsconsultancy.xmltask.jdk15;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.oopsconsultancy.xmltask.XPathAnalyser;
import com.oopsconsultancy.xmltask.XPathAnalyserClient;


/**
 * Uses the standard Java XPath API instead of com.sun.* classes
 *
 * @author Brian Agnew  (Original version)
 * @author Lonzak
 * 
 */
public class XPathAnalyser15 implements XPathAnalyser {

    private XPathAnalyserClient client;
    private Object callback;

    @Override
    public void registerClient(XPathAnalyserClient client, Object callback) {
        this.client = client;
        this.callback = callback;
    }

    @Override
    public int analyse(Node node, String xpathExpression) throws Exception {
        int count = 0;

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile(xpathExpression);

        // Evaluate the expression
        Object result = expr.evaluate(node, XPathConstants.NODESET);

        if (result instanceof NodeList) {
            NodeList nodes = (NodeList) result;
            Node n;
            for (int i = 0; i < nodes.getLength(); i++) {
                this.client.applyNode(nodes.item(i), this.callback);
                count++;
            }
        }
        else {
            // If the result is a string, boolean, or number, apply it as a string
            String strResult = expr.evaluate(node);
            this.client.applyNode(strResult, this.callback);
            count++;
        }
        return count;
    }
}

