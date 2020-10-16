#!/usr/bin/env groovy
import com.moreci.bean.ParameterBean
import com.moreci.common.NodeLabel
import com.moreci.common.PipelineNodeLabel
import com.moreci.enums.PipelineTypeEnum

def call(body) {
  // Evaluate body closure, and fill the configuration in the object
  def pipelineParams = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = pipelineParams
  body()

  def parameters = new ParameterBean(
    team: pipelineParams.team,
    type: pipelineParams.type,
    project: pipelineParams.project,
    component: pipelineParams.component ?: null,
    gitType: pipelineParams.gitType ?: null,
    gitUrl: pipelineParams.gitUrl ?: null,
    branch: pipelineParams.branch,
    credentials: pipelineParams.credentials,
    tag: pipelineParams.tag ?: null,
    deployDev: pipelineParams.deployDev ?: null,
    deployCert: pipelineParams.deployCert ?: null,
    deployProd: pipelineParams.deployProd ?: null
  )

  PipelineTypeEnum pipelineType = parameters.getType() as PipelineTypeEnum
  NodeLabel nodeLabel = PipelineNodeLabel.getItem(pipelineType)
  echo "##########################"
  echo "${parameters}"
  echo "${nodeLabel}"
  echo "${pipelineType}"
  echo "##########################"
  "${pipelineType.func}"(parameters, nodeLabel)
}