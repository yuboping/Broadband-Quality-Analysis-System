#!/bin/bash

##根据时间和文件命名规则拼写ctl文件
##根据传输过来的原始数据进行sqlload加载到数据库中

##数据源
sql_user=$1
if [ ! -n "$sql_user" ]; then 
    echo "sql_user is empty";
    exit;
fi
##原始数据文件
source_file_path=$2
if [ ! -n "$source_file_path" ]; then 
    echo "source_file_path is empty";
    exit;
fi

##原始数据文件
source_filename=$3
if [ ! -n "$source_filename" ]; then 
    echo "source_filename is empty";
    exit;
fi
##数据对应的数据库中表名
table_name=$4
if [ ! -n "$table_name" ]; then 
    echo "table_name is empty";
    exit;
fi
##数据对应的的数据库表字段
table_field=$5
if [ ! -n "$table_field" ]; then 
    echo "table_field is empty";
    exit;
fi
##插入数据前是否truncate
truncate_flag=$6
if [ ! -n "$truncate_flag" ]; then 
    echo "truncate_flag is empty";
    exit;
fi

##文件分隔符
field_split="|"

##将table_field中的字符替换成引号
table_field=`echo $table_field|sed "s/#REPLACE_DATE_FORMAT#/ date 'YYYY-MM-DD HH24:MI:SS'/g"`

##当前脚本路径
localpath="$(dirname $(readlink -f "$0"))"

table_truncate_type="truncate"
if [ $truncate_flag = "false" ];then
    table_truncate_type="append"
fi

##拼写INFO的ctl文件
echo "">$localpath/ctls/$source_filename.ctl
echo -e "load data">>$localpath/ctls/$source_filename.ctl
echo -e "infile '"$source_file_path$source_filename"'">>$localpath/ctls/$source_filename.ctl
echo -e "$table_truncate_type into table $table_name ">>$localpath/ctls/$source_filename.ctl
echo -e "fields terminated by '$field_split'">>$localpath/ctls/$source_filename.ctl
echo -e "trailing nullcols">>$localpath/ctls/$source_filename.ctl
echo -e "($table_field)">>$localpath/ctls/$source_filename.ctl

echo `sqlldr userid=$sql_user errors=10000 skip=1 control="$localpath/ctls/$source_filename.ctl" bad="$localpath/bads/$source_filename.bad" log="$localpath/logs/$source_filename.log"`>/dev/null