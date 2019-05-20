package ru.eltex.magnus.server;

public class StorageException extends Exception{

    public StorageException(Exception exception){
        super(exception);
    }

    public StorageException(String msg){
        super(msg);
    }

    public StorageException(String msg, Exception exception){
        super(msg, exception);
    }
}
