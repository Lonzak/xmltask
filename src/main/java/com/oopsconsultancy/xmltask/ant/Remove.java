package com.oopsconsultancy.xmltask.ant;

import com.oopsconsultancy.xmltask.RemovalAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/**
 * the Ant removal task
 * 
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Remove.java,v 1.4 2006/11/01 22:40:38 bagnew Exp $
 */
public class Remove implements Instruction {

  private String path = null;

  private String ifProperty;

  private String unlessProperty;

  public void setPath(String path) {
    this.path = path;
  }

  @Override
public void process(final XmlTask task) {
    XmlReplace xmlReplace = new XmlReplace(this.path, new RemovalAction());
    xmlReplace.setIf(this.ifProperty);
    xmlReplace.setUnless(this.unlessProperty);
    task.add(xmlReplace);
  }

  /**
   * sets a property determining execution
   * 
   * @param ifProperty
   */
  @Override
public void setIf(final String ifProperty) {
    this.ifProperty = ifProperty;
  }

  /**
   * sets a property determining execution
   * 
   * @param unlessProperty
   */
  @Override
public void setUnless(final String unlessProperty) {
    this.unlessProperty = unlessProperty;
  }
}
