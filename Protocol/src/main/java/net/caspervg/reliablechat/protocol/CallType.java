/*
 * Copyright (c) 2014 Casper Van Gheluwe
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package net.caspervg.reliablechat.protocol;

public enum CallType {
    // Status >= 100 < 200: Whisper Success
    YOU_LOGGED_IN(100, "Logged in successfully"),
    YOU_LOGGED_OUT(101, "Logged out successfully"),
    MESSAGE_SENT(102, "Message sent successfully"),

    // Status >= 200 < 300: Whisper Failure
    NICKNAME_IN_USE(200, "This nickname is already in use"),
    RECIPIENT_NOT_EXIST(201, "The specified recipient does not exist or is not online"),

    // Status >= 300 < 400: Shout Success
    USER_LOGGED_IN(300, "A user has logged in"),
    USER_LOGGED_OUT(301, "A user has logged out"),

    // Status >= 400 < 500: Shout Failure
    SHUTTING_DOWN(400, "Server shutting down");

    private int code;
    private String message;

    CallType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
