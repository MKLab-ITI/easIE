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
public class HTMLElementNotFoundException extends Exception {

    public HTMLElementNotFoundException() {
    }

    public HTMLElementNotFoundException(String message) {
        super(message);
    }

    public HTMLElementNotFoundException(Throwable cause) {
        super(cause);
    }

    public HTMLElementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
