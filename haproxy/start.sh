#!/usr/bin/env bash

HAPROXY_HOME=/Users/fe2s/Soft/haproxy

$HAPROXY_HOME/sbin/haproxy -f haproxy.conf -D -p pid
