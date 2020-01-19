package com.agraph.storage;

import com.agraph.config.Config;
import com.agraph.config.ConfigDescriptor;
import com.agraph.config.Configurable;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public @Data class StructureOptions implements Configurable {

    @ConfigDescriptor(value = "agraph.structure.element.maxIdLen",
            defaultValue = "33", minValue = 1, maxValue = 255)
    private int maxIdLength;

    @ConfigDescriptor(value = "agraph.structure.element.maxLabelLen",
            defaultValue = "32", minValue = 1, maxValue = 255)
    private int maxLabelLength;

    @ConfigDescriptor(value = "agraph.structure.property.maxNameLen",
            defaultValue = "32", minValue = 1, maxValue = 255)
    private int maxPropertyNameLength;

    @ConfigDescriptor(value = "agraph.structure.property.maxValueLen",
            defaultValue = "65535", minValue = 1, maxValue = 65535)
    private int maxPropertyValueLength;

    public StructureOptions(Config conf) {
        this.configure(conf);
    }

    public StructureOptions() {
    }
}
