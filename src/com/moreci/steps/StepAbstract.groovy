package com.moreci.steps

abstract class StepAbstract {
  def root
  String type = 'command'
  String source = './'

  abstract void run()

}
