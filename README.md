# XMLTask - based on OOPS Consultancy XMLTask Project

## Contents
- [Introduction](#introduction)
- [Recent Changes](#recent-changes)
- [Usage](#usage)  
  - [XmlTask](#usagexmltask)  
  - [Attr](#usageattr)  
  - [Cut](#usagecut)  
  - [Copy](#usagecopy)  
  - [Paste](#usagepaste)  
  - [Insert](#usageinsert)  
  - [Replace](#usagereplace)  
  - [Regexp](#usageregexp)  
  - [Remove](#usageremove)  
  - [Rename](#usagerename)  
  - [Call](#usagecall)  
  - [Print](#usageprint)  
  - [XmlCatalog](#usagexmlcatalog)  
  - [Uncomment](#usageuncomment)
- [Buffers](#buffers)
- [Formatting](#formatting)
- [Examples](#examples)

---

## Introduction
`xmltask` provides the facility for automatically editing XML files as part of an [Ant](http://jakarta.apache.org/ant) build. Unlike the standard `filter` task provided with Ant, it is XML-sensitive, but doesn't require you to define XSLTs.

Uses include:
- modifying configuration files for applications during builds
- inserting/removing information for J2EE deployment descriptors
- dynamically building Ant `build.xml` files during builds
- building and maintaining websites built with XHTML
- driving Ant via a *meta* build.xml to abstract out build processes

---

## Recent Changes

**Version 1.17.0**
- Java17 compatible xmltask (originally based on OOPS Consultancy xmltask)
- Mavenized the project
- Clean-up
- junit tests reworked

**Version 1.16.0**

- [Regular expressions](#usageregexp) for changing text are now available.
- Copying/cutting to properties can now handle multiple values from an XPath expression. String trimming and concatenation (with a specified separator character) is supported.
- Support for Java versions prior to 1.5 has been removed. Older versions of `xmltask` are available from the Sourceforge project download area.

`xmltask` is released under the Apache license.

---

## Usage

To use this task, make sure:

1. The `xmltask.jar` is in your `$CLASSPATH`.
2. Reference the `xmltask` in your `build.xml` e.g.:

```xml
<taskdef name="xmltask" classname="com.oopsconsultancy.xmltask.ant.XmlTask"/>
```

> Note: If you use the above with an additional `classpath` attribute then you will have problems with using buffers across multiple `xmltask` calls. See [Buffers](#buffers) for more information.

3. Reference the `xmltask` task as part of your build e.g.:

```xml
<target name="main">
  <xmltask source="input.xml" dest="output.xml">
    ...
  </xmltask>
</target>
```

---

### Usage: XmlTask
<a name="usagexmltask"></a>

`xmltask` allows you to specify sections of an XML file to append to, replace, remove or modify. The sections of the XML document to be modified are specified by XPath references, and the XML to insert can be specified inline in the Ant build.xml, or loaded from files.

- The main `<xmltask>` section takes arguments to define an XML source and a destination file or directory. Note that the XML source is optional if you're creating a new document via `<xmltask>` instructions.  
  `dest` and `todir` can be omitted if you're reading a document and storing subsections in buffers for use by another task (see below).
- `<fileset>`s are used to define sets of files for `xmltask` to operate on. See the standard Ant documentation for information on using filesets.

#### Parameters

| Attribute              | Description                                                                                                                                                                                                    | Required |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| **source**             | the source XML file to load. Can take the form of a wildcarded pattern eg. `**/*.xml`. Note that this capability will be deprecated in favour of `<fileset>` usage.                                            | no       |
| **sourcebuffer**       | the source [buffer](#buffers) containing XML from a previous `<xmltask>` invocation. The buffer must contain a *single* root node (i.e be *well-formed*). If the buffer is empty, then this has the effect of starting with a blank document. | no       |
| **dest**               | the output XML file to write to                                                                                                                                                                               | no       |
| **destbuffer**         | the output buffer to write to                                                                                                                                                                                 | no       |
| **todir**              | the output directory to write to                                                                                                                                                                              | no       |
| **report**             | when set to true, will result in diagnostic output                                                                                                                                                             | no       |
| **public**             | sets the PUBLIC identifier in the output XML DOCTYPE declaration                                                                                                                                              | no       |
| **expandEntityReferences** | when set to true, will enable entity reference expansion. Defaults to true                                                                                                                            | no       |
| **system**             | sets the SYSTEM identifier in the output XML DOCTYPE declaration                                                                                                                                              | no       |
| **preservetype**       | when set to true sets the PUBLIC and SYSTEM identifiers to those of the original document                                                                                                                     | no       |
| **failWithoutMatch**   | when set to true will stop the `xmltask` task (and hence the build process) if any subtask fails to match nodes using the given XPath path                                                                     | no       |
| **indent**             | when set to true enables indented formatting of the resultant document. This defaults to true                                                                                                                 | no       |
| **encoding**           | determines the character encoding value for the output document                                                                                                                                                | no       |
| **outputter**          | determines the output mechanism to be used. See [formatting](#formatting) for more info                                                                                                                       | no       |
| **omitHeader**         | when set to true forces omission of the `<?xml ...?>` header. Note that the XML spec *should* include the header, but it is not mandated for XML v1.0                                                          | no       |
| **standalone**         | when set to true/false sets the `standalone` attribute of the header                                                                                                                                           | no       |
| **clearBuffers**       | Clears buffers after population by previous `xmltask` invocations. Buffers are cleared after every input file is processed. Buffers are specified in a comma-delimited string                                  | no       |


**Examples**:

```xml
<xmltask source="input.xml" dest="output.xml">
```
reads from `input.xml` and writes to `output.xml`.

```xml
<xmltask todir="output">
  <fileset dir=".">
    <includes name="*.xml"/>
  </fileset>
</xmltask>
```
reads from the XML files in the current dir and writes to the same filenames in the `output` dir.

```xml
<xmltask sourcebuffer="servlet" output="servlet.xml">
```
reads from the previously populated buffer `servlet` and writes to `servlet.xml`.

```xml
<xmltask source="input.xml" destbuffer="output">
```
reads from a file `input.xml` and writes to the buffer called `output`.

Nested elements allow replacements to take place, and are applied in the order that they're specified in. Each subsection may match zero or more nodes. Standard XPath paths are used. See examples below for hints, or further [tutorials on XPath elsewhere online](#).

---

### Usage: Cut
<a name="usagecut"></a>

`<cut>` allows an XML section to be cut and stored in a [buffer](#buffers) or a property. Multiple XML nodes or elements can be cut to a buffer or property by using the `append` attribute.

#### Parameters

| Attribute          | Description                                                                                                                                                                                                                                  | Required |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| **path**           | the XPath reference of the element(s) to cut                                                                                                                                                                                                  | yes      |
| **buffer**         | the buffer to store the cut XML                                                                                                                                                                                                               | no       |
| **property**       | the property to store the cut XML                                                                                                                                                                                                             | no       |
| **append**         | when set to `true`, *appends* to the given buffer or property. You can only append when creating a new property since Ant properties are immutable (i.e. when an XPath resolves to multiple text nodes)                                       | no       |
| **attrValue**      | Cutting an attribute will result in the *whole* attribute plus value being cut. When `attrValue` is set to true then only the attribute's *value* is cut. This is *implicit* for cutting to properties                                        | no       |
| **trim**           | trims leading/trailing spaces when writing to properties                                                                                                                                                                                      | no       |
| **propertySeparator** | defines the separating string when appending properties                                                                                                                                                                                   | no       |
| **if**             | only performed if the given property is set                                                                                                                                                                                                   | no       |
| **unless**         | performed *unless* the given property is set                                                                                                                                                                                                  | no       |

**Examples**:

```xml
<cut path="web/servlet/context/root[@id='2']/text()" buffer="namedBuffer"/>
<cut path="web/servlet/context/root[@id='2']/text()" property="property1"/>
```

---

### Usage: Copy
<a name="usagecopy"></a>

`<copy>` allows an XML section to be copied and stored in a [buffer](#buffers) or a property. Multiple XML nodes or elements can be copied to a buffer or property by using the `append` attribute.

#### Parameters

| Attribute          | Description                                                                                                                                                                                                                                  | Required |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| **path**           | the XPath reference of the element(s) to copy                                                                                                                                                                                                  | yes      |
| **buffer**         | the buffer to store the copied XML                                                                                                                                                                                                             | no       |
| **property**       | the property to store the copied XML                                                                                                                                                                                                           | no       |
| **append**         | when set to `true`, *appends* to the given buffer or property. You can only append when creating a new property since Ant properties are immutable (i.e. when an XPath resolves to multiple text nodes)                                       | no       |
| **attrValue**      | Copying an attribute will result in the *whole* attribute plus value being copied. When `attrValue` is set to true then only the attribute's *value* is copied. This is now *implicit* for copying to properties                               | no       |
| **propertySeparator** | defines the separating string when appending properties                                                                                                                                                                                   | no       |
| **trim**           | trims leading/trailing spaces when writing to properties                                                                                                                                                                                      | no       |
| **if**             | only performed if the given property is set                                                                                                                                                                                                   | no       |
| **unless**         | performed *unless* the given property is set                                                                                                                                                                                                  | no       |

**Examples**:

```xml
<copy path="web/servlet/context/root[@id='2']/text()" buffer="namedBuffer"/>
<copy path="web/servlet/context/root[@id='2']/text()" property="property1"/>
```

---

### Usage: Paste
<a name="usagepaste"></a>

`<paste>` allows the contents of a [buffer](#buffers) or a property to be pasted into an XML document. This is a **synonym** for the insert section (see below).

---

### Usage: Insert
<a name="usageinsert"></a>

`<insert>` allows you to specify an XML node and the XML to insert below or alongside it.

#### Parameters

| Attribute          | Description                                                                                                                                                                                                                                                              | Required |
|--------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| **path**           | the XPath reference of the element(s) to insert into                                                                                                                                                                                                                     | yes      |
| **buffer**         | the buffer to paste                                                                                                                                                                                                                                                      | no       |
| **file**           | the file to paste                                                                                                                                                                                                                                                        | no       |
| **xml**            | the literal XML to paste                                                                                                                                                                                                                                                  | no       |
| **expandProperties** | indicates whether properties in body text XML are expanded or not. Defaults to `true`                                                                                                                                                                                | no       |
| **position**       | where the XML is to be inserted in relation to the XML highlighted by `path`. The allowed positions are `before`, `after`, or `under`. The default position is `under`.                                                                                                   | no       |
| **if**             | only performed if the given property is set                                                                                                                                                                                                                               | no       |
| **unless**         | performed *unless* the given property is set                                                                                                                                                                                                                              | no       |

**Examples**:

```xml
<insert path="/web/servlet/context/root[@attr='val']" xml="&lt;B/&gt;"/>
<insert path="/web/servlet/context/root[@attr='val']" file="insert.xml"/>
<insert path="/web/servlet/context/root[@attr='val']" buffer="namedBuffer" position="before"/>
<insert path="/web/servlet/context/root[@attr='val']" xml="${property1}" position="before"/>
```

The XML to insert can be a *document fragment* (i.e., it doesn't require a single root node). Example fragments:

```xml
<welcome-file-list/>
```
(a well-formed document)

```xml
<servlet-mapping id="1"/><servlet-mapping id="2"/>
```
(a well-formed document *without* a root node)

The XML to insert can also be specified as *body text* within the `<insert>` task:

```xml
<insert path="web/servlet/context/root[@id='2']/text()">
  <![CDATA[
    <node/>
  ]]>
</insert>
```
Note that the XML has to be specified within a `CDATA` section. Ant properties are expanded within these sections, unless `expandProperties` is set to `false`.

You can create a new document by not specifying a source file, and making the first instruction for `<xmltask>` an `<insert>` or `<paste>` with the appropriate root node (and any subnodes).

---

### Usage: Replace
<a name="usagereplace"></a>

`<replace>` allows you to specify an XML node and what to replace it with.

#### Parameters

| Attribute     | Description                                                                                                                                                                                                                                                                              | Required |
|---------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| **path**      | the XPath reference of the element(s) to replace. If this represents an attribute, then the value of the attribute will be changed. In this scenario you can only specify text as replacement                                                                                            | yes      |
| **withText**  | the text to insert in place of the nominated nodes                                                                                                                                                                                                                                        | no       |
| **withXml**   | the literal XML to insert in place of the nominated nodes                                                                                                                                                                                                                                 | no       |
| **withFile**  | the file containing XML to insert in place of the nominated nodes                                                                                                                                                                                                                        | no       |
| **withBuffer**| the buffer containing XML to insert in place of the nominated nodes                                                                                                                                                                                                                      | no       |
| **expandProperties** | indicates whether properties in body text XML are expanded or not. Defaults to `true`                                                                                                                                                                                         | no       |
| **if**        | only performed if the given property is set                                                                                                                                                                                                                                              | no       |
| **unless**    | performed *unless* the given property is set                                                                                                                                                                                                                                             | no       |

**Examples**:

```xml
<replace path="web/servlet/context/root[@id='2']/text()" withText="2"/>
<replace path="web/servlet/context/root[@id='2']/@id" withText="3"/>
<replace path="web/servlet/context/root[@id='2']/text()" withXml="&lt;id&gt;"/>
<replace path="web/servlet/context/root[@id='2']/" withFile="substitution.xml"/>
<replace path="web/servlet/context/root[@id='2']/" withBuffer="namedBuffer"/>
```

*(note that to include literal XML using `withXml`, angle brackets have to be replaced with entities). The XML can be a well-formed document *without* any root node.*

The XML to insert can be specified as *body text* within the `<replace>` task e.g.:

```xml
<replace path="web/servlet/context/root[@id='2']/text()">
  <![CDATA[
    <node/>
  ]]>
</replace>
```
Note that the XML has to be specified within a `CDATA` section. Ant properties are expanded within these sections, unless `expandProperties` is set to `false`.

---

### Usage: Attr
<a name="usageattr"></a>

`<attr>` allows you to specify an XML node and how to add, change or remove its attributes.

#### Parameters

| Attribute | Description                                                                                                                                                              | Required |
|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| **path**  | the XPath reference of the element(s) to be changed                                                                                                                      | yes      |
| **attr**  | the name of the attribute to be added/changed or removed                                                                                                                  | yes      |
| **value** | the value to set the attribute to                                                                                                                                         | no       |
| **remove**| if set to `true`, indicates that the nominated attribute should be removed                                                                                                 | no       |
| **if**    | only performed if the given property is set                                                                                                                               | no       |
| **unless**| performed *unless* the given property is set                                                                                                                              | no       |

**Examples**:

```xml
<attr path="web/servlet/context[@id='4']/" attr="id" value="test"/>
<attr path="web/servlet/context[@id='4']/" attr="id" remove="true"/>
```
Note that in the first example, if the attribute `id` doesn't exist, it will be added.

---

### Usage: Remove
<a name="usageremove"></a>

`<remove>` allows you to specify an XML section to remove.

#### Parameters

| Attribute | Description                                                       | Required |
|-----------|-------------------------------------------------------------------|----------|
| **path**  | the XPath reference of the element(s) to be removed              | yes      |
| **if**    | only performed if the given property is set                       | no       |
| **unless**| performed *unless* the given property is set                      | no       |

**Example**:

```xml
<remove path="web/servlet/context[@id='redundant']"/>
```

---

### Usage: Regexp
<a name="usageregexp"></a>

`<regexp>` allows you to specify XML text to change via regular expressions.

#### Parameters

| Attribute        | Description                                                                                                                                                                                      | Required |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| **path**         | the XPath reference of the element(s) to be changed or copied                                                                                                                                    | yes      |
| **pattern**      | The regular expression to apply to the text node or attribute value                                                                                                                              | yes      |
| **replace**      | The text to replace the matched expression with                                                                                                                                                  | no       |
| **property**     | The property to copy the matched expression into. A [capturing group](http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html#cg) must be used to specify the text to capture      | no       |
| **buffer**       | The buffer to copy the matched expression into. A [capturing group](http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html#cg) must be used to specify the text to capture        | no       |
| **casesensitive**| Sets case sensitivity of the regular expression. Defaults to *true*                                                                                                                              | no       |
| **if**           | only performed if the given property is set                                                                                                                                                     | no       |
| **unless**       | performed *unless* the given property is set                                                                                                                                                    | no       |

The `<regexp>` task uses the standard [Java regular expression mechanism](http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html). Replacements can make use of [capturing groups](http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html#cg). When copying to a buffer or a property, a capturing group *must* be specified to determine the text to be copied.

**Examples**:

```xml
<regexp path="/web-app/servlet/servlet-name/text()" pattern="Test" replace="Prod"/>
<regexp path="/web-app/servlet/servlet-name/text()" pattern="Servlet-([a-z])-([0-9]*)" replace="Servlet-$2-$1"/>
<regexp path="/web-app/servlet/servlet-name/text()" pattern="(.*)Test" property="servlet.name"/>
<regexp path="/web-app/servlet/servlet-name/text()" pattern="(.*)Test" buffer="servlet.name"/>
```

Note the use of capturing groups to reverse components of the servlet's name, or to determine the servlet name substring to copy to a buffer or property.

---

### Usage: Rename
<a name="usagerename"></a>

`<rename>` allows you to specify an XML element or attribute to rename.

#### Parameters

| Attribute | Description                                               | Required |
|-----------|-----------------------------------------------------------|----------|
| **path**  | the XPath reference of the element(s) to be renamed       | yes      |
| **to**    | the new node name                                        | yes      |
| **if**    | only performed if the given property is set               | no       |
| **unless**| performed *unless* the given property is set              | no       |

**Examples**:

```xml
<rename path="a/b/c[@id='1']" to="d"/>
<rename path="a/b/@c" to="d"/>
```

---

### Usage: Call
<a name="usagecall"></a>

`<call>` allows you to perform actions or call Ant targets in the same `build.xml` file for nodes identified by an XPath.

#### Parameters

| Attribute   | Description                                                                                                 | Required |
|-------------|-------------------------------------------------------------------------------------------------------------|----------|
| **path**    | the XPath reference of the element(s) to be identified                                                     | yes      |
| **target**  | the Ant target to call for each identified node                                                             | no       |
| **buffer**  | the buffer to use to store each identified node (for the duration of the target call)                       | no       |
| **inheritAll**  | boolean indicating if the target being called inherits all properties. Defaults to *true*              | no       |
| **inheritRefs** | boolean indicating if the target being called inherits all references. Defaults to *false*             | no       |
| **if**      | only performed if the given property is set                                                                 | no       |
| **unless**  | performed *unless* the given property is set                                                                | no       |

**Example**:  

In the below example, the Ant target `CNode` is called for *each* occurrence of the `C` node in the given XPath expression. For each call to `CNode` the buffer `abc` is populated with the node identified (plus any subnodes).

```xml
<call path="a/b/c" target="CNode" buffer="abc"/>
```

In the below example, Ant actions are *embedded* within the `<call>` action (Ant 1.6 and above only):

```xml
<call path="a/b/c">
  <actions>
    <echo>Found a node under a/b/c</echo>
  </actions>
</call>
```

This mechanism can be used to drive Ant builds from existing XML resources such as `web.xml` or `struts.xml`, or to provide a *meta-build* facility for Ant, by driving the `build.xml` from a higher-level proprietary XML config.

Properties can be set for the target being called using XPath syntax or simply as existing properties or static strings. e.g.:

```xml
<call path="a/b/c" target="CNode" buffer="abc">
  <param name="val" path="text()"/>
  <param name="id" path="@id" default="n/a"/>
  <param name="os" value="${os.name}"/>
</call>
```

the property *val* is set to the value of the text node under `C`, and the property *id* is set to the corresponding id attribute. If the id attribute is missing then "n/a" will be substituted. *os* is set to the OS.

The same can be done for *embedded* actions:

```xml
<call path="a/b/c">
  <param name="val" path="text()"/>
  <param name="id" path="@id" default="n/a"/>
  <param name="os" value="${os.name}"/>
  <actions>
    <echo>val = @{val}</echo>
    <echo>id = @{id}</echo>
  </actions>
</call>
```

Note how the parameters are dereferenced in this example (using `@{...}`). Note also that for embedded actions each property *must* have a value assigned to it. If in doubt use the `default` attribute in the `<param>` instruction.

---

### Usage: Print
<a name="usageprint"></a>

`<print>` allows you to dump out to standard output the XML matching a given XPath expression, or the contents of a buffer. This is a considerable help in performing debugging of scripts.

#### Parameters

| Attribute | Description                                                                                  | Required |
|-----------|----------------------------------------------------------------------------------------------|----------|
| **path**  | the XPath reference of the element(s) to be identified                                      | no       |
| **buffer**| the buffer to print out                                                                      | no       |
| **comment**| a corresponding comment to print out                                                       | no       |

**Example**:

```xml
<print path="a/b/c" comment="Nodes matching a/b/c"/>
<print buffer="buffer1" comment="Contents of buffer 1"/>
```

This instruction has no effect on the documents being scanned or generated.

---

### Usage: XmlCatalog
<a name="usagexmlcatalog"></a>

`xmltask` supports the Ant 1.5 [`<xmlcatalog>`](http://jakarta.apache.org/ant/manual/CoreTypes/xmlcatalog.html) element, which allows you to specify local copies of DTDs. This allows you to specify a DOCTYPE referred to in the original document, and the local DTD to use instead (useful if you're behind firewalls etc.).

**Example**:

```xml
<xmlcatalog id="dtds">
  <dtd publicId="-//OOPS Consultancy//DTD Test 1.0//EN" location="./local.dtd"/>
</xmlcatalog>

<xmltask source="18.xml" dest="18-out.xml" report="true">
  <xmlcatalog refid="dtds"/>
  <!-- set a text element to a value -->
  ...
</xmltask>
```

The first snippet references a local copy of a DTD.

Alternatively, you can use the legacy `<entity>` element within `<xmltask>`:

```xml
<entity remote="-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN" local="web.dtd"/>
<entity remote="-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN" local=""/>
```

The first version above specifies a local version of the DTD. The second indicates that the remote entity will be ignored completely. Note that the `remote` attribute can take either the PUBLIC specification or the SYSTEM specification.

---

### Usage: Uncomment
<a name="usageuncomment"></a>

The `uncomment` instruction allows you to uncomment sections of XML. This means you can maintain different XML fragments within one document and enable a subset. For instance, you can maintain different configs and only enable one at deployment.

#### Parameters

| Attribute | Description                                                                                                                                 | Required |
|-----------|---------------------------------------------------------------------------------------------------------------------------------------------|----------|
| **path**  | the path of the comment to uncomment. This must resolve to a comment within the input document                                             | yes      |
| **if**    | only performed if the given property is set                                                                                                 | no       |
| **unless**| performed *unless* the given property is set                                                                                                | no       |

**Example**:

```xml
<xmltask source="server.xml" dest="server.xml" report="true">
  <!-- enables a servlet configuration -->
  <uncomment path="/server/service[@name='Tomcat-Standalone']/comment()"/>
  ...
</xmltask>
```

---

## Buffers
<a name="buffers"></a>

Buffers are used to store nodes found by [`<cut>`](#usagecut) and [`<copy>`](#usagecopy) operations, and those nodes can be inserted into a document using [`<insert> / <paste>`](#usageinsert).

Buffers exist for the duration of the Ant process and consequently can be used across multiple invocations of `<xmltask>`. For example:

```xml
<target name="cut">
  <xmltask source="input.xml" dest="1.xml">
    <cut path="web/servlet/context/config[@id='4']" buffer="storedXml"/>
  </xmltask>
</target>

<target name="paste" depends="cut">
  <xmltask source="input.xml" dest="output.xml">
    <paste path="web/servlet/context/config[@id='5']" buffer="storedXml"/>
  </xmltask>
</target>
```

the buffer *storedXml* is maintained across multiple targets.

A buffer can record *multiple* nodes (either resulting from multiple matches or multiple `<cut>` / `<copy>` operations). This operation is enabled through use of the `append` attribute. e.g.:

```xml
<cut path="web/servlet/context/config" buffer="storedXml" append="true"/>
```

A buffer can store all types of XML nodes e.g. text / elements / attributes. Note that when recording an attribute node, both the name of the attribute and the value will be recorded. To store the value alone of an attribute, the `attrValue` attribute can be used e.g.:

```xml
<copy path="web/servlet/@id" buffer="id" attrValue="true"/>
```

This will store the value of the `id` attribute. The value can be used as a text node in a subsequent [`<insert>`](#usageinsert) / [`<paste>`](#usagepaste).

Buffers can be persisted to files. This permits buffers to be used across Ant invocations, and uses of [`<antcall>`](http://ant.apache.org/manual/CoreTasks/antcall.html). To persist a buffer to a file, simply name it using a file URL. e.g.:

```xml
<cut path="/a/b" buffer="file://build/buffers/1"/>
```

and the operation will write the cut XML to a file `build/buffers/1`. This file will persist after Ant exits, so care should be taken to remove it if required. The file will be created automatically, but any directories required must exist prior to the buffer being used.

---

## Formatting
<a name="formatting"></a>

The formatting of the output document is controlled by the attribute `outputter`. There are three options:

1. **`<xmltask outputter="default"...>`**  
   Outputs the document *as is*. That is, all whitespace etc. is preserved. Note that attribute ordering *may* change and elements containing attributes may be split over several lines, but semantically it remains the same.

2. **`<xmltask outputter="simple"...>`**  
   Outputs the document with a degree of formatting. Elements are indented and given new lines wherever possible to make a more readable document. This is not suitable for all applications since some XML consumers will be whitespace-sensitive.

   You can customize spacing by specifying something like `<xmltask outputter="simple:{indent}...">`.
   For example: `<xmltask outputter="simple:1"...>` produces:

   ```xml
   <root>
    <branch/>
   </root>
   ```

   And `<xmltask outputter="simple:4"...>` produces:

   ```xml
   <root>
       <branch/>
   </root>
   ```

3. **`<xmltask outputter="{classname}"...>`**  
   Outputs the document using the nominated class as the outputting mechanism. This allows you to control the output of the document to your own tastes. The specified class must:
   - Have a default (no-argument) constructor.
   - Implement the `com.oopsconsultancy.xmltask.output.Outputter` interface (which itself extends `org.xml.sax.ContentHandler`).

   For each callback, you should generate your results and write them to the writer object passed in via `setWriter()`. Comments, CDATA sections, etc. can be handled if you also implement `org.xml.sax.ext.LexicalHandler`.

A simple introduction is to look at the `com.oopsconsultancy.xmltask.output.FormattedDataWriter` source code (in the source tarball).

---

## Examples
<a name="examples"></a>

Some examples of common usage:

1. **Extracting the title from an XHTML file and storing it in a buffer**:
   ```xml
   <copy path="/xhtml/head/title/text()" buffer="title"/>
   ```

2. **Extracting the title from an XHTML file and storing it in a property**:
   ```xml
   <copy path="/xhtml/head/title/text()" property="title"/>
   ```

3. **Inserting a servlet definition into a `web.xml` (only if `insert.reqd` is set)**:
   ```xml
   <insert if="insert.reqd" path="/web-xml/servlet[last()]" position="after" file="newservlet.xml"/>
   ```

4. **Inserting a servlet definition into `web.xml` (another way - note properties usage)**:
   ```xml
   <insert path="/web-xml/servlet[last()]" position="after">
     <![CDATA[
       <servlet>
         <servlet-name>
           ${project.name}
         </servlet-name>
       </servlet>
     ]]>
   </insert>
   ```

5. **Replacing text occurrences within particular `div` tags**:
   ```xml
   <replace path="//div[@id='changeMe']/text()" withText="new text"/>
   ```

6. **Changing an attribute (method 1)**:
   ```xml
   <attr path="//div[@id='1']" attr="id" value="2"/>
   ```

7. **Changing an attribute (method 2)**:
   ```xml
   <replace path="//div[@id='1']/@id" withText="2"/>
   ```

8. **Removing an attribute**:
   ```xml
   <remove path="//div[@id='1']/@id"/>
   ```
   or
   ```xml
   <attr path="//div[@id='1']" attr="id" remove="true"/>
   ```

9. **Copying an attribute's value into a buffer**:
   ```xml
   <copy path="//div[@id='1']/@id" attrValue="true" buffer="bufferName"/>
   ```

10. **Copying an attribute's value into a property**:
    ```xml
    <copy path="//div[@id='1']/@id" property="propertyName"/>
    ```

11. **Copying multiple values into one buffer**. Note the clearing of buffers `a`, `b`, and `c` prior to appending. Buffer `b` contains all the `div` elements for each input file:
    ```xml
    <xmltask clearBuffers="a,b,c">
      <fileset dir=".">
        <includes name="*.xml"/>
      </fileset>
      <copy path="//div" buffer="b" append="true"/>
      ...
    </xmltask>
    ```

12. **Removing all comments**:
    ```xml
    <remove path="//child::comment()"/>
    ```

13. **Inserting the appropriate system identifiers in a transformed `web.xml`**:
    ```xml
    <xmltask source="web.xml" dest="release/web.xml"
        public="-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN"
        system="http://java.sun.com/j2ee/dtds/web-app_2_2.dtd" >
      ...
    </xmltask>
    ```
    or
    ```xml
    <xmltask source="web.xml" dest="release/web.xml"
        preserveType="true" >
      ...
    </xmltask>
    ```
    if you're transforming an existing `web.xml`.

14. **Setting the output character set to Japanese encoding**:
    ```xml
    <xmltask source="web.xml" dest="release/web.xml" encoding="Shift-JIS" >
      ...
    </xmltask>
    ```

15. **Converting all unordered lists in an XHTML document to ordered lists**:
    ```xml
    <rename path="//ul" to="ol"/>
    ```

16. **Creating a new document with a root node `<root>`**:
    ```xml
    <xmltask dest="release/web.xml">
      <insert path="/">
        <![CDATA[
          <root/>
        ]]>
      </insert>
      ...
    </xmltask>
    ```

17. **Counting nodes and recording the result in a property**:
    ```xml
    <xmltask source="multiple.xml">
      <copy path="count(/servlet)" property="count"/>
      ...
    </xmltask>
    ```

18. **Identifying elements with namespaces**. This example copies the `node` element which is tied to a namespace via an `xmlns` directive:
    ```xml
    <xmltask source="input.xml">
      <copy path="//*[local-name()='node']" property="count"/>
      ...
    </xmltask>
    ```

19. **Call the `deploy` task for each servlet entry in a `web.xml`**. For each invocation the `servletDef` buffer contains the complete servlet specification from the deployment file, and the property `id` contains the servlet id (if there is no id attribute then `NO ID` will be substituted):
    ```xml
    <xmltask source="web.xml">
      <call path="web/servlet" target="deploy" buffer="servletDef">
        <param name="id" path="@id" default="NO ID"/>
      </call>
    </xmltask>
    ```

20. **Performs actions for each servlet entry in a `web.xml`** (Ant 1.6 and above only):
    ```xml
    <xmltask source="web.xml">
      <call path="web/servlet">
        <param name="id" path="@id" default="NO ID"/>
        <actions>
          <echo>Found a servlet @{id}</echo>
          <!-- perform deployment actions -->
          ...
        </actions>
      </call>
    </xmltask>
    ```

21. **Uncomment and thus enable a set of users in a `tomcat-users.xml` file**. The users are set up in the first 2 comments:
    ```xml
    <xmltask source="tomcat-users.xml">
      <uncomment path="tomcat-users/comment()[1]"/>
      <uncomment path="tomcat-users/comment()[2]"/>
    </xmltask>
    ```

22. **Cutting a section of XML to a buffer, and displaying the buffer**:
    ```xml
    <xmltask source="input.xml">
      <cut path="web/servlet[@id='1']" buffer="servlet"/>
      <print buffer="servlet" comment="Copied to 'servlet' buffer"/>
      ...
    </xmltask>
    ```

23. **Cutting a section of XML to a persisted buffer** (the file `buffers/servlet`) for later use:
    ```xml
    <xmltask source="input.xml">
      <cut path="web/servlet[@id='1']" buffer="file://build/buffers/servlet"/>
      ...
    </xmltask>
    ```

---

_End of document._
