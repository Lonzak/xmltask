package com.oopsconsultancy.xmltask.output;
 
import java.io.Writer;

import javax.xml.transform.Transformer;

import org.xml.sax.ContentHandler;

/** 
 * the interface that xmltask output mechanisms have to
 * adhere to... See FormattedDataWriter for an example of
 * usage
 * 
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: Outputter.java,v 1.2 2003/08/08 21:01:51 bagnew Exp $
 */
public interface Outputter extends ContentHandler {

  /** 
   * this is the writer that the implementing class
   * must write to
   * 
   * @param w 
   */
  public void setWriter(Writer w);

  /** 
   * the transformer will contain definitions for the public and
   * system ids, encoding etc. See the appropriate Javadoc for
   * more info
   * 
   * @param transformer 
   */
  public void setTransformer(Transformer transformer);
}
