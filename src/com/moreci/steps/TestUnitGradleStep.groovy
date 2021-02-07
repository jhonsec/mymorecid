package com.moreci.steps

class TestUnitGradleStep extends StepAbstract {

  String source = './'
  String serverId
  String releaseRepo
  String snapshotRepo
  String command
  String buildPath = 'build.gradle'
  String typepipeline

  @Override
  void run() {
    def server = root.Artifactory.server serverId
    def rtGradle = root.Artifactory.newGradleBuild()
    def buildInfo
    snapshotRepo = (snapshotRepo)? snapshotRepo:releaseRepo
    root.dir(this.source) {
      root.sh "gradle -v"
      root.echo this.typepipeline
      rtGradle.usesPlugin = true
      if (this.typepipeline == "BACKEND_GRADLE_LEGACY") {
        rtGradle.tool = 'apache-gradle-3.5.1' // Tool name from Jenkins configuration
      } else {
        rtGradle.tool = 'apache-gradle-6.0.1'  // Tool name from Jenkins configuration
      }
      rtGradle.resolver server: server, repo: snapshotRepo
      rtGradle.deployer.deployArtifacts = false
      buildInfo = root.Artifactory.newBuildInfo()

      rtGradle.run rootDir: source, buildFile: buildPath, tasks: command, buildInfo: buildInfo
    }
  }
}
