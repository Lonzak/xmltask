package com.oopsconsultancy.xmltask.ant;

/**
 * name + value (XPath expression) for the call instruction. The
 * value can be specified instead of the XPath to provide non-XML
 * config. Defaults can be provided if the XPath doesn't match
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Param.java,v 1.6 2006/09/17 16:20:53 bagnew Exp $
 */
public class Param {

  private String name;
  private String path;
  private String value;
  private String def;

  public Param() {
  }

  /**
   * sets the param name
   *
   * @param name
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * sets the XPath to use
   *
   * @param path
   */
  public void setPath(final String path) {
    this.path = path;
  }

  /**
   * sets the value. Used either directly or via the XPath stuff
   *
   * @param value
   */
  public void setValue(final String value) {
    this.value = value;
  }

  /**
   * set the default to use
   *
   * @param def
   */
  public void setDefault(final String def) {
    this.def = def;
  }


  /**
   * @return the set value, or the default if this doesn't exist. The
   * default may be null as well
   */
  public String getValue() {
    if (this.value != null) {
      return this.value;
    }
    return this.def;
  }

  public String getPath() {
    return this.path;
  }

  public String getName() {
    return this.name;
  }

  public void set(final XmlTask task, final String value) {
    this.value = value;
    // what does the below do ? Nothing - as far as I can tell
 //   task.getProject().setNewProperty(name, value);
  }

  @Override
public String toString() {
    return getName() + " = " + getValue() + (this.path != null ? " [path=" + this.path + "]" : "");
  }
}
