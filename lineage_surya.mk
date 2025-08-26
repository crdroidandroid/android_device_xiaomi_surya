#
# Copyright (C) 2020-2023 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
TARGET_SUPPORTS_OMX_SERVICE := false
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/non_ab_device.mk)

# Inherit from surya device
$(call inherit-product, device/xiaomi/surya/device.mk)

# Inherit some common Lineage stuff.
$(call inherit-product, vendor/lineage/config/common_full_phone.mk)

# crDroid build flag
TARGET_ENABLE_BLUR := true
TARGET_EXCLUDES_AUDIOFX := true

PRODUCT_NAME := lineage_surya
PRODUCT_DEVICE := surya
PRODUCT_BRAND := POCO
PRODUCT_MODEL := M2007J20CG
PRODUCT_MANUFACTURER := Xiaomi

PRODUCT_BUILD_PROP_OVERRIDES += \
    BuildDesc="surya_global-user 11 RKQ1.200826.002 V12.5.4.0.RJGMIXM release-keys" \
    BuildFingerprint=POCO/surya_eea/surya:11/RKQ1.200826.002/V12.5.4.0.RJGEUXM:user/release-keys

PRODUCT_GMS_CLIENTID_BASE := android-xiaomi
