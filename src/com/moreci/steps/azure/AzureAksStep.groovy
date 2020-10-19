package com.moreci.steps.azure

import com.moreci.steps.StepAbstract

class AzureAksStep extends StepAbstract {
    String source = './'
    String pomPath = 'pom.xml'
    String artifactName
    String projectName
    String typepipeline

    @Override
    void run() {
        root.sh "pwd"
        root.sh "tree ."
        if (!(root.fileExists(this.pomPath))) {
            // for gradle - required publish
            this.pomPath = 'build/poms/pom-default.xml'
        }
//        def credential = "azure-"+root.TEAM+'-'+root.ENV
        def pom = root.readMavenPom(file: this.pomPath)
        String version      = pom.getVersion()
        String artifactId   = pom.getArtifactId()
        String groupPath    = pom.getGroupId().replace(".", "/")
        String packaging    = pom.getPackaging()

        artifactName = "${artifactId}-${version}.${packaging}"
//        root.withCredentials([root.azureServicePrincipal(credential)]) {
//            root.sh 'az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET -t $AZURE_TENANT_ID'
//            root.sh 'az account set --subscription ${AZURE_SUBSCRIPTION_ID}'
//            root.sh 'az aks get-credentials --name aks-'+root.TEAM+'-'+root.ENV+' --resource-group RG-'+root.TEAM+'-'+root.ENV+' --admin --overwrite-existing'
//        }

        root.dir(this.source) {
            root.sh "sed -i 's/ambiente/"+root.ENV+"/g' ./cfg/*.yml"
            root.sh "sed -i 's/latest/${version}/g' ./cfg/*.yml"
            //root.sh "sed -i 's/versionApp/${version}/g' ./cfg/*.yml"
            root.sh "cat ./cfg/*.yml"
            root.sh "kubectl apply -f ./cfg/*.yml"
        }
        if (root.ENV == 'prod'){
            root.hygieiaDeployPublishStep applicationName: projectName, artifactDirectory: "./target", artifactGroup: groupPath,  artifactName: artifactName, artifactVersion: version, buildStatus: 'SUCCESS', environmentName: root.ENV
        }else if(this.typepipeline.equals('BACKEND_GRADLE')){
            root.hygieiaDeployPublishStep applicationName: projectName, artifactDirectory: "./build", artifactGroup: groupPath,  artifactName: artifactName, artifactVersion: version, buildStatus: 'SUCCESS', environmentName: root.ENV
        }
        else{
            if(root.currentBuild.result == null || root.currentBuild.result == "SUCCESS"){
                root.hygieiaDeployPublishStep applicationName: projectName, artifactDirectory: "./target", artifactGroup: groupPath,  artifactName: artifactName, artifactVersion: version, buildStatus: 'SUCCESS', environmentName: root.ENV
            }else{
                root.hygieiaDeployPublishStep applicationName: projectName, artifactDirectory: "./target", artifactGroup: groupPath,  artifactName: artifactName, artifactVersion: version, buildStatus: root.currentBuild.result, environmentName: root.ENV
            }
        }
    }
}
