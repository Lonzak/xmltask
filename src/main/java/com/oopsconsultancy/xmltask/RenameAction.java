package com.oopsconsultancy.xmltask;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
 * renames the given node (whether it's an attribute or an element)
 * 
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: RenameAction.java,v 1.2 2003/08/08 21:01:49 bagnew Exp $
 */
public class RenameAction extends Action {

  private final String to;

  public RenameAction(String to) {
    this.to = to;
  }

  /** 
   * renames the given entity. This can be an attribute
   * or an element. If it's an element then a bunch
   * of cloning and copying has to take place. Also have to
   * be careful in case I'm renaming the root node.
   * 
   * @param node 
   * @throws Exception 
   */
  @Override
public boolean apply(Node node) throws Exception {
    if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
      // to rename an attribute I have to remove it and create a new one...
      Attr attr = (Attr)node;
      Element owner = attr.getOwnerElement();
      owner.setAttribute(this.to, attr.getValue());
      remove(node);
    }
    else if (node.getNodeType() == Node.ELEMENT_NODE) {
      // I can't rename elements, so I have to clone this one 
      // with a new name *groan*
      Element elem = (Element)node;
      Node owner = elem.getParentNode();
      Element newElem = elem.getOwnerDocument().createElement(this.to);
      if (owner instanceof Document) {
        // and if I'm renaming the root element
        // then I have to do this...
        owner.removeChild(elem);
        owner.appendChild(newElem);
      }
      else {
        owner.insertBefore(newElem, elem.getNextSibling());
      }

      // I need to clone the subnodes (whatever they may be)
      NodeList nl = elem.getChildNodes();
      for (int n = 0; n < nl.getLength(); n++) {
        newElem.appendChild(nl.item(n).cloneNode(true));
      }
      // and also the element attributes
      NamedNodeMap attrs = elem.getAttributes();
      for (int a = 0; a < attrs.getLength(); a++) {
        newElem.setAttributeNode((Attr)attrs.item(a).cloneNode(true));
      }

      // and remove the old one...
      if (!(owner instanceof Document)) {
        remove(elem);
      }  
    }
    else {
      // nothing else supported...
    }
    return true;
  }

  @Override
public String toString() {
    return "RenameAction(" + this.to + ")";
  }
}

