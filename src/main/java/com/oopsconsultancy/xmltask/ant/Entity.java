package com.oopsconsultancy.xmltask.ant;

public class Entity {

  private XmlTask task = null;
  private String remote = null;
  private String local = null;

  public Entity(XmlTask task) {
    this.task = task;
  }

  public void setRemote(String remote) {
    this.remote = remote;
    register();
  }

  public void setLocal(String local) {
    this.local = local;
    register();
  }

  private void register() {
    if (this.remote != null && this.local != null) {
      this.task.registerEntity(this.remote, this.local);
    }
  }
}

