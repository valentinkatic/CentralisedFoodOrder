package com.katic.centralisedfoodorder.data.remote;

/**
 *
 * Provides an implementation of {@link FirebaseHandler}. This provider can decide whether to
 * provide real or mock implementation
 */
public class FirebaseProvider {

    public static FirebaseHandler provide() {
        return new FirebaseHandlerImpl();
    }

}
