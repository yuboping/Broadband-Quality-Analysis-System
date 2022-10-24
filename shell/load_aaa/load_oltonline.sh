#!/bin/bash


##����ʱ����ļ���������ƴдctl�ļ�
##�ļ������ݽ���sqlload���ص����ݿ���userbill

##source_file_path��ԭʼ�ļ�·��
##source_file_index��ԭʼ�ļ�����ǰ׺
##table_name_index����Ӧ���ݿ��б�ǰ׺
##truncate_flag�����ص���������֮ǰ�Ƿ�truncate��

##�ļ���Ӧʱ��  yyyymmddhhMM
##����ʱ��͹���ϵ��ȷ������
rundate=$1
if [ ! -n "$rundate" ]; then 
 rundate=`date  +%Y%m%d%H`;
 coefficient=10
 min_time=`date "+%M"`
 min_time=`expr $min_time / $coefficient`
 min_time=`expr $min_time \* $coefficient`
 ##��ȫ��λ��ʱ���0
 if [ 1 -eq ${#min_time} ]; then 
 min_time=0$min_time
 fi
 rundate=$rundate$min_time
fi
##��ǰ����·��
localpath=$(dirname $(readlink -f "$0"))
##�������ݿ��û���������
source $localpath/constant_info.sh

##�ļ���
source_file_index='oltonline'
source_filename=$source_file_index'_'$rundate'.txt'
source_file_path='/lcims/kdzlfx/data/aaa/'
##source_file_path='/data/aaa/oltonline/'

##�ָ�rundate:yyyymmddhh
##min_time=${rundate:10:2}
##rundate=${rundate:0:10}

if [ ! -f "$source_file_path$source_filename" ]; then
    echo "$source_file_path$source_filename not exist."
    exit;
fi;

dstr=${rundate}"|"
sed "s/^/$dstr&/g" $source_file_path$source_filename > $source_file_path$source_filename.load
##�ָ�rundate:yyyymmddhh
min_time=${rundate:10:2}
rundate=${rundate:0:10}

##��ǰ׺
table_name_index='OLT_ONLINE'
month=`echo $rundate|cut -c5-6`
table_name=$table_name_index'_'$month
##�ļ���Ӧ���е��ֶ�
table_field="STA_DATE date 'yyyymmddhh24mi',OLTIP,ONLINENUM"

##��������ǰ�Ƿ�truncate��
truncate_flag='false'

bash fun_sqlldr.sh $datasource $source_file_path $source_filename.load $table_name "$table_field" $truncate_flag

rm $source_file_path$source_filename.load
gzip $source_file_path$source_filename
mv  $source_file_path$source_filename* /data/aaa/oltonline/
###
####����kdzlfx��webģ��Ľӿ�����OLT�澯����
current_date=`date "+%Y-%m-%d"`
begin_date=`date "+%Y-%m-%d" -d "-7 days"`
hours_time=`date "+%H"`:$min_time:00
curl 'http://127.0.0.1:8980/data/forecastOltOnline.do?format=yyyy-MM-dd%20HH24:mi:ss&time='$current_date'%20'$hours_time'&beginTime='$begin_date'%20'$hours_time'&endTime='$current_date'%20'$hours_time'&oltip=&isUseDB=false'
