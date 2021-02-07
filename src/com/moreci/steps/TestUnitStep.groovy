package com.moreci.steps

class TestUnitStep extends StepAbstract {
  String source = './'
  String serverId
  String releaseRepo
  String snapshotRepo
  String command
  String pomPath = 'pom.xml'

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

      rtMaven.run pom: pomPath, goals: command, buildInfo: buildInfo
    }
  }
}
