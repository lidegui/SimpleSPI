package com.ldg.serviceprovider.plugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

public class ServiceAnnotationVisitor extends ClassVisitor {

    private String mServiceAnnotationDeclare;
    private ServiceAnnotationEntry mAnnotationEntry

    ServiceAnnotationVisitor(int api, ClassVisitor classVisitor, String serviceAnnotationDeclare) {
        super(api, classVisitor)
        mServiceAnnotationDeclare = serviceAnnotationDeclare
    }

    @Override
    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (mServiceAnnotationDeclare == descriptor) {
            return new AnnotationVisitor(Opcodes.ASM7, super.visitAnnotation(descriptor, visible)) {
                @Override
                void visit(String name, Object value) {
                    if (mAnnotationEntry == null) {
                        mAnnotationEntry = new ServiceAnnotationEntry()
                    }
                    if ("provider" == name) {
                        mAnnotationEntry.providerName = ((Type) value).className
                    } else if ("singleton") {
                        mAnnotationEntry.singleton = (boolean) value
                    }
                    super.visit(name, value)
                }
            }
        }
        return super.visitAnnotation(descriptor, visible)
    }

    ServiceAnnotationEntry getAnnotationEntry() {
        return mAnnotationEntry
    }
}