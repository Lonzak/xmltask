package com.oopsconsultancy.xmltask.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.oopsconsultancy.xmltask.InsertAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/**
 * the Ant insertion task
 * 
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Insert.java,v 1.8 2007/05/04 12:59:32 bagnew Exp $
 */
public class Insert implements Instruction {

  private XmlTask task = null;

  private String path = null;

  private String text = null; // text to insert (can be null)

  private InsertAction action = null;

  private InsertAction.Position position = InsertAction.Position.UNDER;

  private boolean expandProperties = true;

  /**
   * the buffer to insert
   */
  private String buffer;

  /**
   * the raw XML to insert
   */
  private String xml;

  /**
   * the file to insert
   */
  private File file;

  private String ifProperty;

  private String unlessProperty;

  public void setPath(String path) {
    this.path = path;
  }

  public void setPosition(String pos) {
    if ("before".equals(pos)) {
      this.position = InsertAction.Position.BEFORE;
    }
    else if ("after".equals(pos)) {
      this.position = InsertAction.Position.AFTER;
    }
    else if ("under".equals(pos)) {
      this.position = InsertAction.Position.UNDER;
    }
    else {
      log("Don't recognise position '" + pos + "'", Project.MSG_WARN);
    }
    if (this.action != null) {
      this.action.setPosition(this.position);
    }
  }

  private void log(final String msg, final int level) {
    if (this.task != null) {
      this.task.log(msg, level);
    }
    else {
      System.out.println(msg);
    }
  }

  public void setXml(final String xml) throws Exception {
    this.xml = xml;
  }

  public void setFile(final File file) throws Exception {
    this.file = file;
  }

  public void setExpandProperties(final boolean expandProperties) {
    this.expandProperties = expandProperties;
  }

  /**
   * used to insert literal text placed within the build.xml under the insert
   * element
   * 
   * @param text
   * @throws Exception
   */
  public void addText(final String text) throws Exception {
    this.text = text;
  }

  public void setBuffer(final String buffer) throws Exception {
    this.buffer = buffer;
  }

  private void register() {
    try {
      if (this.xml != null) {
        this.action = InsertAction.fromString(this.xml, this.task);
      }
      else if (this.file != null) {
        this.action = InsertAction.fromFile(this.file, this.task);
      }
      else if (this.buffer != null) {
        this.action = InsertAction.fromBuffer(this.buffer, this.task);
      }
      else if (this.text != null) {
        if (this.expandProperties) {
          // we expand properties by default...
          this.text = ProjectHelper.replaceProperties(this.task.getProject(), this.text, this.task.getProject().getProperties());
        }
        this.action = InsertAction.fromString(this.text, this.task);
      }
    }
    catch (Exception e) {
      throw new BuildException("Failed to add text to insert/paste", e);
    }
    if (this.action != null && this.path != null) {
      this.action.setPosition(this.position);
      XmlReplace xmlReplace = new XmlReplace(this.path, this.action);
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
