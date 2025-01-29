package com.oopsconsultancy.xmltask;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * @author brian performs uncommenting of nodes. This
 * contains huge chunks of code from the InsertAction
 * and deserves to be refactored
 */
public class UncommentAction extends Action {

  private DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

  private boolean wellFormed = false;
  
  {
    // why do I do this ? Causes problems with
    // Pete Hale and namespace-scoped insertions
    this.dfactory.setNamespaceAware(true);
  }

  public UncommentAction() {
  }

  /**
   * performs the uncomment action
   * 
   * @see com.oopsconsultancy.xmltask.Action#apply(org.w3c.dom.Node)
   */
  @Override
public boolean apply(final Node node) throws Exception {
    if (node instanceof Comment) {
      Comment comment = (Comment) node;
      System.out.println("Uncommenting " + comment.getData());
      Document commentDoc = readXml(comment.getData());
      
      // and insert...
      Node newnode = this.doc.importNode(commentDoc.getDocumentElement(), true);
      if (!this.wellFormed) {
        // I need to extract the nodes below the dummy root node
        DocumentFragment frag = this.doc.createDocumentFragment();
        NodeList children = newnode.getChildNodes();
        for (int c = 0; c < children.getLength();) {
          // we can do this as the appendChild is removing at the same time
          frag.appendChild(children.item(c));
        }
        newnode = frag;
      }

      Node parent = node.getParentNode();
      if (parent == null) {
        System.err.println("Attempt to insert after root node");
        return false;
      }
      // remove the comment node
      parent.insertBefore(newnode, node.getNextSibling());
      parent.removeChild(comment);
      return true;
    }
    else {
      throw new BuildException(node + " is not a comment");
    }
  }

  /**
   * reads the xml in the comment
   * 
   * @param xml
   * @return an array of nodes to insert
   * @throws Exception
   */
  private Document readXml(String xml) throws Exception {
    DocumentBuilder db = getBuilder();
    try {
      Document doc = db.parse(new InputSource(new StringReader(xml)));
      this.wellFormed = true;
      return doc;
    }
    catch (SAXParseException e) {
      // it could not be well-formed, so we'll wrap and try again...
      xml = InsertAction.DUMMYNODE + xml + InsertAction.DUMMYENODE;
      Document doc = db.parse(new InputSource(new StringReader(xml)));
      this.wellFormed = false;
      return doc;
    }
  }

  private DocumentBuilder getBuilder() throws ParserConfigurationException {
    DocumentBuilder db = this.dfactory.newDocumentBuilder();
    db.setErrorHandler(new ErrorHandler() {
      @Override
    public void error(final SAXParseException e) {
        System.err.println(e.getMessage());
      }

      @Override
    public void fatalError(final SAXParseException e) {
        // I want to disable the error o/p for non-well
        // formed documents
      }

      @Override
    public void warning(final SAXParseException e) {
        System.err.println(e.getMessage());
      }
    });
    return db;
  }

}
