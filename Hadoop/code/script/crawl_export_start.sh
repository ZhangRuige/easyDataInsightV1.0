#/bin/bash

start_time=$(date +%s)
echo "START.$0"

cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date

source /etc/profile
cd `dirname $0`
pwd

rm -r ~/output/Comm ~/output/Pro

if [ -d /home/hadoop/output/Comm ];then
	echo "Comm exists.skip."
else
	java -jar ParseCrawlData.jar comment
fi

if [ -d /home/hadoop/output/pro ];then
	echo "Pro exists.skip."
else
	java -jar ParseCrawlData.jar product
fi

echo 'spend time(s) :'$(( $(date +%s) - $start_time ))
