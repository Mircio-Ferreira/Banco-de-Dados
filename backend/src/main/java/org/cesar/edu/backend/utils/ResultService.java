package org.cesar.edu.backend.utils;

public class ResultService {
    private boolean valid;
    private boolean realized;
    private ListaString error;
    public ResultService(boolean valid, boolean realized, ListaString error) {
        this.valid = valid;
        this.realized = realized;
        this.error = error;
    }
    public void addError(String error){
        this.error.adicionar(error);
    }
}