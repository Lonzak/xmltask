package com.oopsconsultancy.xmltask.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.oopsconsultancy.xmltask.Action;
import com.oopsconsultancy.xmltask.AttrAction;
import com.oopsconsultancy.xmltask.InsertAction;
import com.oopsconsultancy.xmltask.RemovalAction;
import com.oopsconsultancy.xmltask.TextAction;
import com.oopsconsultancy.xmltask.XmlAction;
import com.oopsconsultancy.xmltask.XmlReplace;
import com.oopsconsultancy.xmltask.XmlReplacement;

/**
 * JUnit tests
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: TestXmlReplacement.java,v 1.2 2003/08/08 21:01:51 bagnew Exp $
 */
public class TestXmlReplacement {

    @Test
    public void testReplaceTextInElement() throws Exception {
        runTest(init("test1.xml"), "/a/b", new TextAction("x"), "<a>x</a>");
    }

    @Test
    public void testReplaceRootElement() throws Exception {
        runTest(init("test1.xml"),"/a", new TextAction("x"), "<a><b>Replace me</b></a>");
    }

    @Test
    public void testReplaceTextNode() throws Exception {
        runTest(init("test1.xml"),"/a/b/text()", new TextAction("x"), "<a><b>x</b></a>");
    }

    @Test
    public void testReplaceTextWithSymbols() throws Exception {
        runTest(init("test1.xml"),"/a/b/text()", new TextAction(">>"), "<a><b>&gt;&gt;</b></a>");
    }

    @Test
    public void testReplaceElementWithXml() throws Exception {
        runTest(init("test1.xml"),"/a/b", XmlAction.xmlActionfromString("<c><d>e</d></c>", null), "<a><c><d>e</d></c></a>");
    }

    @Test
    public void testReplaceTextInTextNode() throws Exception {
        runTest(init("test1.xml"),"/a/b/text()", XmlAction.xmlActionfromString("<c><d>e</d></c>", null), "<a><b><c><d>e</d></c></b></a>");
    }

    @Test
    public void testRemoveElement() throws Exception {
        runTest(init("test1.xml"),"/a/b", new XmlAction(), "<a/>");
    }

    @Test
    public void testRemoveRootElement() throws Exception {
        runTest(init("test1.xml"),"/a", new XmlAction(), "");
    }

    @Test
    public void testReplaceElementWithExternalXml() throws Exception {
        runTest(init("test1.xml"),"/a/b", XmlAction.xmlActionfromFile(new File(getFullPathToResourceDirectory("substitute1.xml")), null), "<a><p><q>RRR</q></p></a>");
    }

    @Test
    public void testReplaceTextNodeWithExternalXml() throws Exception {
        runTest(init("test1.xml"),"/a/b/text()", XmlAction.xmlActionfromFile(new File(getFullPathToResourceDirectory("substitute1.xml")), null), "<a><b><p><q>RRR</q></p></b></a>");
    }

    @Test
    public void testReplaceElementWithAttribute() throws Exception {
        runTest(init("test1.xml"),"/a/b", new AttrAction("attr", "val", Boolean.FALSE, null), "<a><b attr=\"val\">Replace me</b></a>");
    }
    
    @Test
    public void testAttrAction() throws Exception {
        runTest(init("test1.xml"),"/a/b/text()", new AttrAction("attr", "val", Boolean.FALSE, null), "<a><b>Replace me</b></a>");
    }

    @Test
    public void testRemoveTextInNode() throws Exception {
        runTest(init("test1.xml"),"/a/b/text()", new RemovalAction(), "<a><b/></a>");
    }
    
    @Test
    public void testRemoveTextEndTag() throws Exception {
        runTest(init("test1.xml"),"/a/b", new RemovalAction(), "<a/>");
    }
    
    @Test
    public void testInsertTextInNode() throws Exception {
        runTest(init("test1.xml"),"/a/b", InsertAction.fromString("<c>Z</c>", null), "<a><b>Replace me<c>Z</c></b></a>");
    }
    
    @Test
    public void testInsertTextInElement() throws Exception {
        runTest(init("test1.xml"),"/a", InsertAction.fromString("<c>Z</c>", null), "<a><b>Replace me</b><c>Z</c></a>");
    }

    @Test
    public void testReplaceTextInAnotherFile() throws Exception {
        runTest(init("test2.xml"),"//z", new TextAction("x"), "<x attr=\"1\"><y id=\"2\"/>xx</x>");
    }

