#!/bin/bash
cwd=$(pwd)
cd /opt
wget http://ftp.fau.de/apache/kafka/0.8.1.1/kafka_2.9.2-0.8.1.1.tgz
tar xzvf kafka_2.9.2-0.8.1.1.tgz
ln -s kafka_2.9.2-0.8.1.1 kafka
cd $cwd