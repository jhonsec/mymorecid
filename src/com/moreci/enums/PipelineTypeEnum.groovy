package com.moreci.enums

enum PipelineTypeEnum {
  DEFAULT('default', 'scriptedPipeline'),

  BACKEND('backend', 'backendPipeline'),
  BACKEND_GRADLE('backend-gradle', 'backendGradlePipeline'),
  BACKEND_LIB('backend-lib', 'backendLibPipeline'),

  FRONTEND('frontend', 'frontendPipeline'),
  FRONTEND_LIB('frontend-lib', 'frontendLibPipeline'),

  ROLLBACK_BACKEND('rollback-backend', 'rollbackBackendPipeline'),
  ROLLBACK_FRONTEND('rollback-frontend', 'rollbackFrontendPipeline')

  private final String id
  private final String func

  PipelineTypeEnum(String id, String func) {
    this.id = id
    this.func = func
  }

  String getId() {
    return id
  }

  String getFunc() {
    return func
  }
}
