package com.oopsconsultancy.xmltask.ant;

import com.oopsconsultancy.xmltask.CutAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/**
 * the Ant cut task
 * 
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Cut.java,v 1.5 2009/09/14 17:18:50 bagnew Exp $
 */
public class Cut extends Copy {

  public Cut() {
  }

  /**
   * cuts a nominated node and copies to either a buffer or a property
   */
  @Override
public void process(final XmlTask task) {
    XmlReplace xmlReplace = null;
    if (this.path != null && this.buffer != null) {
      xmlReplace = new XmlReplace(this.path, new CutAction(this.buffer, this.append, this.attrValue, task, false, this.trim, this.propertySeparator));
    }
    else if (this.path != null && this.property != null) {
      xmlReplace = new XmlReplace(this.path, new CutAction(this.property, this.append, this.attrValue, task, true, this.trim, this.propertySeparator));
    }
    if (xmlReplace != null) {
      xmlReplace.setIf(this.ifProperty);
      xmlReplace.setUnless(this.unlessProperty);
      task.add(xmlReplace);
    }
  }
}
