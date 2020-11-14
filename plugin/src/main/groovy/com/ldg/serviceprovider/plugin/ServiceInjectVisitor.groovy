package com.ldg.serviceprovider.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

public class ServiceInjectVisitor extends ClassVisitor {

    private String mInjectTargetClassName;
    private Map<String, ServiceAnnotationEntry> mServicesMap
    private boolean hasInjected


    ServiceInjectVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }

    void setInjectTargetClassName(String injectTargetClassName) {
        mInjectTargetClassName = injectTargetClassName
    }

    void setServicesMap(Map<String, ServiceAnnotationEntry> servicesMap) {
        mServicesMap = servicesMap
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        def visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        if ("<clinit>" == name
                && !hasInjected
                && mInjectTargetClassName != null
                && mServicesMap != null
                && !mServicesMap.isEmpty()) {
            hasInjected = true
            log("服务管理类${mInjectTargetClassName}")
            log("————————————————————注册服务————————————————————")
            return new AdviceAdapter(Opcodes.ASM7, visitMethod, access, name, descriptor) {
                @Override
                void visitInsn(int opcode) {
                    if (opcode != RETURN) {
                        super.visitInsn(opcode);
                        return
                    }

                    mServicesMap.keySet().each { serviceImpl ->
                        ServiceAnnotationEntry entry = mServicesMap.get(serviceImpl)
                        visitLdcInsn(entry.providerName)
                        visitLdcInsn(serviceImpl)
                        visitLdcInsn(entry.singleton ? ICONST_1 : ICONST_0)
                        visitMethodInsn(Opcodes.INVOKESTATIC, mInjectTargetClassName,
                                "add", "(Ljava/lang/Object;Ljava/lang/String;Z)V", false)
                        log("serviceKey:${entry.providerName}\t serviceImpl:${serviceImpl}")
                    }

                    super.visitInsn(opcode)
                }
            }
        }
        return visitMethod;
    }

    private void log(Object o) {
        println o
        println "\n"
    }
}