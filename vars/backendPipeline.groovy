import com.moreci.bean.ParameterBean
import com.moreci.common.NodeLabel
import com.moreci.enums.StageEnum
import com.moreci.process.FlowAbstract
import com.moreci.process.PipelineFlow

def call(ParameterBean parameterBean, NodeLabel nodeLabel) {
  String defaultBranch = parameterBean.getBranch();
  String team = parameterBean.getTeam();
  FlowAbstract pipelineFlow

  pipeline {
    agent {
      node {
        label nodeLabel.getValue()
      }
    }
    parameters {
      choice(name: 'ENV', choices: ['dev', 'cert', 'prod'], description: 'Environment')
      string(name: 'BRANCH', defaultValue: defaultBranch, description: 'Project Branch')
      string(name: 'TEAM', defaultValue: team, description: 'Team')
    }
    environment {
      ENV = "${params.ENV}"
      NAMESPACE = "${params.TEAM}"
    }
    stages {
      stage('Checkout') {
        steps {
          script {
            parameterBean.setWorkspace(env.WORKSPACE)
            if (params.ENV == 'dev') {
              parameterBean.setBranch('develop')
            } else if (params.ENV == 'cert') {
              parameterBean.setBranch('release/certification')
            } else {
              parameterBean.setBranch('master')
            }
            parameterBean.setEnvironment(params.ENV)
            parameterBean.setTeam(params.TEAM)
            echo "##########################"
            echo "${parameterBean.toString()}"
            echo "##########################"
            pipelineFlow = new PipelineFlow(root: this, parameterBean: parameterBean)
            pipelineFlow.downloadSources().init()
          }
        }
      }
      stage('UnitTest') {
        when { expression { return pipelineFlow.hasStage(StageEnum.UNITTEST) } }
        steps { script { pipelineFlow.runStage(StageEnum.UNITTEST) } }
      }
      stage('Scan') {
        when { expression { return pipelineFlow.hasStage(StageEnum.SCAN) } }
        steps { script { pipelineFlow.runStage(StageEnum.SCAN) } }
      }
      stage('Build') {
        when { expression { return pipelineFlow.hasStage(StageEnum.BUILD) } }
        steps { script { pipelineFlow.runStage(StageEnum.BUILD) } }
      }
      stage('Publish') {
        when { expression { return pipelineFlow.hasStage(StageEnum.PUBLISH) } }
        steps { script { pipelineFlow.runStage(StageEnum.PUBLISH) } }
      }
      stage('Package') {
        when { expression { return pipelineFlow.hasStage(StageEnum.PACKAGE) } }
        steps { script { pipelineFlow.runStage(StageEnum.PACKAGE) } }
      }
      stage('Delivery') {
        // agent { label nodeLabel.getValue(StageEnum.DELIVERY) }
        when { expression { return pipelineFlow.hasStage(StageEnum.DELIVERY) } }
        steps { script { pipelineFlow.runStage(StageEnum.DELIVERY) } }
      }
    }
    post {
      always {
        cleanWs(
          deleteDirs: true,
          patterns: [[
                       pattern: "${parameterBean.getWorkspaceSuffixPath()}/**",
                       type   : 'INCLUDE'
                     ]])
      }
    }
  }
}