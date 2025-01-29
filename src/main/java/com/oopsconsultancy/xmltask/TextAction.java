package com.oopsconsultancy.xmltask;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * modifies the text nodes nominated. If the node
 * is a text node then it's modified. If it's
 * an attribute then set the attribute.
 * Otherwise it is removed and a text node
 * inserted in its place
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: TextAction.java,v 1.5 2009/08/09 18:54:47 bagnew Exp $
 */
public class TextAction extends Action {

  private String str = null;

  public TextAction(String str) {
    if (str == null) {
      throw new IllegalArgumentException("TextAction replacement can't be null");
    }
    this.str = str;
  }

  @Override
public boolean apply(final Node n) throws Exception {
    // we replace either a text node, or a subset of nodes...
    if (isTextNode(n)) {
      n.setNodeValue(this.str);
    }
    else if (n instanceof Attr) {
      ((Attr)n).setValue(this.str);
    }
    else {
      remove(n);

      Node nn = this.doc.createTextNode(this.str);
      n.getParentNode().insertBefore(nn, n);
    }
    return true;
  }

  public static boolean isTextNode(Node n) {
    if (n == null) {
      return false;
    }
    short nodeType = n.getNodeType();
    return nodeType == Node.CDATA_SECTION_NODE || nodeType == Node.TEXT_NODE;
  }

  @Override
public String toString() {
    return "TextAction(" + this.str + ")";
  }
}

