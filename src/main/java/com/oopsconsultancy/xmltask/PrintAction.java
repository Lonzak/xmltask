package com.oopsconsultancy.xmltask;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.w3c.dom.Node;


/**
 * performs diagnostic printing
 *
 * @author <a href="mailto:brian@oopsconsultancy.com">Brian Agnew</a>
 * @version $Id: PrintAction.java,v 1.3 2007/01/22 11:58:09 bagnew Exp $
 */
public class PrintAction extends Action {


  private final String buffer;
  private final String ident;
  private final Task task;

  private PrintAction(final Task task, final String path, final String buffer, final String ident) {
  this.task = task;
    // any of the below can be null (!)
    this.buffer = buffer;
    this.ident = ident;
  }

  /**
   * @param path
   * @return a print action matching against this path
   */
  public static PrintAction newInstanceFromPath(final Task task, final String path, final String ident) {
    return new PrintAction(task, path, null, ident);
  }

  /**
   * @return a print action matching against this buffer
   */
  public static PrintAction newInstanceFromBuffer(final Task task, final String buffer, final String ident) {
    return new PrintAction(task, null, buffer, ident);
  }

  /**
   * called on the given node. If printing a buffer, then simply
   * display the buffer. Otherwise display the matched node. The calling
   * mechanism will determine whether to call just the once
   * if not matching against a path
   *
   * @param node
   * @return true (always)
   * @throws Exception
   */
  @Override
public boolean apply(final Node node) throws Exception {
    Node[] nodes = null;
    String id  =null;
    if (node == null) {
      // then find the buffer. We won't be called with a null
      // node unless we have a buffer (I think)
      if (this.buffer == null) {
        System.err.println("No buffer specified");
        return false;
      }
      nodes = BufferStore.get(this.buffer, this.task);
      if (nodes == null) {
        System.err.println("Couldn't find any entries for buffer '"+ this.buffer +"'");
        return false;
      }
      id = "buffer '" + this.buffer +"'";
    }
    else {
      nodes = new Node[]{node};
      id = "node";
    }
    Transformer serializer = TransformerFactory.newInstance().newTransformer();
    serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    for (int i = 0; i < nodes.length; i++) {
      try {
        System.out.println("{ "+id + " output" + (this.ident != null ? " - " + this.ident : ""));

        serializer.transform(new DOMSource(nodes[i]), new StreamResult(System.out));
        System.out.println("} "+id + " output");
      }
      catch (Exception e) {
        System.err.println("Problem during output");
        e.printStackTrace();
      }

    }

    return true;
  }

  public static void log(final String msg, final Task task) {
    if (task != null) {
      task.log(msg, Project.MSG_VERBOSE);
    }
    else {
      System.out.println(msg);
    }
  }

}

