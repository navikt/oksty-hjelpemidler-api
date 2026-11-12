FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-27@sha256:c3d6ffee12ae4087230fa2446c19a4443dd8047c7f506bfe9f79c8083cffcd24
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]