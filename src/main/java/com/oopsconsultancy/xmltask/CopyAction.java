package com.oopsconsultancy.xmltask;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * copies the nominated node and its children. We can copy the
 * node to a buffer or to a system property
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: CopyAction.java,v 1.9 2009/09/14 17:18:50 bagnew Exp $
 */
public class CopyAction extends Action {

  final protected String propertyBufferName;
  final protected boolean append;
  final protected boolean attrValue;
  final protected Task task;
  final protected boolean isProperty;
  final protected boolean trim;
  private String propertyToWrite = null;
	private final String propertySeparator;

  public CopyAction(final String propertyBufferName, final boolean append, final boolean attrValue, final Task task, final boolean isProperty, final boolean trim, final String propertySeparator) {
    this.propertyBufferName = propertyBufferName;
    this.trim = trim;
    this.append = append;
    this.attrValue = attrValue;
    this.task = task;
    this.isProperty = isProperty;
    this.propertySeparator = propertySeparator == null ? " ":propertySeparator;
    if (attrValue && isProperty) {
    	task.log("Specifying 'attr' for properties is now deprecated", Project.MSG_VERBOSE);
    }
  }

  /**
   * records the selected node. For attributes we can
   * record the value as a text object if required
   *
   * @param node
   * @throws Exception
   */
  protected void record(Node node) throws Exception {
    if (node instanceof Attr && this.attrValue) {
    	// this is only required for buffers...
      Document doc = node.getOwnerDocument();
      String value = ((Attr)node).getValue();
      node = doc.createTextNode(value);
    }

    if (!this.isProperty) {
        if (this.trim) {
        	this.task.log("Trimming not available when copying/cutting to buffers", Project.MSG_WARN);
        }
        BufferStore.set(this.propertyBufferName, node, this.append, this.task);
    }
    else {
      // this currently supports only text and comment nodes
      if (node instanceof Text || node instanceof Comment || node instanceof Attr) {
          String value = node.getNodeValue();
          if (value != null && this.trim) {
              value = value.trim();
          }
    	this.task.log("Copying '"+ value+"' to property " + this.propertyBufferName, Project.MSG_VERBOSE);
    	if (this.propertyToWrite == null) {
    	    this.propertyToWrite = value;
    	}
    	else if (this.append) {
    	    this.propertyToWrite = this.propertyToWrite+ this.propertySeparator + value;
    	}
      }
      else if (node == null) {
        this.task.log("Can only copy/cut text() nodes and attribute values to properties (found no node)", Project.MSG_WARN);
      }
      else {
        this.task.log("Can only copy/cut text() nodes and attribute values to properties (found "+node.getClass().getName()+")", Project.MSG_WARN);
      }
      if (this.append) {
        this.task.log("Cannot append values to properties", Project.MSG_WARN);
      }
    }
  }
  

  /**
   * an action completion. Provided for actions to perform clean up
   * etc.
   */
  @Override
  protected void completeAction() {
      // for property copying we record and then store right
      // at the end (since properties are immutable, we can only
      // write once :-(
      if (this.propertyToWrite != null) {
        this.task.getProject().setNewProperty(this.propertyBufferName, this.propertyToWrite);
      }
      this.propertyToWrite = null;
  }

  @Override
public boolean apply(Node node) throws Exception {
    record(node);
    return true;
  }
  
  @Override
public String toString() {
    return "CopyAction(" + this.propertyBufferName + ")";
  }
}
