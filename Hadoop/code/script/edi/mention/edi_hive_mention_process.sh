#!/bin/bash
echo "START.$0"
start_time=$(date +%s)
source /etc/profile

cd "$(dirname "$0")"

./edi_hive_do_mention.sh

./edi_hive_transfer_mention.sh

echo ">>>$0 DONE.spend time(s) :$(( $(date +%s) - $start_time ))"
