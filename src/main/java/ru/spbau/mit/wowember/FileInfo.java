package ru.spbau.mit.wowember;

public class FileInfo {

    private final boolean isDirectory;
    private final String fileName;

    public FileInfo(String fileName, boolean isDirectory) {
        this.fileName = fileName;
        this.isDirectory = isDirectory;
    }


    public String getFileName() {
        return fileName;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof FileInfo) && fileName.equals(((FileInfo) object).getFileName())
                    && isDirectory == ((FileInfo) object).isDirectory();
    }

}
