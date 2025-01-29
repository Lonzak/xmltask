package com.oopsconsultancy.xmltask;

import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;
import org.w3c.dom.Node;

import com.oopsconsultancy.xmltask.ant.Param;
import com.oopsconsultancy.xmltask.ant.XmlTask;

/**
 * The nominated target is called for
 * each matched node
 *
 * Taken heavily from the CallTarget.java src
 * in the Ant source distribution
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: CallAction.java,v 1.14 2007/01/22 11:58:09 bagnew Exp $
 */
public class CallAction extends Action implements XPathAnalyserClient {

  private final String target;
  private final XmlTask task;
  private final boolean inheritAll;
  private final boolean inheritRefs;
  private final String buffer;
  private final List params;

  private Ant callee;

  public CallAction(final String target, final XmlTask task, final boolean inheritAll, final boolean inheritRefs, final String buffer, final List params) {
    this.target = target;
    this.task = task;
    this.inheritAll = inheritAll;
    this.inheritRefs = inheritRefs;
    this.buffer = buffer;
    this.params = params;
  }

  /**
   * init this task by creating new instance of the ant task and
   * configuring it's by calling its own init method.
   */
  public void init() {
    this.callee = (Ant)this.task.getProject().createTask("ant");
    this.callee.setOwningTarget(this.task.getOwningTarget());
    this.callee.setTaskName(this.task.getTaskName());
    this.callee.setLocation(this.task.getLocation());
    this.callee.init();
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
   * engine where necessary and creating new properties
   * in the sub target, then calls on that.
   *
   * @param node
   * @return success
   * @throws Exception
   */
  @Override
public boolean apply(Node node) throws Exception {
    init();
     resetParams();

    log("Calling target " + this.target + " for " + node + (this.buffer != null ? " (in buffer "+this.buffer:""), Project.MSG_VERBOSE);

    if (this.buffer != null) {
      // record the complete (sub)node in the nominated buffer
      BufferStore.set(this.buffer, node, false, this.task);
    }

    if (this.params != null) {
      for (Iterator i = this.params.iterator(); i.hasNext(); ) {
        Param param = (Param)i.next();

        if (param.getPath() != null) {
          XPathAnalyser xpa = XPathAnalyserFactory.getAnalyser();
          xpa.registerClient(this, param);
          xpa.analyse(node, param.getPath());
        }

        // now set the values
        if (param.getValue() != null) {
          Property p = this.callee.createProperty();
          p.setName(param.getName());
          p.setValue(param.getValue());
        }
      }
    }
    // record the path in special named properties. These are currently
    // undocumented and may disappear!
    String nodeStr = getNodePath(node, false);
    String fqnodeStr = getNodePath(node, true);
    Property p = this.callee.createProperty();
    p.setName("xmltask.path");
    p.setValue(nodeStr);
    p = this.callee.createProperty();
    p.setName("xmltask.fqpath");
    p.setValue(fqnodeStr);


    this.callee.setAntfile(this.task.getProject().getProperty("ant.file"));
    this.callee.setTarget(this.target);
    this.callee.setInheritAll(this.inheritAll);
    this.callee.setInheritRefs(this.inheritRefs);
    
    // make sure we always pass the buffers!
    Ant.Reference buffers = new Ant.Reference();
    buffers.setProject(this.task.getProject());
    buffers.setRefId(BufferStore.BUFFERS_PROJECT_REF);
    buffers.setToRefid(BufferStore.BUFFERS_PROJECT_REF);
    this.callee.addReference(buffers);
    
    this.callee.execute();

    return true;
  }

  /**
   * builds a representation of the node hierarchy
   *
   * @param node
   * @param qualified
   * @return the local or fully-qualified name
   */
  private String getNodePath(Node node, final boolean qualified) {
    // stringbuffer not good for appending, so...
    String op = "";
    while (node != null && node.getParentNode() != null) {
      if (node.getNodeType() != Node.TEXT_NODE) {
        op = "/" + (qualified ? node.getLocalName() : node.getNodeName()) + op;
      }
      node = node.getParentNode();
    }
    return op.toString();
  }

  @Override
public String toString() {
    return "CallAction(" + this.target + ")";
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
