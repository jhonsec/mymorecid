package com.moreci.enums

import com.moreci.steps.CommandStep
import com.moreci.steps.SonarScannerStep
import com.moreci.steps.TestUnitGradleStep
import com.moreci.steps.TestUnitStep
import com.moreci.steps.backend.GradleBuildStep
import com.moreci.steps.backend.GradlePromoteStep
import com.moreci.steps.backend.GradlePublishStep
import com.moreci.steps.backend.MavenDockerPushStep
import com.moreci.steps.backend.MavenLibArtifactoryBuildStep
import com.moreci.steps.backend.MavenLibArtifactoryPublishStep
import com.moreci.steps.backend.MavenPromoteStep

enum StepTypeEnum {

  COMMAND(CommandStep.class), // DEFAULT
  /** TOKEN **/
//  TOKEN_TOKENO(Token.class),
  /** PREPARE **/
//  PREPARE_NODE(NodeNpmrc.class),
  /** UNITTEST **/
//  UNITTEST_NODE(NodeNpmrc.class),
  /** UNITTEST  MVN **/
  UNITTEST_TEST(TestUnitStep.class),
  /** UNITTEST  GRADLE **/
  UNITTEST_TESTGRADLE(TestUnitGradleStep.class),
  /** SCAN **/
  SCAN_SONAR(SonarScannerStep.class),
  /** BUILD MVN**/
  BUILD_MAVEN_LIB_ARTIFACTORY(MavenLibArtifactoryBuildStep.class),
  /** BUILD GRADLE**/
  BUILD_GRADLE(GradleBuildStep.class),
  /** VERSION **/
//  VERSION_NODE(NodeNpmrc.class),
  /** PUBLISH MVN**/
  PUBLISH_MAVEN_LIB_ARTIFACTORY(MavenLibArtifactoryPublishStep.class),
//  PUBLISH_NODE(NodeNpmrc.class),
  PUBLISH_MAVEN_PROMOTE(MavenPromoteStep.class),
  /** PUBLISH GRADLE**/
  PUBLISH_GRADLE(GradlePublishStep.class),
  PUBLISH_GRADLE_PROMOTE(GradlePromoteStep.class),

  /** PACKAGE **/
  PACKAGE_MAVEN_DOCKER(MavenDockerPushStep.class),
  /** DELIVERY **/
//  DELIVERY_AZURE_GIT(AzureGitPush.class),
//  DELIVERY_AZURE_AKS(AzureAks.class),
  /** ROLLBACK **/
//  ROLLBACKB_ROLLBACK(Rollbackb.class),
//  ROLLBACKF_ROLLBACK(Rollbackf.class)

  private final Class value

  StepTypeEnum(Class value) {
    this.value = value
  }

  Class getValue() {
    return value
  }

  static boolean contains(String s) {
    for (StepTypeEnum choice : values())
      if (choice.name() == s)
        return true;
    return false;
  }
}