#!/usr/bin/env bash

CONF_DIR=/Users/fe2s/Projects/zk-tenant/haproxy
HAPROXY_HOME=/Users/fe2s/Soft/haproxy

echo "Reloading HAProxy config"
$HAPROXY_HOME/sbin/haproxy -f $CONF_DIR/haproxy.conf -D -p $CONF_DIR/pid -sf $(cat $CONF_DIR/pid)
