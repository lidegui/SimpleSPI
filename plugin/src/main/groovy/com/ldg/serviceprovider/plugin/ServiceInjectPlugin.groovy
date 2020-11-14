package com.ldg.serviceprovider.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Plugin
import org.gradle.api.Project

public class ServiceInjectPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        BaseExtension android = project.extensions.findByName("android")
        if (android) {
            android.registerTransform(new InjectTransform(project))
        }
    }
}