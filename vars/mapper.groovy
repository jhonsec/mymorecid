#!/usr/bin/env groovy
@Grab('com.fasterxml.jackson.dataformat:jackson-dataformat-properties:2.8.8')
@Grab('com.fasterxml.jackson.core:jackson-databind:2.9.8')
@GrabExclude('org.codehaus.groovy:groovy-all')

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper

def propsToObject(def filePath) {
  String content = readFile(filePath)
  JavaPropsMapper mapper = new JavaPropsMapper();
  LinkedHashMap node = mapper.readValue(content, LinkedHashMap.class);
  return node
}
