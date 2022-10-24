#!/bin/bash

##根据时间和文件命名规则拼写ctl文件
##文件对数据进行sqlload加载到数据库中userbill

##source_file_path：原始文件路径
##source_file_index：原始文件名称前缀
##table_name_index：对应数据库中表前缀
##truncate_flag：加载到数据数据之前是否truncate表

##文件对应时间  yyyymmdd
rundate=$1
if [ ! -n "$rundate" ]; then 
    rundate=`date +%Y%m%d`
fi

tmp_date=`date -d "$rundate" +%Y%m%d`;
if [ "$rundate" != "$tmp_date" ]; then
    echo "{$rundate} format is error.date format is yyyymmdd."
    exit;
fi;

##当前绝对路径
localpath=$(dirname $(readlink -f "$0"))
##加载数据库用户名和密码
source $localpath/constant_info.sh

##文件名
source_file_index='userbill'
source_file_date=`date -d "$rundate" +%Y%m01`;
source_filename=$source_file_index'_'$source_file_date'.txt'

if [ ! -f "$source_file_path/$source_filename" ]; then
    echo "$source_file_path/$source_filename not exist."
    exit;
fi;

##文件对应表前缀
table_name_index='USER_BILL_'
##文件对应表中的字段
table_field="USERNAME,AREANO,TIMELEN,TIMES,TOTALOCTETS"

##数据对应的表
tab_date=`date -d "$rundate -1 month" +%m`;
table_name=$table_name_index''$tab_date

##加载数据前是否truncate表
truncate_flag='true'

sh fun_sqlldr.sh $datasource $source_file_path $source_filename $table_name $table_field $truncate_flag