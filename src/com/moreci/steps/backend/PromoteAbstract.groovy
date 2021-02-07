package com.moreci.steps.backend

import com.moreci.steps.StepAbstract

abstract class PromoteAbstract extends StepAbstract {
  String source = './'
  String sourceRepo // Required
  String targetRepo // Required
  // Load on [PipelineType]Flow
  String serverId
  String ver

  protected void loadVariables(String groupPath, String artifactId, String version, String packaging) {
    def artifactPath = "${groupPath}/${artifactId}/${version}"
    if (packaging == 'jar') {
      def artifactName = "${artifactId}-${version}.pom"
      def sourcePatternpom = "${sourceRepo}/${artifactPath}/${artifactName}"
      def tagetPatternpom = "${targetRepo}/${artifactPath}/${artifactName}"
      def tempPatternpom = "cfg/tmp/${artifactName}"

      // Descarga del artefacto de certificación
      this.downloadArtifact(sourcePatternpom, tempPatternpom)

      // Upload del artefacto a producción
      this.uploadArtifact(tempPatternpom, tagetPatternpom)
    }

    def artifactName = "${artifactId}-${version}.${packaging}"
    def sourcePattern = "${sourceRepo}/${artifactPath}/${artifactName}"
    def tagetPattern = "${targetRepo}/${artifactPath}/${artifactName}"
    def tempPattern = "cfg/tmp/${artifactName}"

    // Descarga del artefacto de certificación
    this.downloadArtifact(sourcePattern, tempPattern)

    // Upload del artefacto a producción
    this.uploadArtifact(tempPattern, tagetPattern)
  }

  protected void downloadArtifact(String pattern, String target) {
    def server = root.Artifactory.server this.serverId
    def downloadSpec = """{
            "files": [{
                    "pattern": "${pattern}",
                    "target": "${target}",
                    "flat": "true"
                }]
            }"""
    server.download(downloadSpec)
  }

  protected void uploadArtifact(String pattern, String target) {
    def server = root.Artifactory.server this.serverId
    def uploadSpec = """{
            "files": [{
                    "pattern": "${pattern}",
                    "target": "${target}"
                }]
            }"""
    server.upload(uploadSpec)
  }
}
