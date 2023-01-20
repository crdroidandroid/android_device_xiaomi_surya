ifneq ($(BOARD_VENDOR_QCOM_GPS_LOC_API_HARDWARE),)

# Set required flags
GNSS_CFLAGS := \
    -Werror \
    -Wno-error=unused-parameter \
    -Wno-error=macro-redefined \
    -Wno-error=reorder \
    -Wno-error=missing-braces \
    -Wno-error=self-assign \
    -Wno-error=enum-conversion \
    -Wno-error=logical-op-parentheses \
    -Wno-error=null-arithmetic \
    -Wno-error=null-conversion \
    -Wno-error=parentheses-equality \
    -Wno-error=undefined-bool-conversion \
    -Wno-error=tautological-compare \
    -Wno-error=switch \
    -Wno-error=date-time

LOCAL_PATH := $(call my-dir)
include $(call all-makefiles-under,$(LOCAL_PATH))

GNSS_SANITIZE_DIAG := cfi bounds null unreachable integer address

endif # ifneq ($(BOARD_VENDOR_QCOM_GPS_LOC_API_HARDWARE),)
