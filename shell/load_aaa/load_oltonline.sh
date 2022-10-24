#!/bin/bash


##根据时间和文件命名规则拼写ctl文件
##文件对数据进行sqlload加载到数据库中userbill

##source_file_path：原始文件路径
##source_file_index：原始文件名称前缀
##table_name_index：对应数据库中表前缀
##truncate_flag：加载到数据数据之前是否truncate表

##文件对应时间  yyyymmddhhMM
##根据时间和规整系数确定分钟
rundate=$1
if [ ! -n "$rundate" ]; then 
 rundate=`date  +%Y%m%d%H`;
 coefficient=10
 min_time=`date "+%M"`
 min_time=`expr $min_time / $coefficient`
 min_time=`expr $min_time \* $coefficient`
 ##补全个位数时间的0
 if [ 1 -eq ${#min_time} ]; then 
 min_time=0$min_time
 fi
 rundate=$rundate$min_time
fi
##当前绝对路径
localpath=$(dirname $(readlink -f "$0"))
##加载数据库用户名和密码
source $localpath/constant_info.sh

##文件名
source_file_index='oltonline'
source_filename=$source_file_index'_'$rundate'.txt'
source_file_path='/lcims/kdzlfx/data/aaa/'
##source_file_path='/data/aaa/oltonline/'

##恢复rundate:yyyymmddhh
##min_time=${rundate:10:2}
##rundate=${rundate:0:10}

if [ ! -f "$source_file_path$source_filename" ]; then
    echo "$source_file_path$source_filename not exist."
    exit;
fi;

dstr=${rundate}"|"
sed "s/^/$dstr&/g" $source_file_path$source_filename > $source_file_path$source_filename.load
##恢复rundate:yyyymmddhh
min_time=${rundate:10:2}
rundate=${rundate:0:10}

##表前缀
table_name_index='OLT_ONLINE'
month=`echo $rundate|cut -c5-6`
table_name=$table_name_index'_'$month
##文件对应表中的字段
table_field="STA_DATE date 'yyyymmddhh24mi',OLTIP,ONLINENUM"

##加载数据前是否truncate表
truncate_flag='false'

bash fun_sqlldr.sh $datasource $source_file_path $source_filename.load $table_name "$table_field" $truncate_flag

rm $source_file_path$source_filename.load
gzip $source_file_path$source_filename
mv  $source_file_path$source_filename* /data/aaa/oltonline/
###
####调用kdzlfx—web模块的接口生成OLT告警数据
current_date=`date "+%Y-%m-%d"`
begin_date=`date "+%Y-%m-%d" -d "-7 days"`
hours_time=`date "+%H"`:$min_time:00
curl 'http://127.0.0.1:8980/data/forecastOltOnline.do?format=yyyy-MM-dd%20HH24:mi:ss&time='$current_date'%20'$hours_time'&beginTime='$begin_date'%20'$hours_time'&endTime='$current_date'%20'$hours_time'&oltip=&isUseDB=false'
