/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl171.net.srv.bidi;

import java.io.Closeable;

/**
 * The ConnectionHandler interface for Message of type T
 */
public interface ConnectionHandler<T> extends Closeable{

    void send(T msg) ;//TODO in this method we must check if client is logged in and only then send the message.
    //TODO do we need to wait with the broadcast message until all clients who are reading data are finished? probably not.

}
