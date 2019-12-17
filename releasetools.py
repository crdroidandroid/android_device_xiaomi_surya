# Copyright (C) 2009 The Android Open Source Project
# Copyright (C) 2019 The Mokee Open Source Project
# Copyright (C) 2020 The LineageOS Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import common
import re

def FullOTA_InstallEnd(info):
  input_zip = info.input_zip
  OTA_InstallEnd(info, input_zip)
  return

def IncrementalOTA_InstallEnd(info):
  input_zip = info.target_zip
  OTA_InstallEnd(info, input_zip)
  return

def AddImage(info, input_zip, basename, dest):
  name = basename
  data = input_zip.read("IMAGES/" + basename)

  common.ZipWriteStr(info.output_zip, name, data)
  info.script.AppendExtra('package_extract_file("%s", "%s");' % (name, dest))

def AddImageRadio(info, input_zip, basename, dest):
  name = basename
  data = input_zip.read("RADIO/" + basename)

  common.ZipWriteStr(info.output_zip, name, data)
  info.script.Print("Patching {} image unconditionally...".format(dest.split('/')[-1]))
  info.script.AppendExtra('package_extract_file("%s", "%s");' % (name, dest))

def OTA_InstallEnd(info, input_zip):
  AddImage(info, input_zip, "dtbo.img", "/dev/block/bootdevice/by-name/dtbo")
  AddImage(info, input_zip, "vbmeta.img", "/dev/block/bootdevice/by-name/vbmeta")
  AddImage(info, input_zip, "vbmeta_system.img", "/dev/block/bootdevice/by-name/vbmeta_system")

  # Firmware
  AddImageRadio(info, input_zip, "cmnlib64.mbn", "/dev/block/bootdevice/by-name/cmnlib64")
  AddImageRadio(info, input_zip, "NON-HLOS.bin", "/dev/block/bootdevice/by-name/modem")
  AddImageRadio(info, input_zip, "cmnlib.mbn", "/dev/block/bootdevice/by-name/cmnlib")
  AddImageRadio(info, input_zip, "hyp.mbn", "/dev/block/bootdevice/by-name/hyp")
  AddImageRadio(info, input_zip, "BTFM.bin", "/dev/block/bootdevice/by-name/bluetooth")
  AddImageRadio(info, input_zip, "tz.mbn", "/dev/block/bootdevice/by-name/tz")
  AddImageRadio(info, input_zip, "aop.mbn", "/dev/block/bootdevice/by-name/aop")
  AddImageRadio(info, input_zip, "xbl_config.elf", "/dev/block/bootdevice/by-name/xbl_config")
  AddImageRadio(info, input_zip, "storsec.mbn", "/dev/block/bootdevice/by-name/storsec")
  AddImageRadio(info, input_zip, "uefi_sec.mbn", "/dev/block/bootdevice/by-name/uefisecapp")
  AddImageRadio(info, input_zip, "imagefv.elf", "/dev/block/bootdevice/by-name/imagefv")
  AddImageRadio(info, input_zip, "qupv3fw.elf", "/dev/block/bootdevice/by-name/qupfw")
  AddImageRadio(info, input_zip, "abl.elf", "/dev/block/bootdevice/by-name/abl")
  AddImageRadio(info, input_zip, "dspso.bin", "/dev/block/bootdevice/by-name/dsp")
  AddImageRadio(info, input_zip, "km4.mbn", "/dev/block/bootdevice/by-name/keymaster")
  AddImageRadio(info, input_zip, "devcfg.mbn", "/dev/block/bootdevice/by-name/devcfg")
  AddImageRadio(info, input_zip, "xbl.elf", "/dev/block/bootdevice/by-name/xbl")
  AddImageRadio(info, input_zip, "ffu.img", "/dev/block/bootdevice/by-name/ffu")
  return
