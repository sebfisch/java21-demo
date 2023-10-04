FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu
CMD bash --login

ENV LANG en_US.UTF-8
ENV JAVA_HOME /usr/lib/jvm/msopenjdk-21-amd64
ENV PATH "${JAVA_HOME}/bin:${PATH}"

ENV USER=me
ENV GROUP=me
ENV HOME=/home/$USER
ARG UID=1000
ENV UID=$UID
ARG GID=1000
ENV GID=$GID

RUN \
  apt-get update && \
  apt-get install -y bsdmainutils ca-certificates git htop libxrender1 libxtst6 libxi6 man sudo && \
  rm -rf /var/lib/apt/lists/ && \
  addgroup --gid $GID $GROUP && \
  adduser --disabled-password --gecos '' --uid $UID --gid $GID $USER && \
  echo "$USER ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/$USER
