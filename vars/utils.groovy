#!/usr/bin/env groovy

def downloadSources(String branch, String url, String credential) {
  checkout([
    $class: "GitSCM",
    branches: [[name: "*/${branch}"]],
    userRemoteConfigs: [[
                          credentialsId: credential,
                          url: url
                        ]]
  ])
}