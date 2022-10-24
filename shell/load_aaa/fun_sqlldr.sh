#!/bin/bash

##����ʱ����ļ���������ƴдctl�ļ�
##���ݴ��������ԭʼ���ݽ���sqlload���ص����ݿ���

##����Դ
sql_user=$1
if [ ! -n "$sql_user" ]; then 
    echo "sql_user is empty";
    exit;
fi
##ԭʼ�����ļ�
source_file_path=$2
if [ ! -n "$source_file_path" ]; then 
    echo "source_file_path is empty";
    exit;
fi

##ԭʼ�����ļ�
source_filename=$3
if [ ! -n "$source_filename" ]; then 
    echo "source_filename is empty";
    exit;
fi
##���ݶ�Ӧ�����ݿ��б���
table_name=$4
if [ ! -n "$table_name" ]; then 
    echo "table_name is empty";
    exit;
fi
##���ݶ�Ӧ�ĵ����ݿ���ֶ�
table_field=$5
if [ ! -n "$table_field" ]; then 
    echo "table_field is empty";
    exit;
fi
##��������ǰ�Ƿ�truncate
truncate_flag=$6
if [ ! -n "$truncate_flag" ]; then 
    echo "truncate_flag is empty";
    exit;
fi

##�ļ��ָ���
field_split="|"

##��table_field�е��ַ��滻������
table_field=`echo $table_field|sed "s/#REPLACE_DATE_FORMAT#/ date 'YYYY-MM-DD HH24:MI:SS'/g"`

##��ǰ�ű�·��
localpath="$(dirname $(readlink -f "$0"))"

table_truncate_type="truncate"
if [ $truncate_flag = "false" ];then
    table_truncate_type="append"
fi

##ƴдINFO��ctl�ļ�
echo "">$localpath/ctls/$source_filename.ctl
echo -e "load data">>$localpath/ctls/$source_filename.ctl
echo -e "infile '"$source_file_path$source_filename"'">>$localpath/ctls/$source_filename.ctl
echo -e "$table_truncate_type into table $table_name ">>$localpath/ctls/$source_filename.ctl
echo -e "fields terminated by '$field_split'">>$localpath/ctls/$source_filename.ctl
echo -e "trailing nullcols">>$localpath/ctls/$source_filename.ctl
echo -e "($table_field)">>$localpath/ctls/$source_filename.ctl

echo `sqlldr userid=$sql_user errors=10000 skip=1 control="$localpath/ctls/$source_filename.ctl" bad="$localpath/bads/$source_filename.bad" log="$localpath/logs/$source_filename.log"`>/dev/null