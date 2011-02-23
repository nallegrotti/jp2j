dir=`dirname $0`
cd $dir
cd ..
ps=`ps -ef | grep mockapi | grep -v grep`
#if [ "$ps" == "" ]; then
	$1/bin/grails $2 $3 $4 > mockapi.log 
#else
#	echo "no levanto nada!!! ps=$ps" >> mockapi.log
#fi
