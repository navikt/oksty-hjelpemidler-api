FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-27@sha256:5b0826d805fb1757579ada4da587e677be30c4b4c20e1ba9ef7602a2699921da
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]