name=im
docker stop $name
docker rm $name
docker build -t $name:uat .
docker run -d -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=uat" -v /home/ec2-user/im-services-v2/logs:/im/logs:rw -v /home/ec2-user/im-services-v2/upload:/upload:rw  --name $name $name:uat
