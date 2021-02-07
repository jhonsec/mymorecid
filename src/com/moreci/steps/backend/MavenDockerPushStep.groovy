package com.moreci.steps.backend

import com.moreci.steps.StepAbstract

class MavenDockerPushStep extends StepAbstract {
    String source = './'
    String pomPath = 'pom.xml'
    String repository // Required
    // Generate from pom.xml
    String artifactName
    String artifactPattern
    // Load on [PipelineType]Flow
    String serverId
    String registry
    String credential
    String projectName

    @Override
    void run() {
        if (!(root.fileExists(this.pomPath))) {
            // for gradle - required publish
            this.pomPath = 'build/poms/pom-default.xml'
        }
        root.dir(this.source) {
            this.process()
        }
    }

    private void process() {
        /** org.apache.maven.model.Model */
        def pom = root.readMavenPom(file: this.pomPath)
        Boolean isSnapshot  = pom.getVersion().contains("-SNAPSHOT")
        String artifactId   = pom.getArtifactId()
        String version      = pom.getVersion()
        String groupPath    = pom.getGroupId().replace(".", "/")
        String packaging    = pom.getPackaging()

        def artifactPath    = "${repository}/${groupPath}/${artifactId}/${version}"
        artifactName        = "${artifactId}-${version}.${packaging}"
        artifactPattern     = "${artifactPath}/${artifactName}"
        root.echo "${artifactId}"
        root.echo "${version}"
        root.echo "${groupPath}"
        root.echo "${packaging}"

        if (isSnapshot) {
            def snapshotVersion = this.getSnapshotVersion(artifactPath)
            artifactPattern    = "${artifactPath}/${artifactId}-${snapshotVersion}.${packaging}"
        }

        // Descarga del artefacto
        this.downloadArtifact(artifactPattern, "cfg/${artifactName}")

        // Upload de la imagen al registry
        this.pushImageToRegistry()
    }

    private String getSnapshotVersion(String artifactPath) {
        def metadataName    = "maven-metadata.xml"
        def metadataTarget  = "cfg/${metadataName}"

        //root.sh "find ${source} --name ${metadataName}"
        root.sh "rm -rf ${metadataTarget}"
        this.downloadArtifact("${artifactPath}/${metadataName}", metadataTarget)

        def metadataText    = root.readFile metadataTarget
        def metadata        = new XmlParser().parseText(metadataText)
        def version         = metadata.versioning.snapshotVersions.snapshotVersion[0].value.text()
        return version
    }

    private void downloadArtifact(String pattern, String target) {
        def server = root.Artifactory.server this.serverId
        def downloadSpec = """
            {
            "files": [
                {
                    "pattern": "${pattern}",
                    "target": "${target}",
                    "flat": "true"
                }
              ]
            }
            """
        server.download(downloadSpec)
    }

    private void pushImageToRegistry(String dockerfilePath = 'cfg') {
        def pom = root.readMavenPom(file: this.pomPath)
        def buildParams
        def version = pom.getVersion()
        def imageName   = "${this.registry}/${this.projectName}:${version}"
        def imageNamelatest   = "${this.registry}/${this.projectName}:latest"
        def latestTag = root.sh (
//                script: 'sudo docker inspect --type=image ${imageName} >/dev/null 2>&1 && echo yes || echo no',   // LINUX
                script: 'docker inspect --type=image ${imageName} >/dev/null 2>&1 && echo yes || echo no',
                returnStdout: true
        ).trim()

        def ssl = root.sh (
                script: 'find '+ this.source+dockerfilePath+' -name *.cer',
                returnStdout: true
        ).trim()
        root.echo "${ssl}"
        if (ssl == null || ssl == ''){
            root.echo "Sin certificado"
            buildParams = "--build-arg nombreArtefacto=${this.artifactName} ${dockerfilePath}"
        }else{
            root.echo "Con certificado"
            buildParams = "--build-arg nombreArtefacto=${this.artifactName} --build-arg environment="+root.ENV+" ${dockerfilePath}"
        }

        root.echo "${latestTag}"
        if (latestTag == 'yes' || latestTag == 'no') {
            root.echo "Ya existe una imagen con el tag == LATEST... Se procedera a Actualizar esta imagen con la version == ${version} como LATEST"
        }else{
            root.echo "Creando primera version de imagen LATEST"
        }
        def dockerImage = root.docker.build(imageName, buildParams)
//        TODO fix windows error: jenkins-pipeline-issue-with-docker-working-directory-is-invalid
//        dockerImage.inside {
//            root.println "Tests passed"
//        }
        root.docker.withRegistry("https://${this.registry}", this.credential) {
            dockerImage.push("${version}")
            dockerImage.push("latest")
        }
        root.sh "docker rmi ${imageName} ${imageNamelatest}"
    }
}
