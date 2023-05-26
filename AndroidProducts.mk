#
# Copyright (C) 2022 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

PRODUCT_MAKEFILES := \
    lmodroid_GX4:$(LOCAL_DIR)/GX4/lmodroid_GX4.mk \
    lmodroid_vidofnir:$(LOCAL_DIR)/vidofnir/lmodroid_vidofnir.mk

COMMON_LUNCH_CHOICES := \
    lmodroid_GX4-user \
    lmodroid_GX4-userdebug \
    lmodroid_GX4-eng \
    lmodroid_vidofnir-user \
    lmodroid_vidofnir-userdebug \
    lmodroid_vidofnir-eng
