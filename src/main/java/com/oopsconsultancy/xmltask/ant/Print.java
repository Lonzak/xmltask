package com.oopsconsultancy.xmltask.ant;

import com.oopsconsultancy.xmltask.PrintAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/**
 * allows diagnostic printing of path matching, buffers etc.
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Print.java,v 1.2 2006/09/17 16:20:53 bagnew Exp $
 */
public class Print {

  private String path;
  private String buffer;
  private String comment;

  public void setBuffer(final String buffer) {
    this.buffer = buffer;
  }

  public void setPath(final String path) {
    this.path = path;
  }

  public void setComment(final String comment) {
    this.comment = comment;
  }

  public void process(final XmlTask task) {
    if (this.buffer != null) {
      task.add(new XmlReplace(this.path, PrintAction.newInstanceFromBuffer(task, this.buffer, this.comment)));
    }
    else if (this.path != null) {
      task.add(new XmlReplace(this.path, PrintAction.newInstanceFromPath(task, this.path, this.comment)));
    }
    else {
      System.err.println("No path or buffer specified for <print> - ignoring");
    }
  }
}

