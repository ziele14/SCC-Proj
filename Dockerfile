FROM tomcat:10.0-jdk17-openjdk
WORKDIR /usr/local/tomcat
COPY target/project-1.0.war webapps/ROOT.war
EXPOSE 8080