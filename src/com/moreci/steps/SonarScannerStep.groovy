package com.moreci.steps

class SonarScannerStep extends StepAbstract {
  String source = './'
  String command

  @Override
  void run() {
    root.dir(this.source) {
      root.withSonarQubeEnv ('SonarQube_MoreCI') {
        root.sh "${this.command}"
      }
      root.timeout (time: 10, unit: "MINUTES") {
        root.waitForQualityGate abortPipeline: true
      }
    }
    //root.hygieiaSonarPublishStep ceQueryIntervalInSeconds: '10', ceQueryMaxAttempts: '30'
//    root.hygieiaCodeQualityPublishStep jacocoFilePattern: './target/jacoco.exec'
//    root.hygieiaCodeQualityPublishStep jacocoFilePattern: './target/site/jacoco/jacoco.xml' // LINUX
    root.hygieiaCodeQualityPublishStep jacocoFilePattern: 'target/site/jacoco/jacoco.xml'
  }
}
