package com.oopsconsultancy.xmltask.ant;

import com.oopsconsultancy.xmltask.CopyAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/**
 * the Ant copy task. Note that Cut derives from this
 * 
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Copy.java,v 1.5 2009/09/14 17:18:50 bagnew Exp $
 */
public class Copy implements Instruction {

  protected String path = null;

  protected String buffer = null;

  protected String property = null;

  protected boolean append = false;

  protected boolean attrValue = false;
  protected boolean trim = false;

  protected String ifProperty;

  protected String unlessProperty;

   protected String propertySeparator;

  /**
   * copies a nominated node to either a buffer or a property
   */
  public Copy() {
  }

  public void setBuffer(final String buffer) {
    this.buffer = buffer;
  }

  public void setProperty(final String property) {
    this.property = property;
  }

  public void setPath(final String path) {
    this.path = path;
  }

  public void setAttrValue(final String val) {
    if ("true".equals(val) || "on".equals(val)) {
      this.attrValue = true;
    }
  }

  @Override
public void process(final XmlTask task) {
    XmlReplace xmlReplace = null;
    if (this.path != null && this.buffer != null) {
      xmlReplace = new XmlReplace(this.path, new CopyAction(this.buffer, this.append, this.attrValue, task, false, this.trim, this.propertySeparator));
    }
    else if (this.path != null && this.property != null) {
      xmlReplace = new XmlReplace(this.path, new CopyAction(this.property, this.append, this.attrValue, task, true, this.trim, this.propertySeparator));
    }
    if (xmlReplace != null) {
      xmlReplace.setIf(this.ifProperty);
      xmlReplace.setUnless(this.unlessProperty);
      task.add(xmlReplace);
    }
  }

  public void setAppend(final String val) {
    if ("true".equals(val) || "on".equals(val)) {
      this.append = true;
    }
  }

  @Override
public void setIf(final String ifProperty) {
    this.ifProperty = ifProperty;
  }

  @Override
public void setUnless(final String unlessProperty) {
    this.unlessProperty = unlessProperty;
  }
  public void setTrim(final boolean trim) {
    this.trim = trim;
  }
  
  public void setPropertySeparator(final String propertySeparator) {
      this.propertySeparator = propertySeparator;
  }
}
