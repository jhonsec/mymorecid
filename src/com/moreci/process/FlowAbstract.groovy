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
    println("##########################")
    def propsEnv = this.props[this.parameterBean.getEnvironment()]
    String[] parallels = (propsEnv.parallel) ? propsEnv.parallel.toString().split(",") : []
    println(propsEnv)
    println(parallels)
    println("##########################")
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

    println("##########################")
    println(stage)
    println(stageItems)
    println("##########################")
    // Si tiene varios steps con seteo de default
    if (stage instanceof LinkedHashMap && stage.containsKey("default")) {
      println("+++ in:: Si tiene varios steps con seteo de default +++ ")
      stageDefault = stage.get("default")
      stageItems = stage.get("steps")
    }

    // Si tiene un solo step
    if (stageItems instanceof LinkedHashMap) {
      println("+++ in:: Si tiene un solo step +++ ")
      items.add(stageItems as LinkedHashMap)
    } // Si tiene varios steps
    else {
      println("+++ in:: Si tiene varios steps +++ ")
      items = stageItems as ArrayList
    }
    println("##########################")
    println(items)
    println(stageDefault)
    println(stageItems)
    println("##########################")

    Closure asObject = { Map map ->
      println("##########################")
      println(map)
      def objectInstance
      def stepAttr = stageDefault + map
      def stepTypeEnumKey = this.getStepTypeEnum(stageEnum, stepAttr.type as String)

      println(stepAttr)
      println(stepTypeEnumKey)
      println("##########################")
      if (this.preloadStep.containsKey(stepTypeEnumKey.value)) {
        objectInstance = this.preloadStep.get(stepTypeEnumKey.value)
      } else {
        objectInstance = stepTypeEnumKey.value.newInstance()
      }

      // First attributes
      objectInstance.root = this.root
      stepAttr.each { key, value ->
        println("+++ stepAttr :: ${key} - ${value} +++")
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
    println("##########################")
    println(this.props)
    println("##########################")
    def propsEnv = this.props[this.parameterBean.getEnvironment()]
    println(propsEnv)
    println("##########################")
    this.stages = propsEnv['stages'] as LinkedHashMap
    println(this.stages)
    println("##########################")
    this.stageFlow = propsEnv['flow'].toString().split(",")
    println(stageFlow)
    println("##########################")
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
