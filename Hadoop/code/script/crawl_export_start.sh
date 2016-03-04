#/bin/bash

start_time=$(date +%s)
echo "START.$0"

cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date

source /etc/profile
cd `dirname $0`
pwd

if [ -e /home/hadoop/output/Comm/part-r-00000 ];then
	echo "Comm exists.skip."
else
	rm -r /home/hadoop/output/Comm
	java -jar ParseCrawlData.jar comment
fi

if [ -e /home/hadoop/output/Pro/part-r-00000 ];then
	echo "Pro exists.skip."
else
	rm -r /home/hadoop/output/Pro
	java -jar ParseCrawlData.jar product
	fetchtime='last.parse.time='$(date "+%a %b %e %T %Y")
	echo ${fetchtime//:/\\:}>$cur_dir/etc/config.properties
fi

echo 'spend time(s) :'$(( $(date +%s) - $start_time ))
 