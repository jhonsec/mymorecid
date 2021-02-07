package com.moreci.steps.backend

import com.moreci.steps.StepAbstract

class MavenLibArtifactoryPublishStep extends StepAbstract {
  String source = './'
  String serverId
  String releaseRepo
  String snapshotRepo

  @Override
  void run() {
    def server = root.Artifactory.server serverId
    def rtMaven = root.myRtMaven /** @see MavenLibArtifactoryBuildStep */
    def buildInfo = root.myBuildInfo /** @see MavenLibArtifactoryBuildStep */

    snapshotRepo = (snapshotRepo)? snapshotRepo:releaseRepo

    root.dir(this.source) {
      rtMaven.deployer releaseRepo: releaseRepo, snapshotRepo: snapshotRepo, server: server
      rtMaven.deployer.deployArtifacts buildInfo
      server.publishBuildInfo buildInfo
    }

  }
}
