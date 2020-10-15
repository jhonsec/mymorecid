package com.moreci.enums

enum StageEnum {
  CHECKOUT('checkout', 'Checkout'),
  TOKEN('token', 'Token'),
  PREPARE('prepare', 'Prepare'),
  UNITTEST('unittest', 'Unit Test'),
  SCAN('scan', 'Scan'),
  BUILD('build', 'Build'),
  VERSION('version', 'Version'),
  PUBLISH('publish', 'Publish'),
  PACKAGE('package', 'Package'),
  DELIVERY('delivery', 'Delivery'),
  ROLLBACK_BACKEND('rollback-backend', 'Rollback'),
  ROLLBACK_FRONTEND('rollback-frontend', 'Rollback')

  final String id
  final String name
  static final Map map

  static {
    map = [:] as TreeMap
    values().each { stage ->
      map.put(stage.id, stage)
    }
  }

  private StageEnum(String id, String name) {
    this.id = id
    this.name = name
  }

  static getStageEnum(id) {
    map.get(id)
  }
}