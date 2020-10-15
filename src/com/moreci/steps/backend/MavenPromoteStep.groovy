package com.moreci.steps.backend

class MavenPromoteStep extends PromoteAbstract {
  String source = './'
  String pomPath = 'pom.xml'
  String sourceRepo // Required
  String targetRepo // Required
  // Load on [PipelineType]Flow
  String serverId
  String ver

  @Override
  void run() {
    root.dir(this.source) {
      this.process()
    }
  }

  private void process() {
    /** org.apache.maven.model.Model */
    def pom = root.readMavenPom(file: this.pomPath)
    // Boolean isSnapshot  = pom.getVersion().contains("-SNAPSHOT")
    String version = pom.getVersion()
    String artifactId = pom.getArtifactId()
    String groupPath = pom.getGroupId().replace(".", "/")
    String packaging = pom.getPackaging()
    ver = version
    def modules = pom.getModules()

    if (modules.size() > 0) {
      this.pomModules(modules)
      this.loadVariables(groupPath, artifactId, version, packaging)
    } else {
      this.loadVariables(groupPath, artifactId, version, packaging)
    }
  }

  private void pomModules(ArrayList modules) {
    String groupPath_mod
    for (int i = 0; i < modules.size(); i++) {
      String pomPath_modules = modules[i] + '/pom.xml'
      def pom_mod = root.readMavenPom(file: pomPath_modules)
      String artifactId_mod = pom_mod.getArtifactId()
      String packaging_mod = pom_mod.getPackaging()
      if (pom_mod.getGroupId() == null) {
        def parent = pom_mod.parent.getGroupId()
        groupPath_mod = parent.replace(".", "/")
      } else {
        groupPath_mod = pom_mod.getGroupId().replace(".", "/")
      }
      String version_mod = ver
      this.loadVariables(groupPath_mod, artifactId_mod, version_mod, packaging_mod)
    }
  }
}
