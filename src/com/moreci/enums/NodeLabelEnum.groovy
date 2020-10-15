package com.moreci.enums

enum NodeLabelEnum {
  MASTER('master'),
  MORECI_BACK('moreciBack'),
  MORECI_FRONT('moreciFront'),
  MORECI_DEPLOY('moreciDeploy'),
  MORECI_GRADLE('moreciGradle')

  private final String value

  NodeLabelEnum(String value) {
    this.value = value
  }

  String getValue() {
    return value
  }
}