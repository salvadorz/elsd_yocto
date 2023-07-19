# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# TODO: Set this  with the path to your assignments rep.  Use ssh protocol and see lecture notes
# about how to setup ssh-agent for passwordless access
SRC_URI = "git://git@github.com/salvadorz/ldd3;protocol=ssh;branch=master \
        file://0001-makefile.patch \
        file://misc-modules-start-stop"

PV = "1.0+git${SRCPV}"
# TODO: set to reference a specific commit hash in your assignment repo
SRCREV = "643047fe28da48ee377ec8acdffc4b36b88667a9"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at 
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
S = "${WORKDIR}/git"

inherit module

EXTRA_OEMAKE:append:task-install = " -C ${STAGING_KERNEL_DIR} M=${S}/misc-modules"
EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

# See https://git.yoctoproject.org/poky/plain/meta/conf/bitbake.conf?h=kirkstone
FILES:${PN} += "${bindir}/module_load"
FILES:${PN} += "${bindir}/module_unload"
FILES:${PN} += "${sysconfdir}/*"

# Update rc.d class for install the init service /etc/rc5.d/
inherit update-rc.d
#flag the package as one which uses init scripts
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "misc-modules-start-stop"

do_configure () {
	:
}

do_compile () {
	oe_runmake
}

do_install () {
	# TODO: Install your binaries/scripts here.
	# Be sure to install the target directory with install -d first
	# Yocto variables ${D} and ${S} are useful here, which you can read about at 
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-D
	# and
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-S
	# See example at https://github.com/cu-ecen-aeld/ecen5013-yocto/blob/ecen5013-hello-world/meta-ecen5013/recipes-ecen5013/ecen5013-hello-world/ecen5013-hello-world_git.bb

	#from poky/meta/conf/bitbake.conf

	#bindir = usr/bin
	install -d ${D}${bindir}
	install -m 0755 ${S}/misc-modules/module_load ${D}${bindir}/
	install -m 0755 ${S}/misc-modules/module_unload ${D}${bindir}/

	#sysconfdir = etc, so etc/init.d
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/misc-modules-start-stop ${D}${sysconfdir}/init.d

    #base_libdir = /lib
	install -d ${D}${base_libdir}/modules/${@KERNEL_VERSION}/
	install -m 0755 ${S}/misc-modules/faulty.ko ${D}${base_libdir}/modules/${@KERNEL_VERSION}/
	install -m 0755 ${S}/misc-modules/hello.ko ${D}${base_libdir}/modules/${@KERNEL_VERSION}/
}
