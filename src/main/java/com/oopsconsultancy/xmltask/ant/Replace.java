package com.oopsconsultancy.xmltask.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;

import com.oopsconsultancy.xmltask.Action;
import com.oopsconsultancy.xmltask.TextAction;
import com.oopsconsultancy.xmltask.XmlAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/**
 * the Ant replacement task
 * 
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Replace.java,v 1.5 2006/11/01 22:40:38 bagnew Exp $
 */
public class Replace implements Instruction {

  private XmlTask task = null;

  private Action action = null;

  private String path = null;

  private boolean expandProperties = true;

  /**
   * the raw text to insert
   */
  private String text;

  /**
   * the file to insert
   */
  private File file;

  /**
   * the explicit XML to insert
   */
  private String xml;

  /**
   * the buffer to insert
   */
  private String buffer;

  private String ifProperty;

  private String unlessProperty;

  public void setPath(String path) {
    this.path = path;
  }

  public void setWithtext(final String to) throws Exception {
    this.action = new TextAction(to);
  }

  public void setWithxml(final String xml) throws Exception {
    this.xml = xml;
  }

  public void setExpandProperties(final boolean expandProperties) {
    this.expandProperties = expandProperties;
  }

  /**
   * used to insert literal text placed within the build.xml under the replace
   * element
   * 
   * @param text
   * @throws Exception
   */
  public void addText(final String text) throws Exception {
    this.text = text;
  }

  public void setWithfile(final File file) throws Exception {
    this.file = file;
  }

  public void setWithBuffer(final String buffer) throws Exception {
    this.buffer = buffer;
  }

  private void register() {
    try {
      if (this.buffer != null) {
		    this.action = XmlAction.xmlActionfromBuffer(this.buffer, this.task);
      }
      if (this.xml != null) {
		    this.action = XmlAction.xmlActionfromString(this.xml, this.task);
      }
      else if (this.file != null) {
        this.action = XmlAction.xmlActionfromFile(this.file, this.task);
      }
      else if (this.text != null) {
        if (this.expandProperties) {
          this.text = ProjectHelper.replaceProperties(this.task.getProject(), this.text, this.task.getProject().getProperties());
        }
        this.action = XmlAction.xmlActionfromString(this.text, this.task);
      }
    }
    catch (Exception e) {
      throw new BuildException("Failed to specify text in replace", e);
    }
    if (this.path != null && this.action != null) {
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

  @Override
public void setIf(final String ifProperty) {
    this.ifProperty = ifProperty;
  }

  @Override
public void setUnless(final String unlessProperty) {
    this.unlessProperty = unlessProperty;
  }
}
