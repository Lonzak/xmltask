package com.oopsconsultancy.xmltask;

import org.apache.tools.ant.Task;
import org.w3c.dom.Node;

/**
 * cuts the nominated node and its children
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: CutAction.java,v 1.5 2009/09/14 17:18:50 bagnew Exp $
 */
public class CutAction extends CopyAction {

  public CutAction(final String buffer, final boolean append, final boolean attrValue, final Task task, final boolean isProperty, final boolean trim, final String propertySeparator) {
    super(buffer, append, attrValue, task, isProperty, trim , propertySeparator);
  }

  @Override
public boolean apply(final Node node) throws Exception {
    record(node);
    remove(node);
    return true;
  }

  @Override
public String toString() {
    return "CutAction(" + this.propertyBufferName + ")";
  }
}

