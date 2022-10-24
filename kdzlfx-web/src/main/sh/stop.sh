#!/bin/bash
LANG=zh_CN.UTF-8
export LANG

RunFlag='ces-web';
for pid in `ps -ef | grep "${RunFlag}" | grep -v "grep" | awk ' { print $2 } '`
do
kill -9 $pid;
echo $pid;
done