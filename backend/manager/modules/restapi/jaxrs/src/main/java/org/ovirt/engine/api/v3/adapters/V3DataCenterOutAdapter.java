/*
Copyright (c) 2016 Red Hat, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.ovirt.engine.api.v3.adapters;

import static org.ovirt.engine.api.v3.adapters.V3OutAdapters.adaptOut;

import org.ovirt.engine.api.model.DataCenter;
import org.ovirt.engine.api.v3.V3Adapter;
import org.ovirt.engine.api.v3.types.V3DataCenter;
import org.ovirt.engine.api.v3.types.V3SupportedVersions;

public class V3DataCenterOutAdapter implements V3Adapter<DataCenter, V3DataCenter> {
    @Override
    public V3DataCenter adapt(DataCenter from) {
        V3DataCenter to = new V3DataCenter();
        if (from.isSetLinks()) {
            to.getLinks().addAll(adaptOut(from.getLinks()));
        }
        if (from.isSetActions()) {
            to.setActions(adaptOut(from.getActions()));
        }
        if (from.isSetComment()) {
            to.setComment(from.getComment());
        }
        if (from.isSetDescription()) {
            to.setDescription(from.getDescription());
        }
        if (from.isSetId()) {
            to.setId(from.getId());
        }
        if (from.isSetHref()) {
            to.setHref(from.getHref());
        }
        if (from.isSetLocal()) {
            to.setLocal(from.isLocal());
        }
        if (from.isSetMacPool()) {
            to.setMacPool(adaptOut(from.getMacPool()));
        }
        if (from.isSetName()) {
            to.setName(from.getName());
        }
        if (from.isSetQuotaMode()) {
            to.setQuotaMode(from.getQuotaMode().value());
        }
        if (from.isSetStatus()) {
            to.setStatus(adaptOut(from.getStatus()));
        }
        if (from.isSetStorageFormat()) {
            to.setStorageFormat(from.getStorageFormat().value());
        }
        if (from.isSetSupportedVersions()) {
            to.setSupportedVersions(new V3SupportedVersions());
            to.getSupportedVersions().getVersions().addAll(adaptOut(from.getSupportedVersions().getVersions()));
        }
        if (from.isSetVersion()) {
            to.setVersion(adaptOut(from.getVersion()));
        }
        return to;
    }
}
