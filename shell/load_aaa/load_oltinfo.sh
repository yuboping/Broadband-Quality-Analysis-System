#!/bin/bash

##����ʱ����ļ���������ƴдctl�ļ�
##�ļ������ݽ���sqlload���ص����ݿ���oltinfo

##source_file_path��ԭʼ�ļ�·��
##source_file_index��ԭʼ�ļ�����ǰ׺
##table_name_index����Ӧ���ݿ��б�ǰ׺
##truncate_flag�����ص���������֮ǰ�Ƿ�truncate��

##��ǰ����·��
localpath=$(dirname $(readlink -f "$0"))
##�������ݿ��û���������
source $localpath/constant_info.sh

##�ļ���
source_file_index='oltinfo'
source_filename=$source_file_index'.txt'

if [ ! -f "$source_file_path/$source_filename" ]; then
    echo "$source_file_path/$source_filename not exist."
    exit;
fi;

##��ǰ׺
table_name_index='OLT_INFO'
table_name=$table_name_index
##�ļ���Ӧ���е��ֶ�
table_field="OLTID,OLTIP,OLTNAME,OLTGEOX,OLTGEOY"

##��������ǰ�Ƿ�truncate��
truncate_flag='true'

sh fun_sqlldr.sh $datasource $source_file_path $source_filename $table_name $table_field $truncate_flag