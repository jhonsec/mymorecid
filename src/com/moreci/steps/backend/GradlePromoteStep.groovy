package com.moreci.steps.backend

class GradlePromoteStep extends PromoteAbstract {
  String source = './'
  String sourceRepo // Required
  String targetRepo // Required
  // Load on [PipelineType]Flow
  String serverId
  String ver

  void run() {
    root.dir(this.source) {
      this.process()
    }
  }

  private void process() {
    String version = this.fileParameter('build.gradle', 'version =').replaceAll("'", "")
    String groupPath = this.fileParameter('build.gradle', 'group =').replaceAll("'", "").replace(".", "/")
    String artifactId = this.fileParameter('settings.gradle', 'rootProject.name =').replaceAll("'", "")
    String packaging = 'jar'
    ver = version

    def modules = this.generateModules()
    if (!modules.isEmpty()) {
      this.pomModules(modules)
    }
    this.loadVariables(groupPath, artifactId, ver, packaging)

  }

  private String fileParameter(String file, String parameter) {
    return root.sh(
      script: "cat $file | grep \"$parameter\" | awk '{print \$3}'",
      returnStdout: true
    ).trim()
  }

  private ArrayList generateModules() {
    def modules_string = root.sh(
      script: "cat settings.gradle | grep \"include \" | awk '{print \$2}'",
      returnStdout: true
    ).trim()

    String base_word = ""
    ArrayList modules = []
    for (int i = 0; i < modules_string.size(); i++) {
      if (modules_string[i] == "\n" || i == (modules_string.size() - 1)) {
        modules.add(base_word)
        base_word = ""
      }
      if (modules_string[i] == "'" || modules_string[i] == '"') {
        continue
      }
      base_word = base_word + modules_string[i]
    }
    return modules
  }

  private void pomModules(ArrayList modules) {
    String groupPath_mod
    for (int i = 0; i < modules.size(); i++) {
      String buildGradle_path = modules[i].replace(":", "/").replaceAll("\\s", "")
      groupPath_mod = this.fileParameter(buildGradle_path + '/build.gradle', 'group =').replaceAll("'", "").replace(".", "/")
      String artifactId_mod = buildGradle_path.split('/').last()
      String packaging_mod = 'jar'
      String version_mod = ver

      this.loadVariables(groupPath_mod, artifactId_mod, version_mod, packaging_mod)
    }
  }
}
