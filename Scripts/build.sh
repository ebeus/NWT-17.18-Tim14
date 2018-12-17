#!/bin/bash

project_paths=("../Eureka" "../Korisnici" "../Putovanje" "../Gateway" "../Log")
pom_xml="/pom.xml"
target_dir="/target/"
extension="*.jar"
copy_destination="../build"

mkdir -p $copy_destination;

for i in "${project_paths[@]}"
do
    :
    mvn clean package -f $i$pom_xml
    if [ $? -ne 0 ]; then
        echo "Build of {$i} failed"
    fi
done

for i in "${project_paths[@]}"
do
    :
    cp $i$target_dir$extension $copy_destination
    if [ $? -ne 0 ]; then
        echo "Cannot copy file ${i}|${target_dir}|${extension} to  ${copy_destination}"
    fi
done

