package com.moreci.process

import com.moreci.bean.ParameterBean
import com.moreci.common.Constants
import com.moreci.enums.StageEnum
import com.moreci.enums.StepTypeEnum
import com.moreci.steps.StepAbstract

abstract class FlowAbstract {
  protected root
  protected Map<Class, StepAbstract> preloadStep = [:]
  protected LinkedHashMap props
  protected LinkedHashMap stages
  public ParameterBean parameterBean
  String[] stageFlow

  protected abstract void prepare()

  void init() {
    if (!this.props) {
      this.loadProperties()
      this.prepare()
    }
  }

  void runStage(StageEnum stage, String workspace = null) {
    this.init()

    workspace = workspace ?: root.env.WORKSPACE

    List<StepAbstract> steps = this.loadSteps(stage)
    //sleep(70000)
    // Exec steps, verify is parallel steps.
    def builders = [:]
    root.echo "##########################"
    def propsEnv = this.props[this.parameterBean.getEnvironment()]
    String[] parallels = (propsEnv.parallel) ? propsEnv.parallel.toString().split(",") : []
    root.echo "${propsEnv.toString()}"
    root.echo "${parallels.toString()}"
    root.echo "##########################"
    root.dir(parameterBean.getCustomWorkspace(workspace)) {
      steps.eachWithIndex { item, i ->
        if (parallels.contains(stage.id)) {
          builders["step${(i + 1)}"] = {
            item.run()
          }
        } else {
          item.run()
        }
      }
      if (builders) {
        root.parallel builders
      }
    }
  }

  protected List<StepAbstract> loadSteps(StageEnum stageEnum) {
    ArrayList<LinkedHashMap> items = []
    def stage = this.stages[stageEnum.id]
    def stageItems = stage
    def stageDefault = [:]

    root.echo "##########################"
    root.echo "${stage.toString()}"
    root.echo "${stageItems.toString()}"
    root.echo "##########################"
    // Si tiene varios steps con seteo de default
    if (stage instanceof LinkedHashMap && stage.containsKey("default")) {
      root.echo "+++ in:: Si tiene varios steps con seteo de default +++ "
      stageDefault = stage.get("default")
      stageItems = stage.get("steps")
    }

    // Si tiene un solo step
    if (stageItems instanceof LinkedHashMap) {
      root.echo "+++ in:: Si tiene un solo step +++ "
      items.add(stageItems as LinkedHashMap)
    } // Si tiene varios steps
    else {
      root.echo "+++ in:: Si tiene varios steps +++ "
      items = stageItems as ArrayList
    }
    root.echo "##########################"
    root.echo "${items.toString()}"
    root.echo "${stageDefault.toString()}"
    root.echo "${stageItems.toString()}"
    root.echo "##########################"

    Closure asObject = { Map map ->
      root.echo "##########################"
      root.echo "${map.toMapString()}"
      def objectInstance
      def stepAttr = stageDefault + map
      def stepTypeEnumKey = this.getStepTypeEnum(stageEnum, stepAttr.type as String)

      root.echo "${stepAttr.toMapString()}"
      root.echo "${stepTypeEnumKey.toString()}"
      root.echo "##########################"
      if (this.preloadStep.containsKey(stepTypeEnumKey.value)) {
        objectInstance = this.preloadStep.get(stepTypeEnumKey.value)
      } else {
        objectInstance = stepTypeEnumKey.value.newInstance()
      }

      // First attributes
      objectInstance.root = this.root
      stepAttr.each { key, value ->
        root.echo "+++ stepAttr :: ${key.toString()} - ${value.toString()} +++"
        if (objectInstance.hasProperty(key as String)) {
          objectInstance[key as String] = value
        }
      }
      return objectInstance
    }
    return items.collect(asObject) as List<StepAbstract>
  }

  protected StepTypeEnum getStepTypeEnum(StageEnum stage, String type = '') {
    StepTypeEnum result = StepTypeEnum.COMMAND
    String stepTypeEnumKey = (stage.toString() + "_" + type.toString()).toUpperCase()

    if (type && !StepTypeEnum.contains(stepTypeEnumKey)) {
      throw new Exception("Error: Dont exist type [${type.toString()}] on stage [${stage.id}], see [devops.properties]")
    }

    if (StepTypeEnum.contains(stepTypeEnumKey)) {
      result = StepTypeEnum[stepTypeEnumKey] as StepTypeEnum
    }
    return result
  }

  Boolean hasStage(StageEnum stage) {
    this.init()
    return this.stageFlow.contains(stage.id)
  }

  protected addPreloadStep(StepAbstract stepInstance) {
    this.preloadStep.put(stepInstance.class, stepInstance)
  }

  protected void loadProperties() {
    this.props = root.mapper.propsToObject(
      parameterBean.getCustomWorkspace() + "/" + Constants.PROJECT_CONFIG_PATH)
    root.echo "##########################"
    root.echo "${this.props.toString()}"
    root.echo "##########################"
    def propsEnv = this.props[this.parameterBean.getEnvironment()]
    root.echo "${propsEnv.toString()}"
    root.echo "##########################"
    this.stages = propsEnv['stages'] as LinkedHashMap
    root.echo "${this.stages.toString()}"
    root.echo "##########################"
    this.stageFlow = propsEnv['flow'].toString().split(",")
    root.echo "${stageFlow.toString()}"
    root.echo "##########################"
  }

  FlowAbstract downloadSources() {
    root.dir(parameterBean.getCustomWorkspace()) {
      root.utils.downloadSources(
        this.parameterBean.getBranch(),
        this.parameterBean.getGitUrl(),
        this.parameterBean.getCredentials()
      )
    }
    return this
  }

}
