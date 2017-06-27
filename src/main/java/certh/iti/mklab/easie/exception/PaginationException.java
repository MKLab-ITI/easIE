/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certh.iti.mklab.easie.exception;

/**
 *
 * @author vasgat
 */
public class PaginationException extends Exception{

    public PaginationException() {

    }

    public PaginationException(String message) {
        super(message);
    }

    public PaginationException(Throwable cause) {
        super(cause);
    }

    public PaginationException(String message, Throwable cause) {
        super(message, cause);
    }
}
