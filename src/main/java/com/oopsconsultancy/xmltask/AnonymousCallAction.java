package com.oopsconsultancy.xmltask;

import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroInstance;
import org.w3c.dom.Node;

import com.oopsconsultancy.xmltask.ant.Param;
import com.oopsconsultancy.xmltask.ant.XmlTask;

/**
 * The defined macro is called for each matched node
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: AnonymousCallAction.java,v 1.1 2006/05/18 15:19:59 bagnew Exp $
 */
public class AnonymousCallAction extends Action implements XPathAnalyserClient {

  private final XmlTask task;
  private final String buffer;
  private final List params;

  private MacroDef macro;

  public AnonymousCallAction(final MacroDef macro, final XmlTask task, final String buffer, final List params) {
	this.macro = macro;
    this.task = task;
    this.buffer = buffer;
    this.params = params;
    
    init();
  }

  /**
   * init this task.
   */
  private void init() {
    for (Iterator it = this.params.iterator(); it.hasNext(); ) {
    	Param param = (Param) it.next();
    	MacroDef.Attribute attribute = new MacroDef.Attribute();
    	attribute.setName(param.getName());
    	this.macro.addConfiguredAttribute(attribute);
    }
  }

  /**
   * reset the set of parameters. We only reset XPath settings
   * since properties will remain the same between invocations
   */
  private void resetParams() {
    for (Iterator i = this.params.iterator(); i.hasNext(); ) {
      Param param = (Param)i.next();
      if (param.getPath() != null) {
        param.setValue(null);
      }
    }
  }

  @Override
public void applyNode(Node n, Object callback) {
    Param param = (Param)callback;
    param.set(this.task, n.getNodeValue());
  }

  @Override
public void applyNode(String str, Object callback) {
    Param param = (Param)callback;
    param.set(this.task, str);
  }

  /**
   * iterates through the parameters, executing the XPath
   * engine where necessary and creating new attributes
   * in the macro instance, then calls on that.
   *
   * @param node
   * @return success
   * @throws Exception
   */
  @Override
public boolean apply(Node node) throws Exception {
    resetParams();

    log("Calling internal macro for " + node + (this.buffer != null ? " (in buffer "+this.buffer:""), Project.MSG_VERBOSE);

    if (this.buffer != null) {
      // record the complete (sub)node in the nominated buffer
      BufferStore.set(this.buffer, node, false, this.task);
    }
    
    MacroInstance instance = new MacroInstance();
    instance.setProject(this.task.getProject());
    instance.setOwningTarget(this.task.getOwningTarget());
    instance.setMacroDef(this.macro);
    
    if (this.params != null) {
      for (Iterator i = this.params.iterator(); i.hasNext(); ) {
        Param param = (Param)i.next();

        if (param.getPath() != null) {
          XPathAnalyser xpa = XPathAnalyserFactory.getAnalyser();
          xpa.registerClient(this, param);
          xpa.analyse(node, param.getPath());
        }

        // now set the value
       	instance.setDynamicAttribute(param.getName().toLowerCase(), param.getValue());
      }
    }
    
    instance.execute();

    return true;
  }

  private void log(String msg, int level) {
    if (this.task != null) {
      this.task.log(msg, level);
    }
    else {
      System.out.println(msg);
    }
  }
}
