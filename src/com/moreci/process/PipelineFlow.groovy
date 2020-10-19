package com.moreci.process

import com.moreci.common.Constants
import com.moreci.steps.TestUnitGradleStep
import com.moreci.steps.TestUnitStep
import com.moreci.steps.azure.AzureAksStep
import com.moreci.steps.backend.GradleBuildStep
import com.moreci.steps.backend.GradlePromoteStep
import com.moreci.steps.backend.GradlePublishStep
import com.moreci.steps.backend.MavenDockerPushStep
import com.moreci.steps.backend.MavenLibArtifactoryBuildStep
import com.moreci.steps.backend.MavenLibArtifactoryPublishStep
import com.moreci.steps.backend.MavenPromoteStep
import com.moreci.steps.frontend.TokenOauth2Step

class PipelineFlow extends FlowAbstract {

  @Override
  protected void prepare() {
    /** ************************************ **/
    /** ********** BACKEND: Steps ********** **/
    /** ************************************ **/

    /** TEST **/
    this.addPreloadStep(new TestUnitStep(
      serverId: Constants.ARTIFACTORY_SERVER_ID
    ))
    this.addPreloadStep(new TestUnitGradleStep(
      serverId: Constants.ARTIFACTORY_SERVER_ID,
      typepipeline: parameterBean.getType()
    ))

    /** BUILD **/
    this.addPreloadStep(new MavenLibArtifactoryBuildStep(
      serverId: Constants.ARTIFACTORY_SERVER_ID
    ))
    this.addPreloadStep(new GradleBuildStep(
      serverId: Constants.ARTIFACTORY_SERVER_ID,
      typepipeline: this.parameterBean.getType()
    ))

    /** PUBLISH **/
    this.addPreloadStep(new MavenLibArtifactoryPublishStep(
      serverId: Constants.ARTIFACTORY_SERVER_ID
    ))
    this.addPreloadStep(new GradlePublishStep(
      serverId: Constants.ARTIFACTORY_SERVER_ID
    ))

    /** PROMOTE **/
    this.addPreloadStep(new MavenPromoteStep(
      serverId: Constants.ARTIFACTORY_SERVER_ID
    ))
    this.addPreloadStep(new GradlePromoteStep(
      serverId: Constants.ARTIFACTORY_SERVER_ID
    ))

    /** DOCKER - AKS **/
    this.addPreloadStep(new MavenDockerPushStep(
      serverId: Constants.ARTIFACTORY_SERVER_ID,
      registry: Constants.DOCKER_REGISTRY,
      credential: Constants.DOCKER_REGISTRY_CREDENTIAL,
      projectName: this.parameterBean.getProject()
    ))
    this.addPreloadStep(new AzureAksStep(
      projectName: this.parameterBean.getProject(),
      typepipeline: this.parameterBean.getType()
    ))
//    this.addPreloadStep(new AzureGitPushStep(
//      team: this.general.getTeam(),
//      deploydev: this.general.getDeploydev(),
//      deploycert: this.general.getDeploycert(),
//      deployprod: this.general.getDeployprod()
//    ))
//    this.addPreloadStep(new RollbackbStep(
//      tag: this.general.getTag(),
//      projectName: this.general.getProject(),
//      gitUrl: this.general.getGitUrl(),
//      team: this.general.getTeam(),
//      gitType: this.general.getGitType()
//    ))

    /** ************************************ **/
    /** ********** FRONTEND: Steps ********** **/
    /** ************************************ **/
//    this.addPreloadStep(new NodeNpmrcStep(
//      artifactAuth: TdpConstants.ARTIFACTORY_AUTH,
//      artifactDomain: TdpConstants.ARTIFACTORY_DOMAIN
//    ))
//    this.addPreloadStep(new RollbackfStep(
//      tag: this.general.getTag(),
//      gitUrl: this.general.getGitUrl(),
//      gitType: this.general.getGitType()
//    ))
//    this.addPreloadStep(new TokenOauth2Step(
//      team: this.general.getTeam()
//    ))

    
  }
}
