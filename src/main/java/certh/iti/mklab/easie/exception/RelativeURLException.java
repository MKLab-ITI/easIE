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
public class RelativeURLException extends Exception {

    public RelativeURLException() {

    }

    public RelativeURLException(String message) {
        super(message);
    }

    public RelativeURLException(Throwable cause) {
        super(cause);
    }

    public RelativeURLException(String message, Throwable cause) {
        super(message, cause);
    }
}
