#!/bin/bash
. /etc/profile.d/rvm.sh
export TROPO1_DRB_PORT=$2
export RAYO_JID=$3
export RAYO_SIP_URI=$4
export RAYO_CONCURRENT_TESTS=true
echo "Using DRB port: $TROPO1_DRB_PORT"
echo "Connecting with jid $RAYO_JID"
echo "Pitching to $RAYO_SIP_URI"
echo "starting load test"
cd $1
echo "Launching rvm"
rvm use ruby-1.8.7
bundle exec rake load_spec


exit