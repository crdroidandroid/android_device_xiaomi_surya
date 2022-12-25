#!/usr/bin/env python
#
# Copyright (C) 2023 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

from hashlib import sha1
import sys

device='surya'
vendor='xiaomi'

lines = [ line for line in open('proprietary-files.txt', 'r') ]
vendorPath = '../../../vendor/' + vendor + '/' + device + '/proprietary'
needSHA1 = False

def cleanup():
  for index, line in enumerate(lines):
    # Remove '\n' character
    line = line[:-1]

    # Skip empty or commented lines
    if len(line) == 0 or line[0] == '#':
      continue

    # Drop SHA1 hash, if existing
    if '|' in line:
      line = line.split('|')[0]
      lines[index] = '%s\n' % (line)

def update():
  for index, line in enumerate(lines):
    # Remove '\n' character
    line = line[:-1]

    # Skip empty lines
    if len(line) == 0:
      continue

    # Check if we need to set SHA1 hash for the next files
    if line[0] == '#':
      needSHA1 = (' - from' in line)
      continue

    if needSHA1:
      # Remove existing SHA1 hash
      line = line.split('|')[0]
      filePath = line.split(':')[1] if len(line.split(':')) == 2 else line

      if filePath[0] == '-':
        file = open('%s/%s' % (vendorPath, filePath[1:]), 'rb').read()
      else:
        file = open('%s/%s' % (vendorPath, filePath), 'rb').read()

      hash = sha1(file).hexdigest()
      lines[index] = '%s|%s\n' % (line, hash)

if len(sys.argv) == 2 and sys.argv[1] == '-c':
  cleanup()
else:
  update()

with open('proprietary-files.txt', 'w') as file:
  for line in lines:
    file.write(line)

  file.close()
