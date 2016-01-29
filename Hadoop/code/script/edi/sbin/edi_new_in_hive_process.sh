#!/bin/bash

start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START $0 AT $cur_date"
source /etc/profile

cd /opt/running/edi/sbin/

CRAWL_HOST_CONF=`sed '/^CRAWL_HOST_CONF=/!d;s/.*=//' ../etc/edi.conf`
echo "$CRAWL_HOST_CONF"
if [ "" = "$CRAWL_HOST_CONF" ];then
	CRAWL_HOST_CONF=crawl_host.conf	#default
fi

cat ../etc/$CRAWL_HOST_CONF | while read CRAWL_HOST
do
	echo "INFO:edi_new_in_hive.sh $CRAWL_HOST running ..."
	sh edi_new_in_hive.sh $CRAWL_HOST
done

echo ">>>$0 DONE.spend time(s) :$(( $(date +%s) - $start_time ))"
