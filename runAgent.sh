#!/bin/bash
java -cp /opt/grinder-3.6/lib/*:./target/libs/ -Dgrinder.jvm.arguments="-Dxmpp.username=rayo -Dxmpp.password=p@ssword  -Dxmpp.server=xmpp.testing.voxeolabs.net -Drayo.server=gw1-ext.testing.voxeolabs.net -Dsip.dial.uri=sip:usera@node1-ext.testing.voxeolabs.net -Dhudson.append.ext=true" net.grinder.Grinder grinder.properties
