//import com.moreci.bean.ParameterBean
//import com.moreci.common.NodeLabel
//import com.moreci.enums.StageEnum
//import com.moreci.process.FlowAbstract
//import com.moreci.process.PipelineFlow
//
//def call(ParameterBean parameterBean, NodeLabel nodeLabel) {
//  String defaultBranch = parameterBean.getBranch()
//  FlowAbstract pipelineFlow
//
//  node(nodeLabel.getValue()) {
//    /** PARAMETERS */
//    properties([
//      parameters([
//        choice(name: 'ENV', choices: ['dev', 'cert', 'prod'], description: 'Environment'),
//        string(name: 'BRANCH', defaultValue: defaultBranch, description: 'Project Branch')
//      ])
//    ])
//
//    /** INITIAL STAGE **/
//    stage("Checkout") {
//      parameterBean.setWorkspace(env.WORKSPACE)
//      parameterBean.setBranch(params.BRANCH)
//      parameterBean.setEnvironment(params.ENV)
//      pipelineFlow = new PipelineFlow(root: this, parameterBean: parameterBean)
//      pipelineFlow.downloadSources().init()
//    }
//
//    /** STAGES **/
//    def myStages = pipelineFlow.getStageFlow()
//    myStages.each {stageKey ->
//      StageEnum stageEnum = StageEnum.getStageEnum(stageKey)
//
//      if (pipelineFlow.hasStage(stageEnum)) {
//        if (nodeLabel.getValue(stageEnum)) {
//          node(nodeLabel.getValue(stageEnum)) {
//            stage(stageEnum.getName()) {
//              pipelineFlow.runStage(stageEnum)
//            }
//          }
//        } else {
//          stage(stageEnum.getName()) {
//            pipelineFlow.runStage(stageEnum)
//          }
//        }
//      }
//    }
//
//  }
//
//}
//