    @Test
    public void testReplaceSpecificTextInAnotherFile() throws Exception {
        runTest(init("test2.xml"),"//z[@id = '3']", new TextAction("x"), "<x attr=\"1\"><y id=\"2\"/>x<z id=\"4\"/></x>");
    }

    @Test
    public void testReplaceElementWithXmlFragment() throws Exception {
        runTest(init("test2.xml"),"//z[@id = '4']", XmlAction.xmlActionfromString("<test>ABC</test>", null), "<x attr=\"1\"><y id=\"2\"/><z id=\"3\"/><test>ABC</test></x>");
    }

    @Test
    public void testAttributeAction() throws Exception {
        runTest(init("test2.xml"),"//x", new AttrAction("attr", "val", Boolean.FALSE, null), "<x attr=\"val\"><y id=\"2\"/><z id=\"3\"/><z id=\"4\"/></x>");
    }
    
    @Test
    public void testAttributeAction2() throws Exception {
        runTest(init("test2.xml"),"//x/descendant-or-self::*", new AttrAction("id", "8", Boolean.FALSE, null), "<x attr=\"1\" id=\"8\"><y id=\"8\"/><z id=\"8\"/><z id=\"8\"/></x>");
    }
    
    @Test
    public void testAttributeAction3() throws Exception {
        runTest(init("test2.xml"),"//*", new AttrAction("id", "8", Boolean.FALSE, null), "<x attr=\"1\" id=\"8\"><y id=\"8\"/><z id=\"8\"/><z id=\"8\"/></x>");
    }
    
    @Test
    public void testReplaceMultipleElements() throws Exception {
        runTest(init("test2.xml"),"/x/*", XmlAction.xmlActionfromString("<test>ABC</test>", null), "<x attr=\"1\"><test>ABC</test><test>ABC</test><test>ABC</test></x>");
    }
    
    @Test
    public void testReplaceAttributes() throws Exception {
        runTest(init("test2.xml"),"//z[2]", XmlAction.xmlActionfromString("<test>ABC</test>", null), "<x attr=\"1\"><y id=\"2\"/><z id=\"3\"/><test>ABC</test></x>");
    }
    
    @Test
    public void testReplaceAttributes2() throws Exception {
        runTest(init("test2.xml"),"//z[last()]", XmlAction.xmlActionfromString("<test>ABC</test>", null), "<x attr=\"1\"><y id=\"2\"/><z id=\"3\"/><test>ABC</test></x>");
    }
    
    @Test
    public void testReplaceAttributes3() throws Exception {
        runTest(init("test3.xml"),"//s/parent::*", XmlAction.xmlActionfromString("<test>ABC</test>", null), "<p><q><test>ABC</test></q></p>");
    }
    

    private static void runTest(Document doc, String xpath, Action action, String expected) throws Exception {
        XmlReplace replace = new XmlReplace(xpath, action);
        XmlReplacement xmlr = new XmlReplacement(doc, null);
        xmlr.add(replace);
        doc = xmlr.apply();
        Assert.assertEquals(expected, serializeDocument(doc));
    }


    private static String serializeDocument(Document doc) throws Exception {

        // Set up an identity transformer to use as serializer.
        Transformer serializer = TransformerFactory.newInstance().newTransformer();
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.setOutputProperty(OutputKeys.INDENT, "no");

        // and output
        Writer pw = new StringWriter();
        serializer.transform(new DOMSource(doc), new StreamResult(pw));
        return pw.toString();
    }
    
    private static Document init(String filename) throws Exception {
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setNamespaceAware(true);
        
        Document doc = dfactory.newDocumentBuilder().parse(new InputSource(loadFileFromResourceDirectory(filename)));
        doc.getDocumentElement().normalize();
        return doc;
    }
    
    private static InputStream loadFileFromResourceDirectory(String filename) throws RuntimeException {
        try{
            if(filename.startsWith("/")){
                return new FileInputStream("./src/test/resources"+filename);
            }
            else{
                return new FileInputStream("./src/test/resources/"+filename);
            }
        }
        catch(FileNotFoundException fnfe){
            throw new RuntimeException("Can't find file '" + filename+ "'.", fnfe);
        }
    }
    
    private static String getFullPathToResourceDirectory(String filename) throws RuntimeException {
        if(filename.startsWith("/")){
            return "./src/test/resources"+filename;
        }
        else{
            return "./src/test/resources/"+filename;
        }
    }
}

