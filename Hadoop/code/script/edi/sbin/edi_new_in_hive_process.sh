#!/bin/bash

start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START .$0 AT $cur_date"
source /etc/profile

cd /opt/running/edi/sbin/

CRAWL_HOST_CONF=`sed '/^CRAWL_HOST_CONF=/!d;s/.*=//' ../etc/edi.conf`
if [ "" = "$CRAWL_HOST_CONF" ];then
	CRAWL_HOST_CONF=crawl_host.conf	#default
fi

for crawl_host in ../etc/$CRAWL_HOST_CONF
do
	./edi_new_in_hive.sh crawl_host
	#skip if there is not new file
done

echo "time cost(s) :$(( $(date +%s) - $start_time ))"
echo ">>>DONE.$0"

