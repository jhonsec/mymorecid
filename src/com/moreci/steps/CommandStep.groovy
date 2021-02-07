package com.moreci.steps

class CommandStep extends StepAbstract {
  String source = './'
  String command
  String commandParams

  @Override
  void run() {
    this.command += (this.commandParams)? " " + this.commandParams : ""
    root.dir(this.source) {
      root.sh "${this.command}"
    }
  }
}
