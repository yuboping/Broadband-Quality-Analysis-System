#!/bin/bash
LANG=zh_CN.UTF-8
export LANG

JAVA_HOME=/home/aiobs6/tool/jdk1.8.0_191
WORK_HOME=/home/aiobs6/ces-web/apache-tomcat-9.0.13

cd ${WORK_HOME}/bin
sh shutdown.sh
sh startup.sh
