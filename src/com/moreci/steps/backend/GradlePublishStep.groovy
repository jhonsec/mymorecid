package com.moreci.steps.backend

import com.moreci.steps.StepAbstract

class GradlePublishStep extends StepAbstract {
  String source = './'
  String serverId
  String buildPath = 'build.gradle'
  String releaseRepo
  String snapshotRepo
  String commandParams

  @Override
  void run() {
    def server = root.Artifactory.server serverId
    def rtGradle = root.myRtGradle /** @see GradleBuildStep */
    def buildInfo = root.myBuildInfo /** @see GradleBuildStep */


    snapshotRepo = (snapshotRepo) ? snapshotRepo : releaseRepo

    root.dir(this.source) {
      root.withCredentials([root.usernamePassword(
        credentialsId: 'jfrog-admin-credentials',
        usernameVariable: 'USER',
        passwordVariable: 'PASSWORD'
      )]) {
        rtGradle.usesPlugin = true
        rtGradle.deployer.mavenCompatible = true
        rtGradle.deployer server: server, repo: snapshotRepo
        rtGradle.deployer.includeEnvVars = true
        rtGradle.run rootDir: source, buildFile: buildPath, tasks: commandParams, buildInfo: buildInfo
        rtGradle.deployer.deployArtifacts buildInfo
        server.publishBuildInfo buildInfo
      }
    }
  }
}
