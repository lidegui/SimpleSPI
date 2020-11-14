package com.ldg.serviceprovider.plugin

public class Logger {

    private String mOutputPath;
    private File mOutputFile;

    private Writer mWriter;

    Logger(String outputPath) {
        mOutputPath = outputPath

        if (mOutputPath) {
            mOutputFile = new File(mOutputPath)
            if (!mOutputFile.exists()) {
                mOutputFile.parentFile.mkdirs()
            }

            mWriter = new BufferedWriter(new FileWriter(mOutputFile))
        } else {
            mOutputFile = null
        }
    }

    public void print(String msg) {
        println msg
        if (mOutputFile == null) {
            return
        }

        mWriter.write(msg)
        mWriter.write("\n")
        mWriter.flush()
    }

}