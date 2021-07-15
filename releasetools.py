#
# Copyright (C) 2009 The Android Open Source Project
# Copyright (C) 2019 The Mokee Open Source Project
# Copyright (C) 2020-2021 The LineageOS Open Source Project
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
#

import common
import re

def FullOTA_Assertions(info):
  AddBasebandAssertion(info, info.input_zip)
  return

def IncrementalOTA_Assertions(info):
  AddBasebandAssertion(info, info.input_zip)
  return

def FullOTA_InstallEnd(info):
  OTA_InstallEnd(info)
  return

def IncrementalOTA_InstallEnd(info):
  OTA_InstallEnd(info)
  return

def AddImage(info, basename, dest):
  name = basename
  path = "IMAGES/" + name
  if path not in info.input_zip.namelist():
    return

  data = info.input_zip.read(path)
  common.ZipWriteStr(info.output_zip, name, data)
  info.script.Print("Patching {} image unconditionally...".format(dest.split('/')[-1]))
  info.script.AppendExtra('package_extract_file("%s", "%s");' % (name, dest))

def OTA_InstallEnd(info):
  AddImage(info, "dtbo.img", "/dev/block/bootdevice/by-name/dtbo")
  AddImage(info, "vbmeta.img", "/dev/block/bootdevice/by-name/vbmeta")
  AddImage(info, "vbmeta_system.img", "/dev/block/bootdevice/by-name/vbmeta_system")
  return

def AddBasebandAssertion(info, input_zip):
  android_info = input_zip.read("OTA/android-info.txt")
  m = re.search(r'require\s+version-baseband\s*=\s*(.+)', android_info)
  if m:
    modem_version, firmware_version = m.group(1).rstrip().split(',')
    modem_versions = modem_version.split('|')
    if ((len(modem_versions) and '*' not in modem_versions) and \
        (len(firmware_version) and '*' not in firmware_version)):
      cmd = 'assert(xiaomi.verify_baseband(' + ','.join(['"%s"' % baseband for baseband in modem_versions]) + ') == "1" || abort("ERROR: This package requires firmware from MIUI {1} or newer. Please upgrade firmware and retry!"););'
      info.script.AppendExtra(cmd.format(modem_versions, firmware_version))
  return
