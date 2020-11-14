package com.ldg.serviceprovider.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.apache.commons.codec.digest.DigestUtils
import org.objectweb.asm.Opcodes

import java.util.jar.JarFile

public class InjectTransform extends Transform {

    public static final String INJECT_CLASS_NAME = "com/ldg/serviceprovider/ServiceManager"
    public static final String SERVICE_ANNOTATION_DECLARE = "Lcom/ldg/serviceprovider/ServiceDeclare;"

    private File mInjectTargetClassFile;
    private Map<String, ServiceAnnotationEntry> mServicesMap

    private Project mProject;

    public InjectTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return "serviceInject"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        // todo fix增量编译crash问题
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        log("——————————————${mProject.name}开始处理Service注解——————————————")

        def isIncremental = transformInvocation.incremental
        def outputProvider = transformInvocation.outputProvider

        if (!isIncremental) {
            outputProvider.deleteAll()
        }

        transformInvocation.inputs.each { tfInput ->
            tfInput.jarInputs
                    .each { jarInput ->
                        def srcFile = jarInput.file
                        def destFile = getJarDestFile(jarInput, transformInvocation.getOutputProvider())
                        if (isIncremental) {
                            switch (jarInput.status) {
                                case Status.REMOVED:
                                    log("remove")
                                    FileUtils.forceDeleteOnExit(srcFile)
                                    break
                                case Status.CHANGED:
                                case Status.ADDED:
                                    log("CHANGED")
                                    transformJar(srcFile, destFile)
                                    break
                                case Status.NOTCHANGED:
                                    log("nochange")
                                    break
                            }
                        } else {
                            transformJar(srcFile, destFile)
                        }

                        FileUtils.copyFile(srcFile, destFile)
                    }

            tfInput.directoryInputs
                    .each { directory ->
                        def destFile = outputProvider.getContentLocation(
                                directory.name,
                                directory.contentTypes,
                                directory.scopes,
                                Format.DIRECTORY)

                        if (isIncremental) {
                            directory.changedFiles
                                    .entrySet()
                                    .each { File srcFile, Status status ->
                                        switch (status) {
                                            case Status.REMOVED:
                                                FileUtils.forceDeleteOnExit(srcFile)
                                                break
                                            case Status.CHANGED:
                                            case Status.ADDED:
                                                if (checkNeedFile(srcFile)) {
                                                    transformClass(srcFile,
                                                            new File(srcFile.absolutePath.replace(directory.file.absolutePath, destFile.absolutePath)))
                                                }
                                                break
                                        }
                                    }
                        } else {
                            directory.file.eachFileRecurse { srcFile ->
                                if (checkNeedFile(srcFile)) {
                                    transformClass(srcFile, new File(srcFile.absolutePath.replace(directory.file.absolutePath, destFile.absolutePath)))
                                }
                            }
                        }

                        FileUtils.copyDirectoryToDirectory(directory.file, destFile)
                    }
        }

        if (mInjectTargetClassFile != null
                && mServicesMap != null
                && !mServicesMap.isEmpty()) {
            ServiceInjectHandler.inject(INJECT_CLASS_NAME, mInjectTargetClassFile, mServicesMap)
        }
        log("——————————————${mProject.name}结束处理Service注解——————————————")
    }

    private boolean checkNeedFile(File file) {
        return file != null && file.name.endsWith(".class") && !file.name.startsWith("R\$") && file.name != "R.class" && file.name != "BuildConfig.class"

    }

    private File getJarDestFile(JarInput jarInput, TransformOutputProvider provider) {
        def name = jarInput.file.name
        if (name.endsWith(".jar")) {
            name = name.substring(0, 4);
        }
        return provider.getContentLocation(name + "_" + DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8),
                jarInput.contentTypes,
                jarInput.scopes,
                Format.JAR)
    }

    private void transformJar(File srcFile, File destFile) {
        if (srcFile == null || !srcFile.file) {
            log("${srcFile}不是文件")
            return
        }

        def jarFile = new JarFile(srcFile)
        def enumeration = jarFile.entries()
        while (enumeration != null && enumeration.hasMoreElements()) {
            def jarEntry = enumeration.nextElement()
            if (jarEntry.name.endsWith(".class")) {
                def inputStream = jarFile.getInputStream(jarEntry)
                inputStream.withCloseable { it ->
                    ClassReader reader = new ClassReader(inputStream)
                    visitClass(reader, destFile)
                    it.close()
                }
            }
        }
        jarFile.close()
    }

    private void transformClass(File srcFile, File destFile) {
        if (srcFile != null && srcFile.file) {
            ClassReader reader = new ClassReader(srcFile.bytes)
            visitClass(reader, destFile)
        } else {
            log("不处理${srcFile}")
        }
    }

    private void visitClass(ClassReader reader, File destFile) {
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        ServiceAnnotationVisitor annotationVisitor = new ServiceAnnotationVisitor(Opcodes.ASM7, writer, SERVICE_ANNOTATION_DECLARE)
        reader.accept(annotationVisitor, ClassReader.EXPAND_FRAMES)

        if (annotationVisitor.getAnnotationEntry() != null) {
            if (mServicesMap == null) {
                mServicesMap = new HashMap<>()
            }

            mServicesMap.put(reader.className.replace("/", "."), annotationVisitor.getAnnotationEntry())
        }

        if (INJECT_CLASS_NAME == reader.className) {
            mInjectTargetClassFile = destFile
        }
    }

    private void log(Object val) {
        println val
        println "\n"
    }

}
