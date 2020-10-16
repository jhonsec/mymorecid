package com.moreci.bean

class ParameterBean {
  String team
  String environment
  String type
  String project
  String component

  String gitType
  String gitUrl
  String branch

  String credentials
  String workspace
  String tag
  String deployDev
  String deployCert
  String deployProd

  String getCustomWorkspace(String workspace = null) {
    def customWorkspace = workspace ?: this.workspace
    def suffixPath = this.getWorkspaceSuffixPath()
    customWorkspace += (suffixPath) ? "/${suffixPath}" : ""
    System.out.println("*** ${customWorkspace.toString()}")
    System.out.println("*** ${suffixPath.toString()}")
    return customWorkspace
  }

  String getWorkspaceSuffixPath() {
    if (!branch)
      throw new Exception("ParameterBean - empty variable [branch]")
    return (component) ? "${branch}/${component}" : branch
  }

  @Override
  public String toString() {
    return "ParameterBean{" +
            "team='" + team + '\'' +
            ", environment='" + environment + '\'' +
            ", type='" + type + '\'' +
            ", project='" + project + '\'' +
            ", component='" + component + '\'' +
            ", gitType='" + gitType + '\'' +
            ", gitUrl='" + gitUrl + '\'' +
            ", branch='" + branch + '\'' +
            ", credentials='" + credentials + '\'' +
            ", workspace='" + workspace + '\'' +
            ", tag='" + tag + '\'' +
            ", deployDev='" + deployDev + '\'' +
            ", deployCert='" + deployCert + '\'' +
            ", deployProd='" + deployProd + '\'' +
            '}';
  }
}
