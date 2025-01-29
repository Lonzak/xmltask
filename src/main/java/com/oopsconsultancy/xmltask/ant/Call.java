package com.oopsconsultancy.xmltask.ant;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.taskdefs.MacroDef;

import com.oopsconsultancy.xmltask.AnonymousCallAction;
import com.oopsconsultancy.xmltask.CallAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/**
 * the Ant call task
 * 
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Call.java,v 1.5 2006/11/01 22:40:38 bagnew Exp $
 */
public class Call implements Instruction {

  private String path = null;

  private String target = null;

  private String buffer = null;

  private boolean inheritAll = true;

  private boolean inheritRefs = false;

  private List params = new ArrayList();

  private MacroDef macro;

  private String unlessProperty;

  private String ifProperty;

  /**
   * executes a target for a set of nodes
   */
  public Call() {
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void setBuffer(String buffer) {
    this.buffer = buffer;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public void setInheritAll(boolean inheritAll) {
    this.inheritAll = inheritAll;
  }

  public void setInheritRefs(boolean inheritRefs) {
    this.inheritRefs = inheritRefs;
  }

  public void addConfiguredParam(Param param) {
    this.params.add(param);
  }

  public Object createActions() {
    this.macro = new MacroDef();
    return this.macro.createSequential();
  }

  @Override
public void process(final XmlTask task) {
    XmlReplace xmlReplace = null;
    if (this.path != null && this.target != null) {
      xmlReplace = new XmlReplace(this.path, new CallAction(this.target, task, this.inheritAll, this.inheritRefs, this.buffer, this.params));
    }
    else if (this.path != null && this.macro != null) {
      xmlReplace = new XmlReplace(this.path, new AnonymousCallAction(this.macro, task, this.buffer, this.params));
    }
    if (xmlReplace != null) {
      xmlReplace.setIf(this.ifProperty);
      xmlReplace.setUnless(this.unlessProperty);
      task.add(xmlReplace);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.oopsconsultancy.xmltask.ant.Instruction#setIf(java.lang.String)
   */
  @Override
public void setIf(final String ifProperty) {
    this.ifProperty = ifProperty;

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.oopsconsultancy.xmltask.ant.Instruction#setUnless(java.lang.String)
   */
  @Override
public void setUnless(final String unlessProperty) {
    this.unlessProperty = unlessProperty;

  }
}
