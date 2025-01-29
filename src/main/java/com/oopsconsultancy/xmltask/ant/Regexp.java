package com.oopsconsultancy.xmltask.ant;

import com.oopsconsultancy.xmltask.RegexpAction;
import com.oopsconsultancy.xmltask.XmlReplace;

/**
 * performs regular expression work
 * 
 * @author brianagnew
 * 
 */
public class Regexp implements Instruction {

	private String ifProperty;
	private String unlessProperty;
	private XmlTask task;
	private String path;
	private String pattern;
	private String property;
	private String buffer;
	private String replace;
	private boolean caseSensitive = true;
	private boolean unicodeCase = false;

	@Override
    public void process(final XmlTask xmltask) {
		this.task = xmltask;
		register();
	}

	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	public void setProperty(final String property) {
		this.property = property;
	}

	public void setBuffer(final String buffer) {
		this.buffer = buffer;
	}

	public void setReplace(final String replace) {
		this.replace = replace;
	}

	public void setCaseSensitive(final boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
	public void setUnicodeCase(final boolean unicodeCase) {
		this.unicodeCase = unicodeCase;
	}
	
	public void setPath(final String path) {
		this.path = path;
	}

	/**
	 * builds the appropriate action
	 */
	private void register() {
		RegexpAction action = null;
		if (this.replace != null) {
			if (this.property != null || this.buffer != null) {
				throw new IllegalArgumentException("Can only specify one of replace/property/buffer for a regexp");
			}
			action = RegexpAction.createReplacement(this.task, this.pattern, this.replace);
		}
		if (this.property != null) {
			if (this.replace != null || this.buffer != null) {
				throw new IllegalArgumentException("Can only specify one of replace/property/buffer for a regexp");
			}
			action = RegexpAction.createCopyToProperty(this.task, this.pattern, this.property);
		}
		if (this.buffer != null) {
			if (this.replace != null || this.property != null) {
				throw new IllegalArgumentException("Can only specify one of replace/property/buffer for a regexp");
			}
			action = RegexpAction.createCopyToBuffer(this.task, this.pattern, this.buffer);
		}
		if (action == null) {
			throw new IllegalStateException("Failed to build a regexp action from inputs");
		}
		action.setCaseInsensitive(!this.caseSensitive);
		action.setUnicodeCase(this.unicodeCase);
		XmlReplace xmlReplace = new XmlReplace(this.path, action);
		xmlReplace.setIf(this.ifProperty);
		xmlReplace.setUnless(this.unlessProperty);
		this.task.add(xmlReplace);
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
