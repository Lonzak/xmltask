package com.oopsconsultancy.xmltask.ant;

import com.oopsconsultancy.xmltask.AttrAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/**
 * the Ant attribute modification task
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Attr.java,v 1.4 2006/11/01 22:40:38 bagnew Exp $
 */
public class Attr implements Instruction {

  private XmlTask task = null;

  private String path = null;
  private String attr = null;
  private String value = null;
  private Boolean remove = null;

  private String ifProperty;

  private String unlessProperty;

  public void setPath(String path) {
    this.path = path;
  }

  public void setAttr(String attr) {
    this.attr = attr;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setRemove(String remove) {
    if ("true".equals(remove) || "yes".equals(remove)) {
      this.remove = Boolean.TRUE;
    }
    else {
      this.remove = Boolean.FALSE;
    }
  }

  private void register() {
    if ((this.value != null || this.remove != null) && this.attr != null && this.path != null) {
      XmlReplace xmlReplace = new XmlReplace(this.path, new AttrAction(this.attr, this.value, this.remove, this.task));
      xmlReplace.setIf(this.ifProperty);
      xmlReplace.setUnless(this.unlessProperty);
      this.task.add(xmlReplace);
    }
  }

  @Override
public void process(final XmlTask task) {
    this.task = task;
    register();
  }

  /* (non-Javadoc)
   * @see com.oopsconsultancy.xmltask.ant.Instruction#setIf(java.lang.String)
   */
  @Override
public void setIf(final String ifProperty) {
    this.ifProperty = ifProperty;
  }

  /* (non-Javadoc)
   * @see com.oopsconsultancy.xmltask.ant.Instruction#setUnless(java.lang.String)
   */
  @Override
public void setUnless(final String unlessProperty) {
    this.unlessProperty = unlessProperty;
  }
}

