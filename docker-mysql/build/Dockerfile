FROM mysql:5.7.17

ENV TZ=Asia/Shanghai
COPY ./preparation /preparation
RUN build_deps='ntpdate' \
    && pack='/preparation' \
    && mv /etc/apt/sources.list /etc/apt/sources.list.bak \
    && mv $pack/sources.list /etc/apt/sources.list \
    && apt-get clean \
    && apt-get update \
    && apt-get -y --force-yes install $build_deps \
    && apt-get purge -y --auto-remove $buil_deps \
    && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone
