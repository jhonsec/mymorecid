package com.moreci.steps.backend

import com.moreci.steps.StepAbstract

class GradleBuildStep extends StepAbstract {
  String source = './'
  String serverId
  String buildPath = 'build.gradle'
  // Set on devops.properties
  String releaseRepo
  String snapshotRepo
  String commandParams
  String typepipeline

  @Override
  void run() {
    def server = root.Artifactory.server serverId
    def rtGradle = root.Artifactory.newGradleBuild()
    def buildInfo
    snapshotRepo = (snapshotRepo)? snapshotRepo:releaseRepo
    root.dir(this.source) {
      root.echo this.typepipeline
      rtGradle.usesPlugin = true
      if (this.typepipeline == 'BACKEND_GRADLE_LEGACY') {
        rtGradle.tool = 'apache-gradle-3.5.1' // Tool name from Jenkins configuration
      } else {
        rtGradle.tool = 'apache-gradle-6.0.1' // Tool name from Jenkins configuration
      }
      rtGradle.resolver server: server, repo: snapshotRepo
      rtGradle.deployer.deployArtifacts = false
      buildInfo = root.Artifactory.newBuildInfo()

      rtGradle.run rootDir: source, buildFile: buildPath, taskas: commandParams, buildInfo: buildInfo
      root.echo root.currentBuild.result
      root.echo commandParams

      //root.hygieiaBuildPublishStep buildStatus: root.currentBuild.result

      /**
       * Variables use in other steps
       */
      root.myRtGradle = rtGradle
      root.myBuildInfo = buildInfo
    }
  }
}
