#!/bin/bash
#分类统计观点

export_pt=`hive -S -e 'select max(pt_date) from edi.$table;'`
