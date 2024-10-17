package com.masferrer.utils;

//Se creo una clase nueva para tener mensajes de excepciones personalizadas
public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException(String message){
        super(message);
    }
}
