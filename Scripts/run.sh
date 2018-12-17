jar_paths="../build/*" 

for i in $jar_paths
do
    echo "Running $i"
    java -jar -XX:+UseSerialGC -Xss512k $i &
done
