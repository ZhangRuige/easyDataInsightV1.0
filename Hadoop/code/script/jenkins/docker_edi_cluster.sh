#!/bin/bash

DESC="Docker Environment EDI Cluster"
NAME=docker_edi_cluster
PIDFILE=/var/run/$NAME.pid
HOSTS=hadoopmysql,jenkins,hadoopnamenode,hadoopdatanode1,hadoopdatanode2,hadoopdatanode3,crawljd


d_create() {
	OLD_IFS="$IFS" 
	IFS="," 
	arr=($HOSTS) 
	IFS="$OLD_IFS"
	for HOSTNAME in ${arr[@]}
	do 
		docker create -ti -h $HOSTNAME \
		-v /opt/running:/opt/running -v /opt/soft:/opt/soft \
		--name $HOSTNAME \
		--net=none \
		--add-host hadoopmysql:192.168.0.20 \
		--add-host jenkins:192.168.0.21 \
		--add-host hadoopnamenode:192.168.0.30 \
		--add-host hadoopdatanode1:192.168.0.31 \
		--add-host hadoopdatanode2:192.168.0.32 \
		--add-host hadoopdatanode3:192.168.0.33 \
		--add-host hadoopdatanode4:192.168.0.34 \
		--add-host hadoopdatanode4:192.168.0.35 \
		--add-host crawljd:192.168.0.50 \
		edi_hadoop_tpl:hadoop '/bin/bash';
	done
	
	d_start
	
}

d_reset_ip() {
	sudo sh docker_out_static_net.sh hadoopmysql 192.168.0.20 255.255.255.0 192.168.0.1 br20
	sudo sh docker_out_static_net.sh jenkins 192.168.0.21 255.255.255.0 192.168.0.1 br21
	sudo sh docker_out_static_net.sh hadoopnamenode 192.168.0.30 255.255.255.0 192.168.0.1 br30
	sudo sh docker_out_static_net.sh hadoopdatanode1 192.168.0.31 255.255.255.0 192.168.0.1 br31
	sudo sh docker_out_static_net.sh hadoopdatanode2 192.168.0.32 255.255.255.0 192.168.0.1 br32
	sudo sh docker_out_static_net.sh hadoopdatanode3 192.168.0.33 255.255.255.0 192.168.0.1 br33
	sudo sh docker_out_static_net.sh crawljd 192.168.0.50 255.255.255.0 192.168.0.1 br50
}

d_restart_service (){
	docker exec "$1" source /etc/profile
	#if exec failure ,next line error "-bash: ./ssh: /bin/sh: bad interpreter: No such file or directory"
	docker exec "$1" /etc/init.d/ssh restart
}

d_start() {
	
	OLD_IFS="$IFS" 
	IFS="," 
	arr=($HOSTS) 
	IFS="$OLD_IFS"
	for HOSTNAME in ${arr[@]}
	do 
		echo -n "Starting $HOSTNAME ..."
		docker restart $HOSTNAME
		d_restart_service $HOSTNAME
	done
	
	d_reset_ip
}

d_stop() {
	OLD_IFS="$IFS" 
	IFS="," 
	arr=($HOSTS) 
	IFS="$OLD_IFS"
	for HOSTNAME in ${arr[@]}
	do 
		docker $HOSTNAME stop
	done
}

case $1 in
	create)
	echo -n "Creating $DESC: $NAME"
	d_create
	echo "."
	;;
	start)
	echo -n "Starting $DESC: $NAME"
	d_start
	echo "."
	;;
	stop)
	echo -n "Stopping $DESC: $NAME"
	d_stop
	echo "."
	;;
	restart)
	echo -n "Restarting $DESC: $NAME"
	d_stop
	sleep 1
	d_start
	echo "."
	;;
	resetip)
	echo -n "Resetting ip $DESC: $NAME"
	d_reset_ip
	sleep 1
	d_start
	echo "."
	;;
	*)
	echo "usage: $NAME {create|start|stop|restart|resetip}"
	exit 1
	;;
esac

exit 0