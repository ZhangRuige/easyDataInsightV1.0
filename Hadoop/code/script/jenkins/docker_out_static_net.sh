#/bin/bash
if [ -z $1 ] || [ -z $2 ] || [ -z $3 ] || [ -z $4 ] || [ -z $5 ];
then
	echo "*****Input the necessary parameters: CONTAINERID IP MASK GATEWAY ETHNAME"
	echo "*****Call the script like: sh manual_con_static_ip.sh  b0e18b6a4432 192.168.5.123 24 192.168.5.1 deth0"
	exit
fi

CONTAINERID=$1
SETIP=$2
SETMASK=$3
GATEWAY=$4
ETHNAME=$5

#判断宿主机网卡是否存在
ifconfig $ETHNAME > /dev/null 2>&1
if [ $? -eq 0 ]; then
    read -p "$ETHNAME exist,do you want delelte it? y/n " del
    if [[ $del == 'y' ]]; then
    ip link del $ETHNAME
    else
    exit
    fi
fi
#
pid=`docker inspect -f '{{.State.Pid}}' $CONTAINERID`
mkdir -p /var/run/netns
find -L /var/run/netns -type l -delete

if [ -f /var/run/netns/$pid ]; then
    rm -f /var/run/netns/$pid
fi
ln -s /proc/$pid/ns/net /var/run/netns/$pid
#
ip link add $ETHNAME type veth peer name B
brctl addif bridge0 $ETHNAME
ip link set $ETHNAME up
ip link set B netns $pid
#先删除容器内已存在的eth0
ip netns exec $pid ip link del eth0 > /dev/null 2>&1
#设置容器新的网卡eth0
ip netns exec $pid ip link set dev B name eth0
ip netns exec $pid ip link set eth0 up
ip netns exec $pid ip addr add $SETIP/$SETMASK dev eth0
ip netns exec $pid ip route add default via $GATEWAY