package com.ldg.serviceprovider.plugin

import com.android.utils.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

public class ServiceInjectHandler {

    public static void inject(String injectTargetClassName, File targetClassFile,
                              Map<String, ServiceAnnotationEntry> services) {
        if (targetClassFile == null
                || services == null
                || services.isEmpty()) {
            return
        }

        String realInjectTargetClassName = injectTargetClassName + ".class"
        File newFile = new File(targetClassFile.parent, targetClassFile.name + ".change")
        newFile.deleteOnExit()


        if (targetClassFile.name.endsWith(".jar")) {
            JarOutputStream jos = new JarOutputStream(new FileOutputStream(newFile))
            JarFile jarFile = new JarFile(targetClassFile)
            def enumeration = jarFile.entries()
            while (enumeration.hasMoreElements()) {
                def jarEntry = enumeration.nextElement()
                jos.putNextEntry(new JarEntry(jarEntry.name))

                jarFile.getInputStream(jarEntry).withCloseable { is ->
                    jos.write(jarEntry.name == realInjectTargetClassName ?
                            visitClass(new ClassReader(is), injectTargetClassName, services)
                            : IOUtils.toByteArray(is))
                    is.close()
                }
                jos.closeEntry()
            }
            jos.flush()
            jos.finish()
            jos.close()
            jarFile.close()
        } else {
            byte[] newContent = visitClass(new ClassReader(targetClassFile.bytes), injectTargetClassName, services)
            org.apache.commons.io.FileUtils.writeByteArrayToFile(newFile, newContent)
        }
        targetClassFile.delete()
        FileUtils.renameTo(newFile, targetClassFile)
        targetClassFile = null
    }

    private static byte[] visitClass(ClassReader reader,
                                     String injectTargetClassName,
                                     Map<String, ServiceAnnotationEntry> services) {
        if (reader == null) {
            return
        }

        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        ServiceInjectVisitor visitor = new ServiceInjectVisitor(Opcodes.ASM7, writer)
        visitor.setInjectTargetClassName(injectTargetClassName)
        visitor.setServicesMap(services)
        reader.accept(visitor, ClassReader.EXPAND_FRAMES)
        return writer.toByteArray()
    }
}