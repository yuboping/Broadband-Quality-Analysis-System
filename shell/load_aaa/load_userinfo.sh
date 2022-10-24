#!/bin/bash

##����ʱ����ļ���������ƴдctl�ļ�
##�ļ������ݽ���sqlload���ص����ݿ���userbill

##source_file_path��ԭʼ�ļ�·��
##source_file_index��ԭʼ�ļ�����ǰ׺
##table_name_index����Ӧ���ݿ��б�ǰ׺
##truncate_flag�����ص���������֮ǰ�Ƿ�truncate��

##�ļ���Ӧʱ��  yyyymmdd
rundate=$1
if [ ! -n "$rundate" ]; then 
    rundate=`date +%Y%m%d`
fi

tmp_date=`date -d "$rundate" +%Y%m%d`;
if [ "$rundate" != "$tmp_date" ]; then
    echo "{$rundate} format is error.date format is yyyymmdd."
    exit;
fi;

##��ǰ����·��
localpath=$(dirname $(readlink -f "$0"))
##�������ݿ��û���������
source $localpath/constant_info.sh

##�ļ���
source_file_index='userinfo'
source_filename=$source_file_index'_'$rundate'.txt'

if [ ! -f "$source_file_path/$source_filename" ]; then
    echo "$source_file_path/$source_filename not exist."
    exit;
fi;

##��ǰ׺
table_name_index='USER_INFO'
table_name=$table_name_index
##�ļ���Ӧ���е��ֶ�
table_field="USERNAME,AREANO,STATUS,BROADBANDWIDTH,GROUPID,OPENDATE,PAUSEDATE"

##��������ǰ�Ƿ�truncate��
truncate_flag='true'

sh fun_sqlldr.sh $datasource $source_file_path $source_filename $table_name $table_field $truncate_flag