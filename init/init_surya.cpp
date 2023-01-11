/*
   Copyright (C) 2020 The LineageOS Project.

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are
   met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of The Linux Foundation nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.

   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
   WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
   ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
   BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
   CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
   SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
   BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
   OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
   IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <vector>

#include <android-base/properties.h>
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>

using android::base::GetProperty;

std::vector<std::string> ro_props_default_source_order = {
    "",
    "bootimage.",
    "odm.",
    "product.",
    "system.",
    "system_ext.",
    "vendor.",
    "vendor_dlkm.",
};

void property_override(char const prop[], char const value[], bool add = true)
{
    prop_info *pi;

    pi = (prop_info *) __system_property_find(prop);
    if (pi)
        __system_property_update(pi, value, strlen(value));
    else if (add)
        __system_property_add(prop, strlen(prop), value, strlen(value));
}

void set_ro_build_prop(const std::string &prop, const std::string &value) {
    for (const auto &source : ro_props_default_source_order) {
        auto prop_name = "ro." + source + "build." + prop;
        if (source == "")
            property_override(prop_name.c_str(), value.c_str());
        else
            property_override(prop_name.c_str(), value.c_str(), false);
    }
};

void set_ro_product_prop(const std::string &prop, const std::string &value) {
    for (const auto &source : ro_props_default_source_order) {
        auto prop_name = "ro.product." + source + prop;
        property_override(prop_name.c_str(), value.c_str(), false);
    }
};

void vendor_load_properties() {
    std::string region;
    std::string hardware_revision;
    std::string hwname;
    std::string build_type;
    region = GetProperty("ro.boot.hwc", "GLOBAL");
    hardware_revision = GetProperty("ro.boot.hwversion", "UNKNOWN");
    hwname = GetProperty("ro.boot.hwname", "surya");
    build_type = GetProperty("ro.build.type", "userdebug");

    std::string model;
    std::string name;
    std::string device;
    std::string fingerprint;
    std::string flavor;
    std::string description;
    std::string marketname;

    if (hwname == "karna") {
        model = "M2007J20CI";
        name = "karna_in";
        device = "karna";
        fingerprint = "POCO/karna_in/karna:12/RKQ1.211019.001/V13.0.2.0.SJGINXM:user/release-keys";
        description = "karna_in-user 12 RKQ1.211019.001 V13.0.2.0.SJGINXM release-keys";
        marketname = "POCO X3";
    } else {
        name = "surya_id";
        device = "surya";
        fingerprint = "POCO/surya_id/surya:12/RKQ1.211019.001/V13.0.5.0.SJGIDXM:user/release-keys";
        description = "surya_id-user 12 RKQ1.211019.001 V13.0.5.0.SJGIDXM release-keys";
        marketname = "POCO X3 NFC";

        if (region == "THAI" || region == "THAI_PA")
            model = "M2007J20CT";
        else
            model = "M2007J20CG";
    }

    flavor = device + "-" + build_type;

    set_ro_build_prop("fingerprint", fingerprint);
    set_ro_product_prop("device", device);
    set_ro_product_prop("model", model);
    set_ro_product_prop("name", name);
    property_override("ro.build.description", description.c_str());
    property_override("ro.build.flavor", flavor.c_str());
    property_override("ro.build.product", device.c_str());
    property_override("ro.boot.hardware.revision", hardware_revision.c_str());
    property_override("bluetooth.device.default_name", marketname.c_str());
    property_override("vendor.usb.product_string", marketname.c_str());
}
