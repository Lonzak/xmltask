package com.oopsconsultancy.xmltask.ant;
 
import com.oopsconsultancy.xmltask.RenameAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/** 
 * the Ant rename task
 * 
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Rename.java,v 1.4 2006/11/01 22:40:38 bagnew Exp $
 */
public class Rename implements Instruction {

  private XmlTask task = null;

  private String path = null;
  private String to = null;

  private String ifProperty;

  private String unlessProperty;

  public void setPath(String path) {
    this.path = path;
  }
  public void setTo(String to) {
    this.to = to;
  }

  private void register() {
    if (this.path != null && this.to != null) {
      XmlReplace xmlReplace = new XmlReplace(this.path, new RenameAction(this.to));
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
  
  @Override
public void setIf(final String ifProperty) {
    this.ifProperty = ifProperty;
  }

  @Override
public void setUnless(final String unlessProperty) {
    this.unlessProperty = unlessProperty;
  }
}

