ps -ef | grep "\-Dmockapi=true" | grep -v grep | awk '{print $2}' | xargs -i kill -9 {}
