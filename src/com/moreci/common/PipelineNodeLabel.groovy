package com.moreci.common

import com.moreci.enums.NodeLabelEnum
import com.moreci.enums.PipelineTypeEnum
import com.moreci.enums.StageEnum

class PipelineNodeLabel {
  static Map<PipelineTypeEnum, NodeLabel> items = [:]

  static void init() {
    if (items) return

    items.put(PipelineTypeEnum.DEFAULT, new NodeLabel(label: NodeLabelEnum.MASTER.value))
//    items.put(PipelineTypeEnum.BACKEND, new NodeLabel(label: NodeLabelEnum.MORECI_BACK.value)
//      .addStageLabel(StageEnum.DELIVERY, NodeLabelEnum.MORECI_DEPLOY.value))
    items.put(PipelineTypeEnum.BACKEND, new NodeLabel(label: NodeLabelEnum.MASTER.value))
    items.put(PipelineTypeEnum.BACKEND_GRADLE, new NodeLabel(label: NodeLabelEnum.MORECI_GRADLE.value)
      .addStageLabel(StageEnum.DELIVERY, NodeLabelEnum.MORECI_DEPLOY.value))
    items.put(PipelineTypeEnum.BACKEND_LIB, new NodeLabel(label: NodeLabelEnum.MORECI_BACK.value))

    items.put(PipelineTypeEnum.FRONTEND, new NodeLabel(label: NodeLabelEnum.MORECI_FRONT.value))
    items.put(PipelineTypeEnum.FRONTEND_LIB, new NodeLabel(label: NodeLabelEnum.MORECI_FRONT))

    items.put(PipelineTypeEnum.ROLLBACK_BACKEND, new NodeLabel(label: NodeLabelEnum.MORECI_BACK.value)
      .addStageLabel(StageEnum.DELIVERY, NodeLabelEnum.MORECI_DEPLOY.value))
    items.put(PipelineTypeEnum.ROLLBACK_FRONTEND, new NodeLabel(label: NodeLabelEnum.MORECI_FRONT.value))
  }

  static NodeLabel getItem(PipelineTypeEnum type) {
    init()
    return items.get(type, null)
  }
}
