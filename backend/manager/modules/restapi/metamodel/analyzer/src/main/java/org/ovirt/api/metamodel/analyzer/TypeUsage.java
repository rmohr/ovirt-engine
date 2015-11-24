/*
Copyright (c) 2015 Red Hat, Inc.

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

package org.ovirt.api.metamodel.analyzer;

import org.ovirt.api.metamodel.concepts.Name;

/**
 * This class is used to remember that an undefined type was used, and how to replace it with the real one.
 */
public class TypeUsage {
    // The name of the undefined type:
    private Name name;

    // The method used to replace the type:
    private TypeSetter setter;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public TypeSetter getSetter() {
        return setter;
    }

    public void setSetter(TypeSetter setter) {
        this.setter = setter;
    }
}
