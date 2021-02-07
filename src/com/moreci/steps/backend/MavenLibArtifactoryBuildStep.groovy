package com.moreci.steps.backend

import com.moreci.steps.StepAbstract

class MavenLibArtifactoryBuildStep extends StepAbstract {
  String source = './'
  String serverId
  String pomPath = 'pom.xml'

  String releaseRepo
  String snapshotRepo
  String commandParams

  @Override
  void run() {
    def server = root.Artifactory.server serverId
    def rtMaven = root.Artifactory.newMavenBuild()
    def buildInfo
    snapshotRepo = (snapshotRepo)? snapshotRepo:releaseRepo

    root.dir(this.source) {
      rtMaven.tool = 'apache-maven-3.6.3' // Tool name from Jenkins configuration
      rtMaven.resolver releaseRepo: releaseRepo, snapshotRepo: snapshotRepo, server: server
      rtMaven.deployer.deployArtifacts = false
      buildInfo = root.Artifactory.newBuildInfo()

      rtMaven.run pom: pomPath, goals: commandParams, buildInfo: buildInfo
      root.echo root.currentBuild.result
      if (root.currentBuild.result == null || root.currentBuild.result == "SUCCESS") {
        root.hygieiaBuildPublishStep buildStatus: "SUCCESS"
      } else {
        root.hygieiaBuildPublishStep buildStatus: root.currentBuild.result
      }

      /**
       * Variables use in other steps
       */
      root.myRtMaven = rtMaven
      root.myBuildInfo = buildInfo
    }
  }
}
