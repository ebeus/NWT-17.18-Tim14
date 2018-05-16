#!/bin/bash

project_paths=("Eureka_Server" "NWT_Tim14_Korisnici_Mikroservis" "Putovanje-mikroservis" "nwt_tim14_gateway" "Log_Mikroservis")
pom_xml="/pom.xml"
target_dir="/target/"
extension="*.jar"
copy_destination="Docker/files"

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

